#!/bin/bash
#
# IBM Maximo Application Suite - SCADA Historian Connector
#
# -------------------------------------------------------------------------
# Licensed Materials - Property of IBM
# 5737-M66, 5900-AAA
# (C) Copyright IBM Corp. 2021 All Rights Reserved.
# US Government Users Restricted Rights - Use, duplication, or disclosure
# restricted by GSA ADP Schedule Contract with IBM Corp.
# -------------------------------------------------------------------------
#

MAS_SHC_HOME="$HOME/ibm/masshc"
export MAS_SHC_HOME
DI_BIN="${MAS_SHC_HOME}/bin"
export DI_BIN
DI_LIB="${MAS_SHC_HOME}/lib"
export DI_LIB

CP="${MAS_SHC_HOME}/jre/lib/*:${MAS_SHC_HOME}/lib/*"
echo ${CP}

${MAS_SHC_HOME}/jre/bin/java -classpath "${CP}" com.ibm.mas.scada.historian.connector.Connector

