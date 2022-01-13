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
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThrows;
import com.ibm.mas.scada.historian.connector.utils.Copyright;

public class ConfigTest {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;

    public static String configDir;
    public static String dataDir;
    public static String logDir;

    @BeforeClass
    public static void init() {
        System.out.println("Initialize Config Tests.");
        Path dir = Paths.get("src","test","resources");
        configDir = dir.toFile().getAbsolutePath();
        dir = Paths.get("tmp");
        dataDir = dir.toFile().getAbsolutePath();
        logDir = dir.toFile().getAbsolutePath();
    }

    @Test
    public void test01() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> { new Config(null, null, null); });
        String expMsg = "is null or empty.";
        assertTrue(ex.getMessage().contains(expMsg));
        assertThrows(IllegalArgumentException.class, () -> { new Config("", null, null); });
        assertThrows(IllegalArgumentException.class, () -> { new Config("conf", null, null); });
        assertThrows(IllegalArgumentException.class, () -> { new Config("conf", "", null); });
        assertThrows(IllegalArgumentException.class, () -> { new Config("conf", "data", null); });
        assertThrows(IllegalArgumentException.class, () -> { new Config("conf", "data", ""); });

        try {
            Config c = new Config(configDir, dataDir, logDir);
            assertNotNull("should not be null", c);
            assertNotNull("connectionConfig should not be null", c.getConnectionConfig());
            assertNotNull("mappingConfig should not be null", c.getMappingConfig());
            assertTrue(c.getConfigDir().endsWith("src/test/resources"));
            assertTrue(c.getDataDir().endsWith("tmp"));
            assertTrue(c.getLogDir().endsWith("tmp"));
            assertNotNull("logger should not be null", c.getLogger());
            assertNotNull("clientSite should not be null", c.getClientSite());
            assertNotNull("cliport should not be null", c.getCLIPort());
            assertNotNull("httpport should not be null", c.getHTTPPort());
        } catch (Exception e) { }
    }
}

