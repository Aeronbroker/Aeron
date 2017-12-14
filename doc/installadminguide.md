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

For installing the IoT Broker are two possibilities: from docker or as Java application.

Deployment of IoT Broker Docker Image and its configuration
---

See [here](https://github.com/Aeronbroker/Aeron/blob/master/doc/docker.md)

Building as Java application
---

See [here](https://github.com/Aeronbroker/Aeron/blob/master/doc/compilationandconfiguration.md)

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

To verify that the IoT Broker is running, access the URL of its hosting machine on the IoT Broker port (default:8060) using a web browser; e.g. *localhost:8060* on the hosting machine. If the IoT Broker home page is not shown, it means that the IoT Broker does not run correctly.
Another way to see wether the IoT Broker is correctly running it is possible to retrieve the version of the IoT Broker by requesting the `sanityCheck` resource:

```
curl http://{IoTBroker_IP}:{IoTBroker_Port}/sanityCheck
```

The response will lok like the following:

```
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<sanityCheck>
  <name>IoT Broker GE</name>
  <type>Sanity Check</type>
  <version>Version: 6.1.0.SNAPSHOT</version>
</sanityCheck>
```

End to End testing
--

An end-to-end test is to send a query to the FIWARE NGSI interface of IoT Broker and receive the response.

It is possible to test one of the supported NGSI-10 resources. For example let us try to send an
HTTP GET to the *contextEntity/EntityId* resource `http://{IoTBroker_IP}:{IoTBroker_Port}/ngsi10/contextEntities/Kitchen`.

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

* HTPP standard port (8060) should be accessible.
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


Historical Database: CouchDB
--

The IoT Broker has a feature for historically store data into a noSQL database such CouchDB.

In order to enable the feature, check [here](https://github.com/Aeronbroker/Aeron/blob/master/doc/historyqueries.md).

Furthermore, the data can be accessed also directly from the CouchDB interface (if you have access directly to the machine or if your CouchDB has been enabled to allow any machine to access it, see [CouchDB documentation](http://docs.couchdb.org/en/master/config/http.html)).

For example, to check all the entity stored into the db you can make the following query:

```
curl 'http://localhost:5984/historicalrepository/_all_docs?startkey=%22entity%22&endkey=%22entity%C3%BF%22'
```
NOTE: change localhost with the actual IP if needed. In case of docker you can map CouchDB of the docker container to a host port.

The result will look like:

```
{"total_rows":20295,"offset":2,"rows":[
{"id":"entity__Room1~Room:::pressure","key":"entity__Room1~Room:::pressure","value":{"rev":"1-0661ad8aedb94aeab067eee62a7ff666"}},
{"id":"entity__Room1~Room:::temperature","key":"entity__Room1~Room:::temperature","value":{"rev":"1-73916d2d5f92dbda275e9eff610f8bf7"}}
]}
```

The key is in the form "*entity__ENTITYID[~ENTITYTYPE]:::ATTRIBUTENAME*". In square brackets (i.e. "[ ]") are optional fields, in capital letters the variable part.


In order to check all historical data of a specific entity, the query to make to CouchDB is:

```
curl 'http://localhost:5984/historicalrepository/_all_docs?startkey=%22obs__Room1~Room%22&endkey=%22obs__Room1~Room%C3%BF%22'
```
The result will look like:

```
{"total_rows":20295,"offset":5404,"rows":[
{"id":"obs__Room1~Room:::pressure|2017-09-15 13:45:57.352","key":"obs__Room1~Room:::pressure|2017-09-15 13:45:57.352","value":{"rev":"1-0661ad8aedb94aeab067eee62a7ff666"}},
{"id":"obs__Room1~Room:::pressure|2017-09-15 13:50:55.352","key":"obs__Room1~Room:::pressure|2017-09-15 13:50:55.352","value":{"rev":"1-0661ad8aedb94aeab067eee62a7ff666"}},
{"id":"obs__Room1~Room:::temperature|2017-09-15 13:45:57.352","key":"obs__Room1~Room:::temperature|2017-09-15 13:45:57.352","value":{"rev":"1-73916d2d5f92dbda275e9eff610f8bf7"}},
{"id":"obs__Room1~Room:::temperature|2017-09-15 13:48:22.352","key":"obs__Room1~Room:::temperature|2017-09-15 13:48:22.352","value":{"rev":"1-73916d2d5f92dbda275e9eff610f8bf7"}}
]}

```
The key is in the form "*obs__ENTITYID[~ENTITYTYPE]:::ATTRIBUTENAME|TIMESTAMP*". In square brackets (i.e. "[ ]") are optional fields, in capital letters the variable part.

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

**Please make sure port 8060 is accessible.**

Resource consumption
--

In the idle State the IoT Broker consumption is:

* Memory consumption = 20MB (idle state)
* CPU Usage = 0% (idle state)
* Thread runnning = 21 (idle state)

If the IoT Broker process uses more then 500MB and the CPU usage is above 40% in the idle state, this can be considered abnormal.

I/O flows
--
The only expected I/O flow is of type HTTP, on standard port 8060 (Tomcat Server).

Black Box Testing
--

A black box testing tool for IoT Broker installations, based on JUNIT, is provided as part of the IoT Broker release. Please see the [README](https://github.com/Aeronbroker/Aeron/blob/master/README.md#using-the-black-box-test ) for details.
	
