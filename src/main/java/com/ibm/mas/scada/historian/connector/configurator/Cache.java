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

import java.io.File;
import java.util.logging.Logger;
import java.util.Set;
import java.util.Properties;
import org.apache.commons.jcs3.JCS;
import org.apache.commons.jcs3.access.CacheAccess;
import com.ibm.mas.scada.historian.connector.utils.Constants;
import com.ibm.mas.scada.historian.connector.utils.Copyright;

public class Cache {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;

    private static Logger logger = Logger.getLogger(Constants.LOGGER_CLASS);
    private static Config config;
    private static String cacheId;
    private static CacheAccess<String, TagData> tagdata;
    private static CacheAccess<String, String> dataoffset;
    private static Set<String> tagDataList;
    private static Set<String> dataOffsetList;

    public Cache(Config config) throws Exception {
        super();
        if (config == null) {
            throw new IllegalArgumentException ("Specified parameter is null or empty.");
        }
        this.config = config;
        this.cacheId = config.getClientSite();
        initCaches(cacheId);
        this.tagDataList = tagdata.getCacheControl().getKeySet();
        this.dataOffsetList = dataoffset.getCacheControl().getKeySet();
    }

    public CacheAccess<String, TagData> getTagDataCache() {
        return tagdata;
    }

    public CacheAccess<String, String> getDataOffsetCache() {
        return dataoffset;
    }

    public Set<String> getTagDataList() {
        return tagDataList;
    }

    public Set<String> getDataOffsetList() {
        return dataOffsetList;
    }

    public void close(boolean deleteFiles) {
        logger.info("Close caching");
        JCS.shutdown();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) { }

        if (deleteFiles) {
            File file = new File(config.getDataDir() + "/tagcache");
            deleteDir(file);
        }
    }

    private void initCaches(String id) {
        Properties props = new Properties();
    
        props.put("jcs.default", "TDCACHE,TDOFFSET");
        props.put("jcs.default.cacheattributes", "org.apache.commons.jcs3.engine.CompositeCacheAttributes");
        props.put("jcs.default.cacheattributes.MaxObjects", "200000");
        props.put("jcs.default.cacheattributes.MemoryCacheName", "org.apache.commons.jcs3.engine.memory.lru.LRUMemoryCache");
        props.put("jcs.default.cacheattributes.DiskUsagePatternName", "UPDATE");
        props.put("jcs.default.cacheattributes.UseMemoryShrinker", "false");
        props.put("jcs.default.cacheattributes.MaxMemoryIdleTimeSeconds", "14400");
        props.put("jcs.default.cacheattributes.ShrinkerIntervalSeconds", "60");
        props.put("jcs.default.elementattributes", "org.apache.commons.jcs3.engine.ElementAttributes");
        props.put("jcs.default.elementattributes.IsEternal", "true");
        props.put("jcs.default.elementattributes.MaxLife", "14400");
        props.put("jcs.default.elementattributes.IdleTime", "7200");
        props.put("jcs.default.elementattributes.IsSpool", "true");
        props.put("jcs.default.elementattributes.IsRemote", "true");
        props.put("jcs.default.elementattributes.IsLateral", "true");
        props.put("jcs.default.resultCache.cacheattributes.OptimizeAtRemoveCount", "1");

        /* tagdata Region cache */
        props.put("jcs.region.tagdata", "TDCACHE");
        props.put("jcs.region.tagdata.cacheattributes", "org.apache.commons.jcs3.engine.CompositeCacheAttributes");
        props.put("jcs.region.tagdata.cacheattributes.MaxObjects", "200000");
        props.put("jcs.region.tagdata.cacheattributes.MemoryCacheName", "org.apache.commons.jcs3.engine.memory.lru.LRUMemoryCache");
        props.put("jcs.region.tagdata.cacheattributes.DiskUsagePatternName", "UPDATE");
        props.put("jcs.region.tagdata.cacheattributes.UseMemoryShrinker", "false");
        props.put("jcs.region.tagdata.cacheattributes.MaxMemoryIdleTimeSeconds", "14400");
        props.put("jcs.region.tagdata.cacheattributes.ShrinkerIntervalSeconds", "60");
        props.put("jcs.region.tagdata.elementattributes", "org.apache.commons.jcs3.engine.ElementAttributes");
        props.put("jcs.region.tagdata.elementattributes.IsEternal", "true");
        props.put("jcs.region.tagdata.elementattributes.MaxLife", "14400");
        props.put("jcs.region.tagdata.elementattributes.IdleTime", "7200");
        props.put("jcs.region.tagdata.elementattributes.IsSpool", "true");
        props.put("jcs.region.tagdata.elementattributes.IsRemote", "true");
        props.put("jcs.region.tagdata.elementattributes.IsLateral", "true");
        props.put("jcs.region.tagdata.resultCache.cacheattributes.OptimizeAtRemoveCount", "1");
        props.put("jcs.auxiliary.TDCACHE", "org.apache.commons.jcs3.auxiliary.disk.indexed.IndexedDiskCacheFactory");
        props.put("jcs.auxiliary.TDCACHE.attributes", "org.apache.commons.jcs3.auxiliary.disk.indexed.IndexedDiskCacheAttributes");
        props.put("jcs.auxiliary.TDCACHE.attributes.DiskPath", config.getDataDir() + "/tagcache/" + id);
        props.put("jcs.auxiliary.TDCACHE.attributes.MaxPurgatorySize", "10000000");
        props.put("jcs.auxiliary.TDCACHE.attributes.MaxKeySize", "1000000");
        props.put("jcs.auxiliary.TDCACHE.attributes.OptimizeAtRemoveCount", "300000");
        props.put("jcs.auxiliary.TDCACHE.attributes.ShutdownSpoolTimeLimit", "60");

        /* data offset Region cache */
        props.put("jcs.region.dataoffset", "TDOFFSET");
        props.put("jcs.region.dataoffset.cacheattributes", "org.apache.commons.jcs3.engine.CompositeCacheAttributes");
        props.put("jcs.region.dataoffset.cacheattributes.MaxObjects", "200000");
        props.put("jcs.region.dataoffset.cacheattributes.MemoryCacheName", "org.apache.commons.jcs3.engine.memory.lru.LRUMemoryCache");
        props.put("jcs.region.dataoffset.cacheattributes.DiskUsagePatternName", "UPDATE");
        props.put("jcs.region.dataoffset.cacheattributes.UseMemoryShrinker", "false");
        props.put("jcs.region.dataoffset.cacheattributes.MaxMemoryIdleTimeSeconds", "14400");
        props.put("jcs.region.dataoffset.cacheattributes.ShrinkerIntervalSeconds", "60");
        props.put("jcs.region.dataoffset.elementattributes", "org.apache.commons.jcs3.engine.ElementAttributes");
        props.put("jcs.region.dataoffset.elementattributes.IsEternal", "true");
        props.put("jcs.region.dataoffset.elementattributes.MaxLife", "14400");
        props.put("jcs.region.dataoffset.elementattributes.IdleTime", "7200");
        props.put("jcs.region.dataoffset.elementattributes.IsSpool", "true");
        props.put("jcs.region.dataoffset.elementattributes.IsRemote", "true");
        props.put("jcs.region.dataoffset.elementattributes.IsLateral", "true");
        props.put("jcs.region.dataoffset.resultCache.cacheattributes.OptimizeAtRemoveCount", "1");
        props.put("jcs.auxiliary.TDOFFSET", "org.apache.commons.jcs3.auxiliary.disk.indexed.IndexedDiskCacheFactory");
        props.put("jcs.auxiliary.TDOFFSET.attributes", "org.apache.commons.jcs3.auxiliary.disk.indexed.IndexedDiskCacheAttributes");
        props.put("jcs.auxiliary.TDOFFSET.attributes.DiskPath", config.getDataDir() + "/tagcache/" + id);
        props.put("jcs.auxiliary.TDOFFSET.attributes.MaxPurgatorySize", "10000000");
        props.put("jcs.auxiliary.TDOFFSET.attributes.MaxKeySize", "1000000");
        props.put("jcs.auxiliary.TDOFFSET.attributes.OptimizeAtRemoveCount", "300000");
        props.put("jcs.auxiliary.TDOFFSET.attributes.ShutdownSpoolTimeLimit", "60");

        /* Access regions */
        JCS.setConfigProperties(props);
        this.tagdata = JCS.getInstance("tagdata");
        this.dataoffset = JCS.getInstance("dataoffset");
    }

    private boolean deleteDir(File dirname) {
        File[] files = dirname.listFiles();
        if (files != null) {
            for (File file : files) {
                deleteDir(file);
            }
        }
        return dirname.delete();
    }
}

