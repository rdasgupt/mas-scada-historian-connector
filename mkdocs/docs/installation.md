# Installing MAS SCADA Historian Connector

The installation process includes downloading the installationpackage from IBM Passport Advantage, 
and completing the initial setup on an on-premise host system or in cloud. The on-premise host system
or the cloud instance must have access to SCADA Historian.

## Before you begin

Before you begin the connector installation, make sure that you complete the following tasks.

### Check if your environment meets the system requirements

<ul>
  <li> The connector is tested on the following operating environments.
    <ul><li> Windows 2016 server or higher </li>
    <li> Windows 10 </li>
    <li> Ubuntu 18.08 </li>
    <li> macOS BigSur </li></ul></li>
  <li> Memory: 8 GB </li>
  <li> Disk space: 10 GB of free disk space for install package, data and logs </li>
  <li> Java Runtime Environment: Java 11. Note that the connector installer will download and install OpenJDK 11 if it can not find Java 11 on the host system. </li>
</ul>

### MAS data connector installation steps

#### On Windows system:

You need Powershell on your Windows system. Powershell installation details: <br>
[How to install Powershell on Windows](https://docs.microsoft.com/en-us/powershell/scripting/install/installing-powershell-core-on-windows?view=powershell-7)?

Use powershell command to download install script from GitHub project, in a temprary directory.
```
Invoke-WebRequest -Uri "https://raw.githubusercontent.com/ibm-watson-iot/mas-scada-bulkingest/master/bin/install.ps1" -OutFile ".\install.ps1"
```

To configure connector tasks, lauch a Command Propmt with admin priviledges and run the following commands:
```
% powershell.exe -ExecutionPolicy Bypass .\configTask.ps1
```

### On macOS or Linux systems

Use one the following options to get the project source on your system:

1. Use a Web browser to download zip file of the GitHub project in /tmp directory. Open a shell prompt and run the follwing commands:
```
$ cd /tmp
$ unzip mas-scada-bulkingest-master.zip
```
2. Use curl command to download zip file of the GitHub project in /tmp directory
```
$ curl https://github.com/ibm-watson-iot/mas-scada-bulkingest/archive/master.zip -L -o /tmp/mas-scada-bulkingest-master.zip
$ cd /tmp
$ unzip mas-scada-bulkingest-master.zip
```
3. Use git command to clone the GitHub project
```
$ cd /tmp
$ git clone https://github.com/ibm-watson-iot/mas-scada-bulkingest
```

To install the connector, open a shell prompt, and run the install script:
```
$ cd /tmp/mas-scada-bulkingest
$ ./bin/install.sh
```

