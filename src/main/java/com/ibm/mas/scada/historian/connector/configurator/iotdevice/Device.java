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
 * IOT Device.
 */
public class Device {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;

    private String deviceId;
    private String authToken;

    /** 
     * Device class constructor.
     */
    public Device() {
        super();
    }

    /** 
     * Device class constructor specifying ID and auth token.
     */
    public Device(String deviceId, String authToken) {
        super();
        this.deviceId = deviceId;
        this.authToken = authToken;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String toString() {
        JSONObject devItem = new JSONObject();
        devItem.put("deviceId", deviceId);
        devItem.put("authToken", authToken);
        JSONObject deviceInfo = new JSONObject();
        JSONObject location = new JSONObject();
        JSONObject metadata = new JSONObject();
        devItem.put("deviceInfo", deviceInfo);
        devItem.put("location", location);
        devItem.put("metadata", metadata);
        return devItem.toString();
    }
}

