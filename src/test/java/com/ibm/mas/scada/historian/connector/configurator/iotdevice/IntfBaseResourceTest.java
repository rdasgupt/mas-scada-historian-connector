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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import com.ibm.mas.scada.historian.connector.utils.Copyright;

/**
 * IntfBaseResource tests.
 */
public class IntfBaseResourceTest {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;

    @Test
    public void test01() {
        IntfBaseResource d = new IntfBaseResource();
        assertNotNull("New IntfBaseResource should not be null", d);
        assertNull(d.getId());
        assertNull(d.getName());
        assertNull(d.getDescription());
        assertNull(d.getCreated());
        assertNull(d.getUpdated());
        assertNull(d.getCreatedBy());
        assertNull(d.getUpdatedBy());
        assertNotNull("New IntfBaseResource refs should not be null", d.getRefs());

        d.setId("XXXX");
        assertEquals("XXXX", d.getId());
        d.setName("XXXX");
        assertEquals("XXXX", d.getName());
        d.setDescription("XXXX");
        assertEquals("XXXX", d.getDescription());
        d.setCreated("XXXX");
        assertEquals("XXXX", d.getCreated());
        d.setUpdated("XXXX");
        assertEquals("XXXX", d.getUpdated());
        d.setCreatedBy("XXXX");
        assertEquals("XXXX", d.getCreatedBy());
        d.setUpdatedBy("XXXX");
        assertEquals("XXXX", d.getUpdatedBy());

        Map <String, String> hm = new HashMap<String, String>();
        hm.put("XXXX", "YYYY");
        d.setRefs(hm);
        Map <String, String> hmr = d.getRefs();
        assertEquals(hm, hmr);
        assertEquals("YYYY", hmr.get("XXXX"));
    }

}
