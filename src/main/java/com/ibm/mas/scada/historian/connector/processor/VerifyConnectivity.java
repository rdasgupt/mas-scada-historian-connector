/*
 * IBM Confidential
 * OCO Source Materials
 * 5725-S86, 5900-A0N, 5737-M66, 5900-AAA
 * (C) Copyright IBM Corp. 2021
 * The source code for this program is not published or otherwise divested of
 * its trade secrets, irrespective of what has been deposited with the U.S.
 * Copyright Office.
 */

package com.ibm.mas.scada.historian.connector.processor;

import java.io.*;
import java.net.*;
import java.util.logging.*;
import java.util.Map;
import org.json.JSONObject;
import org.json.JSONArray;
import com.ibm.mas.scada.historian.connector.configurator.Config;
import com.ibm.mas.scada.historian.connector.utils.Copyright;
import com.ibm.mas.scada.historian.connector.utils.Constants;
import com.ibm.mas.scada.historian.connector.utils.RestClient;

public class VerifyConnectivity {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;
 
    private static String type = "api";
    private static String command = "type";

    private static String installDir = null;
    private static String dataVolume = null;
    private static String configDir;
    private static String dataDir;
    private static String logDir;
    private static String userHome = System.getProperty("user.home");
    private static String os = System.getProperty("os.name");
    private static int    connectorType = Constants.CONNECTOR_DEVICE;
    private static String orgId;
    private static String key;
    private static String token;
    private static String url;
    private static String baseUrl;
    private static JSONObject connectionConfig;
    private static JSONObject iotpConfig;
    private static JSONObject mappingConfig;

    public static void main(String[] args) {
        // check arguments
        for (int i=0; i<args.length; i++) {
            switch(i) {
                case 0:
                    type = args[0];
                    if (!type.equals("api") && !type.equals("mqtt")) {
                        System.out.println("Invalid test type " + type + " is specified");
                        System.exit(1);
                    }
                    break;
        
                case 1:
                    command = args[1];
                    if (!command.equals("type") && !command.equals("dimension")) {
                        System.out.println("Invalid option " + command + " is specified");
                        System.exit(1);
                    }
                    break;
        
                default:
                    System.out.println("Invalid option " + args[i] + " is specified");
                    System.exit(1);
            }
        }

        /* Get install and data dir location from enviironment variables */
        Map <String, String> map = System.getenv();
        for ( Map.Entry <String, String> entry: map.entrySet() ) {
            if ( entry.getKey().compareTo("IBM_SCADA_CONNECTOR_INSTALL_DIR") == 0 ) {
                installDir = entry.getValue();
            } else if ( entry.getKey().compareTo("IBM_SCADA_CONNECTOR_DATA_VOLUME") == 0 ) {
                dataVolume = entry.getValue();
            }
        }
        if ( installDir == null ) {
            if (os.contains("Windows") || os.contains("windows")) {
                installDir = "c:/ibm/masshc";
            } else {
                installDir = userHome + "/ibm/masshc";
            }
        }
        if ( dataVolume == null ) {
            if (os.contains("Windows") || os.contains("windows")) {
                dataVolume = "c:/ibm/masshc";
            } else {
                dataVolume = userHome + "/ibm/masshc";
            }
        }

        /* default config, data and log directories are in volume directory - to make it easy for docker env */
        configDir = dataVolume + "/volume/config";
        dataDir = dataVolume + "/volume/data";
        logDir = dataVolume + "/volume/logs";

        try {
            Config config = new Config(configDir, dataDir, logDir);
            config.setConnectorType(connectorType);

            connectionConfig = config.getConnectionConfig();
            mappingConfig = config.getMappingConfig();
            iotpConfig = connectionConfig.getJSONObject("iotp");
            orgId = iotpConfig.getString("orgId");
            key = iotpConfig.getString("asKey");
            token = iotpConfig.getString("asToken");
            baseUrl = iotpConfig.getString("url") + "/";
            url = iotpConfig.getString("url");

            System.out.println("URL: " + url);

            RestClient restClient = new RestClient(baseUrl, Constants.AUTH_HEADER, key, token, 1);
            String etypeAPI = "/api/v2/core/deviceTypes";
            String entityTypeName = "TestDeviceType";
            restClient.get(etypeAPI + "/" + entityTypeName);
            System.out.println(String.format("EntityType GET Status Code: %d", restClient.getResponseCode()));
        } catch (Exception e) {
            System.out.println("EXCEPTION: " + e.getMessage());
        };

    }
}

