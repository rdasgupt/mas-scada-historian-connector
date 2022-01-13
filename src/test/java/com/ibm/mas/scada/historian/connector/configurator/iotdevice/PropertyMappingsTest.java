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
 * PropertyMappings tests.
 */
public class PropertyMappingsTest {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;

    @Test
    public void test01() {
        PropertyMappings d = new PropertyMappings();
        assertNotNull("New PropertyMappings should not be null", d);
        assertEquals("draft", d.getVersion());
        assertNull(d.getLogicalInterfaceId());
        assertNull(d.getCreated());
        assertNull(d.getUpdated());
        assertNull(d.getCreatedBy());
        assertNull(d.getUpdatedBy());
        assertNull(d.getPropertyMappings());
        assertEquals("on-every-event", d.getNotificationStrategy());

        d.setVersion("1.0.0");
        assertEquals("1.0.0", d.getVersion());
        d.setLogicalInterfaceId("XXXX");
        assertEquals("XXXX", d.getLogicalInterfaceId());
        d.setCreated("XXXX");
        assertEquals("XXXX", d.getCreated());
        d.setUpdated("XXXX");
        assertEquals("XXXX", d.getUpdated());
        d.setCreatedBy("XXXX");
        assertEquals("XXXX", d.getCreatedBy());
        d.setUpdatedBy("XXXX");
        assertEquals("XXXX", d.getUpdatedBy());
        d.setNotificationStrategy("last-event");
        assertEquals("last-event", d.getNotificationStrategy());

        Map <String, String> hm = new HashMap<String, String>();
        hm.put("XXXX", "YYYY");
        Map <String, Map<String, String>> pm = new HashMap<String, Map<String, String>>();
        pm.put("ZZZZ", hm);
        d.setPropertyMappings(pm);
        Map <String, Map<String, String>> pmr = d.getPropertyMappings();
        assertEquals(pm, pmr);
        Map <String, String> hmr = pmr.get("ZZZZ");
        assertEquals(hm, hmr);
        assertEquals("YYYY", hmr.get("XXXX"));
    }

    @Test
    public void test02() {
        Map <String, String> hm = new HashMap<String, String>();
        hm.put("XXXX", "YYYY");
        Map <String, Map<String, String>> pm = new HashMap<String, Map<String, String>>();
        pm.put("ZZZZ", hm);

        PropertyMappings d = new PropertyMappings("TestName", pm);
        assertNotNull("New PropertyMappings should not be null", d);
        assertEquals("draft", d.getVersion());
        assertEquals("on-every-event", d.getNotificationStrategy());
        assertEquals("TestName", d.getLogicalInterfaceId());
        assertNull(d.getCreated());
        assertNull(d.getUpdated());
        assertNull(d.getCreatedBy());
        assertNull(d.getUpdatedBy());
        assertNotNull("PropertyMappings should not be null", d.getPropertyMappings());

        d.setVersion("1.0.0");
        assertEquals("1.0.0", d.getVersion());
        d.setLogicalInterfaceId("XXXX");
        assertEquals("XXXX", d.getLogicalInterfaceId());
        d.setCreated("XXXX");
        assertEquals("XXXX", d.getCreated());
        d.setUpdated("XXXX");
        assertEquals("XXXX", d.getUpdated());
        d.setCreatedBy("XXXX");
        assertEquals("XXXX", d.getCreatedBy());
        d.setUpdatedBy("XXXX");
        assertEquals("XXXX", d.getUpdatedBy());
        d.setNotificationStrategy("last-event");
        assertEquals("last-event", d.getNotificationStrategy());

        Map <String, Map<String, String>> pmr = d.getPropertyMappings();
        assertEquals(pm, pmr);
        Map <String, String> hmr = pmr.get("ZZZZ");
        assertEquals(hm, hmr);
        assertEquals("YYYY", hmr.get("XXXX"));
    }
}

