/*
 * IBM Confidential
 * OCO Source Materials
 * 5725-S86, 5900-A0N, 5737-M66, 5900-AAA
 * (C) Copyright IBM Corp. 2021
 * The source code for this program is not published or otherwise divested of
 * its trade secrets, irrespective of what has been deposited with the U.S.
 * Copyright Office.
 */

package com.ibm.mas.scada.historian.connector.publisher;

import com.ibm.mas.scada.historian.connector.configurator.Config;
import com.ibm.mas.scada.historian.connector.utils.Copyright;

public abstract class Publisher {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;

    public abstract void init(Config config) throws Exception;
    public abstract void connect(String type, String id) throws Exception;
    public abstract void publish(String topicString, String eventName, String payload) throws Exception;
    public abstract boolean isConnected();
    public abstract void close();
}


