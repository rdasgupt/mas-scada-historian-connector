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
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Base64;
import java.nio.file.Files;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * RestClient class.
 */
public class RestClient {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;

    private static Logger logger = Logger.getLogger(Constants.LOGGER_CLASS);

    private String baseUri;
    private int authType;
    private String key;
    private String token;
    private HttpClient client;
    private HttpRequest request;
    private HttpResponse<String> response; // only supports string response
    private String outFile;
    private int methodType;
    private String tenantId = "";

    /** Create RestClient. */
    public RestClient(String baseUri, int authType, String key, String token) {
        this.baseUri = baseUri;
        this.authType = authType;
        this.key = key;
        this.token = token;
        this.client = HttpClient.newHttpClient();
    }

    /** Create RestClient. */
    public RestClient(String baseUri, int authType, String key, String token, String tenantId) {
        this.baseUri = baseUri;
        this.authType = authType;
        this.key = key;
        this.token = token;
        this.client = HttpClient.newHttpClient();
        this.tenantId = tenantId;
    }

    /** Invokes POST method. */
    public void post(String method, String body) throws IOException, InterruptedException {
        String postEndpoint = this.baseUri + method;
        logger.info("POST Endpoint: " + postEndpoint);
        this.methodType = Constants.HTTP_POST;
        if (authType == Constants.AUTH_BASIC) {
            String encodedAuth = Base64.getEncoder()
                .encodeToString((this.key + ":" + this.token).getBytes(StandardCharsets.UTF_8));

            request = HttpRequest.newBuilder()
                .uri(URI.create(postEndpoint))
                .header("Content-Type", "application/json")
                .header("Authorization", "Basic " + encodedAuth)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        } else if (authType == Constants.AUTH_HEADER) {
            request = HttpRequest.newBuilder()
                .uri(URI.create(postEndpoint))
                .header("Content-Type", "application/json")
                .header("x-api-key", key)
                .header("x-api-token", token)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        } else {
            request = HttpRequest.newBuilder()
                .uri(URI.create(postEndpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        }

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /** Invokes GET method. */
    public void get(String method) throws IOException, InterruptedException {
        String getEndpoint = this.baseUri + method;
        logger.info("GET Endpoint: " + getEndpoint);
        this.methodType = Constants.HTTP_GET;
        if (authType == Constants.AUTH_BASIC) {
            String encodedAuth = Base64.getEncoder()
                .encodeToString((this.key + ":" + this.token).getBytes(StandardCharsets.UTF_8));

            request = HttpRequest.newBuilder()
                .uri(URI.create(getEndpoint))
                .header("Authorization", "Basic " + encodedAuth)
                .build();
        } else if (authType == Constants.AUTH_HEADER) {
            request = HttpRequest.newBuilder()
                .uri(URI.create(getEndpoint))
                .header("x-api-key", key)
                .header("x-api-token", token)
                .build();
        } else {
            request = HttpRequest.newBuilder()
                .uri(URI.create(getEndpoint))
                .build();
        }
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /** Invokes PUT method. */
    public void put(String method, String body) throws IOException, InterruptedException {
        String putEndpoint = this.baseUri + method;
        logger.info("PUT Endpoint: " + putEndpoint);
        this.methodType = Constants.HTTP_PUT;
        if (authType == Constants.AUTH_BASIC) {
            String encodedAuth = Base64.getEncoder()
                .encodeToString((this.key + ":" + this.token).getBytes(StandardCharsets.UTF_8));

            request = HttpRequest.newBuilder()
                .uri(URI.create(putEndpoint))
                .header("Content-Type", "application/json")
                .header("Authorization", "Basic " + encodedAuth)
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();
        } else if (authType == Constants.AUTH_HEADER) {
            request = HttpRequest.newBuilder()
                .uri(URI.create(putEndpoint))
                .header("Content-Type", "application/json")
                .header("x-api-key", key)
                .header("x-api-token", token)
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();
        } else {
            request = HttpRequest.newBuilder()
                .uri(URI.create(putEndpoint))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();
        }

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /** Invokes DELETE method. */
    public void delete(String method) throws IOException, InterruptedException {
        String deleteEndpoint = this.baseUri + method;
        logger.info("DELETE Endpoint: " + deleteEndpoint);
        this.methodType = Constants.HTTP_DELETE;
        request = HttpRequest.newBuilder()
            .uri(URI.create(deleteEndpoint))
            .header("Content-Type", "application/json")
            .DELETE()
            .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /** Invokes PATCH method. */
    public void patch(String method, String body) throws IOException, InterruptedException {
        String patchEndpoint = this.baseUri + method;
        logger.info("PATCH Endpoint: " + patchEndpoint);
        String encodedAuth = Base64.getEncoder()
            .encodeToString((this.key + ":" + this.token).getBytes(StandardCharsets.UTF_8));
        request = HttpRequest.newBuilder()
            .uri(URI.create(patchEndpoint))
            .header("Content-Type", "application/json")
            .header("Authorization", "Basic " + encodedAuth)
            .method("PATCH", HttpRequest.BodyPublishers.ofString(body))
            .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /** Invokes POST a file as multipart-form for IoT schema creation. */
    public void uploadFile(String method, String name, String fileName, Path filePath) throws IOException, InterruptedException {
        String postEndpoint = this.baseUri + method;
        logger.info("POST Endpoint: " + postEndpoint);
        this.methodType = Constants.HTTP_POST;
        String boundary = "-------------oiawn4tp89n4e9p5";
        Map<Object, Object> data = new HashMap<>();
        data.put("schemaFile", filePath);
        data.put("name", name);
        data.put("schemaType", "json-schema");
        data.put("description", "OSIPI Connectector schema " + name);
 
        if (authType == Constants.AUTH_BASIC) {
            String encodedAuth = Base64.getEncoder()
                .encodeToString((this.key + ":" + this.token).getBytes(StandardCharsets.UTF_8));

            request = HttpRequest.newBuilder()
                .uri(URI.create(postEndpoint))
                .header("Authorization", "Basic " + encodedAuth)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(iotMultipartData(data, boundary))
                .build();
        } else if (authType == Constants.AUTH_HEADER) {
            request = HttpRequest.newBuilder()
                .uri(URI.create(postEndpoint))
                .header("x-api-key", key)
                .header("x-api-token", token)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(iotMultipartData(data, boundary))
                .build();
        } else {
            request = HttpRequest.newBuilder()
                .uri(URI.create(postEndpoint))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(iotMultipartData(data, boundary))
                .build();
        }

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /** Returns response */
    public HttpResponse<String> getResponse() {
        return response;
    }

    /** Returns response code. */
    public int getResponseCode() {
        int rc = HttpURLConnection.HTTP_NO_CONTENT;
        if (this.response != null) {
            rc = this.response.statusCode();
        }
        return rc;
    }

    /** Returns response body. */
    public String getResponseBody() {
        String emptyResponse = "";
        if (this.response != null) {
            return this.response.body();
        } else {
            return emptyResponse;
        }
    }

    public static BodyPublisher iotMultipartData(Map<Object, Object> data, String boundary) throws IOException {
        var byteArrays = new ArrayList<byte[]>();
        byte[] separator = ("--" + boundary + "\r\nContent-Disposition: form-data; name=").getBytes(StandardCharsets.UTF_8);
        for (Map.Entry<Object, Object> entry : data.entrySet()) {
            byteArrays.add(separator);
 
            if (entry.getValue() instanceof Path) {
                var path = (Path) entry.getValue();
                String mimeType = "application/json";
                byteArrays.add(("\"schemaFile=\";filename=\""
                        + path.getFileName() + "\"\r\nContent-Type: " + mimeType
                        + "\r\n\r\n").getBytes(StandardCharsets.UTF_8));
                byteArrays.add(Files.readAllBytes(path));
                byteArrays.add("\r\n".getBytes(StandardCharsets.UTF_8));
            } else {
                byteArrays.add(("\"" + entry.getKey() + "\"\r\n\r\n" + entry.getValue() + "\r\n").getBytes(StandardCharsets.UTF_8));
            }
        }
        byteArrays.add(("--" + boundary + "--").getBytes(StandardCharsets.UTF_8));
        return BodyPublishers.ofByteArrays(byteArrays);
    }
}

