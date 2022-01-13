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

/**
 * Constnats class.
 */
public final class Constants {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;
    public static final String LOGGER_CLASS = com.ibm.mas.scada.historian.connector.Connector.class.getName();

    /** HTTP No Authorization. */
    public static final int AUTH_NONE = 0;
    /** HTTP Authorization Basic. */
    public static final int AUTH_BASIC = 1;
    /** HTTP Authorization in header. */
    public static final int AUTH_HEADER = 2;

    /** HTTP GET Method. */
    public static final int HTTP_GET = 1;
    /** HTTP POST Method. */
    public static final int HTTP_POST = 2;
    /** HTTP PUT Method. */
    public static final int HTTP_PUT = 3;
    /** HTTP PATCH Method. */
    public static final int HTTP_PATCH = 4;
    /** HTTP DELETE Method. */
    public static final int HTTP_DELETE = 5;

    /** SCADA Historian types */
    public static final String SERVICE_TYPE_OSIPI = "osipi";
    public static final String SERVICE_TYPE_IGNITION = "ignition";

    /** MAS upload/publish types */
    public static final String PUBLISH_TYPE_MQTT = "mqtt";
    public static final String PUBLISH_TYPE_KAFKA = "kafka";
    public static final String PUBLISH_TYPE_DBUPLOAD = "dbupload";

    /** Connector type Alarm. */
    public static final int CONNECTOR_ALARM = 1;
    /** Connector type Device. */
    public static final int CONNECTOR_DEVICE = 2;

    /** Number of stats of the connector. */
    public static final int CONNECTOR_STATS_TAGS = 6;

    /** Destination (Monitor) database type DB2. */
    public static final int DB_DEST_TYPE_DB2 = 1;
    /** Destination (Monitor) database type POSTGRES. */
    public static final int DB_DEST_TYPE_POSTGRE = 2;
    /** Source (SCADA) database type MSSQL. */
    public static final int DB_SOURCE_TYPE_MSSQL = 1;
    /** Source (SCADA) database type MYSQL. */
    public static final int DB_SOURCE_TYPE_MYSQL = 2;
    /** Source (SCADA) database PI historian. */
    public static final int DB_SOURCE_TYPE_PISQL = 3;

    /** Mapping file formats */
    public static final int MAPPING_PIBUILDER = 1;
    public static final int MAPPING_CUSTOM = 2;

    /** Extraction status - init. */
    public static final int EXTRACT_STATUS_INIT = 1;
    /** Extraction status - no table. */
    public static final int EXTRACT_STATUS_NO_TABLE = 2;
    /** Extraction status - table with data. */
    public static final int EXTRACT_STATUS_TABLE_WITH_DATA = 3;
    /** Extraction status - table with no data. */
    public static final int EXTRACT_STATUS_TABLE_NO_DATA = 4;

    /** Run type Debug. */
    public static final int RUN_TYPE_DEBUG = 1;
    /** Run type Production. */
    public static final int RUN_TYPE_PRODUCTION = 2;
    /** Run type Test. */
    public static final int RUN_TYPE_TEST = 3;

    /** Tagpath Seperator */
    public static final String TAGPATH_SEPARATOR = "\\";

    /** IoT Data for OSIPI **/
    public static final int IOTP_OSIPI_DEVICETYPE = 0;
    public static final int IOTP_OSIPI_DEVICEID = 1;
    public static final int IOTP_OSIPI_EVT_NAME = 2;
    public static final int IOTP_OSIPI_EVT_TIMESTAMP = 3;
    public static final int IOTP_OSIPI_VALUE = 4;
    public static final int IOTP_OSIPI_DECIMALACCURACY = 5;
    public static final int IOTP_OSIPI_NAME = 6;
    public static final int IOTP_OSIPI_LABEL = 7;
    public static final int IOTP_OSIPI_TYPE = 8;
    public static final int IOTP_OSIPI_UNIT = 9;
    public static final int IOTP_OSIPI_TAG = 10;
    public static final int IOTP_OSIPI_TOTAL = 11;

    /** IOT MQTT Client types */
    public static final int DEVICE_CLIENT = 1;
    public static final int GATEWAY_CLIENT = 2;
    public static final int APPLICATION_CLIENT = 3;

    /** MQTT Client types */
    public static final int MQTT_SYNC = 1;
    public static final int MQTT_ASYNC = 2;

    private Constants() {
    }
}


