# Mapping Rules Configuration

In MAS Data Lake, the device data for different device types are stored in their own table. 
The tables are named as `IOT_<deviceType>`. The configuration items that controls the transformation of
extracted data from SCADA historian for a specific device type, are defined in mapping configuration file.  
The location of this configuration file is `<InstallRoot>/volume/config/mapping.json`.

## Configuration Object

Provide data configuration object in a json formated file:
```
{
    "serviceName": "String",
    "serviceType": "String",
    "description": "String",
    "csvFileName": "String",
    "deviceTypes": [
        {
            "type": "String",
            "tagpathFilters": [
                "String"
            ]
        }
    ],
    "metrics": {
        "name": "String",
        "value": "String",
        "unit": "String",
        "type": "String",
        "decimalAccuracy": "String",
        "label": "String"
    },
    "dimensions": {
       "tagpath": "String",
       "tagid": "String",
       "site": "String",
       "categories": "String"
    }
}
```

Where:

* `serviceName`: Defines the name of the service for example PITest1
* `csvFileName`: Name of the CSV file.
* `deviceTypes`: Specifies device type and corresponding tagpath patterns.
    * `type`: Specifies device type id.
    * `tagpathFilters`: Specifies list of tagpath patterns.
* `metrics`: This object defines the mapping rule for device metrics data items.
    * `name`: Column name in CSV file used to map metric "Name" 
    * `value`: Metric value is extracted from PI Archive database at runtime. Set this to an empty string in this configuration object.
    * `unit`: Column name in CSV file used to map metric "Unit"
    * `type`: Column name in CSV file used to map metric "Type"
    * `label`: Column name in CSV file used to map metric "Label"
    * `decimalAccuracy`: Column name in CSV file used to map metric "Decimal Accuracy"
* `dimensions`: This object defines the mapping rule for device dimension data.
    ** Required items: **
    * `tagpath`: One or more column name(s) in the CSV file used to create "tag path"
    * `tagid`: Column name in the CSV file used to map "tag id"
    ** Add more dimension data mapping **
    * `site`: Column name in the CSV file used to map "site name".


## Sample CSV file (pidemo.csv) used to define mapping.json configuration file
```
Parent,Name,ObjectType,Error,UniqueID,ParentUniqueID,Description,Categories,AttributeDefaultUOM,AttributeType,AttributeValue,AttributeDataReference,AttributeDisplayDigits
DistillExample\4820Column,Accumulator Level,Attribute,,5ef1040f-b6af-59f7-352c-0442281fb1f9,9f8a3f5d-2d1b-11ec-98b3-06abac9454c4,Liquid level in reflux accumulator,Process Parameters;,%,Double,,PI Point,-5,,,
...
```

## Sample `mapping.json` Configuration File for 

```
{
    "serviceName": "Service1",
    "csvFileName": "pidemo.csv",
    "deviceTypes": [
        {
            "type": "PIDemoType",
            "tagpathFilters": [
                "DistillExample.*"
            ]
        }
    ],
    "metrics": {
        "name": "${Name}",
        "value": "",
        "unit": "${AttributeDefaultUOM}",
        "type": "${AttributeType}",
        "decimalAccuracy": "${AttributeDisplayDigits}",
        "label": "${Description}"
    },
    "dimensions": {
       "tagpath": "${Parent},${Name}",
       "tagid": "${UniqueID}",
       "site": "IBMAustin",
       "categories": "${Categories}"
    }
}
```

NOTES:

* To map a column name specify the column name within curly brackets `{}`, For example "unit": "${AttributeDefaultUOM}. Column names are case sensitive. The "unit" in pidemo.csv example will map to "lb/h".
* You can use comma-separated list of column name(s) to map a metric or dimension data. For example "${Parent},${Name}"  mapping rule is used to specify tagpath. The tagpath in this example will map to "DistillExample\4820Column\Bottoms Flow". A back-slash `\` will be used as a field-separator.
* DO not use curly brackets `{}` to specify a fixed value. For example "site": "IBMAustin".

