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
import java.nio.file.Path;
import org.json.JSONObject;
import org.json.JSONArray;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import com.ibm.mas.scada.historian.connector.utils.Copyright;

public class TagmapConfigTest {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;

    static String configDir;
    static String dataDir;
    static String logDir;
    static Config config;
    static JSONObject mappingConfig;

    @BeforeClass
    public static void init() {
        System.out.println("Initialize TagmapConfig tests.");

        Path dir = Paths.get("src","test","resources");
        configDir = dir.toFile().getAbsolutePath();
        dir = Paths.get("tmp");
        dataDir = dir.toFile().getAbsolutePath();
        logDir = dir.toFile().getAbsolutePath();
        try {
            config = new Config(configDir, dataDir, logDir);
            mappingConfig = config.getMappingConfig();
        } catch (Exception e) { }
    }

    @Test
    public void test01() {
        Exception ex = assertThrows(NullPointerException.class, () -> { new TagmapConfig(null); });

        TagmapConfig tm = null;
        try {
            tm = new TagmapConfig(mappingConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertNotNull("should not be null", tm);
        assertEquals("Service1", tm.getServiceName());
        assertEquals("pidemo.csv", tm.getCsvFileName());
        assertEquals("osipi", tm.getServiceType());
        assertEquals("Sample Tag Mapping file", tm.getDescription());
        assertNotNull("should not be null", tm.getDeviceTypes());
        assertNotNull("should not be null", tm.getMetrics());
        assertNotNull("should not be null", tm.getDimensions());
        assertNotNull("should not be null", tm.getSqlQueryFile());
        assertEquals(true, tm.getInited());
    }

    @Test
    public void test02() {
        JSONObject badMappingConfig = new JSONObject();
        assertThrows(IllegalArgumentException.class, () -> { new TagmapConfig(badMappingConfig); });
        
        badMappingConfig.put("description", "xxxxx");
        badMappingConfig.put("serviceName", "xxxxx");
        assertThrows(IllegalArgumentException.class, () -> { new TagmapConfig(badMappingConfig); });
        badMappingConfig.put("csvFileName", "xxxxx");
        assertThrows(IllegalArgumentException.class, () -> { new TagmapConfig(badMappingConfig); });
        badMappingConfig.put("serviceType", "factoryTalk");
        assertThrows(IllegalArgumentException.class, () -> { new TagmapConfig(badMappingConfig); });
        badMappingConfig.put("serviceType", "osipi");
        assertThrows(NullPointerException.class, () -> { new TagmapConfig(badMappingConfig); });

        JSONArray deviceTypes = new JSONArray();
        JSONObject deviceType = new JSONObject();
        deviceTypes.put(deviceType);
        badMappingConfig.put("deviceTypes", deviceTypes);
        JSONObject metrics = new JSONObject();
        badMappingConfig.put("metrics", metrics);
        assertThrows(IllegalArgumentException.class, () -> { new TagmapConfig(badMappingConfig); });
        metrics.put("name", "xxxx");
        badMappingConfig.put("metrics", metrics.toString());
        JSONObject dimension = new JSONObject();
        badMappingConfig.put("dimension", dimension.toString());
        assertThrows(NullPointerException.class, () -> { new TagmapConfig(badMappingConfig); });
        dimension.put("tagpath", "xxxx");
        badMappingConfig.put("dimension", dimension);
        assertThrows(NullPointerException.class, () -> { new TagmapConfig(badMappingConfig); });
        dimension.put("tagid", "xxxx");
        badMappingConfig.put("dimension", dimension);
        dimension.put("site", "xxxx");
        badMappingConfig.put("dimension", dimension);
        // TagmapConfig tm = new TagmapConfig(badMappingConfig);
        // assertEquals(true, tm.getInited());
    }
}

