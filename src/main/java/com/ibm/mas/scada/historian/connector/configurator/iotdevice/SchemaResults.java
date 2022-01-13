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

import java.lang.Object;
import java.util.List;
import com.ibm.mas.scada.historian.connector.utils.Copyright;

/**
 * SchemaResults.
 */
public class SchemaResults {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;

    private List<Schema> results;
    private Object meta;

    public SchemaResults() {
        super();
    }

    public List<Schema> getResults() {
        return results;
    }

    public void setResults(List<Schema> results) {
        this.results = results;
    }

    public Object getMeta() {
        return meta;
    }

    public void setObject(Object meta) {
        this.meta = meta;
    }
}

