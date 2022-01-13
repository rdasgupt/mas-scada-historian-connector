/*
 * IBM Confidential
 * OCO Source Materials
 * 5725-S86, 5900-A0N, 5737-M66, 5900-AAA
 * (C) Copyright IBM Corp. 2021
 * The source code for this program is not published or otherwise divested of
 * its trade secrets, irrespective of what has been deposited with the U.S.
 * Copyright Office.
 */

package com.ibm.mas.scada.historian.connector.processor;

import java.io.*;
import java.net.*;
import java.util.logging.*;

import com.ibm.mas.scada.historian.connector.configurator.Config;
import com.ibm.mas.scada.historian.connector.utils.OffsetRecord;
import com.ibm.mas.scada.historian.connector.utils.Copyright;
 
public class CLIServer {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;
    private static Logger logger;

    private static Config config;
    private static OffsetRecord offsetRecord;
    private static int serverPort;

    public CLIServer(Config config, OffsetRecord offsetRecord) {
        this.config = config;
        this.offsetRecord = offsetRecord;
        this.logger = config.getLogger();
        serverPort = config.getCLIPort();
    }

    public void start() {
        startCLIServerThread();
    }

    private static String getStats() {
        StringBuilder sb =  new StringBuilder("\r\nIBM MAS SCADA Historian Connector Stats\r\n");
        sb.append(String.format("\r\n"));
        sb.append(String.format("Client Site: %s \r\n", config.getClientSite()));
        sb.append(String.format("Total Registered Tags: %d \r\n", offsetRecord.getEntityCount()));
        sb.append(String.format("\r\n"));
        sb.append(String.format("Last processing cycle stats:\r\n"));
        sb.append(String.format("Extracted Records: %d \r\n", offsetRecord.getProcessedCount()));
        sb.append(String.format("Uploaded Records: %d \r\n", offsetRecord.getUploadedCount()));
        sb.append(String.format("Process Rate per second: %d \r\n", offsetRecord.getRate()));
        sb.append(String.format("Last data extract start time (in seconds): %d \r\n", offsetRecord.getStartTimeSecs()));
        sb.append(String.format("Last data extract end   time (in seconds): %d \r\n", offsetRecord.getEndTimeSecs()));
        return sb.toString();
    }

    private static String setDebug() {
        logger.setLevel(Level.FINE);
        logger.fine("Log level is set to FINE");
        return "Debug level is set.";
    }

    private static String unsetDebug() {
        logger.setLevel(Level.INFO);
        logger.info("Log level is set to INFO");
        return "Debug level is unset.";
    }

    // Thread to create devices
     private static void startCLIServerThread() {
        Runnable thread = new Runnable() {
            public void run() {
                while (true) {
                    try {
                        ServerSocket serverSocket = new ServerSocket(serverPort, 0, InetAddress.getByName("127.0.0.1"));
                        while(true) {
                            logger.info("Waiting for client on port: " + serverSocket.getLocalPort()); 
                            Socket server = serverSocket.accept();
                            logger.info("CLI request received from: " + server.getRemoteSocketAddress()); 
                            PrintWriter response = new PrintWriter(server.getOutputStream(),true);
                            BufferedReader request = new BufferedReader(new InputStreamReader(server.getInputStream()));
                            String command = request.readLine();
                            String responseStr = "Invalid request";
                            if (command.equals("getStats")) {
                                responseStr = getStats(); 
                            } else if (command.equals("setDebug")) {
                                responseStr = setDebug(); 
                            } else if (command.equals("unsetDebug")) {
                                responseStr = unsetDebug(); 
                            }
                            response.println(responseStr);
                            response.flush();
                            response.close();
                            request.close();
                        }
                    }
                    catch(Exception e) {
                        logger.info("Exception caught: " + e.getMessage());
                        logger.info("Restart CLI server.");
                    }
                }
            }
        };
        logger.info("Starting CLI server thread ...");
        new Thread(thread).start();
        logger.info("CLI Server thread is started");
        return;
    }

}

