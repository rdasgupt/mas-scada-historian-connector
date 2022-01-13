/*
 * IBM Confidential
 * OCO Source Materials
 * 5725-S86, 5900-A0N, 5737-M66, 5900-AAA
 * (C) Copyright IBM Corp. 2021
 * The source code for this program is not published or otherwise divested of
 * its trade secrets, irrespective of what has been deposited with the U.S.
 * Copyright Office.
 */

package com.ibm.mas.scada.historian.connector.utils;

import java.util.TimeZone;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * DateUtil tests.
 */
public class DateUtilTest {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;

    private static TimeZone zone;

    @BeforeClass
    public static void init() {
        zone = TimeZone.getTimeZone("America/Chicago");
    }

    /** Tests DateUtil methods setByDate(). */
    @Test
    public void testDateUtil_fromDateStr() {
   
        DateUtil du = new DateUtil(zone);
        assertNotNull("New DateUtil should not be null", du);

        /* Date string is null. */
        String dateStr = null;
        du.setByDate(dateStr);
        assertEquals(0L, du.getTimeMilli());
        assertEquals(0L, du.getTimeSecs());
        assertEquals(0, du.getMonth());
        assertEquals(0, du.getYear());
        assertEquals(0, du.getDay());

        /* Valid date string. */
        dateStr = "2021-11-22 09:24:15";
        du.setByDate(dateStr);
        assertEquals(1637594655000L, du.getTimeMilli());
        assertEquals(1637594655L, du.getTimeSecs());
        assertEquals(11, du.getMonth());
        assertEquals(2021, du.getYear());
        assertEquals(22, du.getDay());

        /* Invalid date string. */
        du = new DateUtil(null);
        dateStr = "xxxx-10-21 10:55:23";
        du.setByDate(dateStr);
        assertEquals(0L, du.getTimeMilli());
        assertEquals(0L, du.getTimeSecs());
        assertEquals(0, du.getMonth());
        assertEquals(0, du.getYear());
        assertEquals(0, du.getDay());

        /* Invalid date string. */
        dateStr = "2021-21 10:55:23";
        du.setByDate(dateStr);
        assertEquals(0L, du.getTimeMilli());
        assertEquals(0L, du.getTimeSecs());
        assertEquals(0, du.getMonth());
        assertEquals(0, du.getYear());
        assertEquals(0, du.getDay());

        /* Date with month greater than 12. */
        du = new DateUtil(zone);
        dateStr = "2021-14-21 10:55:23";
        du.setByDate(dateStr);
        assertEquals(1645462523000L, du.getTimeMilli());
        assertEquals(1645462523L, du.getTimeSecs());
        assertEquals(2, du.getMonth());
        assertEquals(2022, du.getYear());
        assertEquals(21, du.getDay());
    }

    /** Tests DateUtil methods setByMilliseconds(). */
    @Test
    public void testDateUtil_fromTimeMilli() {
        DateUtil du = new DateUtil(zone);
        assertNotNull("New DateUtil should not be null", du);

        /* Time is 0. */
        long dateInMilli = 0L;
        du.setByMilliseconds(dateInMilli);
        assertEquals(0L, du.getTimeMilli());
        assertEquals(0L, du.getTimeSecs());
        assertEquals(12, du.getMonth());
        assertEquals(1969, du.getYear());
        assertEquals(31, du.getDay());

        /* Positive Time. */
        dateInMilli = 1634831723000L;
        du.setByMilliseconds(dateInMilli);
        assertEquals(1634831723000L, du.getTimeMilli());
        assertEquals(1634831723L, du.getTimeSecs());
        assertEquals(10, du.getMonth());
        assertEquals(2021, du.getYear());
        assertEquals(21, du.getDay());

        /* Negative Time. */
        dateInMilli = -1634831723000L;
        du.setByMilliseconds(dateInMilli);
        assertEquals(0L, du.getTimeMilli());
        assertEquals(0L, du.getTimeSecs());
        assertEquals(0, du.getMonth());
        assertEquals(0, du.getYear());
        assertEquals(0, du.getDay());
    }
}

