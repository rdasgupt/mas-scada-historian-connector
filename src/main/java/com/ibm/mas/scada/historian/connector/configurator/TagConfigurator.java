/*
 * IBM Confidential
 * OCO Source Materials
 * 5725-S86, 5900-A0N, 5737-M66, 5900-AAA
 * (C) Copyright IBM Corp. 2021
 * The source code for this program is not published or otherwise divested of
 * its trade secrets, irrespective of what has been deposited with the U.S.
 * Copyright Office.
 */

package com.ibm.mas.scada.historian.connector.configurator;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import org.json.JSONArray;
import com.ibm.mas.scada.historian.connector.configurator.iotdevice.ConfigureInterfaces;
import com.ibm.mas.scada.historian.connector.utils.Constants;
import com.ibm.mas.scada.historian.connector.utils.Copyright;
import com.ibm.mas.scada.historian.connector.utils.RestClient;
import com.ibm.mas.scada.historian.connector.utils.ConnectorException;

public class TagConfigurator {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;
    private static Logger logger;
    private static Config config;
    private static String configDir;
    private static String dataDir;
    private static String orgId;
    private static String key;
    private static String token;
    private static String url;
    private static String baseUrl;
    private static JSONObject connectionConfig;
    private static JSONObject iotpConfig;
    private static JSONObject mappingConfig;
    private static TagDataCache tc;
    private static int totalInCache;
    private static TagmapConfig tmc;
    private static Set<String> tagList;
    private static int SAASEnv;
    private static int scadaType;

    public TagConfigurator (Config config, TagDataCache tc, TagmapConfig tmc) {
        super();
        this.config = config;
        this.logger = config.getLogger();
        this.configDir = config.getConfigDir();
        this.dataDir = config.getDataDir();
        this.connectionConfig = config.getConnectionConfig();
        this.mappingConfig = config.getMappingConfig();
        this.SAASEnv = config.isSAASEnv();
        this.scadaType = config.getScadaType();
        this.iotpConfig = connectionConfig.getJSONObject("iotp");
        this.orgId = iotpConfig.getString("orgId");
        this.key = iotpConfig.getString("apiKey");
        this.token = iotpConfig.getString("apiToken");
        this.baseUrl = iotpConfig.getString("url") + "/";
        this.url = iotpConfig.getString("url");
        this.tc = tc;
        this.tmc = tmc;
        this.tagList = tc.getTagList();
        this.totalInCache = tagList.size();
    }

    public void configure () throws Exception {
        int registeredNow = 0;
        int registeredBefore = 0;

        try {
            RestClient restClient = new RestClient(baseUrl, Constants.AUTH_BASIC, key, token);

            /* For each tagType in tagTypes list:
             *  - Create interfaces and device type
             *  - Get tagList from cache
             *  - For each tagpath in cache, create device
             */
            List<TagDeviceType> tagTypes = tmc.getTagTypes();

            for (int i = 0; i < tagTypes.size(); i++) {

                TagDeviceType tagType = tagTypes.get(i);
                String type = tagType.getType();

                logger.info("Create type and Interfaces for deviceType: " + type);
                JSONObject typeObject = createDeviceTypeObject(type, "Device");
                logger.fine("Create Type Object: " + typeObject.toString());
                restClient.post("device/types", typeObject.toString());
                int respCode = restClient.getResponseCode();
                logger.info(String.format("Create DeviceType Status Code: %d", respCode));
                if (respCode == 409) {
                    logger.info(String.format("Create DeviceType: %s - The device type already exists", type));
                } else if (respCode == 201) {
                    logger.info(String.format("Create DeviceType: %s - The device is created", type));
                } else {
                    String message = String.format("Failed to create DeviceType=%s Code=%d Message=%s", type, respCode, restClient.getResponseBody());
                    throw new ConnectorException(message);
                }

                String gwTypeId = type + "_GW";
                logger.info("Create gatewayType: " + gwTypeId);
                typeObject = createDeviceTypeObject(gwTypeId, "Gateway");
                logger.fine("Create Type Object: " + typeObject.toString());
                restClient.post("device/types", typeObject.toString());
                respCode = restClient.getResponseCode();
                logger.info(String.format("Create GatewayType Status Code: %d", respCode));
                if (respCode == 409) {
                    logger.info(String.format("Create GatewayType: %s - The gateway type already exists", gwTypeId));
                } else if (respCode == 201) {
                    logger.info(String.format("Create GatewayType: %s - The gateway is created", gwTypeId));
                } else {
                    String message = String.format("Failed to create GatewayType=%s Code=%d Message=%s", type, respCode, restClient.getResponseBody());
                    throw new ConnectorException(message);
                }

                try { 
                    ConfigureInterfaces confIntf;
                    if (SAASEnv == 0) {
                        confIntf = new ConfigureInterfaces(scadaType, url, dataDir, orgId, key, token);
                    } else {
                        confIntf = new ConfigureInterfaces(scadaType, dataDir, orgId, key, token);
                    }
                    confIntf.config(type);
 
                    /* activate interface */
                    String activateMethod = "draft/device/types/"+ type;
                    JSONObject activateObject = new JSONObject();
                    activateObject.put("operation","activate-configuration");
                    restClient.patch(activateMethod, activateObject.toString());
                    logger.info(String.format("ActivateInterface Status Code: %d", restClient.getResponseCode()));
                } catch (Exception e) {
                    String message = String.format("Exception caught while configuring interfaces");
                    throw new ConnectorException(message);
                }

                /* Get tag list from cache and bulk register devices */
                int batchSizeBytes = 0, devicesInType = 0, devicesRegistered = 0, alreadyRegistered = 0;
                JSONArray deviceObj = new JSONArray();

                Iterator<String> it = tagList.iterator();

                while (it.hasNext()) {
                    String id = it.next();
                    TagData td = tc.get(id);

                    if (td == null) {
                        logger.warning("Device data is null in cache. Id=" + id);
                        continue;
                    }

                    String deviceType = td.getDeviceType();
                    if (deviceType.equals("") || !type.equals(deviceType)) {
                        continue;
                    }

                    devicesInType += 1;
                    String deviceId = td.getDeviceId();
                    if (td.getDeviceStatus() == 1) {
                        logger.fine(String.format("Found device. Type=%s Id=%s", deviceType, deviceId));
                        alreadyRegistered += 1;
                        continue;
                    }

                    JSONObject device = createDeviceItem(deviceType, deviceId, token);
                    byte[] bytes = device.toString().getBytes("UTF-8");
                    int deviceSizeBytes = bytes.length;
                    if ((batchSizeBytes + deviceSizeBytes) > 512*1024) {
                        /* bulk register devices, reset batchSizeBytes and deviceObj */
                        int noRegistered = bulkDeviceAction(restClient, deviceObj, "add");
                        devicesRegistered += noRegistered;
                        batchSizeBytes = 0;
                        deviceObj = new JSONArray();
                    }

                    /* Add device to deviceObj */
                    logger.fine(String.format("Add device. Type=%s Id=%s", deviceType, deviceId));
                    deviceObj.put(device);
                    batchSizeBytes += deviceSizeBytes;
                    td.setDeviceStatus(1);
                    tc.update(id, td);
                }

                if (batchSizeBytes > 0) {
                    int noRegistered = bulkDeviceAction(restClient, deviceObj, "add");
                    devicesRegistered += noRegistered;
                }

                logger.info(String.format("RegistrationStats: type=%s total=%d devicesRegistered=%d alreadyRegistered=%d", type, devicesInType, devicesRegistered, alreadyRegistered));

                registeredNow += devicesRegistered;
                registeredBefore += alreadyRegistered;
            }

            logger.info(String.format("RegistrationStats: TotalInCache=%d RegisteredNow=%d RegisteredBefore=%d", totalInCache, registeredNow, registeredBefore));

        } catch (Exception ioe) {
            throw ioe;
        }
    }

    public void removeDevices (String removeType) {
        int totalRemoved = 0;

        try {
            RestClient restClient = new RestClient(baseUrl, Constants.AUTH_BASIC, key, token);

            /* Remove all devices of the specified type.
             * If type is null, remove all devices.
             */
            List<TagDeviceType> tagTypes = tmc.getTagTypes();
            for (int i = 0; i < tagTypes.size(); i++) {
                int devicesRemoved = 0;
                TagDeviceType tagType = tagTypes.get(i);
                String type = tagType.getType();

                /* Get tag list from cache and register */
                JSONArray deviceObj = null;
                int batchCount;
                int totalDevices = tagList.size();
                Iterator<String> it = tagList.iterator();
                batchCount = 0;
                while (it.hasNext()) {
                    if (batchCount == 0 ) {
                        deviceObj = new JSONArray();
                        batchCount = 1;
                    }

                    String id = it.next();
                    TagData td = tc.get(id);
                    if (td == null) {
                        totalDevices = totalDevices - 1;
                        continue;
                    }
                    String deviceId = td.getDeviceId();
                    String deviceType = td.getDeviceType();

                    if (removeType != null && !type.equals(removeType)) {
                        totalDevices = totalDevices - 1;
                        continue;
                    } 

                    if (deviceType.equals("") || !type.equals(deviceType)) {
                        totalDevices = totalDevices - 1;
                        continue;
                    }

                    logger.fine(String.format("Remove device=%s Type=%s TotalCount=%d", deviceId, type, totalDevices));
                    JSONObject devItem = new JSONObject();
                    devItem.put("typeId", deviceType);
                    devItem.put("deviceId", deviceId);
                    deviceObj.put(devItem);
                    devicesRemoved += 1;

                    if (batchCount >= 100 || totalDevices == 1) {
                        bulkDeviceAction(restClient, deviceObj, "remove");
                        deviceObj = null;
                        batchCount = 0;
                    }
                    totalDevices = totalDevices - 1;
                }
                if (deviceObj != null) {
                    bulkDeviceAction (restClient, deviceObj, "remove");
                    deviceObj = null;
                }
                logger.info(String.format("TypeRemoveStats: type=%s devicesRemoved=%d", type, devicesRemoved));
                totalRemoved += devicesRemoved;
            }

            logger.info(String.format("DeviceRemoveStats: TotalInCache=%d DevicesRemoved=%d", totalInCache, totalRemoved));
        } catch (Exception ioe) {
            logger.info("Failed to build tag cache. Exception: " + ioe.getMessage());
        }
    }

    private static JSONObject createDeviceItem(String typeId, String deviceId, String token) {
        JSONObject devItem = new JSONObject();
        devItem.put("typeId", typeId);
        devItem.put("deviceId", deviceId);
        devItem.put("authToken", token);
        JSONObject deviceInfo = new JSONObject();
        JSONObject location = new JSONObject();
        JSONObject metadata = new JSONObject();
        devItem.put("deviceInfo", deviceInfo);
        devItem.put("location", location);
        devItem.put("metadata", metadata);
        return devItem;
    }

    private static JSONObject createDeviceTypeObject(String typeId, String classId) {
        JSONObject devItem = new JSONObject();
        devItem.put("id", typeId);
        devItem.put("classId", classId);
        devItem.put("description", "SCADAConnector Device Type");
        JSONObject deviceInfo = new JSONObject();
        JSONObject metadata = new JSONObject();
        devItem.put("deviceInfo", deviceInfo);
        devItem.put("metadata", metadata);
        return devItem;
    }

    private int bulkDeviceAction(RestClient restClient, JSONArray deviceObj, String actionType) {
        boolean done = true;
        int retVal = 0;
        for (int retry=0; retry<5; retry++) {
            done = true;
            try {
                if (actionType.equals("remove")) {
                    restClient.post("bulk/devices/remove", deviceObj.toString());
                    JSONArray jsonArray = new JSONArray(restClient.getResponseBody());
                    retVal = jsonArray.length();
                    // processRemoveResults(deviceObj, restClient.getResponseCode(), restClient.getResponseBody());
                } else {
                    restClient.post("bulk/devices/add", deviceObj.toString());
                    JSONArray jsonArray = new JSONArray(restClient.getResponseBody());
                    retVal = jsonArray.length();
                    // processAddResults(deviceObj, restClient.getResponseCode(), restClient.getResponseBody());
                }
                deviceObj = null;
            } catch (Exception ex) {
                logger.info("BulkDeviceAction:" + actionType + " Exception message: " + ex.getMessage());
                logger.log(Level.FINE, ex.getMessage(), ex);
                done = false;
            }
            if (done) {
                break;
            }
            try {
                Thread.sleep(5000);
            } catch (Exception e) { }
            logger.info(String.format("Retry REST call: retry count=%d", retry));
        }
        return retVal;
    }
}

