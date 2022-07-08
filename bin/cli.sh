#!/bin/bash
#
# IBM Maximo Application Suite - SCADA Historian Connector
#
# -------------------------------------------------------------------------
# Licensed Materials - Property of IBM
# 5737-M66, 5900-AAA
# (C) Copyright IBM Corp. 2021-2022 All Rights Reserved.
# US Government Users Restricted Rights - Use, duplication, or disclosure
# restricted by GSA ADP Schedule Contract with IBM Corp.
# -------------------------------------------------------------------------
#

MAS_SHC_HOME="$HOME/ibm/masshc"
export MAS_SHC_HOME
MAS_SHC_BIN="${MAS_SHC_HOME}/bin"
export MAS_SHC_BIN
MAS_SHC_LIB="${MAS_SHC_HOME}/lib"
export MAS_SHC_LIB

IBM_SCADA_CONNECTOR_INSTALL_DIR="${MAS_SHC_HOME}"
export IBM_SCADA_CONNECTOR_INSTALL_DIR
IBM_SCADA_CONNECTOR_DATA_DIR="${MAS_SHC_HOME}"
export IBM_SCADA_CONNECTOR_DATA_DIR

CP="${MAS_SHC_HOME}/jre/lib/*:${MAS_SHC_HOME}/lib/*"
echo ${CP}

${MAS_SHC_HOME}/jre/bin/java -classpath "${CP}" com.ibm.mas.scada.historian.connector.CLIClient "$@"

