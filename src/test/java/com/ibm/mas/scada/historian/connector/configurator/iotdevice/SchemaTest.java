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
 * Schema tests.
 */
public class SchemaTest {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;

    @Test
    public void test01() {
        Schema d = new Schema();
        assertNotNull("New Schema should not be null", d);
        assertNull(d.getVersion());
        assertNull(d.getId());
        assertNull(d.getName());
        assertNull(d.getDescription());
        assertNull(d.getCreated());
        assertNull(d.getUpdated());
        assertNull(d.getCreatedBy());
        assertNull(d.getUpdatedBy());
        assertNotNull("New IntfBaseResource refs should not be null", d.getRefs());
        assertNull(d.getSchemaType());
        assertNull(d.getSchemaFileName());
        assertNull(d.getContentType());

        d.setVersion("1.0.0");
        assertEquals("1.0.0", d.getVersion());
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
        d.setSchemaType("XXXX");
        assertEquals("XXXX", d.getSchemaType());
        d.setSchemaFileName("XXXX");
        assertEquals("XXXX", d.getSchemaFileName());
        d.setContentType("XXXX");
        assertEquals("XXXX", d.getContentType());

        Map <String, String> hm = new HashMap<String, String>();
        hm.put("XXXX", "YYYY");
        d.setRefs(hm);
        Map <String, String> hmr = d.getRefs();
        assertEquals(hm, hmr);
        assertEquals("YYYY", hmr.get("XXXX"));
    }

    @Test
    public void test02() {
        Schema d = new Schema("TestName", "TestDescription");
        assertNotNull("New Schema should not be null", d);
        assertNull(d.getVersion());
        assertNull(d.getId());
        assertEquals("TestName", d.getName());
        assertEquals("TestDescription", d.getDescription());
        assertNull(d.getCreated());
        assertNull(d.getUpdated());
        assertNull(d.getCreatedBy());
        assertNull(d.getUpdatedBy());
        assertNotNull("New IntfBaseResource refs should not be null", d.getRefs());
        assertNull(d.getSchemaType());
        assertNull(d.getSchemaFileName());
        assertNull(d.getContentType());

        d.setVersion("1.0.0");
        assertEquals("1.0.0", d.getVersion());
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
        d.setSchemaType("XXXX");
        assertEquals("XXXX", d.getSchemaType());
        d.setSchemaFileName("XXXX");
        assertEquals("XXXX", d.getSchemaFileName());
        d.setContentType("XXXX");
        assertEquals("XXXX", d.getContentType());

        Map <String, String> hm = new HashMap<String, String>();
        hm.put("XXXX", "YYYY");
        d.setRefs(hm);
        Map <String, String> hmr = d.getRefs();
        assertEquals(hm, hmr);
        assertEquals("YYYY", hmr.get("XXXX"));
    }
}

