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

import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * Constants class tests.
 */
public class ConstantsTest {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;

    /** Tests all Constants value. */
    @Test
    public void testConstants() {
        assertEquals(Constants.AUTH_NONE, 0);
        assertEquals(Constants.AUTH_BASIC, 1);
        assertEquals(Constants.AUTH_HEADER, 2);
        assertEquals(Constants.HTTP_GET, 1);
        assertEquals(Constants.HTTP_POST, 2);
        assertEquals(Constants.HTTP_PUT, 3);
        assertEquals(Constants.CONNECTOR_ALARM, 1);
        assertEquals(Constants.CONNECTOR_DEVICE, 2);
        assertEquals(Constants.CONNECTOR_STATS_TAGS, 6);
        assertEquals(Constants.DB_DEST_TYPE_DB2, 1);
        assertEquals(Constants.DB_DEST_TYPE_POSTGRE, 2);
        assertEquals(Constants.DB_SOURCE_TYPE_MSSQL, 1);
        assertEquals(Constants.DB_SOURCE_TYPE_MYSQL, 2);
        assertEquals(Constants.EXTRACT_STATUS_INIT, 1);
        assertEquals(Constants.EXTRACT_STATUS_NO_TABLE, 2);
        assertEquals(Constants.EXTRACT_STATUS_TABLE_WITH_DATA, 3);
        assertEquals(Constants.EXTRACT_STATUS_TABLE_NO_DATA, 4);
        assertEquals(Constants.RUN_TYPE_DEBUG, 1);
        assertEquals(Constants.RUN_TYPE_PRODUCTION, 2);
        assertEquals(Constants.RUN_TYPE_TEST, 3);
    }
}

