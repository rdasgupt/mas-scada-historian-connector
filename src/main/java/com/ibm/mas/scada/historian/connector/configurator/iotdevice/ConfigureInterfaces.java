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

import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;
import java.io.FileWriter;
import org.json.JSONObject;
import com.ibm.mas.scada.historian.connector.configurator.iotdevice.IoTClient;
import com.ibm.mas.scada.historian.connector.configurator.iotdevice.ConfigureInterfaces;
import com.ibm.mas.scada.historian.connector.configurator.iotdevice.LogicalInterface;
import com.ibm.mas.scada.historian.connector.utils.Copyright;
import com.ibm.mas.scada.historian.connector.utils.Constants;

public class  ConfigureInterfaces {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;
    private static Logger logger = Logger.getLogger(Constants.LOGGER_CLASS);

    private static IoTClient iotp = null;
    private static String dataDir;

    public ConfigureInterfaces(String dataDir, String orgId, String key, String token) {
        this.iotp = new IoTClient(orgId, key, token);
        this.dataDir = dataDir;
    }

    public IoTClient getIotp() {
        return this.iotp;
    }

    public void config(String deviceType) throws Exception {
        LogicalInterface li = null;
        try {
            li = iotp.getLogicalInterface(deviceType);
            if (li == null) {
                JSONObject piSchema = CreateSchemas.buildSchema("eventSchema");
                String eventSchemaFileName = dataDir + "/eventSchema.json";
                FileWriter file = new FileWriter(eventSchemaFileName);
                file.write(piSchema.toString());
                file.flush();
                file.close();
                String piSchemaId = iotp.createSchema(deviceType + "_PI", "eventSchema", eventSchemaFileName).getId();
                logger.info("Type: " + deviceType + " PI schemaId: " + piSchemaId);

                JSONObject liSchema = CreateSchemas.buildSchema("liSchema");
                String liSchemaFileName = dataDir + "/liSchema.json";
                file = new FileWriter(liSchemaFileName);
                file.write(liSchema.toString());
                file.flush();
                file.close();
                String liSchemaId = iotp.createSchema(deviceType + "_LI", "liSchema", liSchemaFileName).getId();
                logger.info("Type: " + deviceType + " LI schemaId: " + liSchemaId);

                PhysicalInterface pi = iotp.createPhysicalInterface(deviceType);
                String piId = pi.getId();
                logger.info("Type: " + deviceType + " Physical Interface Id: " + piId);

                String evtType = "scadaevent";
                EventType eventType = iotp.createEventType(evtType, piSchemaId);
                String evtId = eventType.getId();
                logger.info("Type: " + deviceType + " Event Id: " + piId);

                logger.info("Type: " + deviceType + " Add event type to physical interface");
                iotp.addEventToPI(evtType, eventType.getId(), piId);

                logger.info("Type: " + deviceType + " Add physical interface to Device Type");
                iotp.addPIToDeviceType(pi, deviceType);

                li = iotp.createOrReplaceLogicalInterface(deviceType, liSchemaId);
                String liId = li.getId();
                logger.info("Type: " + deviceType + " Logical Interface Id: " + liId);

                logger.info("Type: " + deviceType + " Add logical interface to Device Type");
                iotp.addLIToDeviceType(li, deviceType);

                logger.info("Type: " + deviceType + " Add event mapping");
                Map<String, String> mapping = new HashMap<String, String>();
                mapping.put("name", "$exists($event.name) ? $event.name: null");
                mapping.put("value", "$exists($event.value) ? $event.value: null");
                mapping.put("type", "$exists($event.type) ? $event.type: null");
                mapping.put("decimalAccuracy", "$exists($event.decimalAccuracy) ? $event.decimalAccuracy: null");
                mapping.put("label", "$exists($event.label) ? $event.label: null");
                mapping.put("evt_timestamp", "$exists($event.evt_timestamp) ? $event.evt_timestamp: null");
                mapping.put("unit", "$exists($event.unit) ? $event.unit: null");
                mapping.put("tag", "$exists($event.tag) ? $event.tag: null");
                Map<String,  Map<String,String>> eventMappings =  new HashMap<String,  Map<String,String>>();
                eventMappings.put(evtType, mapping);

                logger.info("Type: " + deviceType + " Add mappings");
                iotp.addMappingsToDeviceType(li, eventMappings, deviceType);
            } else {
                logger.info(String.format("Logical Interface: Name=%s DeviceType=%s", li.getName(), deviceType));
            }
        } catch(Exception e) {
            logger.info("Exception message: " + e.getMessage());
            throw e;
        }
    }
}

