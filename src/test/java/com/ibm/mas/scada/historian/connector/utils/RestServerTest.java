/*
 * IBM Confidential
 * OCO Source Materials
 * 5725-S86, 5900-A0N, 5737-M66, 5900-AAA
 * (C) Copyright IBM Corp. 2021
 * The source code for this program is not published or otherwise divested of
 * its trade secrets, irrespective of what has been deposited with the U.S.
 * Copyright Office.
 */

package com.ibm.mas.scada.historian.connector.utils;

import java.io.IOException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


/**
 * RestServer tests.
 */
public class RestServerTest {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;

    /** RestServer test 1. */
    @Test
    public void testRestServer_01() {
        /* Empty host and invalid port - thorws IllegalArgumentException */
        int gotException = 0;
        try {
            RestServer restServer = new RestServer("", 66000, "/");
        } catch (IllegalArgumentException ilx) {
            gotException = 1;
        } catch (IOException iox) { }
        assertEquals(gotException, 1);
    }

     /** RestServer test 2. */
     @Test
     public void testRestServer_02() {
        /* null host and invalid port - thorws IllegalArgumentException */
        int gotException = 0;
        try {
            RestServer restServer = new RestServer(null, 66000, "/");
        } catch (IllegalArgumentException ilx) {
            gotException = 1;
        } catch (IOException iox) { }
        assertEquals(gotException, 1);
    }

    /** RestServer test 3. */
    @Test
    public void testRestServer_03() {
        RestServer restServer;
        int gotException = 0;
        int getRunState = 0;
        try {
            restServer = new RestServer("127.0.0.1", 8050, "/");
            restServer.setSleepTimeMilli(-1000);
            restServer.start();
            while (true) {
                Thread.yield();
                getRunState = restServer.getRunState();
                if (getRunState == 2) {
                    break;
                }
            }
            /* inturrupt thread */
            for (Thread thread : Thread.getAllStackTraces().keySet()) {
                if (thread.getId() == restServer.getThreadId()){
                    thread.interrupt();
                }
            }
            restServer.stop();
        } catch (IllegalArgumentException ilx) {
            gotException = 1;
        } catch (IOException iox) {
            gotException = 2;
        }
        assertEquals(0, gotException);
        assertEquals(2, getRunState);
    }

    /** RestServer test 4. */
    @Test
    public void testRestServer_04() {
        RestServer restServer;
        int gotException = 0;
        int getRunState = 0;
        try {
            restServer = new RestServer("127.0.0.1", 8050, "/");
            restServer.stop();
            restServer.setSleepTimeMilli(-10000);
            restServer.start();
            while (true) {
                Thread.yield();
                getRunState = restServer.getRunState();
                if (getRunState == 1) {
                    break;
                }
            }
            restServer.stop();
        } catch (IllegalArgumentException ilx) {
            gotException = 1;
        } catch (IOException iox) {
            gotException = 2;
        }
        assertEquals(0, gotException);
        assertEquals(1, getRunState);
    }
}
