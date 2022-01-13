/*
 * IBM Confidential
 * OCO Source Materials
 * 5725-S86, 5900-A0N, 5737-M66, 5900-AAA
 * (C) Copyright IBM Corp. 2021
 * The source code for this program is not published or otherwise divested of
 * its trade secrets, irrespective of what has been deposited with the U.S.
 * Copyright Office.
 */

package com.ibm.mas.scada.historian.connector.utils;

import java.util.logging.Logger;
import org.apache.commons.jcs3.access.CacheAccess;
import org.apache.commons.jcs3.access.exception.CacheException;
import com.ibm.mas.scada.historian.connector.configurator.Cache;

public class OffsetRecordCache {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;
    private static Logger logger = Logger.getLogger(Constants.LOGGER_CLASS);

    private String offsetid;
    private CacheAccess<String, String> dataoffset;
 
    public OffsetRecordCache (String offsetId, Cache cache) throws NullPointerException {
        super();
        if (offsetId == null || offsetId.equals("") || cache == null) {
            throw new NullPointerException ("Specified parameters cannot be null");
        }

        this.offsetid = offsetId;
        this.dataoffset = cache.getDataOffsetCache();
        logger.info("OffsetRecordCache is set");
    }

    public void put(String offsetid, String offsetRec) {
        try {
            dataoffset.putSafe(offsetid, offsetRec);
        } catch (CacheException e) {
            logger.warning(String.format("Failed to add String in cache: id=%s errorMsg=%s", offsetid, e.getMessage()));
        }
    }

    public void update(String offsetid, String offsetRec) {
        dataoffset.put(offsetid, offsetRec);
    }

    public void remove(String offsetid) {
        dataoffset.remove(offsetid);
    }

    public String get(String offsetid) {
        String offsetRec = dataoffset.get(offsetid);
        return offsetRec;
    }

    public CacheAccess<String, String> getDataffsets() {
        return dataoffset;
    }

    public String getStringStats() {
        return dataoffset.getCacheControl().getStats();
    }
}

