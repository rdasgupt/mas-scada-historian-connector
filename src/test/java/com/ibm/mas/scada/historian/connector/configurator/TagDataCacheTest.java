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

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import com.ibm.mas.scada.historian.connector.configurator.Cache;
import com.ibm.mas.scada.historian.connector.configurator.Config;
import com.ibm.mas.scada.historian.connector.utils.Copyright;

/**
 * TagDataCacheTest tests.
 */
public class TagDataCacheTest {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;

    public static String configDir;
    public static String dataDir;
    public static String logDir;

    @BeforeClass
    public static void init() {
        System.out.println("Initialize TagDataCache Tests.");
        Path dir = Paths.get("src","test","resources");
        configDir = dir.toFile().getAbsolutePath();
        dir = Paths.get("tmp");
        dataDir = dir.toFile().getAbsolutePath();
        logDir = dir.toFile().getAbsolutePath();
    }

    @Test
    public void test01() {
        Exception ex = assertThrows(NullPointerException.class, () -> { new TagDataCache(null); });
        String expMsg = "Specified parameter cannot be null";
        assertTrue(ex.getMessage().contains(expMsg));
    }

    @Test
    public void test02() throws Exception {
        Config config = new Config(configDir, dataDir, logDir);
        assertNotNull("should not be null", config);
        Cache cache = new Cache(config);
        assertNotNull("should not be null", cache);
        TagDataCache tc = new TagDataCache(cache);
        assertNotNull("should not be null", tc);
        TagData td = new TagData("Site1", "rutherford/room1/temp", "9f8a3f5a-2d1b-11ec-98b3-06abac9454c4", "yyyy");
        assertNotNull("New TagData should not be null", td);
        td.setDeviceStatus(0);
        td.setDimensionStatus(0);
        tc.put("9f8a3f5a-2d1b-11ec-98b3-06abac9454c4", td);
        TagData tdr = tc.get("9f8a3f5a-2d1b-11ec-98b3-06abac9454c4");
        assertEquals("Site1", tdr.getServiceName());
        assertEquals("rutherford/room1/temp", tdr.getTagPath());
        cache.close(false);

        cache = new Cache(config);
        assertNotNull("should not be null", cache);
        tc = new TagDataCache(cache);
        assertNotNull("should not be null", tc);

        td = new TagData("Site1", "rutherford/room1/pressure", "9f8a3f5a-2d1b-11ec-98b3-06abac9454a5", "yyyy");
        assertNotNull("New TagData should not be null", td);
        td.setDeviceStatus(0);
        td.setDimensionStatus(0);
        tc.put("9f8a3f5a-2d1b-11ec-98b3-06abac9454a5", td);

        tdr = tc.get("9f8a3f5a-2d1b-11ec-98b3-06abac9454a5");
        assertEquals("Site1", tdr.getServiceName());
        assertEquals("rutherford/room1/pressure", tdr.getTagPath());
        assertEquals(0, tdr.getDeviceStatus());
        assertEquals(0, tdr.getDimensionStatus());

        td.setDeviceStatus(1);
        td.setDimensionStatus(1);
        tc.update("9f8a3f5a-2d1b-11ec-98b3-06abac9454a5", td);
        tdr = tc.get("9f8a3f5a-2d1b-11ec-98b3-06abac9454a5");
        assertEquals(1, tdr.getDeviceStatus());
        assertEquals(1, tdr.getDimensionStatus());

        TagData tdx = tc.get("9f8a3f5a-2d1b-11ec-98b3-06abac9454c4");
        assertEquals("Site1", tdx.getServiceName());
        assertEquals("rutherford/room1/temp", tdx.getTagPath());
        assertEquals(0, tdx.getDeviceStatus());
        assertEquals(0, tdx.getDimensionStatus());
        tdx.setDeviceStatus(1);
        tdx.setDimensionStatus(1);
        assertEquals(1, tdx.getDeviceStatus());
        assertEquals(1, tdx.getDimensionStatus());
        tc.update("9f8a3f5a-2d1b-11ec-98b3-06abac9454c4", tdx);

        TagData tdy = tc.get("9f8a3f5a-2d1b-11ec-98b3-06abac9454c4");
        assertEquals(1, tdy.getDeviceStatus());

        tc.remove("9f8a3f5a-2d1b-11ec-98b3-06abac9454a5");
        tdr = tc.get("9f8a3f5a-2d1b-11ec-98b3-06abac9454a5");
        assertNull("should be null", tdr);

        cache.close(true);
    }
}

