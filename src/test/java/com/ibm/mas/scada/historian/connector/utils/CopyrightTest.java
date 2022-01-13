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
import static org.junit.Assert.assertTrue;

/**
 * Copyright tests.
 */
public final class CopyrightTest {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;

    /** Tests all. */
    @Test
    public void testAll() {
        Copyright copyright = new Copyright();
        assertTrue(copyright.COPYRIGHT.contains("Licensed Materials - Property of IBM"));
        assertTrue(copyright.COPYRIGHT_SHORT.contains("Copyright IBM Corp."));
    }
}

