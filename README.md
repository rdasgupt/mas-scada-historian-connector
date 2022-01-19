# IBM MAS Data Ingest Connector for SCADA Historian

[![GitHub issues](https://img.shields.io/github/issues/ibm-watson-iot/mas-scada-historian-connector.svg)](https://github.com/ibm-watson-iot/mas-scada-historian-connector/issues)
[![GitHub](https://img.shields.io/github/license/ibm-watson-iot/mas-scada-historian-connector.svg)](https://github.com/ibm-watson-iot/mas-scada-historian-connector/blob/master/LICENSE)

The data ingest connector application is installed on an on-premise host system to extract tag data from SCADA historian
 and send/upload data to MAS Monitor as an MQTT event. The host system must have access to SCADA historian to extract ta
g data and MAS Monitor to configure and send events.

This project includes source to build the connector application that:

* Registers device types, physical and logical interfaces, and devices from user defined tag mapping rules and asset CSV
 file.
* Extracts tag data from SCADA historian.
* Transforms tag data into MQTT events and send events to MAS Monitor.


## Dependencies

* OpenJDK 11+
* Commons JCS
* org.json
* JDBC driver for MSSQL, MySQL and IBM DB2


## Build 

Clone the GitHub project
```
$ git clone https://github.ibm.com/Ranjan-Dasgupta/messagesight-installer
```

Build and package using gradle
```
$ cd mas-scada-historian-connector
$ gradle build
$ ./bin/package.sh
```

Build and package using maven
```
$ cd mas-scada-historian-connector
$ mvn package -DskipTests
$ ./bin/package.sh
```


## Documentation
Refer to the connector documentation for installation and configuration details:

https://ibm-watson-iot.github.io/mas-scada-historian-connector/

