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

import java.util.Map;
import org.json.JSONObject;
import com.ibm.mas.scada.historian.connector.utils.Copyright;

public class PropertyMappings {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;

    private String logicalInterfaceId;
    private String notificationStrategy = "on-every-event";
    private Map<String, Map<String,String>> propertyMappings;
    private String version = "draft";
    private String created;
    private String updated;
    private String createdBy;
    private String updatedBy;
    
    public PropertyMappings() {
    	super();
    }

    public PropertyMappings(String logicalInterfaceId, Map<String, Map<String, String>> propertyMappings) {
    	super();
    	this.logicalInterfaceId = logicalInterfaceId;
    	this.propertyMappings = propertyMappings;
    }

    public String getLogicalInterfaceId() {
    	return logicalInterfaceId;
    }

    public void setLogicalInterfaceId(String logicalInterfaceId) {
    	this.logicalInterfaceId = logicalInterfaceId;
    }

    public String getNotificationStrategy() {
    	return notificationStrategy;
    }

    public void setNotificationStrategy(String notificationStrategy) {
    	this.notificationStrategy = notificationStrategy;
    }

    public Map<String, Map<String, String>> getPropertyMappings() {
    	return propertyMappings;
    }

    public void setPropertyMappings(Map<String, Map<String, String>> propertyMappings) {
    	this.propertyMappings = propertyMappings;
    }

    public String getVersion() {
    	return version;
    }

    public void setVersion(String version) {
    	this.version = version;
    }

    public String getCreated() {
    	return created;
    }

    public void setCreated(String created) {
    	this.created = created;
    }

    public String getUpdated() {
    	return updated;
    }

    public void setUpdated(String updated) {
    	this.updated = updated;
    }

    public String getCreatedBy() {
    	return createdBy;
    }

    public void setCreatedBy(String createdBy) {
    	this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
    	return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
    	this.updatedBy = updatedBy;
    }

    public String toString() {
        JSONObject mappings = new JSONObject(this.propertyMappings);
        JSONObject obj = new JSONObject();
        obj.put("logicalInterfaceId", this.logicalInterfaceId);
        obj.put("notificationStrategy", this.notificationStrategy);
        obj.put("version", this.version);
        obj.put("propertyMappings", mappings);
        return obj.toString();
    }

}

