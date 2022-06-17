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

import java.util.concurrent.*;
import java.util.logging.*;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.Set;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import com.ibm.mas.scada.historian.connector.utils.Constants;
import com.ibm.mas.scada.historian.connector.utils.Copyright;
import com.ibm.mas.scada.historian.connector.utils.RestClient;

public class TagDimension {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;
    private static Logger logger = Logger.getLogger(Constants.LOGGER_CLASS);
    private static Config config;
    private static TagDataCache tc;
    private static JSONObject connConfig;
    private static JSONObject mappingConfig;
    private static boolean createTagDimensions;
    private static boolean createEntityType;
    private static JSONObject tagDimension;
    private static String tagpathMap;
    private static JSONArray deviceTypes;
    private static String client;
    private static String schemaName;
    private static String baseUrl;
    private static String tenantId;
    private static RestClient restClient;
    private static int apiVersion = 2;
    private static int scadaType = 2;

    public TagDimension(Config config, TagDataCache tc) {
        if (config == null || tc == null) {
            throw new NullPointerException("TagDimension: config or tc parameter cannot be null");
        }

        this.config = config;
        this.tc = tc;
        this.connConfig = config.getConnectionConfig();
        this.mappingConfig = config.getMappingConfig();
        this.createTagDimensions = mappingConfig.optBoolean("createTagDimensions", true);
        this.createEntityType = mappingConfig.optBoolean("createEntityType", false);
        this.tagDimension = mappingConfig.optJSONObject("dimensions");
        this.deviceTypes = mappingConfig.optJSONArray("deviceTypes");
        if (deviceTypes.length() == 0 || tagDimension.length() == 0) {
            throw new IllegalArgumentException ("Specified parameter is not valid");
        }
        this.tagpathMap = tagDimension.optString("tagpath");

        this.client = mappingConfig.optString("site");

        JSONObject iotp = connConfig.optJSONObject("iotp");
        this.schemaName = iotp.getString("schemaName");
        String key = iotp.getString("apiKey");
        String token = iotp.getString("apiToken");
        String asKey = iotp.getString("asKey");
        String asToken = iotp.getString("asToken");
        this.apiVersion = iotp.optInt("asAPIVersion", 2);
        this.baseUrl = "https://" + iotp.getString("asHost");
        this.tenantId = iotp.getString("tenantId");
        if (config.isSAASEnv() == 0) {
            if (apiVersion == 2) {
                this.restClient = new RestClient(baseUrl, Constants.AUTH_HEADER, asKey, asToken, tenantId);
            } else {
                this.restClient = new RestClient(baseUrl, Constants.AUTH_HEADER, asKey, asToken);
            }
        } else {
            this.restClient = new RestClient(baseUrl, Constants.AUTH_HEADER, key, token);
        }
    }

    public void startDimensionProcess() {
        startDimensionThread();
    }

    private static int createDimensions() {
        int dimAdded = 0;
        int dimRegistered = 0;
        int batchCount = 0;
        JSONArray dimensionObj = null;
        Set<String> tagList = tc.getTagList();
        int totalCountInCache = tagList.size();
        int totalCount = totalCountInCache;
        int retval = 0;

        if (!createTagDimensions) {
            logger.info("Tag dimensions creation is disabled.");
            return 1;
        }

        /* Create entity types */
        boolean entityExists = true;
        for (int i = 0; i < deviceTypes.length(); i++) {
            JSONObject deviceType = deviceTypes.getJSONObject(i);
            String entityTypeName = deviceType.getString("type");
            String etypeAPI;
            if (apiVersion == 2) {
                etypeAPI = "/api/v2/core/deviceTypes";
            } else {
                etypeAPI = "/api/meta/v1/" + tenantId + "/entityType";
            }
            try {
                restClient.get(etypeAPI + "/" + entityTypeName);
                logger.info(String.format("EntityType POST Status Code: %d", restClient.getResponseCode()));
                if (restClient.getResponseCode() != 200) {
                    entityExists = false;
                }
            } catch(Exception ex) {
                logger.info("Get EntityType " + entityTypeName + " failed. Exception: " + ex.getMessage());
                logger.log(Level.FINE, ex.getMessage(), ex);
                entityExists = false;
            }

            if (!entityExists && createEntityType) {
                logger.info(String.format("Create entity type: %s", entityTypeName));
                String metricTableName = "IOT_" + entityTypeName.toUpperCase();
                String dimensionTableName = metricTableName + "_CTG";
                JSONArray entityObj = new JSONArray();
                JSONObject entityTypeObj = new JSONObject();
                entityTypeObj.put("name", entityTypeName);
                entityTypeObj.put("description", entityTypeName);
                entityTypeObj.put("metricTableName", metricTableName);
                entityTypeObj.put("dimensionTableName", dimensionTableName);
                entityTypeObj.put("metricTimestampColumn", "EVT_TIMESTAMP");
                entityTypeObj.put("schemaName", schemaName);
                JSONArray dataItemDtoArray = new JSONArray();
                if (scadaType == Constants.SCADA_OSIPI) {
                    dataItemDtoArray.put(createDataDtoObject("evt_timestamp", "METRIC", "EVT_TIMESTAMP", "TIMESTAMP"));
                    dataItemDtoArray.put(createDataDtoObject("entity_id", "METRIC", "DEVICEID", "LITERAL"));
                    dataItemDtoArray.put(createDataDtoObject("name", "METRIC", "NAME", "LITERAL"));
                    dataItemDtoArray.put(createDataDtoObject("value", "METRIC", "VALUE", "LITERAL"));
                    dataItemDtoArray.put(createDataDtoObject("type", "METRIC", "TYPE", "LITERAL"));
                    dataItemDtoArray.put(createDataDtoObject("unit", "METRIC", "UNIT", "LITERAL"));
                    dataItemDtoArray.put(createDataDtoObject("decimalAccuracy", "METRIC", "DECIMALACCURACY", "LITERAL"));
                    dataItemDtoArray.put(createDataDtoObject("label", "METRIC", "LAVEL", "LITERAL"));
                    dataItemDtoArray.put(createDataDtoObject("evt_name", "METRIC", "EVT_NAME", "LITERAL"));
                    dataItemDtoArray.put(createDataDtoObject("tag", "METRIC", "TAG", "LITERAL"));
                    dataItemDtoArray.put(createDataDtoObject("TAGID",  "DIMENSION", "TAGID", "LITERAL"));
                    dataItemDtoArray.put(createDataDtoObject("TAGPATH", "DIMENSION", "TAGPATH", "LITERAL"));
                    dataItemDtoArray.put(createDataDtoObject("SITE", "DIMENSION", "SITE", "LITERAL"));
                    dataItemDtoArray.put(createDataDtoObject("CATEGORIES", "DIMENSION", "CATEGORIES", "LITERAL"));
                } else {
                    dataItemDtoArray.put(createDataDtoObject("evt_timestamp", "METRIC", "EVT_TIMESTAMP", "TIMESTAMP"));
                    dataItemDtoArray.put(createDataDtoObject("entity_id", "METRIC", "DEVICEID", "LITERAL"));
                    dataItemDtoArray.put(createDataDtoObject("name", "METRIC", "NAME", "LITERAL"));
                    dataItemDtoArray.put(createDataDtoObject("intvalue", "METRIC", "INTVALUE", "LITERAL"));
                    dataItemDtoArray.put(createDataDtoObject("floatvalue", "METRIC", "FLOATVALUE", "LITERAL"));
                    dataItemDtoArray.put(createDataDtoObject("stringvalue", "METRIC", "STRINGVALUE", "LITERAL"));
                    dataItemDtoArray.put(createDataDtoObject("datevalue", "METRIC", "DATEVALUE", "LITERAL"));
                    dataItemDtoArray.put(createDataDtoObject("type", "METRIC", "TYPE", "LITERAL"));
                    dataItemDtoArray.put(createDataDtoObject("unit", "METRIC", "UNIT", "LITERAL"));
                    dataItemDtoArray.put(createDataDtoObject("decimalAccuracy", "METRIC", "DECIMALACCURACY", "LITERAL"));
                    dataItemDtoArray.put(createDataDtoObject("evt_name", "METRIC", "EVT_NAME", "LITERAL"));
                    dataItemDtoArray.put(createDataDtoObject("tag", "METRIC", "TAG", "LITERAL"));
                    dataItemDtoArray.put(createDataDtoObject("TAGID",  "DIMENSION", "TAGID", "LITERAL"));
                    dataItemDtoArray.put(createDataDtoObject("TAGPATH", "DIMENSION", "TAGPATH", "LITERAL"));
                    dataItemDtoArray.put(createDataDtoObject("DATATYPE", "DIMENSION", "DATATYPE", "LITERAL"));
                    dataItemDtoArray.put(createDataDtoObject("SITE", "DIMENSION", "SITE", "LITERAL"));
                }
                entityTypeObj.put("dataItemDto", dataItemDtoArray);
                entityObj.put(entityTypeObj);
                try {
                    restClient.post(etypeAPI, entityTypeObj.toString());
                    logger.info(String.format("EntityType POST Status Code: %d", restClient.getResponseCode()));
                    if (restClient.getResponseCode() != 200) {
                        entityExists = false;
                    }
                } catch(Exception ex) {
                    logger.warning("EntityType " + entityTypeName + " failed to create. Exception: " + ex.getMessage());
                    logger.log(Level.FINE, ex.getMessage(), ex);
                    entityExists = false;
                }
            }
        }

        if (!entityExists) {
            return 0;
        }

        logger.info(String.format("Create Dimension Data: Tagpath InCache:%d", totalCountInCache));

        boolean done = true;
        List<String> doneTags = new ArrayList<String>();
        Iterator<String> it = tagList.iterator();
        while (it.hasNext()) {
            if (batchCount == 0 ) {
                dimensionObj = new JSONArray();
                batchCount = 1;
            }

            String id = it.next();
            TagData td = tc.get(id);
            if (td == null) {
                totalCount = totalCount - 1;
                continue;
            }

            String deviceId = td.getDeviceId();
            String deviceType = td.getDeviceType();
            String dimensionsString = td.getDimensions();
            JSONObject dimensionsJson = new JSONObject(dimensionsString);
            String tagpath = td.getTagPath();
            int dimensionStatus = td.getDimensionStatus();
            String dimAPI = "/api/master/v1/" + tenantId + "/entityType/" + deviceType + "/dimensional";

            if (dimensionStatus == 0) {
                logger.info(String.format("Add dimension: tagpath:%s Type:%s Id:%s", tagpath, deviceType, deviceId));

                Iterator<String> keys = dimensionsJson.keys();
                while(keys.hasNext()) {
                    String key = keys.next();
                    String val = dimensionsJson.getString(key);
                    dimensionObj.put(createDimItem(deviceId, key.toUpperCase(), "LITERAL", val));
                    batchCount += 1;
                }
                td.setDimensionStatus(1);
                tc.update(id, td);
                doneTags.add(id);
                dimAdded += 1;
            } else {
                logger.fine("Dimension was already added: tagpath: " + tagpath + "    Dimention ID: " + deviceId);
                dimRegistered += 1;
            }
 
            if (dimensionStatus == 0) {
                for (int retry=0; retry<5; retry++) {
                    done = true;
                    if (batchCount == 1) break;
                    try {
                        // invoke API to create dimensional data
                        logger.fine("DimensionObj: " + dimensionObj.toString());
                        restClient.post(dimAPI, dimensionObj.toString());
                        logger.info(String.format("Dimension POST Status Code: %d", restClient.getResponseCode()));
                    } catch(Exception ex) {
                        logger.info("Exception message: " + ex.getMessage());
                        logger.log(Level.FINE, ex.getMessage(), ex);
                        done = false;
                    }
                    if (done) break;
                    try {
                        Thread.sleep(5000);
                    } catch(Exception e) {}
                    logger.info(String.format("Retry REST call: retry count=%d", retry)); 
                }
                batchCount = 0;
            }
            if (done == false) break;
            totalCount = totalCount - 1;
        }

        if (done == false) {
            // cycle didn't complete successfully, reset dimension state so that these can be retried in the next cycle
            logger.info("Could not register dimensions of all tags in the cycle. Backing out status of unregistered tags.");
            Iterator<String> doneTagList = doneTags.iterator();
            while (doneTagList.hasNext()) {
                String id = doneTagList.next();
                TagData td = tc.get(id);
                td.setDimensionStatus(0);
                dimRegistered -= 1;
            }
        } else {
            retval = 1;
        }
    
        logger.info(String.format("Total=%d New=%d AlreadyRegistered=%d", totalCountInCache, dimAdded, dimRegistered));
        return retval;
    }

    private static JSONObject createDimItem(String id, String name, String type, String value) {
        JSONObject dimItem = new JSONObject();
        dimItem.put("id", id);
        dimItem.put("name", name);
        dimItem.put("type", type);
        dimItem.put("value", value);
        return dimItem;
    }

    private static JSONObject createDataDtoObject(String dtoName, String dtoType, String colName, String colType) {
        JSONObject dtoObj = new JSONObject();
        dtoObj.put("name", dtoName);
        dtoObj.put("type", dtoType);
        dtoObj.put("columnName", colName);
        dtoObj.put("columnType", colType);
        return dtoObj;
    }

    /* Thread to create dimensions */
    private static void startDimensionThread() {
        Runnable thread = new Runnable() {
            public void run() {
                while(true) {
                    int status = createDimensions();
                    if (status == 1) {
                        break;
                    }
                    try {
                        Thread.sleep(10000);
                    } catch (Exception e) { }
                }
            }
        };
        logger.info("Starting Dimensions thread ...");
        new Thread(thread).start();
        logger.info("Dimension creation thread is started");
        return;
    }
}

