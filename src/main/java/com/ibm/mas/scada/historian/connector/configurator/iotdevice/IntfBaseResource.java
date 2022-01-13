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
import com.ibm.mas.scada.historian.connector.utils.Copyright;

/**
 * IntfBaseResource.
 */
public class IntfBaseResource {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;

    protected String id;
    protected String name;
    protected String description;
    protected String created;
    protected String updated;
    protected String createdBy;
    protected String updatedBy;
    protected Map<String, String> refs;

    /** 
     * IntfBaseResource class constructor.
     */
    public IntfBaseResource() {
        super();
        this.refs = new HashMap<String, String>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Map<String, String> getRefs() {
        return refs;
    }

    public void setRefs(Map<String, String> refs) {
        this.refs = refs;
    }

}

