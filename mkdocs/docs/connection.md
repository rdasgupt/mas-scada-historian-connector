# Connection Configuration

The `connection.json` is a JSON file that allows you to define the connection related 
configuration item to connect to SCADA historian database and MAS. The location of this 
file is `<InstallRoot>/ibm/masshc/volume/config/connection.json`. 

## Configuration Object

Provide connection configuration object in a json formated file:

```
{
    "id": "String",
    "historian": {
        "type": "String",
        "jdbcUrl": "String",
        "user": "String",
        "password": "String",
        "serverTimezone": "String",
        "startDate": "String",
        "dbType": "String",
        "schema": "String",
        "database": "String",
        "extractInterval": integer
    },
    "iotp": {
        "url": "String",
        "orgId": "String",
        "host": "String",
        "port": integer,
        "schemaName": "String",
        "tenantId": "String",
        "apiKey": "String",
        "apiToken": "String",
        "asHost": "String",
        "asKey": "String",
        "asToken": "String",
        "asAPIVersion": integer
    }
}
```

Where:

* `id`: Description to identify the connection json file
* `historian`: This configuration object is required. The configuration items specified in this object are used
to connect to SCADA historian to extract device data and send to MAS Monitor.
    ** Required Items: **
    * `type`: Historian type. The valid options are "osipi" or "ignition".
    * `jdbcUrl`: JDBC URL to connect to the historian database. Example "jdbc:pisql://10.208.72.125/Data Source=pidemo; Integrated Security=SSPI;"
    * `user`: User name to connect to historian.
    * `password`: Password to connect to historian.
    * `serverTimezone`: Timezone of historian database server. Example "American/Chicago"
    * `startDate`: Extract device data from the specified date. Valid format is "YYYY-MM-DD HH:MM:SS"
    ** Optional Items: **
    * `dbType`: Database server configured as SCADA hostorian. The default value is "pisql" (OSIPI historian)
    * `schema`: Schema name. The default value is "piarchive"
    * `database`: Database name. The default value is "picomp2"
    * `extractInterval`: Data from historian is extracted in chunk. The `extractInterval` specifies the time window in seconds for the chunk. The default value is 60 seconds. The valid range is 30 to 900 seconds.
* `iotp`: This configuration object is required. The configuration items are used to configure MAS and send device data to MAS. To configure `iotp` object, you need credentials from MAS Monitor. You can get these information using MAS Monitor dashboard.
    ** Required Items: **
    * `url`: Specifies base URL to invoke APIs to configure MAS
    * `orgId`: Specifies a six character organization Id assigned to your IoT Platform service.
    * `host`: Specifies host to connect to MAS to send device data
    * `port`: Specifies port to connect to MAS 
    * `apiKey`: Specifies API Key to configure device types, devices, interfaces and send MQTT messages.
    * `apiToken`: Specifies API Token to configure device types, devices, interfaces and send MQTT messages.
    * `schemaName`: Specifies the schema to configure dimensions
    * `tenantid`: Specifies tenant id.
    * `asHost`: Specifies host to configure dimensions
    * `asAPIVersion`: Specifies API Version. Valid options are 1 and 2.
    * `asKey`: Specifies API Key to configure dimensions
    * `asToken`: Specifies API Token to configure dimensions


## Sample `connection.json` Configuration File

```
{
    "id": "Connection JSON file of ABC Corp.",
    "historian": {
        "type": "osipi",
        "jdbcUrl": "jdbc:pisql://10.208.72.125/Data Source=pidemo; Integrated Security=SSPI;",
        "user": "Administrator",
        "password": "xxxxxxxxxx",
        "serverTimezone": "American/Chicago",
        "startDate": "2021-12-05 05:00:00"
    },
    "iotp": {
        "url": "https://tenant1.iot.monitordemo.ibmmam.com/api/v0002",
        "orgId": "tenant1",
        "host": "tenant1.messaging.iot.monitordemo.ibmmam.com",
        "port": 443,
        "schemaName": "BLUADMIN",
        "apiKey": "a-xxxxxx-tavok0xsxt",
        "apiToken": "cNyH_XXXXXX-p2ppVl",
        "tenantId": "tenant1",
        "asHost": "tenant1.api.monitor.monitordemo3.ibmmam.com",
        "asAPIVersion": 1,
        "asKey": "xxxxxxxxxxxxxxxxxx",
        "asToken": "xxxxxxxxxxxxxxxxxxxx"
    }
}
```

