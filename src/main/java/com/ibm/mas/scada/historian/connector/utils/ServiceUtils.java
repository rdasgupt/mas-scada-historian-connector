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

import java.util.Set;
import java.util.Iterator;
import java.util.List;
import java.util.Arrays;

import com.ibm.mas.scada.historian.connector.configurator.TagData;
import com.ibm.mas.scada.historian.connector.configurator.TagDataCache;

public class ServiceUtils {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;

    public ServiceUtils() {
    }

    public void listTags(TagDataCache tc) {
        Set<String> tagList = tc.getTagList();
        Iterator<String> it = tagList.iterator();
        while (it.hasNext()) {
            String id = it.next();
            TagData td = tc.get(id);
            if (td == null) {
                System.out.println("Device data is null in cache. Id=" + id);
                continue;
            }
            System.out.println("TagId: " + td.getDeviceId() + " TagPath: " + td.getTagPath());
        }
    }

    public static boolean isJUnitEnv() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        List<StackTraceElement> list = Arrays.asList(stackTrace);
        for (StackTraceElement element : list) {
            if (element.getClassName().startsWith("org.junit.")) {
                return true;
            }
        }
        return false;
    }
}

