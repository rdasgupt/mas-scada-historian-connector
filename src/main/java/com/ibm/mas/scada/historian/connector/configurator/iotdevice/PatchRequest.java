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

import com.ibm.mas.scada.historian.connector.utils.Copyright;

public class PatchRequest {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;

    public static final String ACTIVATE = "activate-configuration";
    public static final String DEACTIVATE = "deactivate-configuration";
    public static final String VALIDATE = "validate-configuration";

    private String operation;

    public PatchRequest() {
        super();
    }

    public PatchRequest(String operation) {
        super();
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}

