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
 * DeviceType tests.
 */
public class DeviceTypeTest {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;

    @Test
    public void test01() {
        DeviceType dt = new DeviceType();
        assertNotNull("New DeviceType should not be null", dt);
        assertNull(dt.getId());
        dt.setId("TestType");
        assertEquals("TestType", dt.getId());
        assertNull(dt.getClassId());
        dt.setClassId("device");
        assertEquals("device", dt.getClassId());
        assertNull(dt.getDescription());
        dt.setDescription("XXXX");
        assertEquals("XXXX", dt.getDescription());
    }

    @Test
    public void test02() {
        DeviceType dt = new DeviceType("TestType", "device", "XXXX");
        assertNotNull("New DeviceType should not be null", dt);
        assertEquals("TestType", dt.getId());
        assertEquals("device", dt.getClassId());
        assertEquals("XXXX", dt.getDescription());
    }

}

