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

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.json.JSONObject;
import org.json.JSONArray;
import com.ibm.mas.scada.historian.connector.utils.Copyright;

public class TagDeviceType {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;

    private String     type;
    private JSONArray  tagpathFilters;
    private JSONArray  discardFilters;
    private List<Pattern> patterns = new ArrayList<>();
    private List<Pattern> discardPatterns = new ArrayList<>();
    private String sqlQueryFile;

    public TagDeviceType(JSONObject typeConfig) throws NullPointerException, IllegalArgumentException {
        super();
        this.type = typeConfig.optString("type");
        this.tagpathFilters = typeConfig.optJSONArray("tagpathFilters");
        this.discardFilters = typeConfig.optJSONArray("discardFilters");
        if (this.type.equals("") || this.tagpathFilters.length() == 0) {
            throw new IllegalArgumentException ("Specified parameter is not specified or not valid.");
        }
        for (int i = 0; i < this.tagpathFilters.length(); i++) {
            patterns.add(i, Pattern.compile(this.tagpathFilters.getString(i)));
        }
        for (int j = 0; j < this.discardFilters.length(); j++) {
            discardPatterns.add(j, Pattern.compile(this.discardFilters.getString(j)));
        }
        this.sqlQueryFile = typeConfig.optString("sqlQueryFile");
    }

    public String getType() {
        return this.type;
    }

    public JSONArray getTagpathFilters() {
        return this.tagpathFilters;
    }

    public JSONArray getDiscardFilters() {
        return this.discardFilters;
    }

    public List<Pattern> getPatterns() {
        return this.patterns;
    }

    public List<Pattern> getDiscardPatterns() {
        return this.discardPatterns;
    }

    public String getSqlQueryFile() {
        return this.sqlQueryFile;
    }

    public boolean verifyTagpath(String tagPath) {
        if (discardFilters.length() > 0) {
            for (Pattern discardPattern: discardPatterns) {
                Matcher md = discardPattern.matcher(tagPath);
                boolean mdmatches = md.matches();
                if (mdmatches == true) {
                    return false;
                }
            }
        }
 
        for (Pattern pattern: patterns) {
            Matcher m = pattern.matcher(tagPath);
            boolean matches = m.matches();
            if (matches == true) {
                return true;
            }
        }
        return false;
    } 
}

