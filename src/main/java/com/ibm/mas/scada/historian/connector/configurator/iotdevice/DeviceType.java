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
 * IOT DeviceType.
 */
public class DeviceType {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;

    private String id;
    private String classId;
    private String description;

    /** 
     * DeviceType class constructor.
     */
    public DeviceType() {
        super();
    }

    /** 
     * DeviceType class constructor specifying ID, classId and description.
     */
    public DeviceType(String id, String classId, String description) {
        super();
        this.id = id;
        this.classId = classId;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toString() {
        JSONObject devItem = new JSONObject();
        devItem.put("id", id);
        devItem.put("classId", classId);
        devItem.put("description", description);
        JSONObject deviceInfo = new JSONObject();
        JSONObject metadata = new JSONObject();
        devItem.put("deviceInfo", deviceInfo);
        devItem.put("metadata", metadata);
        return devItem.toString();
    }
}

