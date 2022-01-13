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

import java.util.concurrent.ArrayBlockingQueue;
import com.ibm.mas.scada.historian.connector.configurator.Config;
import com.ibm.mas.scada.historian.connector.configurator.TagDataCache;
import com.ibm.mas.scada.historian.connector.utils.Copyright;
import com.ibm.mas.scada.historian.connector.utils.OffsetRecord;

public abstract class Provider {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;

    public abstract void init(Config config, TagDataCache tc, OffsetRecord offsetRecord, ArrayBlockingQueue<String[]> iotDataQueue) throws Exception;
    public abstract long extract() throws Exception;
    public abstract long processedRecordCount();
}


