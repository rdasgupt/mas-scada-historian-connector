/*
 * IBM Confidential
 * OCO Source Materials
 * 5725-S86, 5900-A0N, 5737-M66, 5900-AAA
 * (C) Copyright IBM Corp. 2021
 * The source code for this program is not published or otherwise divested of
 * its trade secrets, irrespective of what has been deposited with the U.S.
 * Copyright Office.
 */

package com.ibm.mas.scada.historian.connector.configurator.iotdevice;

import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import java.nio.file.Paths;
import java.nio.file.Path;
import org.junit.BeforeClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import com.ibm.mas.scada.historian.connector.configurator.iotdevice.IoTClient;
import com.ibm.mas.scada.historian.connector.utils.Copyright;

public class ConfigureInterfacesTest {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;

    static String dataDir;

    @BeforeClass
    public static void init() {
        Path dir = Paths.get("tmp");
        dataDir = dir.toFile().getAbsolutePath();
    }

    @Test
    public void test01() {
        try {
            ConfigureInterfaces ci = new ConfigureInterfaces(0, dataDir, "o77abp", "a-o77abp-pgf2dnn1w5", "t*?znzYpoPvKU5Et07");
            assertNotNull("should not be null", ci);
            IoTClient iotp = ci.getIotp();
            iotp.createDeviceType("TestType", "Device");
            ci.config("TestType");
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
}

