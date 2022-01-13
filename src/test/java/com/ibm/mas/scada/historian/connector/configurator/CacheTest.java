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

public class CacheTest {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;

    @Test
    public void test01() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> { new Cache(null); });
        String expMsg = "is null or empty.";
        assertTrue(ex.getMessage().contains(expMsg));

        Path dir = Paths.get("src","test","resources");
        String configDir = dir.toFile().getAbsolutePath();
        dir = Paths.get("tmp");
        String dataDir = dir.toFile().getAbsolutePath();
        String logDir = dir.toFile().getAbsolutePath();
        Config config = null;
        try {
            config = new Config(configDir, dataDir, logDir);
        } catch (Exception e) { }
        assertNotNull("should not be null", config);
 
        Cache c = null;
        try {
            c = new Cache(config);
        } catch (Exception e) { }
        assertNotNull("should not be null", c);
        assertNotNull("tagDataCache should not be null", c.getTagDataCache());
        assertNotNull("dataOffsetCache should not be null", c.getDataOffsetCache());
        assertNotNull("tagDataList should not be null", c.getTagDataList());
        assertNotNull("dataOffsetList should not be null", c.getDataOffsetList());
        c.close(true);
    }
}

