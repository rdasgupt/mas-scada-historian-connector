@ECHO OFF
REM
REM IBM Maximo Application Suite - SCADA Historian Connector
REM
REM -------------------------------------------------------------------------
REM Licensed Materials - Property of IBM
REM 5737-M66, 5900-AAA
REM (C) Copyright IBM Corp. 2021 All Rights Reserved.
REM US Government Users Restricted Rights - Use, duplication, or disclosure
REM restricted by GSA ADP Schedule Contract with IBM Corp.
REM -------------------------------------------------------------------------
REM
REM Invoke Powershell script to download packages and install

powershell.exe -ExecutionPolicy Bypass .\bin\install.ps1

