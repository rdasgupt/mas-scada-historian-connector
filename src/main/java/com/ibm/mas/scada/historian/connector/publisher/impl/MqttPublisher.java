/*
 * IBM Confidential
 * OCO Source Materials
 * 5725-S86, 5900-A0N, 5737-M66, 5900-AAA
 * (C) Copyright IBM Corp. 2021
 * The source code for this program is not published or otherwise divested of
 * its trade secrets, irrespective of what has been deposited with the U.S.
 * Copyright Office.
 */

package com.ibm.mas.scada.historian.connector.publisher.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.mas.scada.historian.connector.utils.Constants;
import com.ibm.mas.scada.historian.connector.utils.Copyright;
import com.ibm.mas.scada.historian.connector.configurator.Config;
import com.ibm.mas.scada.historian.connector.publisher.Publisher;
import com.ibm.mas.scada.historian.connector.publisher.impl.mqtt.Client;

public class MqttPublisher extends Publisher {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;
    private static final String CLASS = Constants.LOGGER_CLASS;
    private static Logger logger = Logger.getLogger(CLASS);

   public Publisher publisher;
   public Client client;

    public Publisher MqttPublisher() {
        return publisher;
    }

    @Override
    public void init(Config config) throws Exception {
        final String METHOD = "initPublisher";
        logger.logp(Level.FINE, CLASS, METHOD, "Initialize publisher");
        this.client = new Client(config);
    }

    @Override
    public void connect(String type, String id) throws Exception {
        final String METHOD = "connectPublisher";
        logger.logp(Level.FINE, CLASS, METHOD, "Connect publisher");
        client.connect(type, id);
    }

    @Override
    public void publish(String topicString, String eventName, String payload) throws Exception {
        client.publish(topicString, eventName, payload);
    }

    @Override
    public boolean isConnected() {
        return client.isConnected();
    }

    @Override
    public void close() {
        final String METHOD = "close";
        logger.logp(Level.FINE, CLASS, METHOD, "Close client");
        try {
            client.close();
        } catch (Exception e) { }
    }
}

