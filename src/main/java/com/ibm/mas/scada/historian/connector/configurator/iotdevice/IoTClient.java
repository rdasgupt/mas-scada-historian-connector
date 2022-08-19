/*
 * IBM Confidential
 * OCO Source Materials
 * 5725-S86, 5900-A0N, 5737-M66, 5900-AAA
 * (C) Copyright IBM Corp. 2021
 * The source code for this program is not published or otherwise divested of
 * its trade secrets, irrespective of what has been deposited with the U.S.
 * Copyright Office.
 */

package com.ibm.mas.scada.historian.connector.configurator.iotdevice;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.ibm.mas.scada.historian.connector.utils.Constants;
import com.ibm.mas.scada.historian.connector.utils.Copyright;
import com.ibm.mas.scada.historian.connector.utils.RestClient;

public class IoTClient {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;
    private static Logger logger = Logger.getLogger(Constants.LOGGER_CLASS);

    private String orgId;
    private String baseUrl;
    private String authHeader;
    private String deviceToken;
    private RestClient restClient;
    private ObjectMapper objectMapper;

    public IoTClient (String url, String orgId, String key, String token) {
        this.orgId = orgId;
        if (url.equals("")) {
            this.baseUrl = "https://" + orgId + ".internetofthings.ibmcloud.com/api/v0002";
        } else {
            this.baseUrl = url;
        }
        this.authHeader = "Basic " + Base64.getEncoder().encodeToString((key + ":" + token).getBytes(StandardCharsets.UTF_8));
        this.restClient = new RestClient(baseUrl, Constants.AUTH_BASIC, key, token, 0);
        this.deviceToken = token;
        this.objectMapper = new ObjectMapper();
        AnnotationIntrospector introspector = new JacksonAnnotationIntrospector();
        objectMapper.setAnnotationIntrospector(introspector);
    }
    
    public void createDeviceType (String deviceType, String deviceClass) throws Exception {    
        logger.info("Creating DeviceType " +  deviceType);
        DeviceType dtype = new  DeviceType(deviceType, deviceClass,"SCADA Connector device type");
        invokeIoTP("/device/types", null, Constants.HTTP_POST, dtype, null);
        logger.info(String.format("Create DeviceType StatusCode: %d", restClient.getResponseCode()));
    }

    public void registerDevice (String deviceType, String deviceId) throws Exception {
        logger.fine("Registering Device " +  deviceType + ":" + deviceId);
        Device device = new Device(deviceId, deviceToken);
        invokeIoTP("/device/types/"+deviceType+"/devices", null, Constants.HTTP_POST, device, null);
        logger.info(String.format("Create Device StatusCode: %d", restClient.getResponseCode()));
    }

    public LogicalInterface getLogicalInterface (String name) throws Exception {
        logger.info("Querying Logical Interface " +  name);
        LogicalInterfaceResults  results = (LogicalInterfaceResults)(invokeIoTP(
            "/draft/logicalinterfaces", "name="+name, Constants.HTTP_GET, null, LogicalInterfaceResults.class));
        List<LogicalInterface> resultsList = results.getResults();
        if (resultsList.isEmpty()) {
            return null;
        } else {
            return resultsList.get(0);
       }
    }

    public Schema createSchema (String schemaName, String schemaType, String schemaFilePath) throws Exception {
        Schema schema = null;
        SchemaResults results = (SchemaResults)(invokeIoTP(
            "/schemas", "name=" + schemaName, Constants.HTTP_GET, null, SchemaResults.class));
        List<Schema> resultsList = results.getResults();
        if (resultsList.isEmpty()) {
            logger.info("Uploading schema " +  schemaName);
            String fileName = "eventSchema.json";
            if (schemaType.equals("liSchema")) {
                fileName = "liSchema.json";
            }
            // Path filePath = Paths.get("src","main","resources", fileName);
            Path filePath = Paths.get(schemaFilePath);
            restClient.uploadFile("/draft/schemas", schemaName, fileName, filePath);
            logger.info("CreateSchema response code: " + restClient.getResponseCode());
            InputStream is = new ByteArrayInputStream(restClient.getResponseBody().getBytes());
            schema = objectMapper.readValue(is, Schema.class);
        } else {
            schema = resultsList.get(0);
        }
        return schema;
    }

    public PhysicalInterface createPhysicalInterface (String name) throws Exception {
        logger.info("Creating Physical Interface " +  name);
        PhysicalInterface result;
        PhysicalInterfaceResults  results = (PhysicalInterfaceResults)(invokeIoTP(
            "/physicalinterfaces", "name="+name, Constants.HTTP_GET, null, PhysicalInterfaceResults.class));
        List<PhysicalInterface> resultsList = results.getResults();
        if (resultsList.isEmpty()) {
            PhysicalInterface pi = new PhysicalInterface(name, "SCADA Connector PI");
            result = (PhysicalInterface)(invokeIoTP(
                "/draft/physicalinterfaces", null, Constants.HTTP_POST, pi, pi.getClass()));
        } else {
            result = resultsList.get(0);    
        }
        return result;
    }

    public EventType createEventType (String name, String schemaId) throws Exception {
        EventType result = null;
        EventType et = null;
        EventTypeResults  results = (EventTypeResults)(invokeIoTP(
            "/draft/event/types", "name="+name, Constants.HTTP_GET, null, EventTypeResults.class));
        List<EventType> resultsList = results.getResults();
        if (resultsList.isEmpty()) {
            et = null;
        } else {
            et = resultsList.get(0);
        }

        if (et == null) {
            et = new EventType(name, "SCADA Connector event", schemaId);
            result = (EventType)(invokeIoTP("/draft/event/types", null, Constants.HTTP_POST, et, et.getClass()));
        } else {
            String etId = et.getId();
            et = new EventType(name, "SCADA Connector event", schemaId);
            et.setId(etId);
            result = (EventType)(invokeIoTP("/draft/event/types/"+etId, null, Constants.HTTP_PUT, et, et.getClass()));
        }
        return result;
    }

    public void addEventToPI (String name, String eventTypeId, String pi) throws Exception {
        logger.info("Adding event type " +  name + " to PI ");
        EventTypeMapping mapping = new    EventTypeMapping(name, eventTypeId);
        invokeIoTP("/draft/physicalinterfaces/"+pi+"/events", null, Constants.HTTP_POST, mapping, mapping.getClass());
        return;
    }

    public void addPIToDeviceType (PhysicalInterface pi, String deviceType) throws Exception {
        logger.info("Adding PI " +  pi.getId() + " to DeviceType " + deviceType);

        invokeIoTP("/draft/device/types/"+deviceType+"/physicalinterface", null, Constants.HTTP_POST, pi, pi.getClass());
        return;
    }

    public LogicalInterface createLI (String name, String schemaId) throws Exception {
        logger.info("Creating Logical Interface " +  name);
        LogicalInterface li = new LogicalInterface(name, "SCADAConnector LI", schemaId);
        LogicalInterface result = (LogicalInterface)(invokeIoTP(
            "/draft/logicalinterfaces", null, Constants.HTTP_POST, li, li.getClass()));
        return result;
    }

    public LogicalInterface createOrReplaceLogicalInterface (String name, String schemaId) throws Exception {
        LogicalInterface result = null;
        LogicalInterface li = getLogicalInterface(name);
        if (li == null) {
            result = createLI(name, schemaId);
        } else {
            String liId = li.getId();
            li = new LogicalInterface(name, "SCADAConnector LI", schemaId);
            li.setId(liId);
            result = (LogicalInterface)(invokeIoTP(
                "/draft/logicalinterfaces/"+liId, null, Constants.HTTP_PUT, li, li.getClass()));
        }
        return result;
    }

    public LogicalInterface addLIToDeviceType (LogicalInterface li, String deviceType) throws Exception {
        logger.info("Adding LI " +  li.getName() + " to DeviceType " + deviceType);
        LogicalInterface result = (LogicalInterface)(invokeIoTP(
            "/draft/device/types/"+ deviceType + "/logicalinterfaces", null, Constants.HTTP_POST, li, li.getClass()));
        return result;
    }

    public PropertyMappings addMappingsToDeviceType(LogicalInterface li, Map<String,Map<String,String>> mappings, String deviceType) throws Exception {
        logger.info("Adding Mappings for LI " +  li.getId() + " and DeviceType " + deviceType);
        PropertyMappings input = new PropertyMappings(li.getId(), mappings);
        PropertyMappings result = (PropertyMappings)(invokeIoTP(
            "/draft/device/types/"+ deviceType + "/mappings", null, Constants.HTTP_POST, input, input.getClass()));
        return result;
    }

    public void activateIMForDeviceType (String deviceType) throws Exception {
        logger.info("Activating "  + deviceType);
        PatchRequest request = new PatchRequest(PatchRequest.ACTIVATE);
        invokeIoTP("/draft/device/types/"+ deviceType, null, Constants.HTTP_PATCH, request,  null);
        return;
    }

    private <T> Object invokeIoTP(String base, String query, int type, Object input, Class<T> resultClass) throws Exception {
 
        StringBuilder sb = new StringBuilder();
        sb.append(base);
        if (query != null ) {
           sb.append("?").append(query);
        }
        String method = sb.toString();

        switch (type) {
        case Constants.HTTP_POST:
            restClient.post(method, input.toString());
            break;
        case Constants.HTTP_PUT:
            restClient.put(method, input.toString());
            break;
        case Constants.HTTP_GET:
            restClient.get(method);
            break;
        case Constants.HTTP_DELETE:
            restClient.delete(method);
            break;
        case Constants.HTTP_PATCH:
            restClient.patch(method, input.toString());
            break;
        default:
            return null;
        }

        int httpCode = restClient.getResponseCode();
        if(httpCode >= 200 && httpCode < 300) {
            if (resultClass != null) {
                InputStream is = new ByteArrayInputStream(restClient.getResponseBody().getBytes());
                return objectMapper.readValue(is, resultClass);
            } else {
                return null;
            }
        }

        if ((type == Constants.HTTP_POST) && httpCode == 409) {
            logError(restClient.getResponse());
            return null;
        }

        logError(restClient.getResponse());
        return null;
    }

    private void logError(HttpResponse<String> response) {
        if (response != null) {
            logger.info("Error " + response.statusCode());
            logger.info("ErrorMessage: " + response.body());
        } else {
            logger.info("null HTTP Response");
        }
    }
}

