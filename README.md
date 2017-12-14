This project is part of [FIWARE](https://www.fiware.org/).

[![License badge](https://img.shields.io/badge/licence-NEC-blue.svg)](https://github.com/Aeronbroker/Aeron/blob/master/LICENSE.md)
[![Documentation badge](https://img.shields.io/badge/docs-latest-brightgreen.svg?style=flat)](http://fiware-iot-broker.readthedocs.org/en/latest/)
[![Docker badge](https://img.shields.io/docker/pulls/fiware/iotbroker.svg)](https://hub.docker.com/r/fiware/iotbroker/)
[![Support badge](https://img.shields.io/badge/support-issues-yellowgreen.svg)](https://github.com/Aeronbroker/Aeron/issues)

IoT Broker
===========


* [What you get](#what-you-get)
* [Why you should get it ](#why-you-should-get-it)
* [Minimum System Requirements](#minimum-system-requirements)
* How to get IoT Broker
	* [Docker](#iot-broker-docker-file)
	* [Source Code](#building-iot-broker-source-code)
		* [Configure the IoT Broker with setup scripts](#configure-the-iot-broker-with-setup-scripts)
		* [Configure the IoT Broker manually](#configure-the-iot-broker-manually)
	* [Quick Start: Using the runtime environment included by this repository](#quick-start-using-the-runtime-environment-included-by-this-repository)
	* [IoT Broker Installation and Administration guide](#iot-broker-installation-and-administration-guide)
	* [IoT Broker User and Programmer Guide](#iot-broker-user-and-programmer-guide)
* [Testing: Using the Black Box Test](#testing-using-the-black-box-test)
* IoT Broker features
	* [Entity composition](#entity-composition)
	* [History Queries](#history-queries)
	* [NGSI Deployment Emulator](#ngsi-deployment-emulator)
	* [Semantic Grounding](#semantic-grounding)
	* [Association](#association)
* Other IoT Components
	* [NEC ConfMan](#nec-confman)
	* [NEC IoT Knowledge](#nec-iot-knowledge)
* [Directory Structure](#directory-structure)
* [IoT Broker References](#iot-broker-references)
* [Bugs & Questions](#bugs-&-questions)

IoT Broker is the FIWARE reference implementation by the IoT Broker Generic Enabler by NEC. The source code of this implementation is published via the GitHub repository Aeronbroker/Aeron.

The IoT Broker is an Internet-of-Things middleware based on the FIWARE NGSI standard. It is provided by NEC Laboratories Europe as part of their contribution to the European Future Internet Platform FIWARE.

What you get
---

See [what you get](https://github.com/Aeronbroker/Aeron/blob/master/doc/whatyouget.md)



Minimum System Requirements
---

See [Minimum System Requirements](https://github.com/Aeronbroker/Aeron/blob/master/doc/requirements.md)


IoT Broker Docker image
---

See [IoT Broker Docker image](https://github.com/Aeronbroker/Aeron/blob/master/doc/docker.md)


IoT Broker as Java application
---

See [IoT Broker as Java application](https://github.com/Aeronbroker/Aeron/blob/master/doc/compilationandconfiguration.md)


IoT Broker User and Programmer Guide
---

See: https://forge.fiware.org/plugins/mediawiki/wiki/fiware/index.php/IoT_Broker_-_User_and_Programmers_Guide

Testing: Using the Black Box Test
---
See [Black Box Test](https://github.com/Aeronbroker/Aeron/blob/master/doc/compilationandconfiguration.md)


IoT Broker extension point
---

See: https://github.com/Aeronbroker/Aeron/blob/master/doc/extensionpoint.md


Entity composition
---
See: https://github.com/Aeronbroker/Aeron/blob/master/eu.neclab.iotplatform.entitycomposer/README.MD


History Queries
---

See: https://github.com/Aeronbroker/Aeron/blob/master/doc/historyqueries.md


NGSI Deployment Emulator
---

See: https://github.com/Aeronbroker/Aeron/blob/master/eu.neclab.iotplatform.ngsiemulator/README.md


Semantic Grounding
---
See: https://github.com/Aeronbroker/Aeron/blob/master/doc/semanticgrounding.md


Association
---
See: https://forge.fiware.org/plugins/mediawiki/wiki/fiware/index.php/NGSI_association


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
| ├── doc		|	 Documentation rearding several aspects of the IoT Broker. |
| ├── docker		|	 Docker file useful for building a docker image. |
| ├── eu.neclab.iotplatform.couchdb		|	 Optional bundle for connection with a couchDB database for dumping context information in a Big Data Repository.|
| ├── eu.neclab.iotplatform.entitycomposer |	Optional bundle handling the composition of entities. |
|├── eu.neclab.iotplatform.iotbroker.builder | Maven builder project compiling all required and optional IoT Broker OSGi bundles.|
|├── eu.neclab.iotplatform.iotbroker.client | Required bundle for the HTTP client used by IoT Broker. |
|├── eu.neclab.iotplatform.iotbroker.commons | Required bundle for basic IoT Broker functionalities. |
|├── eu.neclab.iotplatform.iotbroker.core | Required bundle containing the functional core of the IoT Broker. |
|├── eu.neclab.iotplatform.iotbroker.embeddediotagent.core | Optional bundle for enabling storage and retrieve of (historical) context information. |
|├── eu.neclab.iotplatform.iotbroker.embeddediotagent.couchdb | Optional bundle for enabling the storage of the (historical) context information into CouchDB. (This bundle must be activated if eu.neclab.iotplatform.iotbroker.embeddediotagent.core is active).|
|├── eu.neclab.iotplatform.iotbroker.embeddediotagent.indexer | Optional bundle for indexing the (historical) context information (This bundle must be activated if eu.neclab.iotplatform.iotbroker.embeddediotagent.core is active).|
|├── eu.neclab.iotplatform.iotbroker.embeddediotagent.registry | Optional bundle that keeps registrations to an IoT Discovery accordingly to the ContextElement registered into the embbededagent.|
|├── eu.neclab.iotplatform.iotbroker.embeddediotagent.storage | Optional bundle for handling the serialiation of the (historical) context information(This bundle must be activated if eu.neclab.iotplatform.iotbroker.embeddediotagent.core is active).|
|├── eu.neclab.iotplatform.iotbroker.ext.resultfilter | Required bundle for filtering results. Deploying this bundle  will effectuate that faulty query responses from IoT data sources are not forwarded to IoT applications.|
|├── eu.neclab.iotplatform.iotbroker.restcontroller | Required bundle implementing the HTTP REST interface of the IoT Broker.|
|├── eu.neclab.iotplatform.iotbroker.storage | Required bundle to setup and connect to an internal database. Note that this	database only used to store state information on data subscriptions and does not store any context data. For storing context data, please use the optional bundle eu.neclab.iotplatform.couchdb.|
|├── eu.neclab.iotplatform.knowledgebase | Optional bundle for interacting with the NEC IoT Knowledge server|
|├── eu.neclab.iotplatform.ngsi.api | Required bundle containing the libraries implementing the FIWARE NGSI data model.|
|├── eu.neclab.iotplatform.ngsiemulator | Library used for emulating NGSI deployment (IoT Provider, IoT Consumer, IoT Application) |
|├── fiwareRelease | Configuration folder used by IoT Broker bundles at runtime. This folder is referenced by the runtime environment in the "IoTBroker-runner" folder.|
|├── IoTbrokerParent | Maven parent of the IoT Broker Maven project (used only at compilation time).|
|├── IoTBroker-runner | Pre-configured runtime environment for IoT Broker, based on the Equinox OSGi implementation.|
|├── iotplatform.iotbroker.blackboxtest | Maven project for a blackbox test based on JUNIT. This can be used to test a running instance of IoT Broker. The tests require the running instance to include some optional bundles included by this release.|
|├── iotplatform.iotbroker.testingscenario | Repository containing testing scenarios for the IoT Broker and its features. |
|├── lib | Folder containing some OSGI bundles needed by IoT Broker to run.|
|├── linuxPackages | Repository containing files useful for creating Linux packages.|
|├── puppet |Puppet scripts for installing, starting, stopping, and uninstalling the IoT Broker.|
|├── SQL_database | HSQLDB folder containing internal IoT Broker database files.|
|├── targetPlatform| Contains the OSGi bundles needed for the IoT Broker at runtime. These bundles are pre-configured to be loaded by the runtime environment in the "IoTBroker-runner" folder.|
|└── tomcat-configuration-fragment | Required bundle for tomcat server configuration.|


NEC ConfMan
---

See: https://github.com/Aeronbroker/NEConfMan

NEC IoT Knowledge
---

See: https://github.com/Aeronbroker/NECIoTKnowledge


IoT Broker References
---

| Document                    | Reference                                                                                                        | Contents |
| --------------------------- | ---------------------------------------------------- | -------------------------------------------|
| This file | README.md | How to compile and run the IoT Broker. |
| FIWARE wiki                 | https://forge.fi-ware.org/plugins/mediawiki/wiki/fiware/index.php/Main_Page                                      | Generic wiki about the FIWARE project and platform. |
| IoT Broker GE specification | https://forge.fi-ware.org/plugins/mediawiki/wiki/fiware/index.php/FIWARE.OpenSpecification.IoT.Backend.IoTBroker | Specification of IoT Broker Generic Enabler, including information about its role in the FIWARE Intenet-of-Things architecture, functionalities, and data flows.|
| IoT Broker API specification | http://aeronbroker.github.io/Aeron/    | Specification of the FIWARE NGSI-10 API exposed by the IoT Broker.  |
| FIWARE NGSI API specification | https://forge.fi-ware.org/plugins/mediawiki/wiki/fiware/index.php/FI-WARE_NGSI_Open_RESTful_API_Specification    | Specificaton of the FIWARE NGSI API, which is the interface exposed by the IoT Broker and many other FIWARE enablers.  |
|IoT Broker Installation and Administration guide| [doc/installadminguide.md](./doc/installadminguide.md) | Installation and administration guide for IoT Broker.
|IoT Broker User and Programmer Guide| [doc/userprogrammersguide.md](doc/userprogrammersguide.md) | Guide showing some interactions with the IoT Broker. |
|IoT Broker extension point documentation| [doc/extensionpoint.md](./doc/extensionpoint.md) | Documentation on how to write and use plugins for additional data source types. |
|Entity composition guide| [eu.neclab.iotplatform.entitycomposer/README.MD](./eu.neclab.iotplatform.entitycomposer/README.MD) | Documentation on how to use the IoT Broker to compose entities from other entities. |
|History Queries| [doc/historyqueries.md](./doc/historyqueries.md) | Documentation on history queries on the IoT Broker. |
|IoT Broker on DockerHub | https://hub.docker.com/r/fiware/iotbroker/ | DockerHub Repository for IoT Broker docker image |
| IoT Broker in FIWARE catalogue   | http://catalogue.fiware.org/enablers/iot-broker                                                             | Access to IoT Broker binaries and images. |
| NEC ConfMan   | https://github.com/Aeronbroker/NEConfMan| NEC implementation of FIWARE IoT Discovery GE. |


Bugs & Questions
---
Please contact iotplatform@neclab.eu, flavio.cirillo@neclab.eu or stefan.gessler@neclab.eu.
