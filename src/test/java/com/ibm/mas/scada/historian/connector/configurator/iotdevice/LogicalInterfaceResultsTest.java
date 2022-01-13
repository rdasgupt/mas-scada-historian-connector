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

import java.lang.Object;
import java.util.List;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import com.ibm.mas.scada.historian.connector.configurator.iotdevice.LogicalInterface;
import com.ibm.mas.scada.historian.connector.utils.Copyright;

/**
 * LogicalInterfaceResults tests.
 */
public class LogicalInterfaceResultsTest {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;

    @Test
    public void test01() {
        LogicalInterfaceResults d = new LogicalInterfaceResults();
        assertNotNull("New LogicalInterfaceResults should not be null", d);
        assertNull(d.getResults());
        assertNull(d.getMeta());

        List <LogicalInterface> lil = new ArrayList<>();
        LogicalInterface li = new LogicalInterface("name", "description", "schemaId");
        lil.add(li);
        d.setResults(lil);
        List <LogicalInterface> lir = d.getResults();
        assertEquals(lil, lir);
        LogicalInterface rli = lir.get(0);
        assertEquals("schemaId", rli.getSchemaId());

        d.setObject((Object)li);
        Object o = d.getMeta();
        assertNotNull("Meta should not be null", o);
    }
}

