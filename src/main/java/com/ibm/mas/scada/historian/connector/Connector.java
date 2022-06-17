/*
 * IBM Confidential
 * OCO Source Materials
 * 5725-S86, 5900-A0N, 5737-M66, 5900-AAA
 * (C) Copyright IBM Corp. 2021
 * The source code for this program is not published or otherwise divested of
 * its trade secrets, irrespective of what has been deposited with the U.S.
 * Copyright Office.
 */

package com.ibm.mas.scada.historian.connector;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Map;
import com.ibm.mas.scada.historian.connector.configurator.Config;
import com.ibm.mas.scada.historian.connector.configurator.Cache;
import com.ibm.mas.scada.historian.connector.configurator.TagBuilder;
import com.ibm.mas.scada.historian.connector.configurator.TagConfigurator;
import com.ibm.mas.scada.historian.connector.configurator.TagDataCache;
import com.ibm.mas.scada.historian.connector.configurator.TagmapConfig;
import com.ibm.mas.scada.historian.connector.configurator.TagDimension;
import com.ibm.mas.scada.historian.connector.processor.ProcessManager;
import com.ibm.mas.scada.historian.connector.utils.Copyright;
import com.ibm.mas.scada.historian.connector.utils.Constants;

/**
 * An application to extract tag data from SCADA historian and send the data to IBM MAS Monitor.
 */
public final class Connector {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;
    private static Logger logger;

    private Connector() {
    }

    /**
     * Mas SCADA Historian Connector.
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
        String installDir = null;
        String dataVolume = null;
        String configDir;
        String dataDir;
        String logDir;
        String userHome = System.getProperty("user.home");
        int    connectorType = Constants.CONNECTOR_DEVICE;

        System.out.println("IBM MAS Connector for SCADA Historian.");

        if (args.length >= 3) {
            configDir = args[0];
            dataDir = args[1];
            logDir = args[2];
            if (args.length > 3) {
                String cType = args[3];
                if (cType.equals("alarm")) {
                    connectorType = Constants.CONNECTOR_ALARM;
                }
            }
        } else {
            if (args.length == 1) {
                String cType = args[3];
                if (cType.equals("alarm")) {
                    connectorType = Constants.CONNECTOR_ALARM;
                }
            }

            /* Get install and data dir location from enviironment variables */
            Map <String, String> map = System.getenv();
            for ( Map.Entry <String, String> entry: map.entrySet() ) {
                if ( entry.getKey().compareTo("MASSHC_INSTALL_DIR") == 0 ) {
                    installDir = entry.getValue();
                } else if ( entry.getKey().compareTo("MASSHC_DATA_VOLUME") == 0 ) {
                    dataVolume = entry.getValue();
                }
            }
            if ( installDir == null ) {
                installDir = userHome + "/ibm/masshc";
            }
            if ( dataVolume == null ) {
                dataVolume = userHome + "/ibm/masshc";
            }

            /* default config, data and log directories are in volume directory - to make it easy for docker env */
            configDir = dataVolume + "/volume/config";
            dataDir = dataVolume + "/volume/data";
            logDir = dataVolume + "/volume/logs";
        }

        try {
            Config config = new Config(configDir, dataDir, logDir);
            config.setConnectorType(connectorType);
            logger = config.getLogger();
            logger.info("==== Initialize Caching =====");
            Cache cache = new Cache(config);
            logger.info("==== Build tags from CSV file =====");
            TagBuilder tagBuilder = new TagBuilder(config, cache);
            tagBuilder.build();
            TagDataCache tc = tagBuilder.getTagDataCache();
            TagmapConfig tmc = tagBuilder.getTagmapConfig();
            logger.info("==== Configure types and devices in IoT =====");
            TagConfigurator tagConfigurator = new TagConfigurator(config, tc, tmc);
            tagConfigurator.configure();
            logger.info("==== Extract and process Historian data =====");
            ProcessManager pm = new ProcessManager(config, cache, tc);
            pm.processData();

            /* wait for some time for events to flow in database and entity type to 
             * to get created, then start dimension creation thread
             */
            try {
                Thread.sleep(15000);
            } catch (Exception e) { }
            logger.info("==== Add dimensions data =====");
            TagDimension tagDimension = new TagDimension(config, tc);
            tagDimension.startDimensionProcess();

            /* monitor data processing thread */
            while (true) {
                if (pm.getStatus()) {
                    try {
                        Thread.sleep(10000);
                    } catch (Exception e) { }
                    continue;
                }
                break;
            }

            cache.close(false);
        } catch (Exception ex) {
            if (logger != null) {
                logger.log(Level.SEVERE, ex.getMessage(), ex);
            } else {
                System.out.println("main: Exception caught: " + ex.getMessage());
            }
        }

        logger.info("Shutting down SCADA Historian connector.");
    }
}

