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

import java.lang.Math;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Date Utility.
 */
public class DateUtil {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;

    private static Logger logger = Logger.getLogger(Constants.LOGGER_CLASS);
    private static final int SECONDS_IN_MILLI = 1000;

    private static Pattern p;
    private static Calendar cal;

    private TimeZone localTZ;
    private long timeMilli;
    private long timeSecs;
    private int  month;
    private int  year;
    private int  day;

    /**
     * DateUtil class.
     */
    public DateUtil(TimeZone localTZ) {
        if (localTZ != null) {
            this.localTZ = localTZ;
        } else {
            localTZ = TimeZone.getDefault();
        }

        this.cal = Calendar.getInstance(localTZ);
        this.p = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2}) (\\d{2}):(\\d{2}):(\\d{2})");
    }

    /**
     * Set date by date string.
     */
    public void setByDate(String dateStr) {
        timeMilli = 0;
        timeSecs = 0;
        month = 0;
        year = 0;
        day = 0;
        try {
            Matcher m = p.matcher(dateStr);
            if (m.matches()) {
                cal.set(Integer.parseInt(m.group(1)), (Integer.parseInt(m.group(2)) - 1), 
                    Integer.parseInt(m.group(3)), Integer.parseInt(m.group(4)), 
                    Integer.parseInt(m.group(5)), Integer.parseInt(m.group(6)));
                timeMilli = cal.getTimeInMillis();
                timeSecs = Math.abs(timeMilli / SECONDS_IN_MILLI);
                timeMilli = timeSecs * SECONDS_IN_MILLI;
                year = cal.get(Calendar.YEAR);
                month = cal.get(Calendar.MONTH) + 1;
                day = cal.get(Calendar.DATE);
            } else {
                logger.info("Failed to set time values. No match pattern for: " + dateStr);
            }
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            logger.info("Failed to set time values. Exception: " + e.getMessage());
        }
    }

    /**
     * Set date by milli seconds.
     */
    public void setByMilliseconds(long tmMilli) {
        timeMilli = 0;
        timeSecs = 0;
        month = 0;
        year = 0;
        day = 0;

        if (tmMilli < 0L) {
            return;
        }
        timeMilli = tmMilli;
        timeSecs = tmMilli / SECONDS_IN_MILLI;
        cal = Calendar.getInstance(localTZ);
        cal.setTimeInMillis(tmMilli);
        month = cal.get(Calendar.MONTH) + 1;
        year = cal.get(Calendar.YEAR);
        day = cal.get(Calendar.DAY_OF_MONTH);
    }

    /** Returns time is millis. */
    public long getTimeMilli() {
        return timeMilli;
    }

    /** Returns time is seconds. */
    public long getTimeSecs() {
        return timeSecs;
    }

    /** Returns month in the time. */
    public int getMonth() {
        return month;
    }

    /** Returns year in the time. */
    public int getYear() {
        return year;
    }

    /** Returns day in the time. */
    public int getDay() {
        return day;
    }
}


