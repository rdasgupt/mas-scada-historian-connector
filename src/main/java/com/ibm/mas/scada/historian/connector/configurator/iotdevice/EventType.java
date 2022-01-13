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

import org.json.JSONObject;
import com.ibm.mas.scada.historian.connector.utils.Copyright;

/**
 * EventType.
 */
public class EventType extends IntfBaseResource {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;

    private String version;
    private String schemaId;

    public EventType() {
        super();
    }

    public EventType(String name, String description, String schemaId) {
        super();
        this.name = name;
        this.description = description;
        this.schemaId = schemaId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSchemaId() {
        return schemaId;
    }

    public void setSchemaId(String schemaId) {
        this.schemaId = schemaId;
    }

    public String toString() {
        JSONObject obj = new JSONObject();
        if (this.id != null) {
            obj.put("id", this.id);
        }
        obj.put("name", this.name);
        obj.put("description", this.description);
        obj.put("schemaId", this.schemaId);
        return obj.toString();
    }
}

