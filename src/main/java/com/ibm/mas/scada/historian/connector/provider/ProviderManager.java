/*
 * IBM Confidential
 * OCO Source Materials
 * 5725-S86, 5900-A0N, 5737-M66, 5900-AAA
 * (C) Copyright IBM Corp. 2021
 * The source code for this program is not published or otherwise divested of
 * its trade secrets, irrespective of what has been deposited with the U.S.
 * Copyright Office.
 */

package com.ibm.mas.scada.historian.connector.provider;

import java.util.concurrent.*;
import java.util.logging.Logger;
import java.util.concurrent.ArrayBlockingQueue;
import com.ibm.mas.scada.historian.connector.configurator.Config;
import com.ibm.mas.scada.historian.connector.configurator.TagDataCache;
import com.ibm.mas.scada.historian.connector.provider.Provider;
import com.ibm.mas.scada.historian.connector.provider.impl.OsipiProvider;
import com.ibm.mas.scada.historian.connector.provider.impl.IgnitionProvider;
import com.ibm.mas.scada.historian.connector.utils.Copyright;
import com.ibm.mas.scada.historian.connector.utils.OffsetRecord;
import com.ibm.mas.scada.historian.connector.utils.Constants;

public class ProviderManager {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;
    private static Logger logger;
    private static Config config;
    private static TagDataCache tc;
    private static OffsetRecord offsetRecord;
    private static Provider provider;
    private static ArrayBlockingQueue<String[]> iotDataQueue;

    public ProviderManager(Config config, TagDataCache tc, ArrayBlockingQueue<String[]> iotDataQueue, OffsetRecord offsetRecord) {
        this.config = config;
        this.tc = tc;
        this.offsetRecord = offsetRecord;
        this.iotDataQueue = iotDataQueue;
        this.logger = config.getLogger();
    }

    public void start() throws Exception {
        if (config.getScadaType() == Constants.SCADA_OSIPI) {
            logger.info("Data Provider: OSIPI");
            provider = new OsipiProvider();
        } else {
            logger.info("Data Provider: IGNITION");
            provider = new IgnitionProvider();
        }
        provider.init(config, tc, offsetRecord, iotDataQueue);
        startDataProviderThread();
    }

    public static long extractAndQueueData() {
        long waitTime = 0;
        try {
            waitTime = provider.extract();
        } catch(Exception e) { 
            logger.warning("extractAndQueueData: Exception message: " + e.getMessage());
        }
        logger.info(String.format("Records extracted: %d", provider.processedRecordCount()));
        return waitTime;
    }

    /* Thread to queue iotData */
    private static void startDataProviderThread() {
        Runnable thread = new Runnable() {
            public void run() {
                while(true) {
                    long waitTime = extractAndQueueData();
                    if (waitTime == 0) {
                        waitTime = 5000;
                    }
                    try {
                        Thread.sleep(waitTime);
                    } catch (Exception e) { }
                }
            }
        };
        logger.info("Starting IoT Data extract and queue thread ...");
        new Thread(thread).start();
        logger.info("IoT Data extract and queue thread is started");
        return;
    }

}


