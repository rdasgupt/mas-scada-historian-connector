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
import com.ibm.mas.scada.historian.connector.utils.Copyright;

public class CLIClient {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;
 
    private static String type = "server";
    private static String command = "getStats";
    private static int port = 0;

    public void run() {
        try {
            InetAddress host = InetAddress.getByName("localhost"); 
            Socket socket = new Socket(host, port); 
            PrintWriter request = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader response = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            request.println(command); 
            String responseStr = null;
            while ((responseStr = response.readLine()) != null) {
                System.out.println(responseStr);
            }
            request.close();
            response.close();
            socket.close();
        }
        catch(Exception ex) {
            System.out.println("Failed to connect to MAS Ignition Connector on port " + port); 
            ex.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        // check arguments
        for (int i=0; i<args.length; i++) {
            switch(i) {
                case 0:
                    type = args[0];
                    if (!type.equals("device") && !type.equals("alarm")) {
                        System.out.println("Invalid type " + type + " is specified");
                        System.exit(1);
                    }
                    break;
        
                case 1:
                    command = args[1];
                    if (!command.equals("getStats") && !command.equals("setDebug") && !command.equals("unsetDebug")) {
                        System.out.println("Invalid command " + command + " is specified");
                        System.exit(1);
                    }
                    break;
        
                case 2:
                    port = Integer.parseInt(args[2]);
                    if (port <= 1023) {
                        System.out.println("Invalid port " + args[2] + " is specified");
                        System.exit(1);
                    }
                    break;

                default:
                    System.out.println("Invalid option " + args[i] + " is specified");
                    System.exit(1);
            }
        }

        if (port == 0) {
            port = 4550;
            if (type.equals("alarm")) port = 4551;
        }
        
        CLIClient client = new CLIClient();
        client.run();
    }
}

