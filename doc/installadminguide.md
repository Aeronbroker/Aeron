Introduction
==

Welcome to the Installation and Administration Guide for the IoT Broker GE reference implementation. This page gives an overview on how the IoT Broker can be installed and how to configure it.

Minimum System Requirements
==

* Processor: 1 CPU 1.2 GHZ
* RAM:  1 GB 
* DISK Space:50 MB
* JAVA : Java 7
* Operating System: 32 or 64-bit version Windows or Linux  

IoT Broker Installation 
==

For installing the IoT Broker are two basic possibilities.

Deployment of IoT Broker Docker Image
--

The easiest way to come to a running version of the IoT Broker is to run it as a Docker Container. The image is published at DockerHub as *fiware/iotbroker*. To pull the docker container onto your local system, enter the command 

```
 docker pull fiware/iotbroker
```

and for running it the command 

```
 docker run -d -p 80:80 fiware/iotbroker
```
 
will do.

Building IoT Broker from Source
--

Get the IoT Broker GitHub project onto your machine. If git is installed, then the project can be cloned by opening a command line, navigating to the folder where IoT Broker shall be installed, and typing the command
 
```
 git clone https://github.com/Aeronbroker/Aeron.git
```

Alternatively, the project can be downloaded as a [zip file](https://github.com/Aeronbroker/Aeron/archive/master.zip).
Please follow the compilation instructions in the [README](https://github.com/Aeronbroker/Aeron/blob/master/README.md).

After having compiled the sources, the resulting jar files can either be run by the pre-configured OSGi platform included by the IoT Broker project (see [README](https://github.com/Aeronbroker/Aeron/blob/master/README.md)), or by setting up a custom OSGi environment and deploying the compiled IoT Broker bundles and their dependencies there.

IoT Broker System Configuration
==

The basic configuration of the IoT Broker is done via configuration of the OSGi runtime environment. In the pre-configured Equinox environment included by this release (*IoTBroker-runner* folder), this is done by the file */configuration/config.ini*. The most important parameters to specify here are

* The port the IoT Broker NGSI interface listens to. The default is port 80.
* The location of the *fiwareRelease* folder where further configuration information is found. As this needs to be an absolute path, setting this parameter is for most installations necessary in order to get the IoT Broker running.

Further configuration is done by files in the *fiwareRelease* folder. The most important configuration file is found in

* fiwareRelease/iotbrokerconfig/iotBroker/config/config.xml*. Here the user can set
* **ngsi9Uri** and **pathPreFix_ngsi9:** The URL of the Iot Discovery GE instance the IoT Broker communicates with in order to retrieve the Context Registrations it needs.
* **pathPreFix_ngsi10:** The root of the FIWARE NGSI resource tree the IoT Broker exposes.
* **pub_sub_addr:** The address of an NGSI component where updates are forwarded to (e.g. a FIWARE Context Broker GE instance).
* **X-Auth-Token:** The security token for connecting to components secured by the FIWARE access control mechanisms.

Please note that the Iot Broker needs to be restarted before any changes will take effect.

IoT Broker System Monitoring
==

Log Files
--

The IoT Broker is using the [pax logging system](https://ops4j1.jira.com/wiki/display/paxlogging/Pax+Logging).
The logs are stored in the *reportLog* folder, a subdirectory of *IoTBroker-runner*.

The logger is configured in  *fiwareRelease/iotbrokerconfig/bundleConfigurations/services* folder. An example of the *org.ops4j.pax.logging* file is shown below.

```
 log4j.rootLogger=INFO, ReportFileAppender, console
 #Console Appender 
 log4j.appender.console=org.apache.log4j.ConsoleAppender
 log4j.appender.console.layout=org.apache.log4j.PatternLayout
 log4j.appender.console.layout.ConversionPattern=%d{ISO8601} | %-5.5p | (%F:%M:%L) | %m%n
 #Solve the digerest Tomcat logger errors
 log4j.logger.org.apache.commons=WARN
 log4j.logger.org.apache.commons.beanutils=WARN
 log4j.logger.org.apache.struts=WARN
 #File Appender
 # ReportFileAppender - used to log messages in the report.log file.
 log4j.appender.ReportFileAppender=org.apache.log4j.FileAppender
 log4j.appender.ReportFileAppender.File=.//reportLog//report.log
 log4j.appender.ReportFileAppender.layout=org.apache.log4j.PatternLayout
 log4j.appender.ReportFileAppender.layout.ConversionPattern= %-4r [%t] %-5p %c %x - %m%n
```

In the example above, the log level is selected in the first line. The possible log levels are:

* log4j.rootLogger=INFO
* log4j.rootLogger=DEBUG

Monitoring Panel
--

The IoT Broker has a monitoring panel that is accessible via the "admin login" button on the index page web page. For login, the administrator should use the ADMIN credentials. These credentials are set in the configuration file *fiwareRelease/iotbrokerconfig/iotbroker/config/users.properties*. The default username and password are (ADMIN, admin). 

The Monitoring panel is accessed via an HTTPS connection based on an SSL certificate. In the *fiwareRelease/iotbrokerconfig/iotbroker/https* folder there is already a dummy key, but for security reason the admin should generate a private key containing a valid certificate. A simple way to generate one of these is to use Java's keytool utility located in the *$JAVA_HOME/bin directory*.

Example:
```
'keytool -genkey -alias admin -keyalg RSA -keystore ...\fiwareRelease\iotBroker\https\key.keystore'
```

Sanity Check Procedures
===

The Sanity Check Procedures are the steps that a System Administrator will take to verify that an installation is ready to be tested. This is therefore a preliminary set of tests to ensure that obvious or basic malfunctioning is fixed before proceeding to unit tests, integration tests and user validation.

First Sanity Check
--

To verify that the IoT Broker is running, access the URL of its hosting machine on the IoT Broker port (default:80) using a web browser; e.g. *localhost:80* on the hosting machine. If the IoT Broker home page is not shown, it means that the IoT Broker does not run correctly.

End to End testing
--

An end-to-end test is to send a query to the FIWARE NGSI interface of IoT Broker and receive the response.

It is possible to test one of the supported NGSI-10 resources. For example let us try to send an
HTTP GET to the *contextEntity/EntityId* resource `http://{IoT Broker IP}/ngsi10/contextEntities/Kitchen`.

You should get back an XML response, with an ERROR CODE, similar to this:
```
  <contextElementResponse>
 	<contextElement>
 		<entityId isPattern="false">
 			<id>Kitchen</id>
 		</entityId>
 	</contextElement>
 	<statusCode>
 		<code>500</code>
 		<reasonPhrase>RECEIVER INTERNAL ERROR</reasonPhrase>
 		<details xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 			xmlns:xs="http://www.w3.org/2001/XMLSchema" xsi:type="xs:string">Error I/O with:
 			http://localhost:8999</details>
 	</statusCode>
  </contextElementResponse>
```
or
```
  <contextElementResponse>
 	<contextElement>
 		<entityId   isPattern="false">
 			<id>Kitchen</id>
 		</entityId>
 	</contextElement>
 	<statusCode>
 		<code>404</code>
 		<reasonPhrase>CONTEXT ELEMENT NOT FOUND</reasonPhrase>
 	</statusCode>
  </contextElementResponse>
```
If you get the first error this means that there is no communication between the IoT Broker GE and an instance of the IoT Discovery GE. In case of the second error this means that the entity that you are requesting is not available. 

List of Running Processes
--

* Only JavaVM process

Network interfaces Up & Open
--

* HTPP standard port (80) should be accessible.
* HTPPS port (9443) should be accessible.

Databases
--

The IoT Broker uses an [HSQLDB](http://hsqldb.org/) database running on port 9001. This database is embedded by the IoT Broker and therefore requires no particular configuration or installation procedure (username and password can be changed in the config.xml file). For checking the status of the database it is possible to send an SQL query using the hsqldb driver on port 9001 with username *NEC* and password *neclab*.

The database logs are in the folder *SQL_database* and the file name is *database.txt*.

At startup the IoT Broker automatically starts the database instance as shown by the IoT Broker logger: 

```
  [Server@3b2d38f6]: [Thread[SpringOsgiExtenderThread-8,5,spring-osgi-extender[1c57a236]-threads]]: checkRunning(false) entered
  [Server@3b2d38f6]: [Thread[SpringOsgiExtenderThread-8,5,spring-osgi-extender[1c57a236]-threads]]: checkRunning(false) exited
  [Server@3b2d38f6]: Initiating startup sequence...
  [Server@3b2d38f6]: Server socket opened successfully in 1 ms.
  Jul 29, 2013 9:17:23 AM org.hsqldb.persist.Logger logInfoEvent
  INFO: checkpointClose start
  Jul 29, 2013 9:17:23 AM org.hsqldb.persist.Logger logInfoEvent
  INFO: checkpointClose end
  [Server@3b2d38f6]: Database [index=0, id=0, db=file:E:\SQL_database\database, alias=linkdb] opened sucessfully in 738 ms.
  [Server@3b2d38f6]: Startup sequence completed in 750 ms.
  [Server@3b2d38f6]: 2013-07-29 09:17:23.347 HSQLDB server 2.2.9 is online on port 9001
  [Server@3b2d38f6]: To close normally, connect and execute SHUTDOWN SQL
  [Server@3b2d38f6]: From command line, use [Ctrl]+[C] to abort abruptly
```
  
Diagnosis Procedures
==

The Diagnosis Procedures are the first steps that a System Administrator will take to locate the source of an error in a GE. Once the nature of the error is identified with these tests, the system admin will very often have to resort to more concrete and specific testing to pinpoint the exact point of error and a possible solution. Such specific testing is out of the scope of this section.


Resource availability
--

The minimum of RAM needed for running the IoT Broker is 256MB, however our recommandation is to have 512MB of available RAM.
IoT Broker binaries need about 30MB of hard disk space. However, as the IoT Broker will use an internal database for storing subscription information, it is recommended to have at least 100MB of space available on the disk.

Remote Service Access
--

Depending on the setup, the IoT Broker communicates via the FIWARE NGSI interface with the following FIWARE components.

* An instance of the IoT Discovery GE. This is mandatory, as otherwise the IoT Broker cannot determine which data sources are available and thus cannot deliver any data. The address of this component is configurable in the IoT Broker config files (see above).
* A default NGSI data consumer/broker where NGSI data updates are forwarded to. This is typically an instance of the Context Broker GE. However, this can be in principle any FIWARE NGSI-compliant component, and such a component is not present in all installations. The address of this component is configurable in the IoT Broker config files (see above).
* One or more NGSI-compliant data sources, e.g. instances of the Data Handling GE, or Context Broker GE instances. 

**Please make sure port 80 is accessible.**

Resource consumption
--

In the idle State the IoT Broker consumption is:
* Memory consumption = 20MB (idle state)
* CPU Usage = 0% (idle state)
* Thread runnning = 21 (idle state)

If the IoT Broker process uses more then 500MB and the CPU usage is above 40% in the idle state, this can be considered abnormal.

I/O flows
--
The only expected I/O flow is of type HTTP, on standard port 80 (Tomcat Server).

Black Box Testing
--

A black box testing tool for IoT Broker installations, based on JUNIT, is provided as part of the IoT Broker release. Please see the [README](https://github.com/Aeronbroker/Aeron/blob/master/README.md#using-the-black-box-test ) for details.
	
