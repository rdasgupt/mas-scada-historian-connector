# IBM MAS SCADA Historian Connector

This document describes steps to install and configure **MAS SCADA Historian Connector**. 

You can install the Connector as an archive file directly on an on-premise host system or in cloud. The
host system must have access to SCADA historian to extract tag data and MAS Monitor to send tag events.
The connector application configures device type, device ID, physical and logical interfaces, and 
dimension data in IBM MAS Monitor from user defined configuration file, extracts device data from SCADA 
historian and sends the device data to MAS for visualizatiion and AI-driven analytics.

Documentation is broken down into following sections:

- [Overview](overview.md)
- [Installation](installation.md)
- [Configuration](configuration.md)
    - [Connection Configuration](connection.md)
    - [Mapping Rules Configuration](mapping.md)

