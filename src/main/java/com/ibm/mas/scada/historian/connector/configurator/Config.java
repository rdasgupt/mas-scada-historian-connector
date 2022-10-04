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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Handler;
import java.util.logging.SimpleFormatter;
import java.util.logging.FileHandler;
import org.json.JSONObject;
import com.ibm.mas.scada.historian.connector.utils.Constants;
import com.ibm.mas.scada.historian.connector.utils.Copyright;
import com.ibm.mas.scada.historian.connector.utils.ConnectorException;

public class Config {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;

    private static Logger logger = Logger.getLogger(Constants.LOGGER_CLASS);
    private static String configDir;
    private static String dataDir;
    private static String logDir;
    private static JSONObject connectionConfig;
    private static JSONObject mappingConfig;
    private static String clientSite;
    private static int httpPort;
    private static int cliPort;
    private static int scadaType = Constants.SCADA_OSIPI;
    private static int connectorType = Constants.CONNECTOR_DEVICE;
    private static int SAASEnv = 0;
    private static int apiVersion = 1;

    public Config(String configPath, String dataPath, String logPath) throws IllegalArgumentException, IOException, Exception {
        this.configDir = getConfigFromEnv("configDir", "MASSHC_CONFIG_DIR", configPath);
        this.dataDir = getConfigFromEnv("dataDir", "MASSHC_DATA_DIR", dataPath);
        this.logDir = getConfigFromEnv("logDir", "MASSHC_LOG_DIR", logPath);
        process();
    }

    public String getDataDir() {
        return this.dataDir;
    }

    public String getConfigDir() {
        return this.configDir;
    }

    public String getLogDir() {
        return this.logDir;
    }

    public Logger getLogger() {
        return logger;
    }

    public String getClientSite() {
        return clientSite;
    }

    public int getCLIPort() {
        return cliPort;
    }

    public int getHTTPPort() {
        return httpPort;
    }

    public int isSAASEnv() {
        return SAASEnv;
    }

    public JSONObject getConnectionConfig() {
        return this.connectionConfig;
    }

    public JSONObject getMappingConfig() {
        return this.mappingConfig;
    }

    public int getApiVersion() {
        return this.apiVersion;
    }

    public int getScadaType() {
        return scadaType;
    }

    public void setConnectorType(int type) {
        connectorType = type;
    }

    public int getConnectorType() {
        return connectorType;
    }

    private void process() throws IOException, IllegalArgumentException, Exception {
        String logFile = logDir + "/ibmScadaConnector.log";
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT : %4$s : %2$s : %5$s%6$s%n");
        logger.setUseParentHandlers(false);
        Handler[] handlers = logger.getHandlers();
        for (int i = 0; i < handlers.length; i++) {
            Handler lh = handlers[i];
            logger.removeHandler(lh);
        }
        Handler consoleHandler = null;
        FileHandler fh = new FileHandler(logFile, 5242880, 5, true);
        logger.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
        logger.removeHandler(consoleHandler);

        String connectionConfigFile = configDir + "/connection.json";
        String mappingConfigFile = configDir + "/mapping.json";

        logger.info("Connection Config: " + connectionConfigFile);
        logger.info("Mapping Config: " + mappingConfigFile);

        try {
            String fileContent = new String(Files.readAllBytes(Paths.get(connectionConfigFile)));
            if (fileContent == null || fileContent.equals("")) {
                throw new IOException ("Connection configuration file is missing or empty.");
            } 
            this.connectionConfig = new JSONObject(fileContent);

            fileContent = new String(Files.readAllBytes(Paths.get(mappingConfigFile)));
            if (fileContent == null || fileContent.equals("")) {
                throw new IOException ("Mapping file is missing or empty.");
            } 
            this.mappingConfig = new JSONObject(fileContent);
            this.apiVersion = mappingConfig.optInt("apiVersion", 1);
        } catch (IOException ioe) {
            logger.info("Invalid configuration. Exception: " + ioe.getMessage());
            throw ioe;
        }

        this.clientSite = this.connectionConfig.optString("id", "NotSetInConfig");
        this.cliPort = connectionConfig.optInt("cliPort", 4550);
        this.httpPort = connectionConfig.optInt("httpPort", 5080);
        this.SAASEnv = connectionConfig.optInt("isSAASEnv", 0);

        JSONObject historianConfig = connectionConfig.getJSONObject("historian");
        String historianType = historianConfig.optString("type", "NotDefined");
        logger.info(String.format("SCADA Historian Type: %s", historianType));
        if (historianType.equals("osipi")) {
            scadaType = Constants.SCADA_OSIPI;
        } else if (historianType.equals("ignition")) {
            scadaType = Constants.SCADA_IGNITION;
        } else {
            String exMessage = String.format("Unsupported historian type %s is specified", historianType);
            throw new ConnectorException(exMessage);
        }

    }

    private String getConfigFromEnv(String name, String envitem, String value) throws IllegalArgumentException {
        String retval = null;
        if (value == null || value.equals("")) {
            if (envitem != null && !envitem.equals("")) { 
                Map <String, String> map = System.getenv();
                for ( Map.Entry <String, String> entry: map.entrySet() ) {
                    if ( entry.getKey().compareTo(envitem) == 0 ) {
                        retval = entry.getValue();
                        break;
                    }
                }
            }
        } else {
            retval = value;
        }
 
        if (retval == null || retval.equals("")) {
            throw new IllegalArgumentException ("Specified parameter" + name + " is null or empty.");
        }

        return retval;
    }

}

