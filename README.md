This project is part of [FIWARE](https://www.fiware.org/).

IoT Broker
===========

IoT Broker is the FIWARE reference implementation by the IoT Broker Generic Enabler by NEC. The source code of this implementation is published via the GitHub repository Aeronbroker/Aeron.

The IoT Broker is an Internet-of-Things middleware based on the FIWARE NGSI standard. It is provided by NEC Laboratories Europe as part of their contribution to the European Future Internet Platform FIWARE.

IoT Broker References
---

| Document                    | Reference                                                                                                        | Contents |
| --------------------------- | ---------------------------------------------------- | -------------------------------------------|
| This file | README.md | How to compile and run the IoT Broker. |
| FIWARE wiki                 | https://forge.fi-ware.org/plugins/mediawiki/wiki/fiware/index.php/Main_Page                                      | Generic wiki about the FIWARE project and platform. |
| IoT Broker GE specification | https://forge.fi-ware.org/plugins/mediawiki/wiki/fiware/index.php/FIWARE.OpenSpecification.IoT.Backend.IoTBroker | Specification of IoT Broker Generic Enabler, including information about its role in the FIWARE Intenet-of-Things architecture, functionalities, and data flows.|
| IoT Broker API specification | http://aeronbroker.github.io/Aeron/    | Specification of the FIWARE NGSI-10 API exposed by the IoT Broker.  |
| FIWARE NGSI API specification | https://forge.fi-ware.org/plugins/mediawiki/wiki/fiware/index.php/FI-WARE_NGSI_Open_RESTful_API_Specification    | Specificaton of the FIWARE NGSI API, which is the interface exposed by the IoT Broker and many other FIWARE enablers.  |
|IoT Broker Installation and Administration guide| [doc/installadminguide](./doc/installadminguide) | Installation and administration guide for IoT Broker.
|IoT Broker User and Programmer Guide| https://forge.fiware.org/plugins/mediawiki/wiki/fiware/index.php/IoT_Broker_-_User_and_Programmers_Guide | Guide showing some toy interactions with the IoT Broker. |
|IoT Broker extension point documentation| [doc/extensionpoint.md](./doc/extensionpoint.md) | Documentation on how to write and use plugins for additional data source types. |
|Entity composition guide| [eu.neclab.iotplatform.entitycomposer/README.MD](./eu.neclab.iotplatform.entitycomposer/README.MD) | Documentation on how to use the IoT Broker to compose entities from other entities. |
|IoT Broker on DockerHub | https://hub.docker.com/r/fiware/iotbroker/ | DockerHub Repository for IoT Broker docker image |
| IoT Broker in FIWARE catalogue   | http://catalogue.fiware.org/enablers/iot-broker                                                             | Access to IoT Broker binaries and images. |
| NEC ConfMan   | https://github.com/Aeronbroker/NEConfMan| NEC implementation of FIWARE IoT Discovery GE. |


What you get
---

IoT Broker is an implementation of the IoT Broker Generic Enabler from FIWARE (http://catalogue.fiware.org/enablers/iot-broker). 
It is designed as a lightweight and scalable middleware component that separates IoT applications from the underlying device installations. 
This implementation satisfies all properties described in the specification of the FIWARE Generic Enabler (https://forge.fi-ware.org/plugins/mediawiki/wiki/fiware/index.php/FIWARE.OpenSpecification.IoT.Backend.IoTBroker).

The IoT Broker has unique properties that you will not find in other IoT Platforms:

* While IoT Broker decouples applications from underlying IoT device installations, it achieves this going far beyond the 
common publish/subscribe paradigm. Instead, the IoT Broker actively communicates simultaneously with multiple IoT gateways 
and devices in order to obtain exactly the information that is required by the running IoT applications. 
As a result, information only is generated and exchanged when needed. This is in contrast to the state-of-the-art 
middleware components where any piece of information - whether needed or not - is stored inside a central 
repository (which is still available as an optional component of the IoT Broker).
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

Most of the below sub-directories are containing source code for OSGi bundles the IoT Broker 
runs with. Some of these bundles are required for a working installation of IoT Broker; others
are implementing optional features and do not need to be deployed when these features are not 
needed. In addition, this repository also contains an OSGi runtime environment and pre-configured
startup script for the IoT Broker, as well as a blackbox test utility for testing a running IoT Broker
instance.

|Directory            | Contents|
|---------------------|---------|
| ├── eu.neclab.iotplatform.couchdb		|	 Optional bundle for connection with a couchDB database for storing and retrieving context information.|										
| ├── eu.neclab.iotplatform.entitycomposer |	Optional bundle for connection with a couchDB database for storing and retrieving context information. |
|├── eu.neclab.iotplatform.iotbroker.builder | Maven builder project compiling all required and optional IoT Broker OSGi bundles.|
|├── eu.neclab.iotplatform.iotbroker.client | Required bundle for the HTTP client used by IoT Broker. |
|├── eu.neclab.iotplatform.iotbroker.commons | Required bundle for basic IoT Broker functionalities. |
|├── eu.neclab.iotplatform.iotbroker.core | Required bundle containing the functional core of the IoT Broker. |
|├── eu.neclab.iotplatform.iotbroker.ext.resultfilter |Optional bundle for filtering results. Deploying this bundle  will effectuate that faulty query responses from IoT data sources are not forwarded to IoT applications.|
|├── eu.neclab.iotplatform.iotbroker.restcontroller | Required bundle implementing the HTTP REST interface of the IoT Broker.|
|├── eu.neclab.iotplatform.iotbroker.storage | Required bundle to setup and connect to an internal database. Note that this	database only used to store state information on data subscriptions and does not store any context data. For storing context data, please use the optional bundle eu.neclab.iotplatform.couchdb.|
|├── eu.neclab.iotplatform.ngsi.api | Required bundle containing the libraries implementing the FIWARE NGSI data model.|
|├── fiwareRelease | Configuration folder used by IoT Broker bundles at runtime. This folder is referenced by the runtime environment in the "IoTBroker-runner" folder.|			 
| ├── IoTbrokerParent |Maven parent project.|
|├── IoTBroker-runner | Pre-configured runtime environment for IoT Broker, based on the Equinox OSGi implementation.|
|├── iotplatform.iotbroker.blackboxtest | Maven project for a blackbox test based on JUNIT. This can be used to test a running instance of IoT Broker. The tests require the running instance to include some optional bundles included by this release.|
|├── lib |Folder contains some OSGI bundles needed by IoT Broker to run.|
|├── puppet |Puppet scripts for installing, starting, stopping, and uninstalling the IoT Broker.|
|├── SQL_database | HSQLDB folder containing internal IoT Broker database files.|
|├── targetPlatform| Contains the OSGi bundles needed for the IoT Broker at runtime. These bundles are pre-configured to be loaded by the runtime environment in the "IoTBroker-runner" folder.|
|└── tomcat-configuration-fragment | Required bundle for tomcat server configuration.|


Building IoT Broker Source Code
---

IoT Broker requires the following software to be installed
for being built:

* JAVA JDK 7 or OpenJDK 7
* MAVEN 3

The basic procedure for compiling the IoT Broker is the following:

**Build the parent:** Navigate into the IoTBrokerParent folder and compile the pom file as follows:

```
mvn install
```

**Build the IoT broker bundles:** Navigate into the eu.neclab.iotplatform.iotbroker.builder folder and use Maven for compiling the pom file:

```
mvn install
```

  This command will generate 10 OSGI bundles inside the *eu.neclab.iotplatform.iotbroker.builder\target* folder:
  
  (Note that the version numbers might differ from what is
  written in this document.)

```
entitycomposer-4.4.3.jar
iotbroker.client-4.4.3.jar
iotbroker.commons-4.4.3.jar
iotbroker.core-4.4.3.jar
iotbroker.couchdb-4.4.3.jar
iotbroker.ext.resultfilter-4.4.3.jar
iotbroker.restcontroller-4.4.3.jar
iotbroker.storage-4.4.3.jar
ngsi.api-4.4.3.jar
tomcat-configuration-fragment-4.4.3.jar
```

How to Use the IoT Broker Bundles in general
---

The Iot Broker bundles are OSGI based and can be used with arbitrary OSGI frameworks like EQUINOX, FELIX, etc.
The IoT Broker OSGI bundles have been tested with the EQUINOX and the FELIX framework.
IoT Broker requires several VM arguments for the runtime that need to be specified (e.g. in Equinox modify the config.ini file):

* dir.config=/user/home (parent directory of the fiwareRelease folder (absolute path needed))
* bundles.configuration.location=.//fiwareRelease//configuration//configadmin (Location of the
logger configuration file)
* ngsiclient.layer=connector (Needed for binding the right http bundle)
* hsqldb.directory=.//SQL_database/database (Location of the HSQLDB database)
* tomcat.init.port=80 (the port the IoT Broker REST Interface will be listening to)

In addition to that, the fiwareRelease folder needs to be copied e.g. into the user/home directory (or to the folder specified by dir.config).

For example of an OSGI configuration using the EQUINOX framework (e.g. config.ini), please refer to the *IoTBroker-runner* folder.

Using the runtime environment included by this repository
---

This repository contains a complete runtime environment for the IoT Broker. In the *IoTBroker-runner* folder you can find the Equinox OSGi environment together with its configuration file. The configuration files contains references the *fiwareRelease* folder, to the bundles in the *targetPlatform* folder, as well as to the IoT Broker bundles that can be found in the *eu.neclab.iotplatform.iotbroker.builder/target* folder after having successfully compiled the bundles.

To arrive at a running IoT Broker installation, the following steps need to be completed:
1. Compile the IoT Broker bundles as described above
2. Open the file *IoTBroker-runner/configuration/config.ini*. The parameter *dir.config* needs to be changed so that it points inside
the location of the *fiwareRelease* folder. For example, if this project has been cloned to C:\Aeron on a Windows machine, then the 
line has to be changed to *dir.config=C://Aeron//fiwareRelease*.
3. Run the IoT Broker by running one of the startup scripts for Windows or Unix in the *IoTBroker-runner* folder.

Using the Black Box Test
---
While individual classes of the IoT Broker are already tested during compilation with Maven, the project *iotplatform.iotbroker.blackboxtest* is a dedicated black box test for a running instance of the IoT Broker using server mocks. It is based on the JUNIT-Framework.
These tests assume that the IoT Broker is listening to port 80 and communicates with an IoT Discovery component at port 8002 on localhost. This corresponds to the default configuration.

To run the tests, navigate to the directory *iotplatform.iotbroker.blackboxtest* and compile the project by the command 

'''
mvn test
'''

The IoT Broker instance should be already running at the time of compilation. Please note that a deployment of IoT Broker including at least all bundles loaded by the pre-configured OSGi environment ('IoTBroker-runner' folder) is necessary for the tests to run successfully.

Minimum System Requirements
---

Minimum System Requirements:

* Processor: 1 CPU 1.2 GHZ
* RAM: 1 GB
* DISK Space:50 MB
* JAVA : Java 7
* Operating System: 32 or 64-bit version Windows or Linux


Bugs & Questions
---
Please contact salvatore.longo@neclab.eu or tobias.jacobs@neclab.eu.
