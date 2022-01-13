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
 * EventTypeMapping.
 */
public class  EventTypeMapping {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;

    private String eventId;
    private String eventTypeId;

    public EventTypeMapping() {
    	super();
    }

    public EventTypeMapping(String eventId, String eventTypeId) {
    	super();
    	this.eventId = eventId;
    	this.eventTypeId = eventTypeId;
    }

    public String getEventId() {
    	return eventId;
    }

    public void setEventId(String eventId) {
    	this.eventId = eventId;
    }

    public String getEventTypeId() {
    	return eventTypeId;
    }

    public void setEventTypeId(String eventTypeId) {
    	this.eventTypeId = eventTypeId;
    }

    public String toString() {
        JSONObject obj = new JSONObject();
        obj.put("eventId", this.eventId);
        obj.put("eventTypeId", this.eventTypeId);
        return obj.toString();
    }
}

