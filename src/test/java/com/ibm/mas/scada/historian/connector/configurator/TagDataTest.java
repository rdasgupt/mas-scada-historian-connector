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

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.json.JSONObject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import com.ibm.mas.scada.historian.connector.utils.Copyright;

/**
 * TagData tests.
 */
public class TagDataTest {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;

    @BeforeClass
    public static void init() {
        System.out.println("Initialize TagData Tests.");
    }

    @Test
    public void test01() {
        TagData d = new TagData();
        assertNotNull("New TagData should not be null", d);
        assertNull(d.getServiceName());
        assertNull(d.getTagPath());
        assertNull(d.getDeviceId());
        assertNull(d.getDeviceType());
        assertNull(d.getMetrics());
        assertNull(d.getDimensions());
        assertEquals(0, d.getDeviceStatus());
        assertEquals(0, d.getDimensionStatus());
    }

    @Test
    public void test02() {
        TagData d = new TagData("Austin", "/building/temp", "TestDev", "TestDevType");
        assertNotNull("New TagData should not be null", d);

        JSONObject metrics = new JSONObject();
        d.setMetrics(metrics.toString());
        JSONObject dimensions = new JSONObject();
        d.setDimensions(dimensions.toString());

        assertEquals("Austin", d.getServiceName());
        assertEquals("/building/temp", d.getTagPath());
        assertEquals("TestDev", d.getDeviceId());
        assertEquals("TestDevType", d.getDeviceType());
        assertEquals(metrics.toString(), d.getMetrics());
        assertEquals(dimensions.toString(), d.getDimensions());

        d.setDeviceStatus(1);
        assertEquals(1, d.getDeviceStatus());
        d.setDimensionStatus(1);
        assertEquals(1, d.getDimensionStatus());
        d.setServiceName("London");
        assertEquals("London", d.getServiceName());
        d.setTagPath("/floor01");
        assertEquals("/floor01", d.getTagPath());
        d.setDeviceId("xxxx-yyyy-0000-aaaa");
        assertEquals("xxxx-yyyy-0000-aaaa", d.getDeviceId());
        d.setDeviceType("floor");
        assertEquals("floor", d.getDeviceType());
    }
}

