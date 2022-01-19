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
        "asHost": "String",
        "schemaName": "String",
        "apiKey": "String",
        "apiToken": "String",
        "tenantId": "String"
    }
}
```

Where:

* `id`: Description to identify the connection json file
* `historian`: This configuration object is required. The configuration items specified in this object are used
to connect to SCADA historian to extract device data and send to MAS Monitor.
    ** Required Items: **
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
    * `asHost`: Specifies host to configure dimensions
    * `schemaName`: Specifies the schema to configure dimensions
    * `apiKey`: Specifies API Key.
    * `apiToken`: Specifies API Token.
    * `tenantid`: Specifies tenant id.


## Sample `connection.json` Configuration File

```
{
    "id": "Connection JSON file of ABC Corp.",
    "historian": {
        "jdbcUrl": "jdbc:pisql://10.208.72.125/Data Source=pidemo; Integrated Security=SSPI;",
        "user": "Administrator",
        "password": "xxxxxxxxxx",
        "serverTimezone": "American/Chicago",
        "startDate": "2021-12-05 05:00:00"
    },
    "iotp": {
        "url": "https://xxxxxx.internetofthings.ibmcloud.com/api/v0002",
        "orgId": "xxxxxx",
        "host": "xxxxxx.messaging.internetofthings.ibmcloud.com",
        "port": 8883,
        "asHost": "api-xxxxx.connectedproducts.internetofthings.ibmcloud.com",
        "schemaName": "BLUADMIN",
        "apiKey": "a-xxxxxx-tavok0xsxt",
        "apiToken": "cNyH_XXXXXX-p2ppVl",
        "tenantId": "xxxxx"
    }
}
```

