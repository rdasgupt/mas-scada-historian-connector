@ECHO OFF
REM
REM IBM Maximo Application Suite - SCADA Historian Connector
REM
REM -------------------------------------------------------------------------
REM Licensed Materials - Property of IBM
REM 5737-M66, 5900-AAA
REM (C) Copyright IBM Corp. 2021-2022 All Rights Reserved.
REM US Government Users Restricted Rights - Use, duplication, or disclosure
REM restricted by GSA ADP Schedule Contract with IBM Corp.
REM -------------------------------------------------------------------------
REM

set MAS_SHC_HOME="C:\IBM\masshc"
set MAS_SHC_BIN=%MAS_SHC_HOME%\bin
set MAS_SHC_LIB=%MAS_SHC_HOME%\lib

set IBM_SCADA_CONNECTOR_INSTALL_DIR=%MAS_SHC_HOME%
set IBM_SCADA_CONNECTOR_DATA_DIR=%MAS_SHC_HOME%

set CP=%MAS_SHC_HOME%\jre\lib\*;%MAS_SHC_HOME%\lib\*

%MAS_SHC_HOME%\jre\bin\java -Djdk.internal.httpclient.disableHostnameVerification=true -classpath "%CP%" com.ibm.mas.scada.historian.connector.Connector

