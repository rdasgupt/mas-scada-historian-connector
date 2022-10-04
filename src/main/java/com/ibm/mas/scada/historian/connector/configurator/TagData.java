/*
 * IBM Confidential
 * OCO Source Materials
 * 5725-S86, 5900-A0N, 5737-M66, 5900-AAA
 * (C) Copyright IBM Corp. 2021
 * The source code for this program is not published or otherwise divested of
 * its trade secrets, irrespective of what has been deposited with the U.S.
 * Copyright Office.
 */

package com.ibm.mas.scada.historian.connector.configurator;

import java.io.Serializable;
import com.ibm.mas.scada.historian.connector.utils.Copyright;

/**
 * TagData.
 */
public class TagData implements Serializable {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;

    private String     serviceName;
    private String     tagPath;
    private String     deviceId;
    private String     deviceType;
    private String     deviceTypeUUID;
    private String     token;
    private String     metrics;
    private String     dimensions;
    private int        deviceStatus;
    private int        dimensionStatus;

    public TagData() {
        super();
    }

    public TagData(String serviceName, String tagpath, String deviceId, String deviceType) {
        super();
        this.serviceName = serviceName;
        this.tagPath = tagpath;
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.deviceTypeUUID = "";
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return this.serviceName;
    }

    public void setTagPath(String tagpath) {
        this.tagPath = tagpath;
    }

    public String getTagPath() {
        return this.tagPath;
    }

    public String getDeviceId() {
        return this.deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceType() {
        return this.deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceTypeUUID() {
        return this.deviceTypeUUID;
    }

    public void setDeviceTypeUUID(String uuid) {
        this.deviceTypeUUID = uuid;
    }

    public int getDeviceStatus() {
        return this.deviceStatus;
    }

    public void setDeviceStatus(int status) {
        this.deviceStatus = status;
    }

    public int getDimensionStatus() {
        return this.dimensionStatus;
    }

    public void setDimensionStatus(int status) {
        this.dimensionStatus = status;
    }

    public String getMetrics() {
        return this.metrics;
    }

    public void setMetrics(String metrics) {
        this.metrics = metrics;
    }

    public String getDimensions() {
        return this.dimensions;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

