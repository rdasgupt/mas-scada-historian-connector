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
import com.ibm.mas.scada.historian.connector.utils.Copyright;

public class  Schema extends IntfBaseResource {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;

    private String version;
    private String schemaType;
    private String schemaFileName;
    private String contentType;

    public Schema() {
    	super();
    	this.refs = new HashMap<String, String>();	
    }

    public Schema(String name, String description) {
    	super();
    	this.name = name;
    	this.description = description;
    	this.refs = new HashMap<String, String>();	
    }

    public String getContentType() {
    	return contentType;
    }

    public void setContentType(String contentType) {
    	this.contentType = contentType;
    }

    public String getSchemaFileName() {
    	return schemaFileName;
    }

    public void setSchemaFileName(String schemaFileName) {
    	this.schemaFileName = schemaFileName;
    }

    public String getSchemaType() {
    	return schemaType;
    }

    public void setSchemaType(String schemaType) {
    	this.schemaType = schemaType;
    }

    public String getVersion() {
    	return version;
    }

    public void setVersion(String version) {
    	this.version = version;
    }

}

