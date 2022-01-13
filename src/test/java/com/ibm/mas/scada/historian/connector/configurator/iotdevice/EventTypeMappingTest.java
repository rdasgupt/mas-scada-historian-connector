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
 * EventTypeMapping tests.
 */
public class EventTypeMappingTest {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;

    @Test
    public void test01() {
        EventTypeMapping d = new EventTypeMapping();
        assertNotNull("New EventTypeMapping should not be null", d);
        assertNull(d.getEventId());
        assertNull(d.getEventTypeId());

        d.setEventId("1.0.0");
        assertEquals("1.0.0", d.getEventId());
        d.setEventTypeId("XXXX");
        assertEquals("XXXX", d.getEventTypeId());
    }

    @Test
    public void test02() {
        EventTypeMapping d = new EventTypeMapping("TestName", "TestDescription");
        assertNotNull("New EventTypeMapping should not be null", d);
        assertEquals("TestName", d.getEventId());
        assertEquals("TestDescription", d.getEventTypeId());
    }
}

