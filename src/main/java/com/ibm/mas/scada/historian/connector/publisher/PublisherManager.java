/*
 * IBM Confidential
 * OCO Source Materials
 * 5725-S86, 5900-A0N, 5737-M66, 5900-AAA
 * (C) Copyright IBM Corp. 2021
 * The source code for this program is not published or otherwise divested of
 * its trade secrets, irrespective of what has been deposited with the U.S.
 * Copyright Office.
 */

package com.ibm.mas.scada.historian.connector.publisher;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Arrays;
import org.json.JSONObject;
import java.util.concurrent.ArrayBlockingQueue;

import com.ibm.mas.scada.historian.connector.configurator.Config;
import com.ibm.mas.scada.historian.connector.publisher.Publisher;
import com.ibm.mas.scada.historian.connector.publisher.impl.MqttPublisher;
import com.ibm.mas.scada.historian.connector.utils.Copyright;
import com.ibm.mas.scada.historian.connector.utils.Constants;
import com.ibm.mas.scada.historian.connector.utils.OffsetRecord;
import com.ibm.mas.scada.historian.connector.utils.ConnectorException;

public class PublisherManager {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;
    private static Logger logger;
    private static Config config;
    private static String orgId;
    private static Publisher publisher;
    private static ArrayBlockingQueue<String[]> iotDataQueue;
    private static OffsetRecord offsetRecord;
    private static int iotClientType;
    private static String publishProtocol;
    private static int scadaType;

    public PublisherManager(Config config, ArrayBlockingQueue<String[]> iotDataQueue, OffsetRecord offsetRecord) {
        this.config = config;
        this.logger = config.getLogger();
        this.iotDataQueue = iotDataQueue;
        this.offsetRecord = offsetRecord;
        this.scadaType = config.getScadaType();
        JSONObject connectionConfig = config.getConnectionConfig();
        this.publishProtocol = connectionConfig.optString("publishProtocol", Constants.PUBLISH_TYPE_MQTT);
        this.iotClientType = connectionConfig.optInt("iotClientType", Constants.DEVICE_CLIENT);
        // this.iotClientType = connectionConfig.optInt("iotClientType", Constants.GATEWAY_CLIENT);
        JSONObject iotpConfig = connectionConfig.getJSONObject("iotp");
        this.orgId = iotpConfig.getString("orgId");
    }

    public void start() throws Exception {
        /* start data extraction and publish process */
        JSONObject mappingConfig = config.getMappingConfig();

        /* use mqtt as default */
        if (publishProtocol.equals(Constants.PUBLISH_TYPE_MQTT)) {
            publisher = new MqttPublisher();
        } else if (publishProtocol.equals(Constants.PUBLISH_TYPE_DBUPLOAD)) {
            // publisher = new JdbcUploader();
            System.out.println("Not implemented yet");
        } else {
            String message = String.format("Unsupported Publish protocol %s is defined", publishProtocol);
            throw new ConnectorException(message);
        }

        publisher.init(config);
        startDataPublisherThread();
    }

    /* publishe events */
    public static void publishDataFromQueueByDevice() {
        try {
            String lastClientId = "";
            while (true) {
                String[] iotDataItems = iotDataQueue.take();

                JSONObject msgObject = new JSONObject();
                String deviceType;
                String deviceId;
                String eventName;
                String clientId;

                if (scadaType == Constants.SCADA_OSIPI) {
                    deviceType = iotDataItems[Constants.IOTP_OSIPI_DEVICETYPE];
                    deviceId = iotDataItems[Constants.IOTP_OSIPI_DEVICEID];
                    eventName = iotDataItems[Constants.IOTP_OSIPI_EVT_NAME];
                    msgObject.put("evt_timestamp", Integer.valueOf(iotDataItems[Constants.IOTP_OSIPI_EVT_TIMESTAMP]));
                    msgObject.put("value", iotDataItems[Constants.IOTP_OSIPI_VALUE]);
                    msgObject.put("decimalAccuracy", iotDataItems[Constants.IOTP_OSIPI_DECIMALACCURACY]);
                    msgObject.put("name", iotDataItems[Constants.IOTP_OSIPI_NAME]);
                    msgObject.put("label", iotDataItems[Constants.IOTP_OSIPI_LABEL]);
                    msgObject.put("type", iotDataItems[Constants.IOTP_OSIPI_TYPE]);
                    msgObject.put("unit", iotDataItems[Constants.IOTP_OSIPI_UNIT]);
                    msgObject.put("tag", iotDataItems[Constants.IOTP_OSIPI_TAG]);
                } else {
                    deviceType = iotDataItems[Constants.IOTP_IGNITION_DEVICE_DEVICETYPE];
                    deviceId = iotDataItems[Constants.IOTP_IGNITION_DEVICE_DEVICEID];
                    eventName = iotDataItems[Constants.IOTP_IGNITION_DEVICE_EVT_NAME];
                    msgObject.put("evt_timestamp", iotDataItems[Constants.IOTP_IGNITION_DEVICE_EVT_TIMESTAMP]);
                    if (iotDataItems[Constants.IOTP_IGNITION_DEVICE_INTVALUE] == null) {
                        msgObject.put("intvalue", "");
                    } else {
                        msgObject.put("intvalue", iotDataItems[Constants.IOTP_IGNITION_DEVICE_INTVALUE]);
                    }
                    if (iotDataItems[Constants.IOTP_IGNITION_DEVICE_FLOATVALUE] == null) {
                        msgObject.put("floatvalue", "");
                    } else {
                        msgObject.put("floatvalue", iotDataItems[Constants.IOTP_IGNITION_DEVICE_FLOATVALUE]);
                    }
                    if (iotDataItems[Constants.IOTP_IGNITION_DEVICE_STRINGVALUE] == null) {
                        msgObject.put("stringvalue", "");
                    } else {
                        msgObject.put("stringvalue", iotDataItems[Constants.IOTP_IGNITION_DEVICE_STRINGVALUE]);
                    }
                    if (iotDataItems[Constants.IOTP_IGNITION_DEVICE_DATEVALUE] == null) {
                        msgObject.put("datevalue", "");
                    } else {
                        msgObject.put("datealue", iotDataItems[Constants.IOTP_IGNITION_DEVICE_DATEVALUE]);
                    }
                    msgObject.put("decimalAccuracy", iotDataItems[Constants.IOTP_IGNITION_DEVICE_DECIMALACCURACY]);
                    msgObject.put("type", iotDataItems[Constants.IOTP_IGNITION_DEVICE_TYPE]);
                    msgObject.put("unit", iotDataItems[Constants.IOTP_IGNITION_DEVICE_UNIT]);
                    msgObject.put("tag", iotDataItems[Constants.IOTP_IGNITION_DEVICE_TAG]);
                }

                clientId = "d:" + orgId + ":" + deviceType + ":" + deviceId;
                if (clientId.equals(lastClientId)) { 
                    publisher.publish(null, eventName, msgObject.toString());
                } else {
                    if (!lastClientId.equals("")) {
                        if (publisher.isConnected()) {
                            publisher.close();
                        }
                    }
                    publisher.connect(deviceType, deviceId);
                    publisher.publish(null, eventName, msgObject.toString());
                    // publisher.close();
                }
                lastClientId = clientId;
                offsetRecord.setUploadedCount(1);
                try {
                    Thread.sleep(1);
                } catch (Exception e) { }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public static void publishDataFromQueueByApp() {
        boolean isConnected = false;
        try {
            publisher.connect(null, null);
            isConnected = true;
            while (true) {
                String[] iotDataItems = iotDataQueue.take();
                String deviceType = iotDataItems[Constants.IOTP_OSIPI_DEVICETYPE];
                String deviceId = iotDataItems[Constants.IOTP_OSIPI_DEVICEID];
                String eventName = iotDataItems[Constants.IOTP_OSIPI_EVT_NAME];
                JSONObject msgObject = new JSONObject();
                msgObject.put("evt_timestamp", iotDataItems[Constants.IOTP_OSIPI_EVT_TIMESTAMP]);
                msgObject.put("value", iotDataItems[Constants.IOTP_OSIPI_VALUE]);
                msgObject.put("decimalAccuracy", iotDataItems[Constants.IOTP_OSIPI_DECIMALACCURACY]);
                msgObject.put("name", iotDataItems[Constants.IOTP_OSIPI_NAME]);
                msgObject.put("label", iotDataItems[Constants.IOTP_OSIPI_LABEL]);
                msgObject.put("type", iotDataItems[Constants.IOTP_OSIPI_TYPE]);
                msgObject.put("unit", iotDataItems[Constants.IOTP_OSIPI_UNIT]);
                msgObject.put("tag", iotDataItems[Constants.IOTP_OSIPI_TAG]);
                String topicString = "iot-2/type/" + deviceType + "/id/" + deviceId + "/evt/" + eventName + "/fmt/json";
                publisher.publish(topicString, eventName, msgObject.toString());
                offsetRecord.setUploadedCount(1);
                try {
                    Thread.sleep(1);
                } catch (Exception e) { }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        if (isConnected) {
            try {
                publisher.close();
            } catch (Exception e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    // Thread to queue iotData
    private static void startDataPublisherThread() {
        Runnable thread = new Runnable() {
            public void run() {
                logger.info("Start iotData publish cycle");
                while(true) {
                    if (iotClientType == Constants.DEVICE_CLIENT) {
                        publishDataFromQueueByDevice();
                    } else {
                        publishDataFromQueueByApp();
                    }
                    try {
                        Thread.sleep(5000);
                    } catch (Exception e) {}
                }
            }
        };
        logger.info("Starting IoT Data publish thread ...");
        new Thread(thread).start();
        logger.info("IoT Data publish thread is started");
        return;
    }

    public static void uploadDataFromQueue() {
        // dummy function
    }

    // Thread to upload device data from queue
    private static void startDataUploadThread() {
        Runnable thread = new Runnable() {
            public void run() {
                logger.info("Start upload cycle");
                while(true) {
                    uploadDataFromQueue();
                    try {
                        Thread.sleep(5000);
                    } catch (Exception e) {}
                }
            }
        };
        logger.info("Starting Data upload thread ...");
        new Thread(thread).start();
        logger.info("Data upload thread is started");
        return;
    }

}


