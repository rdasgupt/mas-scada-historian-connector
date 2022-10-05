/*
 * IBM Confidential
 * OCO Source Materials
 * 5725-S86, 5900-A0N, 5737-M66, 5900-AAA
 * (C) Copyright IBM Corp. 2021
 * The source code for this program is not published or otherwise divested of
 * its trade secrets, irrespective of what has been deposited with the U.S.
 * Copyright Office.
 */

package com.ibm.mas.scada.historian.connector.provider.impl.ignition;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.nio.file.*;
import java.sql.*;
import java.sql.Types.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import org.json.JSONObject;
import java.util.concurrent.ArrayBlockingQueue;

import com.ibm.mas.scada.historian.connector.utils.Constants;
import com.ibm.mas.scada.historian.connector.utils.Copyright;
import com.ibm.mas.scada.historian.connector.utils.OffsetRecord;
import com.ibm.mas.scada.historian.connector.utils.ServiceUtils;
import com.ibm.mas.scada.historian.connector.utils.ConnectorException;
import com.ibm.mas.scada.historian.connector.configurator.Config;
import com.ibm.mas.scada.historian.connector.configurator.TagData;
import com.ibm.mas.scada.historian.connector.configurator.TagDataCache;

public class DataProvider {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;
    private static Logger logger = Logger.getLogger(Constants.LOGGER_CLASS);

    private static Config config;
    private static TagDataCache tc;
    private static OffsetRecord offsetRecord;
    private static ArrayBlockingQueue<String[]> iotDataQueue;
    private static String sqlQueryTemplate;
    private static String sourceJDBCUrl;
    private static List<String> sourceDBColumnNames = new ArrayList<String>();
    private static int type;
    private static String dbUser;
    private static String dbPass;
    private static int sourceDBColumnCount = 0;
    private static long processedRowCount = 0;
    private static int connectorType = Constants.CONNECTOR_DEVICE;
    private static String clientSite;

    public DataProvider (Config config, TagDataCache tc,  OffsetRecord offsetRecord, ArrayBlockingQueue<String[]> iotDataQueue) throws Exception {
        if (config == null || iotDataQueue == null || offsetRecord == null) {
            throw new NullPointerException("DataProvider: config or tc parameter cannot be null");
        }

        this.config = config;
        this.tc = tc;
        this.offsetRecord = offsetRecord;
        this.iotDataQueue = iotDataQueue;
        this.connectorType = config.getConnectorType();
        this.clientSite = config.getClientSite();

        JSONObject connConfig = config.getConnectionConfig();
        JSONObject historian = connConfig.getJSONObject("historian");
        sourceJDBCUrl = historian.getString("jdbcUrl");
        String schema = historian.getString("schema");
        String database = historian.getString("database");
        String serverTimezone = historian.getString("serverTimezone");

        String sqlTemplateFile = config.getConfigDir() + "/deviceSqlTemplate.sql";
        if ( connectorType == Constants.CONNECTOR_ALARM) {
            sqlTemplateFile = config.getConfigDir() + "/alarmSqlTemplate.sql";
        }
        sqlQueryTemplate = new String(Files.readAllBytes(Paths.get(sqlTemplateFile)));

        dbUser = historian.getString("user");
        dbPass = historian.getString("password");

        String dbType = historian.getString("dbType");
        this.type = Constants.DB_SOURCE_TYPE_UNKNOWN;
        if (dbType.equals("mssql")) {
            this.type = Constants.DB_SOURCE_TYPE_MSSQL;
        } if (dbType.equals("mysql")) {
            this.type = Constants.DB_SOURCE_TYPE_MYSQL;
            if (!serverTimezone.equals("")) {
                sourceJDBCUrl = sourceJDBCUrl + "?serverTimezone=" + serverTimezone;
            }
        }

        logger.info(String.format("Source DB URL: %s User: %s  Password: %s", sourceJDBCUrl, dbUser, dbPass));
    }    

    /* Extract data from historian */
    public long extract() throws Exception {

        Connection conn = null;
        Statement stmt = null;

        logger.info("Connecting to source to extract data");

        long waitTime = 0;
        long cycleStartTimeMillis = System.currentTimeMillis();
        long cycleEndTimeMillis = 0;
        long cycleTime = 0;
        long startTimeSecs = offsetRecord.getStartTimeSecs();
        long startTimeMilli = startTimeSecs * 1000;
        long endTimeSecs = offsetRecord.getEndTimeSecs();
        long endTimeMilli = endTimeSecs * 1000;
        int month = offsetRecord.getMonth();
        int year = offsetRecord.getYear();

        processedRowCount = 0;

        logger.info(String.format("StartTime:%d EndTime:%d Year:%d Month:%d currTime:%d", 
            startTimeSecs, endTimeSecs, year, month, (cycleStartTimeMillis/1000)));

        String querySql = getDBSql(startTimeMilli, endTimeMilli, year, month);
        logger.info("Extract SQL: " + querySql);

        conn = getSourceConnection();
        stmt = conn.createStatement();

        ResultSet rs = null;
        int gotData = 1;
        try {
            rs = stmt.executeQuery(querySql);
        } catch (Exception qex) {
            gotData = 0;
            if (qex instanceof SQLException) {
                int errCode = ((SQLException)qex).getErrorCode();
                logger.info(String.format("SQLException errCode=%d message=%s", errCode, qex.getMessage()));
                if (errCode == 1146) {
                    /* pisql - table doesn't exist. Connector cannot work. FATAL error */
                    logger.severe("SQL Table does not exist: " + querySql);
                    System.exit(1);
                }
            } else {
                logger.info("Exception: " + qex.getMessage());
            }
        }

        if (gotData == 0) {
            resetDBConnection(stmt, rs, conn);
            return waitTime;
        }

        // Get column count and column type of TS column and cache it
        try {
            sourceDBColumnCount = sourceDBColumnNames.size();
            if (sourceDBColumnCount == 0) {
                sourceDBColumnNames = new ArrayList<String>();
                final ResultSetMetaData rsmd = rs.getMetaData();
                sourceDBColumnCount = rsmd.getColumnCount();
                logger.info(String.format("Number of columns in the source DB table: %d", sourceDBColumnCount));
                for (int i = 1; i <= sourceDBColumnCount; i++) {
                    String colName = rsmd.getColumnName(i);
                    sourceDBColumnNames.add(colName);
                }
            }
        } catch(Exception e) {
            resetDBConnection(stmt, rs, conn);
            return waitTime;
        }
   
        /* Set extracted data in processing queue */
        long currentTotalCount = 0;
        try {
            processedRowCount = setIotDataQueue(rs, iotDataQueue);
            currentTotalCount = offsetRecord.setProcessedCount(processedRowCount);
        } catch(Exception e) {
            logger.log(Level.INFO, e.getMessage(), e);
        } 
 
        logger.info(String.format("Extraction process stats: current=%d total=%d", processedRowCount, currentTotalCount));

        if (rs != null) rs.close();
        if (stmt != null) stmt.close();
        if (conn != null) conn.close();

        /* update times for extractions */
        int waitFlag = offsetRecord.updateOffsetData(endTimeSecs);
        waitTime = offsetRecord.getWaitTimeMilli(waitFlag, cycleStartTimeMillis);
        if (processedRowCount == 0) {
            logger.info(String.format("No Data extracted: currCount=%d waitTimeMilli=%d entities=%d\n",
                currentTotalCount, waitTime, offsetRecord.getEntityCount()));
        } else {
            cycleEndTimeMillis = System.currentTimeMillis();
            long timeDiff = (cycleEndTimeMillis - cycleStartTimeMillis);
            long rate = processedRowCount * 1000 / timeDiff;
            offsetRecord.setRate(rate);
        }

        return waitTime;
    }

    public long getProcessedCount() {
        return processedRowCount;
    }

    private static Connection getSourceConnection() {
        Connection conn = null;
        while (conn == null) {
            try {
                if ( type == Constants.DB_SOURCE_TYPE_PISQL ) {
                    String driver = "com.osisoft.jdbc.Driver";
                    Properties plist = new Properties();
                    plist.put("user", dbUser);
                    plist.put("password", dbPass);  
                    // Class.forName(driver).newInstance();    
                    Class.forName(driver).getDeclaredConstructor().newInstance();    
                    conn = DriverManager.getConnection(sourceJDBCUrl,plist);
                } else if ( type == Constants.DB_SOURCE_TYPE_MYSQL ) {
                    conn = DriverManager.getConnection(sourceJDBCUrl, dbUser, dbPass);
                } else {
                    conn = DriverManager.getConnection(sourceJDBCUrl);
                }

            } catch(Exception e) {
                logger.log(Level.INFO, e.getMessage(), e);
                conn = null;
            }
            if (conn == null) {
                logger.info("Retry source DB connection after 5 seconds.");
                try {
                    Thread.sleep(5000);
                } catch (Exception e) {}
            }
        }

        return conn;
    }

    private int setIotDataQueue(ResultSet rs, ArrayBlockingQueue<String[]> iotDataQueue) throws Exception {
        int rowCount = 0;

        while (rs.next()) {
            TagData td = null;

            String tagid = rs.getString("tagid");
            String tagpath = rs.getString("tagpath");
            String idString = clientSite + ":" + tagid + ":" + tagpath;
            idString = idString.trim();

            try {
                td = tc.get(idString);
            } catch(Exception e) { }
            if (td == null) {
                logger.warning("Tag data is not in cache: " + idString);
                continue;
            }

            // System.out.println(String.format("tagId=%s tagPath=%s value=%s", tagid, td.getTagPath(), value));

            String evtts = rs.getString("t_stamp");
            String tmString = td.getMetrics();
            JSONObject tm = new JSONObject(tmString);
            String [] iotData = new String[Constants.IOTP_IGNITION_DEVICE_TOTAL];
            iotData[Constants.IOTP_IGNITION_DEVICE_DEVICETYPE] = td.getDeviceType();
            iotData[Constants.IOTP_IGNITION_DEVICE_DEVICEID] = td.getDeviceId();
            iotData[Constants.IOTP_IGNITION_DEVICE_EVT_NAME] = "EventA";
            // iotData[Constants.IOTP_IGNITION_DEVICE_EVT_TIMESTAMP] = evtts.replace(' ', 'T') + "Z";
            iotData[Constants.IOTP_IGNITION_DEVICE_EVT_TIMESTAMP] = evtts;
            iotData[Constants.IOTP_IGNITION_DEVICE_INTVALUE] = rs.getString("intvalue");
            iotData[Constants.IOTP_IGNITION_DEVICE_FLOATVALUE] = rs.getString("floatvalue");
            iotData[Constants.IOTP_IGNITION_DEVICE_STRINGVALUE] = rs.getString("stringvalue");
            iotData[Constants.IOTP_IGNITION_DEVICE_DATEVALUE] = rs.getString("datevalue");
            iotData[Constants.IOTP_IGNITION_DEVICE_TYPE] = rs.getString("datatype");
            iotData[Constants.IOTP_IGNITION_DEVICE_DECIMALACCURACY] = tm.optString("decimalAccuracy", ""); 
            iotData[Constants.IOTP_IGNITION_DEVICE_UNIT] = tm.optString("unit", "");
            iotData[Constants.IOTP_IGNITION_DEVICE_TAG] = td.getTagPath();

            iotDataQueue.put(iotData);

            rowCount += 1;
        }

        logger.info("Total rows in source data map: " + rowCount);

        return rowCount;
    }

    private static void resetDBConnection(Statement stmt, ResultSet rs, Connection conn) throws Exception {
        if (stmt != null) stmt.close();
        if (rs != null) rs.close();
        if (conn != null) conn.close();
        try {
            Thread.sleep(50);
        } catch (Exception e) {}
    }

    private static String getDBSql(long startMilli, long endMilli) {
        String sqlStr = "";
        try {
            Date sDate = new Date(startMilli);
            Date eDate = new Date(endMilli);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String sDateStr = df.format(sDate);
            String eDateStr = df.format(eDate);
            sqlStr = String.format(sqlQueryTemplate, sDateStr, eDateStr);
        } catch(Exception ex) {
            ex.printStackTrace();
        }

        return sqlStr;
    }

    private static String getDBSql(long startMilli, long endMilli, int year, int month) {
        String sqlStr = String.format(sqlQueryTemplate, year, month, startMilli, endMilli);
        return sqlStr;
    }

}

