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

import java.lang.InterruptedException;
import java.util.Set;
import java.util.logging.Logger;
import org.apache.commons.jcs3.access.CacheAccess;
import com.ibm.mas.scada.historian.connector.configurator.Cache;
import com.ibm.mas.scada.historian.connector.utils.Constants;
import com.ibm.mas.scada.historian.connector.utils.Copyright;

public class TagDataCache {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;
    private static Logger logger = Logger.getLogger(Constants.LOGGER_CLASS);

    private CacheAccess<String, TagData> tagcache;
    private Set<String> tagList;
 
    public TagDataCache (Cache cache) throws NullPointerException {
        super();
        if (cache == null) {
            throw new NullPointerException ("Specified parameter cannot be null");
        }

        this.tagcache = cache.getTagDataCache();
    }

    public void put(String tagid, TagData td) {
        try {
            tagcache.putSafe(tagid, td);
        } catch (Exception e) {
            logger.warning(String.format("Failed to add TagData in cache: id=%s errorMsg=%s", tagid, e.getMessage()));
        }
    }

    public void update(String tagid, TagData td) {
        tagcache.put(tagid, td);
    }

    public void remove(String tagid) {
        tagcache.remove(tagid);
    }

    public TagData get(String tagid) {
        TagData td = tagcache.get(tagid);
        return td;
    }

    public Set<String> getTagList() {
        tagList = tagcache.getCacheControl().getKeySet();
        return tagList;
    }

    public CacheAccess<String, TagData> getTagpaths() {
        return tagcache;
    }

    public String getTagDataStats() {
        return tagcache.getCacheControl().getStats();
    }

}

