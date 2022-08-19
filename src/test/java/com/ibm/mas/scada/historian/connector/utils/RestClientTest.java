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

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * RestClient class tests.
 */
public class RestClientTest {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;

    private static RestServer restServer;

    @BeforeClass
    public static void init() {
        try {
            restServer = new RestServer("127.0.0.1", 8050, "/");
            restServer.start();
        } catch (IllegalArgumentException ilx) {
        } catch (IOException iox) { }
        assertNotNull("New RestServer should not be null", restServer);
    }

    /** Tests RestClient methods with no authorization. */
    @Test
    public void testRestClient_01() {
        String baseUri = "http://www.google.com";
        int authType = Constants.AUTH_NONE;
        String key = "xxxx";
        String token = "yyyy";
        int respCode;
        String respBody;
        String method;
        String body;

        RestClient client = new RestClient(baseUri, authType, key, token, 0);
        assertNotNull("New RestClient should not be null", client);

        /* Valid get. */
        method = "/";
        try {
            client.get(method);
        } catch (IOException ioe) {
        } catch (InterruptedException ite) { }
        respCode = client.getResponseCode();
        assertEquals(200, respCode);
        respBody = client.getResponseBody();
        assertTrue(respBody.contains("google"));

        /* Valid post. */
        method = "/test";
        body = "{test:1}";
        try {
            client.post(method, body);
        } catch (IOException ioe) {
        } catch (InterruptedException ite) { }
        respCode = client.getResponseCode();
        // assertEquals(404, respCode);

        /* Invalid get. */
        method = "/xxxxxxxx/";
        try {
            client.get(method);
        } catch (IOException ioe) {
        } catch (InterruptedException ite) { }
        respCode = client.getResponseCode();
        assertEquals(404, respCode);
        respBody = client.getResponseBody();
        assertTrue(respBody.contains("google"));
    }

    /** Tests RestClient methods with AUTH_BASIC. */
    @Test
    public void testRestClient_02() {
        String baseUri = "https://www.google.com";
        int authType = Constants.AUTH_BASIC;
        String key = "xxxx";
        String token = "yyyy";
        int respCode;
        String respBody;
        String method;
        String body;

        RestClient client = new RestClient(baseUri, authType, key, token, 0);
        assertNotNull("New RestClient should not be null", client);

        /* Valid get. */
        method = "/";
        try {
            client.get(method);
        } catch (IOException ioe) {
        } catch (InterruptedException ite) { }
        respCode = client.getResponseCode();
        assertEquals(200, respCode);
        respBody = client.getResponseBody();
        assertTrue(respBody.contains("google"));

        /* Valid post. */
        method = "/";
        body = "{test:1}";
        try {
            client.post(method, body);
        } catch (IOException ioe) {
        } catch (InterruptedException ite) { }
        respCode = client.getResponseCode();
        assertEquals(405, respCode);

        /* Invalid get. */
        method = "/xxxxxxxx/";
        try {
            client.get(method);
        } catch (IOException ioe) {
        } catch (InterruptedException ite) { }
        respCode = client.getResponseCode();
        assertEquals(404, respCode);
        respBody = client.getResponseBody();
        assertTrue(respBody.contains("google"));
    }

    /** Tests RestClient methods with AUTH_HEADER. */
    @Test
    public void testRestClient_03() {
        String baseUri = "https://www.google.com";
        int authType = Constants.AUTH_HEADER;
        String key = "xxxx";
        String token = "yyyy";
        int respCode;
        String respBody;
        String method;
        String body;

        RestClient client = new RestClient(baseUri, authType, key, token, 0);
        assertNotNull("New RestClient should not be null", client);

        /* Valid get. */
        method = "/";
        try {
            client.get(method);
        } catch (IOException ioe) {
        } catch (InterruptedException ite) { }
        respCode = client.getResponseCode();
        assertEquals(200, respCode);
        respBody = client.getResponseBody();
        assertTrue(respBody.contains("google"));

        /* Valid post. */
        method = "/";
        body = "{test:1}";
        try {
            client.post(method, body);
        } catch (IOException ioe) {
        } catch (InterruptedException ite) { }
        respCode = client.getResponseCode();
        assertEquals(405, respCode);

        /* Invalid get. */
        method = "/xxxxxxxx/";
        try {
            client.get(method);
        } catch (IOException ioe) {
        } catch (InterruptedException ite) { }
        respCode = client.getResponseCode();
        assertEquals(404, respCode);
        respBody = client.getResponseBody();
        assertTrue(respBody.contains("google"));
    }

    /** Tests RestClient methods with AUTH_BASIC. */
    @Test
    public void testRestClient_04() {
        String baseUri = "http://127.0.0.1:8050";
        int authType = Constants.AUTH_NONE;
        String key = "xxxx";
        String token = "yyyy";
        int respCode;
        String respBody;
        String method;
        String body;

        RestClient client = new RestClient(baseUri, authType, key, token, 0);
        assertNotNull("New RestClient should not be null", client);

        /* Valid get. */
        method = "/test";
        try {
            client.get(method);
        } catch (IOException ioe) {
        } catch (InterruptedException ite) { }
        respCode = client.getResponseCode();
        assertEquals(200, respCode);
        respBody = client.getResponseBody();
        assertTrue(respBody.contains("IBM MAS"));

        body = "{test:1}";
        try {
            client.post(method, body);
        } catch (IOException ioe) {
        } catch (InterruptedException ite) { }
        respCode = client.getResponseCode();
        assertEquals(200, respCode);
    }

    @Test
    public void testRestClient_05() {
        String baseUri = "http://127.0.0.1:8050";
        int authType = Constants.AUTH_NONE;
        String key = "xxxx";
        String token = "yyyy";
        int respCode;
        String respBody;
        String method;
        String body;

        RestClient client = new RestClient(baseUri, authType, key, token, 0);
        assertNotNull("New RestClient should not be null", client);

        /* Valid post. */
        method = "/test";
        body = "{test:1}";
        try {
            client.post(method, body);
        } catch (IOException ioe) {
        } catch (InterruptedException ite) { }
        respCode = client.getResponseCode();
        assertEquals(200, respCode);

        /* put method. */
        try {
            client.put(method, body);
        } catch (IOException ioe) {
        } catch (InterruptedException ite) { }
        respCode = client.getResponseCode();
        assertEquals(404, respCode);
    }

    @AfterClass
    public static void cleanup() {
        restServer.stop();
    }
}
