/*
 * IBM Confidential
 * OCO Source Materials
 * 5725-S86, 5900-A0N, 5737-M66, 5900-AAA
 * (C) Copyright IBM Corp. 2021
 * The source code for this program is not published or otherwise divested of
 * its trade secrets, irrespective of what has been deposited with the U.S.
 * Copyright Office.
 */

package com.ibm.mas.scada.historian.connector;

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
import com.ibm.mas.scada.historian.connector.utils.ServiceUtils;

/**
 * Unit test for MAS Connector for SCADA Historian.
 */
public class ConnectorTest {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;

    public static String configDir;
    public static String dataDir;
    public static String logDir;
    public static String mapConfigFile;

    @BeforeClass
    public static void init() {
        System.out.println("Config Tests.");
        Path dir = Paths.get("src","test","resources");
        configDir = dir.toFile().getAbsolutePath();
        dir = Paths.get("tmp");
        dataDir = dir.toFile().getAbsolutePath();
        logDir = dir.toFile().getAbsolutePath();
    }

    @Test
    public void testConnector() {
        String[] args = {configDir, dataDir, logDir};

        try {
            /* convert this to mock test */
            if (ServiceUtils.isJUnitEnv()) {
                System.out.println("By pass main tests till a test system can be set to test.");
            } else {
                Connector.main(args);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

