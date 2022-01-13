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

import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import com.ibm.mas.scada.historian.connector.utils.Copyright;

/**
 * Device tests.
 */
public class DeviceTest {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;

    @Test
    public void test01() {
        Device d = new Device();
        assertNotNull("New Device should not be null", d);
        assertNull(d.getDeviceId());
        d.setDeviceId("TestDevice");
        assertEquals("TestDevice", d.getDeviceId());
        assertNull(d.getAuthToken());
        d.setAuthToken("XXXX");
        assertEquals("XXXX", d.getAuthToken());
    }

    @Test
    public void test02() {
        Device d = new Device("TestDevice", "XXXX");
        assertNotNull("New Device should not be null", d);
        assertEquals("TestDevice", d.getDeviceId());
        assertEquals("XXXX", d.getAuthToken());
    }
}
