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

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.concurrent.ArrayBlockingQueue;

import com.ibm.mas.scada.historian.connector.configurator.Cache;
import com.ibm.mas.scada.historian.connector.configurator.Config;
import com.ibm.mas.scada.historian.connector.configurator.TagDataCache;
import com.ibm.mas.scada.historian.connector.utils.OffsetRecord;
import com.ibm.mas.scada.historian.connector.provider.ProviderManager;
import com.ibm.mas.scada.historian.connector.publisher.PublisherManager;
import com.ibm.mas.scada.historian.connector.utils.Copyright;

public class ProcessManager {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;
    private static Logger logger;
    private static Config config;
    private static Cache cache;
    private static TagDataCache tc;
    private static boolean status = true;

    public ProcessManager(Config config, Cache cache, TagDataCache tc) {
        this.config = config;
        this.cache = cache;
        this.tc = tc;
        this.logger = config.getLogger();
    }

    public void processData() throws Exception {
        try {
            ArrayBlockingQueue<String[]> iotDataQueue = new ArrayBlockingQueue<>(1000);
            OffsetRecord offsetRecord = new OffsetRecord(config, cache, false);

            logger.info("Start CLI server ====>");
            CLIServer server = new CLIServer(config, offsetRecord);
            server.start();
    
            logger.info("Start publisher ====>");
            PublisherManager pubm = new PublisherManager(config, iotDataQueue, offsetRecord);
            pubm.start();
    
            logger.info("Start data provider ====>");
            ProviderManager provm = new ProviderManager(config, tc, iotDataQueue, offsetRecord);
            provm.start();
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw e;
        }
    }

    public boolean getStatus() {
        return status;
    }
}

