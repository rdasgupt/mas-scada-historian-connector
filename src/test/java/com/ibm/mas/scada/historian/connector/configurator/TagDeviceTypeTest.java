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

import org.json.JSONObject;
import org.json.JSONArray;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import com.ibm.mas.scada.historian.connector.utils.Copyright;

public class TagDeviceTypeTest {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;

    static JSONObject typeConfig;
    static JSONArray tagpathFilters;
    static JSONArray discardFilters;

    @BeforeClass
    public static void init() {
        System.out.println("Initialize TagPathMapping Tests.");

        typeConfig = new JSONObject();
        typeConfig.put("type", "TestDeviceType");
        tagpathFilters = new JSONArray();
        tagpathFilters.put("Assets\\\\.*");
        typeConfig.put("tagpathFilters", tagpathFilters);
        discardFilters = new JSONArray();
        typeConfig.put("discardFilters", discardFilters);
    }

    @Test
    public void test01() {
        TagDeviceType tm = new TagDeviceType(typeConfig);
        assertNotNull("should not be null", tm);
        assertEquals("TestDeviceType", tm.getType());
        assertEquals(tagpathFilters, tm.getTagpathFilters());
        assertEquals(discardFilters, tm.getDiscardFilters());
        assertEquals(1, tm.getPatterns().size());
        assertEquals(0, tm.getDiscardPatterns().size());
        String tagpath = "Assets\\TX532\\Manufacturer";
        assertTrue(tm.verifyTagpath(tagpath));
    }
}

