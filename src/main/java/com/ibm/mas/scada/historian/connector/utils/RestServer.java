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

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.Math;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.logging.*;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

/**
 * RestServer used as local stats server and test server for unit tests.
 */ 
public class RestServer {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;

    private static Logger logger = Logger.getLogger(Constants.LOGGER_CLASS);
    private static int running;
    private static int sleepTimeMilli = 5000;
    private static HttpServer httpServer;
    private static long tid;

    private String hostIP;
    private int port = 8050;
    private InetSocketAddress isa;
    private String context;

    /** RestServer.  */
    public RestServer(String hostIP, int port, String context) throws IllegalArgumentException, IOException {
        logger.info(String.format("HTTP Host:Port: %d", port));
        this.hostIP = hostIP;
        if (hostIP == null || hostIP.isEmpty()) {
            this.hostIP = "127.0.0.1";
        }
        this.port = port;
        this.isa = new InetSocketAddress(this.hostIP, this.port);
        this.context = context;
        this.httpServer = HttpServer.create(this.isa, 0);
    }
    
    /** Start server. */
    public void start() throws IOException {
        httpServer.createContext(context, new HttpHandler() {
            public void handle(HttpExchange he) throws IOException {
                String rm = he.getRequestMethod();
                if (rm.equalsIgnoreCase("GET")) {
                    logger.info("HTTP Server received a GET request");
                    URI uri = he.getRequestURI();
                    String response = createResponse(uri);
                    he.sendResponseHeaders(200, response.getBytes().length);
                    OutputStream os = he.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                    he.close();
                } if (rm.equalsIgnoreCase("POST")) {
                    logger.info("HTTP Server received a POST request");
                    Headers requestHeaders = he.getRequestHeaders();
                    int contentLength = Integer.parseInt(requestHeaders.getFirst("Content-length"));
                    InputStream is = he.getRequestBody();
                    byte[] data = new byte[contentLength];
                    int length = is.read(data);
                    he.sendResponseHeaders(200, contentLength);
                    OutputStream os = he.getResponseBody();
                    os.write(data);
                    he.close();
                } else {
                    logger.info("HTTP Server received an unsupported request");
                    he.sendResponseHeaders(404, 0);
                    he.close();
                }
            }
        });
        httpServer.setExecutor(null);
        startServerThread();
    }

    /** Stop server. */
    public void stop() {
        logger.info("Stopping Web Server");
        if (running > 0) {
            httpServer.stop(1);
        }
        running = 0;
    }

    /** Set sleepTimeMilli. */
    public void setSleepTimeMilli(int value) {
        this.sleepTimeMilli = value;
    }

    /** Return run state. */
    public int getRunState() {
        return this.running;
    }

    /** Return thread id. */
    public long getThreadId() {
        return this.tid;
    }

    private String createResponse(URI uri) {
        StringBuilder sb =  new StringBuilder("<!DOCTYPE HTML PUBLIC '-//IETF//DTD HTML 2.0//EN'> \r\n");
        sb.append("<html> <head> <title>Test HTTP server</title> </head> <body> \r\n");
        sb.append("<h3 style=\"color:white;background-color:black;\"> &nbsp; IBM MAS Connector</h3> \r\n");
        sb.append("</body> </html> \r\n");
        return sb.toString();
    }

    // Thread to create devices
     private static void startServerThread() {
        Runnable thread = new Runnable() {
            public void run() {
                running = 0;
                tid = Thread.currentThread().getId();
                while(true) {
                    logger.info("Check/Start local HTTP server");
                    if (running == 0) {
                        logger.info("Start Web Server");
                        running = 1;
                        httpServer.start();
                    } 

                    try {
                        Thread.sleep(sleepTimeMilli);
                        running = 2;
                    } catch (IllegalArgumentException ile) {
                        /* IllegalArgumentException is thrown is sleeo time is negative */
                        sleepTimeMilli = Math.abs(sleepTimeMilli);
                    } catch (InterruptedException ine) { }
                }
            }
        };
        logger.info("Starting HTTP Serrver thread ...");
        new Thread(thread).start();
        logger.info("HTTP Server thread has started");
        return;
    }
}
