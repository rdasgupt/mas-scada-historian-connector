/*
 * IBM Confidential
 * OCO Source Materials
 * 5725-S86, 5900-A0N, 5737-M66, 5900-AAA
 * (C) Copyright IBM Corp. 2021
 * The source code for this program is not published or otherwise divested of
 * its trade secrets, irrespective of what has been deposited with the U.S.
 * Copyright Office.
 */

package com.ibm.mas.scada.historian.connector.provider.impl;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.ArrayBlockingQueue;

import com.ibm.mas.scada.historian.connector.utils.Constants;
import com.ibm.mas.scada.historian.connector.utils.Copyright;
import com.ibm.mas.scada.historian.connector.utils.OffsetRecord;
import com.ibm.mas.scada.historian.connector.configurator.Config;
import com.ibm.mas.scada.historian.connector.configurator.TagDataCache;
import com.ibm.mas.scada.historian.connector.provider.Provider;
import com.ibm.mas.scada.historian.connector.provider.impl.ignition.DataProvider;

public class IgnitionProvider extends Provider {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;
    private static final String CLASS = Constants.LOGGER_CLASS;
    private static Logger logger;

    public Provider provider;
    public DataProvider dp;

    public Provider IgnitionProvider() {
        return provider;
    }

    @Override
    public void init(Config config, TagDataCache tc, OffsetRecord offsetRecord, ArrayBlockingQueue<String[]> iotDataQueue) throws Exception {
        final String METHOD = "init";
        logger = config.getLogger();
        logger.logp(Level.FINE, CLASS, METHOD, "Initialize Ignition provider");
        this.dp = new DataProvider(config, tc, offsetRecord, iotDataQueue);
    }

    @Override
    public long extract() throws Exception {
        final String METHOD = "extract";
        logger.logp(Level.FINE, CLASS, METHOD, "Ignition Extract data");
        long waitTime = dp.extract();
        return waitTime;
    }

    @Override
    public long processedRecordCount() {
        final String METHOD = "processedRecordCount";
        logger.logp(Level.FINE, CLASS, METHOD, "Ignition process records");
        return dp.getProcessedCount();
    }
}

