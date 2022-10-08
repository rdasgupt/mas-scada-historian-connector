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

import java.util.Date;
import java.util.Calendar;
import java.util.TimeZone;
import java.nio.file.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.*;
import org.json.JSONObject;
import java.util.concurrent.atomic.AtomicLong;
import com.ibm.mas.scada.historian.connector.configurator.Cache;
import com.ibm.mas.scada.historian.connector.configurator.Config;

public class OffsetRecord {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;
    private static Logger logger = Logger.getLogger(Constants.LOGGER_CLASS);

    private static String offsetId;
    private static OffsetRecordCache dataOffsetRecord;
    private static String startDate;
    private static String historianTimeZone;
    private static long startTimeSecs;
    private static long endTimeSecs;
    private static long offsetInterval = 30L;
    private static long offsetIntervalHistorical = 1800L;
    private static int  month;
    private static int  year;
    private static int  day;
    private static String offsetFile;
    private static int status;
    private static AtomicLong processedCount = new AtomicLong(0);
    private static AtomicLong uploadedCount = new AtomicLong(0);
    private static AtomicLong rate = new AtomicLong(0);
    private static AtomicLong entityCount = new AtomicLong(0);
    private static AtomicLong entityTypeCount = new AtomicLong(0);
    private static int currTimeWindowCycle = 0;
    private static TimeZone localTZ = TimeZone.getDefault();
    private static Calendar cal = Calendar.getInstance();

    public OffsetRecord(Config config, Cache cache, boolean newOffsetFile) {
        JSONObject connConfig = config.getConnectionConfig();
        JSONObject historianConfig = connConfig.getJSONObject("historian");
        this.startDate = historianConfig.getString("startDate");
        this.historianTimeZone = historianConfig.optString("serverTimezone", "America/Chicago");
        this.offsetId = config.getClientSite();
        offsetInterval = historianConfig.optLong("extractInterval", 600L);
        if (offsetInterval > 120) offsetInterval = 120;
        offsetIntervalHistorical = historianConfig.optLong("extractIntervalHistorical", 1800L);

        this.dataOffsetRecord = new OffsetRecordCache(offsetId, cache);
        if (newOffsetFile) {
             dataOffsetRecord.update(offsetId,"");
        }

        readOffsetCache();
        logger.info(String.format("StartDate:%s StartTime:%d EndTime:%d", startDate, startTimeSecs, endTimeSecs));
    }

    public long getStartTimeSecs() {
        return startTimeSecs;
    }
        
    public long getEndTimeSecs() {
        return endTimeSecs;
    }
        
    public int getMonth() {
        return month;
    }
        
    public int getYear() {
        return year;
    }

    public int getDay() {
        return day;
    }

    public long setProcessedCount(long count) {
        return processedCount.addAndGet(count);
    }

    public long getProcessedCount() {
        return processedCount.get();
    }
        
    public long setUploadedCount(long count) {
        return uploadedCount.addAndGet(count);
    }

    public long getUploadedCount() {
        return uploadedCount.get();
    }
        
    public long setEntityCount(long count) {
        return entityCount.addAndGet(count);
    }

    public long getEntityCount() {
        return entityCount.get();
    }
        
    public long setEntityTypeCount(long count) {
        return entityTypeCount.addAndGet(count);
    }

    public long getEntityTypeCount() {
        return entityTypeCount.get();
    }
        
    public void setRate(long count) {
        if (count == 0) return;
        rate.set(count);
    }

    public long getRate() {
        return rate.get();
    }

    public static void update() {
        updateOffsetCache();
    }

    public long getWaitTimeMilli(int waitFlag, long cycleStartTimeMillis) {
        long waitTime = 100;
        if (waitFlag == 1) {
            long cycleEndTimeMillis = System.currentTimeMillis();
            long timeDiff = (cycleEndTimeMillis - cycleStartTimeMillis) / 1000;
            if (timeDiff < offsetInterval) {
                waitTime = (offsetInterval - timeDiff) * 1000;
            }
        } else if (waitFlag == 2) {
            waitTime = offsetInterval * 1000;
        }
        return waitTime;
    }

    /* for OSIPI historian data */
    public int updateOffsetData(long lastEndTimeSecs) {
        int retval = 0;
        retval = updateOffset(null, lastEndTimeSecs, Constants.EXTRACT_STATUS_TABLE_WITH_DATA);
        return retval;
    }

    /* for SCADA Historians other than OSIPI */
    public int updateOffsetData(long lastStartTimeSecs, long lastEndTimeSecs, int lastYear, int lastMonth, int status) {
        int retval = 0;
        long curTimeMillis = System.currentTimeMillis();  
        long curTimeSecs = curTimeMillis/1000;
        cal.setTimeInMillis(curTimeMillis);
        int curYear = cal.get(Calendar.YEAR);
        int curMonth = cal.get(Calendar.MONTH) + 1; // Calendar base MONTH is 0

        if (status == Constants.EXTRACT_STATUS_NO_TABLE) {
            int nextYear = lastYear;
            int nextMonth = lastMonth + 1;
            if (nextMonth > 13) {
                nextMonth = 1;
                nextYear = nextYear + 1;
                if (nextYear > curYear) {
                    nextYear = curYear;
                    nextMonth = curMonth;
                }
            }

            if (lastYear > curYear) {
                // start date in config is a future date or incorrect system date. can not proceed
                logger.severe("Future start date in config or incorrect system time. Can not proceed.");
                logger.info("Reset start date to current date");
                nextYear = curYear;
                nextMonth = curMonth;
            }

            String newDateStr = String.format("%4d-%02d-01 00:00:00", nextYear, nextMonth);
            retval = updateOffset(newDateStr, 0, status);
        } else {
            Date date = new Date(lastEndTimeSecs * 1000);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateStr = df.format(date);
            retval = updateOffset(dateStr, 0, status);
        }

        return retval;
    }

    private void readOffsetCache() {
        int createCache = 0;
        JSONObject ofrec = null;
        logger.info("Get offset data from cache");
        try {
            String offsetRecordStr = dataOffsetRecord.get(offsetId);
            ofrec = new JSONObject(offsetRecordStr);
            logger.info("OffsetRecord found: " + offsetRecordStr);
        }
        catch (Exception e) {
            logger.info("OffsetRecord is not found. " + e.getMessage());
            createCache = 1;
        }

        if (ofrec != null) {
            // update data 
            startTimeSecs = ofrec.getLong("startTimeSecs");
            if (startTimeSecs != 0) {
                endTimeSecs = ofrec.getLong("endTimeSecs");
                month = ofrec.getInt("month");
                year = ofrec.getInt("year");
                status = ofrec.getInt("status");
                processedCount.set(ofrec.getInt("processed"));
                uploadedCount.set(ofrec.getInt("uploaded"));
                rate.set(ofrec.getInt("rate"));
            } else {
                createCache = 1;
            }
        }

        if (createCache == 1 || ofrec == null) {
            updateOffset(startDate, 0, Constants.EXTRACT_STATUS_INIT);
        }
    }

    private static int updateOffset(String dateStr, long timeSecs, int status) {
        int retval = 1;
        TimeZone zone = TimeZone.getTimeZone(historianTimeZone);

        DateUtil duc = new DateUtil(zone);
        long currentTimeMilli = System.currentTimeMillis();
        duc.setByMilliseconds(currentTimeMilli);

        DateUtil du = new DateUtil(zone);
        if (timeSecs != 0) {
            du.setByMilliseconds(timeSecs * 1000);
        } else {
            du.setByDate(dateStr);
        }
        startTimeSecs = du.getTimeSecs();
        endTimeSecs = startTimeSecs + offsetInterval;
        month = du.getMonth();
        year = du.getYear();
        day = du.getDay();

        // compare dateStr with current date
        if (du.getTimeMilli() > duc.getTimeMilli()) {
            // dateStr is in future
            startTimeSecs = duc.getTimeSecs();
            endTimeSecs = startTimeSecs + offsetInterval;
            month = duc.getMonth();
            year = duc.getYear();
            day = duc.getDay();
            retval = 2;
        } else if (du.getTimeMilli() < duc.getTimeMilli()) {
            // dateStr is in past - historical data
            endTimeSecs = startTimeSecs + offsetIntervalHistorical;
            if (endTimeSecs > duc.getTimeSecs()) {
                endTimeSecs = duc.getTimeSecs();
            }
            retval = 0;
        }

        if (startTimeSecs == endTimeSecs) {
            endTimeSecs = startTimeSecs + 10;
            try {
                Thread.sleep(10000);
            } catch(Exception e) {}
        }

        updateOffsetCache();
        return retval;
    }

    private static void updateOffsetCache() {
        JSONObject ofrec = new JSONObject();
        ofrec.put("startTimeSecs", startTimeSecs);
        ofrec.put("endTimeSecs", endTimeSecs);
        ofrec.put("month", month);
        ofrec.put("year", year);
        ofrec.put("status", status);
        ofrec.put("processed", processedCount.get());
        ofrec.put("uploaded", uploadedCount.get());
        ofrec.put("rate", rate.get());
        dataOffsetRecord.update(offsetId, ofrec.toString());

        String offsetRecordStr = dataOffsetRecord.get(offsetId);
        logger.info("OffsetRecord: " + offsetRecordStr);
    }
}

