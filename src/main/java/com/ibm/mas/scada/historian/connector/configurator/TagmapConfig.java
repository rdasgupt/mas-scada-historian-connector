/*
 * IBM Confidential
 * OCO Source Materials
 * 5725-S86, 5900-A0N, 5737-M66, 5900-AAA
 * (C) Copyright IBM Corp. 2021
 * The source code for this program is not published or otherwise divested of
 * its trade secrets, irrespective of what has been deposited with the U.S.
 * Copyright Office.
 */

package com.ibm.mas.scada.historian.connector.configurator;

import org.json.JSONObject;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import com.ibm.mas.scada.historian.connector.utils.Constants;
import com.ibm.mas.scada.historian.connector.utils.Copyright;

public class TagmapConfig {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;
    private static Logger logger = Logger.getLogger(Constants.LOGGER_CLASS);

    private JSONObject mappingConfig;
    private String serviceName;
    private String serviceType;
    private String description;
    private String csvFileName;
    private JSONArray deviceTypes;
    private JSONObject metrics;
    private JSONObject dimensions;
    private List<TagDeviceType> tagTypes = new ArrayList<>();
    private String tagpathMap;
    private String tagidMap;
    private String sqlQueryFile;
    private boolean inited;
    private int mappingFormat;

    public TagmapConfig(JSONObject mappingConfig) {
        super();

        inited = false;
        serviceName = mappingConfig.optString("serviceName");
        description = mappingConfig.optString("description");
        csvFileName = mappingConfig.optString("csvFileName");
        serviceType = mappingConfig.optString("serviceType");
        sqlQueryFile = mappingConfig.optString("sqlQueryFile");
        if (serviceName.equals("") || csvFileName.equals("") || serviceType.equals("")) {
            throw new IllegalArgumentException ("TagmapConfig: missing required parameter(s)");
        }
        if (!serviceType.equals("osipi") && !serviceType.equals("ignition")) {
            throw new IllegalArgumentException ("TagmapConfig: Invalid serviceType");
        }
        deviceTypes = mappingConfig.optJSONArray("deviceTypes");
        metrics = mappingConfig.optJSONObject("metrics");
        dimensions = mappingConfig.optJSONObject("dimensions");
        if (deviceTypes.length() == 0 || metrics.length() == 0 || dimensions.length() == 0) {
            throw new IllegalArgumentException ("TagmapConfig: Invalid deviceTypes, metrics or dimensions");
        }
        tagpathMap = dimensions.optString("tagpath");
        tagidMap = dimensions.optString("tagid");
        if (tagpathMap.equals("") || tagidMap.equals("")) {
            throw new IllegalArgumentException ("TagmapConfig: Missing tagpath and tagid in dimensions object");
        }

        for (int i = 0; i < deviceTypes.length(); i++) {
            JSONObject deviceType = deviceTypes.getJSONObject(i);
            TagDeviceType tagDeviceType = new TagDeviceType(deviceType);
            logger.info("Adding type to TagDeviceTypes list. Id: " + tagDeviceType.getType());
            tagTypes.add(i, tagDeviceType);
        }

        mappingFormat = mappingConfig.optInt("mappingFormat", Constants.MAPPING_PIBUILDER);

        inited = true;
        logger.info("TagmapConfig is initialized.");
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getServiceType() {
        return serviceType;
    }

    public String getDescription() {
        return description;
    }

    public String getCsvFileName() {
        return csvFileName;
    }

    public String getSqlQueryFile() {
        return sqlQueryFile;
    }

    public JSONArray getDeviceTypes() {
        return deviceTypes;
    }

    public JSONObject getMetrics() {
        return metrics;
    }

    public JSONObject getDimensions() {
        return dimensions;
    }

    public String getTagpathMap() {
        return tagpathMap;
    }

    public String getTagidMap() {
        return tagidMap;
    }

    public int getMappingFormat() {
        return mappingFormat;
    }

    public boolean getInited() {
        return inited;
    }

    public List<TagDeviceType> getTagTypes() {
        return tagTypes;
    }
}

