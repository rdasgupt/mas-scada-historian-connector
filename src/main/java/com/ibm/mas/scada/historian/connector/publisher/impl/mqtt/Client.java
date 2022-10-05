/*
 * IBM Confidential
 * OCO Source Materials
 * 5725-S86, 5900-A0N, 5737-M66, 5900-AAA
 * (C) Copyright IBM Corp. 2021
 * The source code for this program is not published or otherwise divested of
 * its trade secrets, irrespective of what has been deposited with the U.S.
 * Copyright Office.
 */

package com.ibm.mas.scada.historian.connector.publisher.impl.mqtt;

import org.json.JSONObject;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Properties;
import java.net.Socket;
import javax.net.ssl.*;
import java.security.SecureRandom;

import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttAsyncClient;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.MqttSubscription;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import com.ibm.mas.scada.historian.connector.configurator.Config;
import com.ibm.mas.scada.historian.connector.utils.Copyright;
import com.ibm.mas.scada.historian.connector.utils.Constants;

public class Client implements MqttCallback {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;
    private static Logger logger;

    private static Config config;
    private static String configDir;
    private static String dataDir;
    private static String key;
    private static String token;
    private static String trustStore;
    private static String trustStorePwd;
    private static String mqttHost;
    private static String mqttClientId;
    private static int count = 200;
    private static int sleepus = 0;
    private static boolean verbose = false;
    private static MqttAsyncClient connAsync;
    private static MqttClient connSync;
    private static MqttConnectionOptions opt;
    private static MqttProperties prp;
    private static Client client;
    private static String orgId;
    private static int iotClientType;
    private static int mqttClientType;
    private static boolean isConnected;
    private static int trustServerCert = 1;

    public Client() {
        super();
    }

    public Client(Config config) {
        super();
        this.config = config;
        this.logger = config.getLogger();
        this.configDir = config.getConfigDir();
        this.dataDir = config.getDataDir();
        JSONObject connectionConfig = config.getConnectionConfig();
        JSONObject iotpConfig = connectionConfig.getJSONObject("iotp");
        this.key = iotpConfig.getString("apiKey");
        this.token = iotpConfig.getString("apiToken");
        this.orgId = iotpConfig.getString("orgId");
        String host = iotpConfig.getString("host");
        String port = String.valueOf(iotpConfig.getInt("port"));
        this.trustServerCert = iotpConfig.getInt("trustServerCert");
        this.trustStore = iotpConfig.optString("trustStore", "");
        this.trustStorePwd = iotpConfig.optString("trustStorePassword", "");
        this.mqttHost = "ssl://" + host + ":" + port;
        this.iotClientType= connectionConfig.optInt("iotClientType", Constants.DEVICE_CLIENT);
        this.mqttClientType = connectionConfig.optInt("mqttClientType", Constants.MQTT_SYNC);
        this.isConnected = false;
        logger.info(String.format("MQTT host:%s trustSerer:%d", mqttHost, trustServerCert));
    }

    public void connect(String type, String id) throws MqttException {
        try {
            isConnected = false;
            MqttSubscription [] subs = new MqttSubscription[1];
            subs[0] = new MqttSubscription("iot-2/type/+/id/+/err/data", 2);
            opt = new MqttConnectionOptions();
            opt.setCleanStart(true);
            opt.setPassword(token.getBytes());
            if (trustServerCert == 1) {
                SSLContext sc = SSLContext.getInstance("TLS"); 
                sc.init(null, trustAllCerts, new java.security.SecureRandom()); 
                SSLSocketFactory socketFactory = sc.getSocketFactory ();
                opt.setSocketFactory(socketFactory);
            } else if (trustStore != null && !trustStore.equals("")) {
                Properties sslClientProps = new Properties();
                sslClientProps.setProperty("com.ibm.ssl.trustStore", trustStore);
                sslClientProps.setProperty("com.ibm.ssl.trustStorePassword", trustStorePwd);
                sslClientProps.setProperty("com.ibm.ssl.trustStoreType", "JKS");
                opt.setSSLProperties(sslClientProps);
                logger.fine("MQTT Set trustStore");
            }

            if (iotClientType == Constants.DEVICE_CLIENT) {
                this.mqttClientId = "d:" + orgId + ":" + type + ":" + id;
                opt.setUserName("use-token-auth");
            } else if (iotClientType == Constants.GATEWAY_CLIENT) {
                /* TODO: gw needs to be created if this option is used */
                this.mqttClientId = "g:" + orgId + ":gwType:gwId";
                opt.setUserName("use-token-auth");
            } else if (iotClientType == Constants.APPLICATION_CLIENT) {
                this.mqttClientId = "a:" + orgId + ":scadaConnectorApp";
                opt.setUserName(key);
            }

            logger.info("Publish MQTT client id: " + mqttClientId);

            client = new Client();

            if (mqttClientType == Constants.MQTT_SYNC) {
                connSync = new MqttClient(mqttHost, mqttClientId);
                connSync.setCallback(client);
                connSync.connect(opt);
                try {
                    connSync.subscribe(subs);
                } catch (Exception e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
                isConnected = true;
            } else {
                connAsync = new MqttAsyncClient(mqttHost, mqttClientId);
                connAsync.setCallback(client);
                IMqttToken connToken = connAsync.connect(opt);
                connToken.waitForCompletion();
                IMqttToken subToken = null;
                try {
                    subToken = connAsync.subscribe(subs);
                    subToken.waitForCompletion();
                    int [] rc =  subToken.getGrantedQos();
                    logger.fine("MQTT Subscription granted QoS=" + ((rc != null && rc.length > 0) ? rc[0] : "null"));
                } catch (Exception e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
                isConnected = true;
            }
        } catch (Exception e)  {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void publish(String topicString, String eventName, String payload) throws MqttException {
        MqttMessage msg = new MqttMessage();
        msg.setQos(0);
        msg.setPayload(payload.getBytes());
        if (topicString == null) {
            topicString = "iot-2/evt/" + eventName + "/fmt/json";
        }
        logger.fine("MQTT Publish: " + topicString + " | " + payload);
        if (mqttClientType == Constants.MQTT_SYNC) {
            connSync.publish(topicString.trim(), msg);
        } else {
            IMqttToken subToken = connAsync.publish(topicString.trim(), msg);
            subToken.waitForCompletion();
        }
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void close() throws MqttException {
        try {
            if (mqttClientType == Constants.MQTT_SYNC) {
                connSync.disconnect();
                connSync.close(true);
            } else {
                connAsync.disconnect();
                connAsync.close(true);
            }
        } catch (Exception e)  {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        isConnected = false;
    }

    public void disconnected(MqttDisconnectResponse response) {
        logger.info("MQTT Client is disconnected: " + response);
        isConnected = false;
    }

    public void mqttErrorOccurred(MqttException e) {
        logger.log(Level.SEVERE, e.getMessage(), e);
    }

    public void messageArrived(String topic, MqttMessage message) throws Exception {
        logger.info("MQTT Client received message: " + message);
    }

    public void deliveryComplete(IMqttToken token) {
        logger.fine("MQTT Client message delivered: " + token);
    }

    public void connectComplete(boolean reconnect, String serverURI) {
        logger.fine("MQTT Client connection is complete: " + serverURI);
    }

    public void authPacketArrived(int reasonCode, MqttProperties properties) {
    }

    private static TrustManager[] trustAllCerts = new TrustManager[]{
        new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            public void checkClientTrusted(
                final java.security.cert.X509Certificate[] certs, String authType) {
            }
            public void checkServerTrusted(
                java.security.cert.X509Certificate[] certs, String authType) {
            }
            public void checkClientTrusted(
                final java.security.cert.X509Certificate[] a_certificates, final String a_auth_type, final Socket a_socket) {
            }
            public void checkServerTrusted(
                final java.security.cert.X509Certificate[] a_certificates, final String a_auth_type, final Socket a_socket) {
            }
            public void checkClientTrusted(
                final java.security.cert.X509Certificate[] a_certificates, final String a_auth_type, final SSLEngine a_engine) {
            }
            public void checkServerTrusted(
                final java.security.cert.X509Certificate[] a_certificates, final String a_auth_type, final SSLEngine a_engine) {
            }
        }
    };
}

