# Overview

IBM MAS SCADA Historian connector application performs the following tasks:

- Registers device types, physical and logical interfaces, device id, and dimensions from user defined tag mapping rules
 and asset CSV file.
- Extracts tag data from SCADA historian.
- Transforms tag data into MQTT events and send events to MAS Monitor. The device events can be used by IBM MAS Monitor for visualizatiion and AI-driven analytics. 

## High-Level Architecture

![MASHistorianConnector](overview.png)

1. OSISOft data historian (PI Data Archive) stores sensor data collected from PLCs.
2. MAS SCADA Historian connector extracts PI Point data from PI Data Archive.
3. MAS SCADA Historian connector sends PI Point data to IBM MAS Monitor using MQTT protocol.
4. MAS Monitor is used for PI Point data visualization and AI-driven analytics.

