/*
 * IBM Confidential
 * OCO Source Materials
 * 5725-S86, 5900-A0N, 5737-M66, 5900-AAA
 * (C) Copyright IBM Corp. 2021
 * The source code for this program is not published or otherwise divested of
 * its trade secrets, irrespective of what has been deposited with the U.S.
 * Copyright Office.
 */

package com.ibm.mas.scada.historian.connector.configurator.iotdevice;

import org.json.JSONObject;
import com.ibm.mas.scada.historian.connector.utils.Copyright;
import com.ibm.mas.scada.historian.connector.utils.Constants;

public class  CreateSchemas {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;

    private CreateSchemas() {
    }

    public static JSONObject buildSchema(int scadaType, String schemaType)
    {
        JSONObject schema = new JSONObject();
        schema.put("$schema", "http://json-schema.org/draft-04/schema#");
        schema.put("type", "object");

        if (schemaType.equals("eventSchema")) {
            schema.put("title", "SCADA Historian Connector Event Schema");
        } else {
            schema.put("title", "SCADA Historian Connector Logical Interface Schema");
        }
        schema.put("description", "JSON Schema that defines the structure of tagpath metrics");

        JSONObject jsonProperties = new JSONObject();

        JSONObject tagEvtTimeProp = new JSONObject();
        tagEvtTimeProp.put("description", "Event timestamp");
        tagEvtTimeProp.put("type", "number");
        // tagEvtTimeProp.put("type", "string");
        // tagEvtTimeProp.put("format", "date-time");
        jsonProperties.put("evt_timestamp", tagEvtTimeProp);

        JSONObject tagDAProp = new JSONObject();
        tagDAProp.put("description", "Decimal Accuracy");
        tagDAProp.put("type", "string");
        jsonProperties.put("decimalAccuracy", tagDAProp);

        if (scadaType == Constants.SCADA_OSIPI) {
            JSONObject tagNameProp = new JSONObject();
            tagNameProp.put("description", "Tag Name");
            tagNameProp.put("type", "string");
            jsonProperties.put("name", tagNameProp);

            JSONObject labelProp = new JSONObject();
            labelProp.put("description", "Label");
            labelProp.put("type", "string");
            jsonProperties.put("label", labelProp);

            JSONObject valueProp = new JSONObject();
            valueProp.put("description", "Value");
            valueProp.put("type", "string");
            jsonProperties.put("value", valueProp);
        } else {
            JSONObject intvalueProp = new JSONObject();
            intvalueProp.put("description", "Int Value");
            intvalueProp.put("type", "string");
            jsonProperties.put("intvalue", intvalueProp);

            JSONObject floatvalueProp = new JSONObject();
            floatvalueProp.put("description", "Float Value");
            floatvalueProp.put("type", "string");
            jsonProperties.put("floatvalue", floatvalueProp);

            JSONObject stringvalueProp = new JSONObject();
            stringvalueProp.put("description", "String Value");
            stringvalueProp.put("type", "string");
            jsonProperties.put("stringvalue", stringvalueProp);

            JSONObject datevalueProp = new JSONObject();
            datevalueProp.put("description", "Date Value");
            datevalueProp.put("type", "string");
            jsonProperties.put("datevalue", datevalueProp);

        }

        JSONObject typeProp = new JSONObject();
        typeProp.put("description", "Type of value");
        typeProp.put("type", "string");
        jsonProperties.put("type", typeProp);

        JSONObject unitProp = new JSONObject();
        unitProp.put("description", "Unit");
        unitProp.put("type", "string");
        jsonProperties.put("unit", unitProp);

        JSONObject tagProp = new JSONObject();
        tagProp.put("description", "Tag Path");
        tagProp.put("type", "string");
        jsonProperties.put("tag", tagProp);

        schema.put("properties", jsonProperties);
        return schema;
    }
}

