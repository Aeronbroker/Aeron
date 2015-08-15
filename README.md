IoT Broker
===========

IoT Broker is the FIWARE reference implementation by the IoT Broker Generic Enabler by NEC. The source code of this implementation is published via the GitHub repository Aeronbroker/Aeron.

The IoT Broker is an Internet-of-Things middleware based on the FIWARE NGSI standard. It is provided by NEC Laboratories Europe as part of their contribution to the European Future Internet Platform FIWARE.

IoT Broker Documentation
---

| Document                    | Reference                                                                                                        |
| --------------------------- | ---------------------------------------------------------------------------------------------------------------- |
| FIWARE wiki                 | https://forge.fi-ware.org/plugins/mediawiki/wiki/fiware/index.php/Main_Page                                      |
| IoT Broker GE specification | https://forge.fi-ware.org/plugins/mediawiki/wiki/fiware/index.php/FIWARE.OpenSpecification.IoT.Backend.IoTBroker |
| FIWARE NGSI API specification | https://forge.fi-ware.org/plugins/mediawiki/wiki/fiware/index.php/FI-WARE_NGSI_Open_RESTful_API_Specification    |
| IoT Broker in FIWARE catalogue   | http://catalogue.fiware.org/enablers/iot-broker                                                             |


What you get
---

IoT Broker is an implementation of the IoT Broker Generic Enabler from FIWARE (http://catalogue.fiware.org/enablers/iot-broker). 
It is specified as a lightweight and scalable middleware component that separates IoT applications from the underlying device installations. 
This implementation satisfies all properties described in the specification of the FIWARE Generic Enabler (https://forge.fi-ware.org/plugins/mediawiki/wiki/fiware/index.php/FIWARE.OpenSpecification.IoT.Backend.IoTBroker).

The IoT Broker has unique properties that you will not find in other IoT Platforms:

* While IoT Broker decouples applications from underlying IoT device installations, it achieves this going far beyond the 
common publish/subscribe paradigm. Instead, the IoT Broker actively communicates simultaneously with multiple IoT gateways 
and devices in order to obtain exactly the information that is required by the running IoT applications. 
As a result, information only is generated and exchanged when needed. This is in contrast to the state-of-the-art 
middleware components where any piece of information - whether needed or not - is just ''dumped'' inside a central 
repository.
* The IoT Broker has the ability to automatically translate information to the right abstraction level and therefore closes the 
gap between information-centric applications and device-centric IoT installations. 
For example, a simple device can typically only deliver values without being aware of the meaning of these values 
in the application's context. On the other hand, IoT applications have to be written without consideration of the device 
installations in order to be applicable in more than one specific environment. This gap is closed by IoT Broker by the use 
of so-called associations between device-level and thing-level information.
* The IoT Broker is based on the simple and powerful information model standardized in OMA Next Generation Service Interface 
Context Enabler (FIWARE NGSI). This API has emerged in FIWARE as an important open standard of information 
exchange, implemented by a considerable number of FIWARE GEs. In FIWARE NGSI, all objects of the real world, 
being it sensor/actuator devices or arbitrary objects (like tables, rooms, cars, ...) are represented as so-called 
Context Entities, while information about these objects is expressed in the form of attributes. For more information 
about the FIWARE NGSI information model and the related interfaces, please refer to the Open API Specification (https://forge.fi-ware.org/plugins/mediawiki/wiki/fiware/index.php/FI-WARE_NGSI_Open_RESTful_API_Specification).

Why you should get it 
---

The main features of IoT Broker are:

* Offering a single point of contact to the user, hiding the complexity of the multi-provider nature of the 
  Internet of Things.
* Collecting and aggregating information about thousands of real-world objects on behalf of the user.
* Provide means to assemble lower-level device information (device-centric access) into higher-level thing information 
  (information-centric access).
  


Directory Structure
------------------------
.

├── iotplatform.couchdb				     Optional bundle for
                                         connection with 
										 couchDB

├── iotbroker.builder                    Maven builder

├── iotbroker.client                     HTTP client

├── iotbroker.commons                    Commons package for 
                                         basic functionalities

├── iotbroker.core                       Functional core

├── iotbroker.resultfilter               Optional bundle for 
                                         filtering results

├── iotbroker.restcontroller             HTTP REST interface

├── iotbroker.storage                    Connector to internal 	  	
									     database

├── ngsi.api                             Implementation of 
                                         FIWARE NGSI API
										 
├── fiwareRelease                        Configuration folder
										 used by IoT Broker
										 bundles during 
										 runtime

├── IoTbrokerParent                      Maven parent project

├── lib								     Folder contains OSGI
										 bundles needed by
										 IoT Broker to run

├── SQL_database                         HSQLDB folder containing
										 the internal IoT Broker
										 database files.

└── tomcat-configuration-fragment 		 Tomcat server configuration


Building IoT Broker Source Code
---

IoT Broker requires the following software to be installed
for being built:

* JAVA JDK 7 or OpenJDK 7
* MAVEN 3

The basic procedure for compiling the IoT Broker is the following:

Navigate into the IoTBrokerParent folder and compile the pom file as follows:

```
mvn install
```

* The next step is to build the IoT Broker. Navigate into the eu.neclab.iotplatform.iotbroker.builder folder and use Maven for compiling the pom file:

```
mvn install
```

  This command will generate 9 OSGI bundles inside the eu.neclab.iotplatform.iotbroker.builder\target folder:
  
  (Note that the version numbers might differ from what is
  written in this document.)

```
iotbroker.client-4.3.3.jar
iotbroker.commons-4.3.3.jar
iotbroker.core-4.3.3.jar
iotbroker.couchdb-4.3.3.jar
iotbroker.ext.resultfilter-4.3.3.jar
iotbroker.restcontroller-4.3.3.jar
iotbroker.storage-4.3.3.jar
ngsi.api-4.3.3.jar
tomcat-configuration-fragment-4.3.3.jar
```

How to Use the IoT Broker Bundles
---

The Iot Broker bundles are OSGI based and can be used with arbitrary OSGI frameworks like EQUINOX, FELIX, etc.
The IoT Broker OSGI bundles have been tested with the EQUINOX and the FELIX framework.
IoT Broker requires several VM arguments for the runtime that need to be specified (e.g. in Equinox modify the config.ini file):

* dir.config=/user/home (parent directory of the fiwareRelease folder)
* bundles.configuration.location=.//fiwareRelease//configuration//configadmin (Location of the
logger configuration file)
* ngsiclient.layer=connector (Needed for binding the right http bundle)
* hsqldb.directory=.//SQL_database/database (Location of the HSQLDB database)
* tomcat.init.port=8090 (the port the IoT Broker REST Interface will be listening to)

In addition to that, the fiwareRelease folder needs to be copied in the user/home directory (or to the folder specified by dir.config).

An example of an OSGI configuration using the EQUINOX framework (e.g. config.ini) is shown below. For a working example configuration please also see the latest binary release to be found in http://catalogue.fiware.org/enablers/iot-broker.

One of the most simple ways to compile and run your custom version of IoT Broker is to (a) do the modifications of the source code as desired (b) compile the IoT Broker OSGi bundles, (c) replace in a recent binary release of IoT Broker (see http://catalogue.fiware.org/enablers/iot-broker) the IoT Broker bundles by your new custom bundles.

```
##############################
# Equinox settings
##############################
eclipse.ignoreApp=true
osgi.clean=true
osgi.noShutdown=true
osgi.bundles.defaultStartLevel=4
osgi.java.profile=java6-server.profile
osgi.java.profile.bootdelegation=override
# PaxLogging configuration folder (absolute path)
bundles.configuration.location=//root//IoTBroker_4.3.3//fiwareRelease//iotbrokerconfig//bundleConfigurations
# IoT Broker Server port
tomcat.init.port=80
# Internal Database folder
hsqldb.directory=.//SQL_database//database//linkDB
# Internal Database port
hsqldb.port=9001
# Enable the Database Logs
hsqldb.silent=false
# Absolute path to the config folder
dir.config=//root//IoTBroker_4.3.3//fiwareRelease//
ngsiclient.layer=connector 
java.awt.headless=true 
file.encoding=UTF-8
-server -Xms2048m -Xmx2048m
-XX:NewSize=1024m -XX:MaxNewSize=1024m -XX:PermSize=1024m
-XX:MaxPermSize=1024m -XX:+DisableExplicitGC
##############################
# Client bundles to install
##############################
osgi.bundles= plugins/equinox/org.eclipse.core.contenttype-3.4.100.v20100505-1235.jar@start, \
plugins/equinox/org.eclipse.equinox.common-3.6.0.v20110506.jar@2:start, \
plugins/equinox/org.eclipse.core.jobs-3.5.0.v20100515.jar@start, \
plugins/equinox/org.eclipse.equinox.app-1.3.0.v20100512.jar@start, \
plugins/equinox/org.eclipse.equinox.preferences-3.3.0.v20100503.jar@start, \
plugins/equinox/org.eclipse.equinox.registry-3.5.0.v20100503.jar@start, \
plugins/equinox/org.eclipse.osgi.services-3.2.100.v20100503.jar@start, \
plugins/equinox/org.eclipse.equinox.cm_3.2.0.v20070116.jar@1:start, \
plugins/pax/pax-confman-propsloader-0.2.2.jar@2:start, \
plugins/pax/pax-logging-api-1.7.0-20120710.130402-38.jar@2:start, \
plugins/pax/pax-logging-service-1.7.0-20120710.130445-38.jar@2:start, \
plugins/bundles/com.springsource.javax.activation-1.1.1.jar@start, \
plugins/bundles/javax.persistence-2.0.0.jar@start, \
plugins/bundles/httpclient-4.2.0-osgi.jar@start, \
plugins/bundles/httpcore-4.2.0-osgi.jar@start, \
plugins/bundles/com.springsource.org.apache.commons.io-1.4.0.jar, \
plugins/bundles/com.springsource.org.apache.commons.codec-1.6.0.jar@start, \
plugins/bundles/com.springsource.javax.annotation-1.0.0.jar@start, \
plugins/bundles/com.springsource.javax.ejb-3.0.0.jar@start, \
plugins/bundles/com.springsource.javax.el-1.0.0.jar@start, \
plugins/bundles/com.springsource.javax.mail-1.4.0.jar@start, \
plugins/bundles/com.springsource.javax.persistence-1.0.0.jar@start, \
plugins/bundles/com.springsource.javax.servlet.jsp.jstl-1.1.2.jar@start, \
plugins/bundles/com.springsource.javax.servlet.jsp-2.1.0.jar@start, \
plugins/bundles/com.springsource.javax.servlet-2.5.0.jar@start, \
plugins/bundles/com.springsource.javax.xml.bind-2.0.0.jar@start, \
plugins/bundles/com.springsource.javax.xml.stream-1.0.1.jar@start, \
plugins/bundles/com.springsource.javax.xml.rpc-1.1.0.jar@start, \
plugins/bundles/com.springsource.javax.xml.soap-1.3.0.jar@start, \
plugins/bundles/com.springsource.javax.xml.ws-2.1.1.jar@start, \
plugins/bundles/com.springsource.org.aopalliance-1.0.0.jar@start, \
plugins/bundles/com.springsource.org.apache.catalina-6.0.18.jar@start, \
plugins/bundles/com.springsource.org.apache.coyote-6.0.18.jar, \
plugins/bundles/com.springsource.org.apache.el-6.0.18.jar@start, \
plugins/bundles/com.springsource.org.apache.juli.extras-6.0.18.jar@start, \
plugins/bundles/com.springsource.org.apache.taglibs.standard-1.1.2.jar@start, \
plugins/bundles/catalina.start.osgi-1.0.0.jar@start, \
plugins/bundles/jasper.osgi-5.5.23-SNAPSHOT.jar@start, \
plugins/bundles/catalina-config-3.5.1.jar, \
plugins/jaxb/jaxb-impl-2.1.5_1.0.0.jar@start, \
plugins/db/hsqldb_1.0.0.jar@start, \
plugins/json/org.json_1.0.0.jar@start, \
plugins/json/gson-2.2.2.jar@start, \
plugins/json/jackson-core-asl-1.9.2.jar@start, \
plugins/json/jackson-mapper-asl-1.9.2.jar@start, \
plugins/spring 3.2.3/org.springframework.aop-3.2.3.RELEASE.jar@start, \
plugins/spring 3.2.3/org.springframework.aspects-3.2.3.RELEASE.jar@start, \
plugins/spring 3.2.3/org.springframework.beans-3.2.3.RELEASE.jar@start, \
plugins/spring 3.2.3/org.springframework.context.support-3.2.3.RELEASE.jar@start, \
plugins/spring 3.2.3/org.springframework.context-3.2.3.RELEASE.jar@start, \
plugins/spring 3.2.3/org.springframework.core-3.2.3.RELEASE.jar@start, \
plugins/spring 3.2.3/org.springframework.expression-3.2.3.RELEASE.jar@start, \
plugins/spring 3.2.3/org.springframework.jdbc-3.2.3.RELEASE.jar@start, \
plugins/spring 3.2.3/org.springframework.orm-3.2.3.RELEASE.jar@start, \
plugins/spring 3.2.3/org.springframework.oxm-3.2.3.RELEASE.jar@start, \
plugins/spring 3.2.3/org.springframework.transaction-3.2.3.RELEASE.jar@start, \
plugins/spring 3.2.3/org.springframework.web.servlet-3.2.3.RELEASE.jar@start, \
plugins/spring 3.2.3/org.springframework.web-3.2.3.RELEASE.jar@start, \
plugins/spring 3.2.3/org.springframework.security.config-3.1.4.RELEASE.jar@start, \
plugins/spring 3.2.3/org.springframework.security.core-3.1.4.RELEASE.jar@start, \
plugins/spring 3.2.3/org.springframework.security.web-3.1.4.RELEASE.jar@start, \
plugins/spring DM/spring-osgi-annotation-2.0.0.M1.jar@start, \
plugins/spring DM/spring-osgi-core-2.0.0.M1.jar@start, \
plugins/spring DM/spring-osgi-extender-2.0.0.M1.jar@start, \
plugins/spring DM/spring-osgi-io-2.0.0.M1.jar@start, \
plugins/spring DM/spring-osgi-web-2.0.0.M1.jar@start, \
plugins/spring DM/spring-osgi-web-extender-2.0.0.M1.jar@start, \
plugins/monitor/javamelodybundle_1.0.0.jar@start, \
plugins/monitor/jrobin_1.5.9.1.jar@start, \
plugins/bundles/guava-18.0.jar@start, \
plugins/broker/iotbroker.commons-4.3.3.jar@start, \
plugins/broker/iotbroker.storage-4.3.3.jar@start, \
plugins/broker/iotbroker.client-4.3.3.jar@start, \
plugins/broker/iotbroker.core-4.3.3.jar@start, \
plugins/broker/iotbroker.restcontroller-4.3.3.jar@start, \
plugins/broker/ngsi.api-4.3.3.jar@start, \
plugins/broker/iotbroker.ext.resultfilter-4.3.3.jar@start, \
plugins/broker/tomcat-configuration-fragment-4.3.3.jar, \
plugins/broker/iotbroker.couchdb-4.3.3.jar@start
```

Installing and Using the IoT Broker
---

Minimum System Requirements:
* Processor: 1 CPU 1.2 GHZ
* RAM: 1 GB
* DISK Space:50 MB
* JAVA : Java 7
* Operating System: 32 or 64-bit version Windows or Linux

The administration and programming manuals for the IoT Broker can be found on the FIWARE Catalogue page,
under the "Documentation" tab.

User and Programmers Guide: https://forge.fi-ware.org/plugins/mediawiki/wiki/fiware/index.php/IoT_Broker_-_User_and_Programmers_Guide

Installation and Administration Guide: https://forge.fi-ware.org/plugins/mediawiki/wiki/fiware/index.php/IoT_Broker_-_Installation_and_Administration_Guide

Pre-compiled and configured binaries: 
http://catalogue.fiware.org/enablers/iot-broker


Bugs & Questions
---
Please contact salvatore.longo@neclab.eu or tobias.jacobs@neclab.eu.
