#
#
# IBM Maximo Application Suite - SCADA Historian Connector
#
# -------------------------------------------------------------------------
# Licensed Materials - Property of IBM
# 5737-M66, 5900-AAA
# (C) Copyright IBM Corp. 2022 All Rights Reserved.
# US Government Users Restricted Rights - Use, duplication, or disclosure
# restricted by GSA ADP Schedule Contract with IBM Corp.
# -------------------------------------------------------------------------
#
# Windows Powershell Script to configure windows tasks to start 
# IBM MAS SCADA Historian Connector application. 
#
# Prereq:
# - Download and install MAS SCADA Historian Connector application from
#   https://github.com/ibm-watson-iot/mas-scada-historian-connector
#
#   The install and data roots should be:
#
#   InstallPath = "C:\ibm\masshc"
#   DataPath = "C:\ibm\masshc"
#
# To run this script, open a Windows command propmt with Admin privilege
# and run the following commands:
#
# c:> cd c:\ibm\masshc
# c:> powershell.exe -ExecutionPolicy Bypass .\bin\taskconfig.ps1
#

$InstallPath = "C:\IBM\masshc"
$DataPath = "C:\IBM\masshc"

# Create Data dir
Write-Host "Creating Data directory $DataPath"
if(!(Test-Path $DataPath))
{
    New-Item -Path "$DataPath" -ItemType Directory
    New-Item -Path "$DataPath\volume\logs" -ItemType Directory
    New-Item -Path "$DataPath\volume\config" -ItemType Directory
    New-Item -Path "$DataPath\volume\data" -ItemType Directory
}


# Set Environment variables
[System.Environment]::SetEnvironmentVariable('IBM_SCADA_CONNECTOR_INSTALL_DIR', $InstallPath,[System.EnvironmentVariableTarget]::Machine)
[System.Environment]::SetEnvironmentVariable('IBM_SCADA_CONNECTOR_DATA_DIR', $DataPath,[System.EnvironmentVariableTarget]::Machine)

# Schedule upload tasks
$scheduleObject = New-Object -ComObject schedule.service
$scheduleObject.connect()
$rootFolder = $scheduleObject.GetFolder("\")
$rootFolder.CreateFolder("IBM")
$ibmFolder = $scheduleObject.GetFolder("\IBM")
$ibmFolder.CreateFolder("Maximo Application Suite")

# Entity data upload task
# Task config xml
$xmlentity = @"
<?xml version="1.0" encoding="UTF-16"?>
<Task version="1.4" xmlns="http://schemas.microsoft.com/windows/2004/02/mit/task">
  <RegistrationInfo>
    <Author>IBM</Author>
    <Description>Uploads device data from SCADA historian to IBM MAS for Monitoring</Description>
    <URI>\IBM\Maximo Application Suite\SCADAHistorianConnector</URI>
  </RegistrationInfo>
  <Triggers>
    <RegistrationTrigger>
      <Repetition>
        <Interval>P31D</Interval>
        <StopAtDurationEnd>false</StopAtDurationEnd>
      </Repetition>
      <Enabled>true</Enabled>
    </RegistrationTrigger>
  </Triggers>
  <Principals>
    <Principal id="Author">
      <UserId>S-1-5-18</UserId>
      <RunLevel>HighestAvailable</RunLevel>
    </Principal>
  </Principals>
  <Settings>
    <MultipleInstancesPolicy>IgnoreNew</MultipleInstancesPolicy>
    <DisallowStartIfOnBatteries>true</DisallowStartIfOnBatteries>
    <StopIfGoingOnBatteries>true</StopIfGoingOnBatteries>
    <AllowHardTerminate>true</AllowHardTerminate>
    <StartWhenAvailable>true</StartWhenAvailable>
    <RunOnlyIfNetworkAvailable>true</RunOnlyIfNetworkAvailable>
    <IdleSettings>
      <StopOnIdleEnd>true</StopOnIdleEnd>
      <RestartOnIdle>false</RestartOnIdle>
    </IdleSettings>
    <AllowStartOnDemand>true</AllowStartOnDemand>
    <Enabled>true</Enabled>
    <Hidden>false</Hidden>
    <RunOnlyIfIdle>false</RunOnlyIfIdle>
    <DisallowStartOnRemoteAppSession>false</DisallowStartOnRemoteAppSession>
    <UseUnifiedSchedulingEngine>true</UseUnifiedSchedulingEngine>
    <WakeToRun>false</WakeToRun>
    <ExecutionTimeLimit>PT0S</ExecutionTimeLimit>
    <Priority>7</Priority>
    <RestartOnFailure>
      <Interval>PT5M</Interval>
      <Count>3</Count>
    </RestartOnFailure>
  </Settings>
  <Actions Context="Author">
    <Exec>
      <Command>c:\ibm\masshc\jre\bin\java.exe</Command>
      <Arguments>-classpath "c:\ibm\masshc\jre\lib\*;c:\ibm\masshc\lib\*" com.ibm.mas.scada.historian.connector.Connector</Arguments>
      <WorkingDirectory>C:\IBM\masshc\volume\data</WorkingDirectory>
    </Exec>
  </Actions>
</Task>
"@

# Register entity data upload task
$taskName = "SCADAHistorianConnector"
$taskPath = "\IBM\Maximo Application Suite"
Register-ScheduledTask -Xml $xmlentity -TaskName $taskName -TaskPath $taskPath


