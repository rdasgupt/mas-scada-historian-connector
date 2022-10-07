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

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.Logger;
import java.util.UUID;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.json.JSONObject;
import com.ibm.mas.scada.historian.connector.utils.Constants;
import com.ibm.mas.scada.historian.connector.utils.Copyright;

public class TagBuilder {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;
    private static Logger logger;

    private static Config config;
    private static String configDir;
    private static String dataDir;
    private static String serviceName;
    private static String csvFileName;
    private static JSONObject metrics;
    private static JSONObject dimensions;
    private static String tagpathMap;
    private static String tagidMap;
    private static List<TagDeviceType> tagTypes;
    private static TagmapConfig tmc;
    private static TagDataCache tc;
    private static Pattern mapPattern = Pattern.compile("\\{([^}]*?)\\}");
    private static int mappingFormat;
    private static int scadaType;
    private static String clientSite;

    public TagBuilder (Config config, Cache cache) throws Exception {
        super();
        if (config == null || cache == null) {
            throw new IllegalArgumentException ("Specified parameter is null.");
        }
        this.config = config;
        this.logger = config.getLogger();
        this.configDir = config.getConfigDir();
        this.dataDir = config.getDataDir();
        this.scadaType = config.getScadaType();

        JSONObject mappingConfig = config.getMappingConfig();
        this.tmc = new TagmapConfig(mappingConfig);
        this.serviceName = tmc.getServiceName();
        this.csvFileName = tmc.getCsvFileName();
        this.metrics = tmc.getMetrics();
        this.dimensions = tmc.getDimensions();
        this.tagpathMap = tmc.getTagpathMap();
        this.tagidMap = tmc.getTagidMap();
        this.tagTypes = tmc.getTagTypes();
        this.mappingFormat = tmc.getMappingFormat();
        this.tc = new TagDataCache(cache);
        this.clientSite = config.getClientSite();
    }

    public void build () {
        if (mappingFormat == Constants.MAPPING_PIBUILDER) {
            build_forPibuilderCSV();
        } else {
            build_forCustomCSV();
        }
    }

    private void build_forPibuilderCSV() {
        int j;
        String[] tagpathCols = getColumnNames(2, tagpathMap);
        String[] tagidCols = getColumnNames(1, tagidMap);
        String[] metricNames = metrics.getNames(metrics);
        String[] dimensionNames = dimensions.getNames(dimensions);
        String[] metricCSVColNames = new String[metricNames.length];
        String[] dimensionCSVColNames = new String[dimensionNames.length];

        for (j = 0; j < metricNames.length; j++) {
            String metricValue = metrics.getString(metricNames[j]);
            metricCSVColNames[j] = getColumnName(metricValue);
        }
            
        for (j = 0; j < dimensionNames.length; j++) {
            String dimensionValue = dimensions.getString(dimensionNames[j]);
            dimensionCSVColNames[j] = getColumnName(dimensionValue);
        }
            
        try {
            /* For each tagType in tagTypes list:
             *  - extract tagpath column data from CSV
             *  - check if tagpath belongs to this tagType
             *  - build tagData and add to tagpath and tagid cache
             */
            for (int i = 0; i < tagTypes.size(); i++) {
                TagDeviceType tagType = tagTypes.get(i);
                String type = tagType.getType();
                logger.info("Parse CSV file to build Tags for DeviceType: " + type);
                int total = 0, processed = 0;
            
                String csvFilePath = configDir + "/" + csvFileName;
                Reader reader = Files.newBufferedReader(Paths.get(csvFilePath), StandardCharsets.ISO_8859_1);
                CSVFormat format = CSVFormat.Builder.create(CSVFormat.DEFAULT)
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setAllowMissingColumnNames(true)
                    .setIgnoreEmptyLines(true)
                    .build();

                CSVParser csvParser = new CSVParser(reader, format);

                TagData tagData = null;
                String tagpath;
                String tagid;
                JSONObject metricData = null;
                JSONObject dimensionData = null;

                for (CSVRecord csvRecord : csvParser) {
                    total += 1;

                    String objectType = "";
                    if (scadaType == Constants.SCADA_OSIPI) {
                        objectType = csvRecord.get("ObjectType");
                    }

                    /* only process Attributes */
                    if ((scadaType == Constants.SCADA_OSIPI && objectType.equals("Attribute")) ||
                        (scadaType == Constants.SCADA_IGNITION)) {

                        tagpath = getValueFromCsvRecord(tagpathCols, true, csvRecord, tagpathMap).trim();
                        if (!tagType.verifyTagpath(tagpath)) {
                            continue;
                        }

                        String attrDataRef = csvRecord.get("AttributeDataReference");
                        if (attrDataRef.equals("PI Point")) {
                            tagData = new TagData();
                            tagData.setServiceName(serviceName);
                            tagData.setTagPath(tagpath);
                            tagData.setDeviceType(type);

                            /* add metrics */
                            metricData = new JSONObject();
                            for (i = 0; i < metricNames.length; i++) {
                                String csvColName = metricCSVColNames[i];
                                if (csvColName == null) {
                                    metricData.put(metricNames[i], metrics.getString(metricNames[i]));
                                } else {
                                    metricData.put(metricNames[i], csvRecord.get(csvColName));
                                } 
                            }

                            /* add dimensions */
                            dimensionData = new JSONObject();
                            dimensionData.put("tagpath", tagpath);
                            for (i = 2; i < dimensionNames.length; i++) {
                                String csvColName = dimensionCSVColNames[i];
                                if (csvColName == null) {
                                    dimensionData.put(dimensionNames[i], dimensions.getString(dimensionNames[i]));
                                } else {
                                    dimensionData.put(dimensionNames[i], csvRecord.get(csvColName));
                                } 
                            }
                            continue;

                        } else if (attrDataRef.equals("String Builder")) {
                            if (tagData != null) {
                                String tmpTagpath = tagData.getTagPath();
                                if (tagpath.contains(tmpTagpath)) {
                                    tagpath = tagpath.replaceAll("\\|Tagname", "");
                                    tagid = getValueFromCsvRecord(tagidCols, false, csvRecord, tagidMap).trim();

                                    /* no need to set cache if exist */
                                    TagData td = tc.get(tagid);
                                    if (td != null) {
                                        continue;
                                    }

                                    tagData.setDeviceId(tagid);
                                    String metricDataString = metricData.toString();
                                    tagData.setMetrics(metricDataString);
                                    dimensionData.put("tagid", tagid);
                                    dimensionData.put("tagpath", tagpath);
                                    String dimensionDataString = dimensionData.toString();
                                    tagData.setDimensions(dimensionDataString);

                                    /* add Tag data in Tagid cache */
                                    logger.info(String.format("New TagData: type=%s id=%s path=%s", type,tagid,tagpath));
                                    tc.put(tagid, tagData);
                                    processed += 1;
                                    tagData = null;
                                }
                            }
                        }
                    }
                }
                logger.info(String.format("TagInfo. DeviceType=%s TotalElements=%d Tags=%d", type, total, processed));
                csvParser.close();
            }
        } catch (IOException ioe) {
            logger.info("Failed to build tag cache. Exception: " + ioe.getMessage());
        }
    }

    private void build_forCustomCSV () {
        int j;

        String[] tagpathCols = getColumnNames(1, tagpathMap);
        String[] tagidCols = getColumnNames(1, tagidMap);
        String[] metricNames = metrics.getNames(metrics);
        String[] dimensionNames = dimensions.getNames(dimensions);
        String[] metricCSVColNames = new String[metricNames.length];
        String[] dimensionCSVColNames = new String[dimensionNames.length];

        for (j = 0; j < metricNames.length; j++) {
            String metricValue = metrics.getString(metricNames[j]);
            metricCSVColNames[j] = getColumnName(metricValue);
        }
            
        for (j = 0; j < dimensionNames.length; j++) {
            String dimensionValue = dimensions.getString(dimensionNames[j]);
            dimensionCSVColNames[j] = getColumnName(dimensionValue);
        }
            
        try {
            /* For each tagType in tagTypes list:
             *  - extract tagpath column data from CSV
             *  - check if tagpath belongs to this tagType
             *  - build tagData and add to tagpath and tagid cache
             */
            for (int i = 0; i < tagTypes.size(); i++) {
                TagDeviceType tagType = tagTypes.get(i);
                String type = tagType.getType();
                logger.info("Parse CSV file to build Tags for DeviceType: " + type);
                int total = 0, processed = 0;
            
                String csvFilePath = configDir + "/" + csvFileName;
                Reader reader = Files.newBufferedReader(Paths.get(csvFilePath), StandardCharsets.ISO_8859_1);
                CSVFormat format = CSVFormat.Builder.create(CSVFormat.DEFAULT)
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .build();

                CSVParser csvParser = new CSVParser(reader, format);
                   
                for (CSVRecord csvRecord : csvParser) {
                    String tagpath;
                    String tagid;

                    total += 1;

                    String objectType = "";
                    if (scadaType == Constants.SCADA_OSIPI) {
                        objectType = csvRecord.get("ObjectType");
                    }

                    /* only process Attributes */
                    if ((scadaType == Constants.SCADA_OSIPI && objectType.equals("Attribute")) ||
                        (scadaType == Constants.SCADA_IGNITION)) {

                        tagpath = getValueFromCsvRecord(tagpathCols, true, csvRecord, tagpathMap).trim();

                        if (tagType.verifyTagpath(tagpath)) {
                            TagData td;
                            tagpath = tagpath.replaceAll("\\|Tagname", "");
                            tagid = getValueFromCsvRecord(tagidCols, false, csvRecord, tagidMap).trim();
                            String idString = clientSite + ":" + tagid + ":" + tagpath;
                            idString = idString.trim();

                            /* no need to set cache if exist */
                            if (scadaType == Constants.SCADA_IGNITION) {
                                td = tc.get(idString);
                            } else {
                                td = tc.get(tagid);
                            }
                            if (td != null) {
                                continue;
                            }

                            TagData tagData = new TagData();
                            tagData.setServiceName(serviceName);
                            tagData.setTagPath(tagpath);
                            if (scadaType == Constants.SCADA_IGNITION) {
                                String did = UUID.nameUUIDFromBytes(idString.getBytes()).toString();
                                tagData.setDeviceId(did);
                            } else {
                                tagData.setDeviceId(tagid);
                            }
                            tagData.setDeviceType(type);

                            /* add metrics */
                            JSONObject metricData = new JSONObject();
                            for (i = 0; i < metricNames.length; i++) {
                                String csvColName = metricCSVColNames[i];
                                if (csvColName == null) {
                                    metricData.put(metricNames[i], metrics.getString(metricNames[i]));
                                } else {
                                    metricData.put(metricNames[i], csvRecord.get(csvColName));
                                } 
                            }
                            String metricDataString = metricData.toString();
                            tagData.setMetrics(metricDataString);

                            /* add dimensions */
                            JSONObject dimensionData = new JSONObject();
                            for (i = 0; i < dimensionNames.length; i++) {
                                String csvColName = dimensionCSVColNames[i];
                                if (dimensionNames[i].equals("tagpath")) {
                                    dimensionData.put("tagpath", tagpath);
                                } else if (dimensionNames[i].equals("tagid")) {
                                    dimensionData.put("tagid", tagid);
                                } else {
                                    if (csvColName == null) {
                                        dimensionData.put(dimensionNames[i], dimensions.getString(dimensionNames[i]));
                                    } else {
                                        dimensionData.put(dimensionNames[i], csvRecord.get(csvColName));
                                    } 
                                }
                            }
                            String dimensionDataString = dimensionData.toString();
                            tagData.setDimensions(dimensionDataString);

                            /* add Tag data in TagData cache */
                            tc.put(idString, tagData);

                            processed += 1;

                            logger.info(String.format("Device: type=%s id=%s tagpath=%s", type, tagid, tagpath));
                        }
                    }
                }
                logger.info(String.format("TagInfo. DeviceType=%s TotalElements=%d Tags=%d", type, total, processed));
                csvParser.close();
            }
        } catch (IOException ioe) {
            logger.info("Failed to build tag cache. Exception: " + ioe.getMessage());
        }
    }


    public TagDataCache getTagDataCache() {
        return tc;
    }

    public TagmapConfig getTagmapConfig() {
        return tmc;
    }

    private String[] getColumnNames(int cols, String mapStr) {
        Matcher m = mapPattern.matcher(mapStr);
        String[] columnNames = new String[cols];
        int columnNumber = 0;
        while (m.find()) {
            columnNames[columnNumber] =  m.group(0).replace("{", "").replace("}", "");
            columnNumber += 1;
        }
        return columnNames;
    }

    private String getColumnName (String mapStr) {
        Matcher m = mapPattern.matcher(mapStr);
        while (m.find()) {
            String columnName =  m.group(0).replace("{", "").replace("}", "");
            return columnName;
        }
        return null;
    }

    private String getValueFromCsvRecord (String[] columnNames, boolean useSeparator, CSVRecord csvRecord, String dval) {
        String value = "";
        if (columnNames.length == 0) {
            return dval;
        }

        for (int j = 0; j < columnNames.length; j++) {
            String colName = columnNames[j];
            if (j == 0) {
                value = value + csvRecord.get(colName);
            } else {
                if (useSeparator) {
                    value = value + Constants.TAGPATH_SEPARATOR + csvRecord.get(colName);
                } else {
                    value = value + csvRecord.get(colName);
                }
            }
        }
        return value;
    }

}

