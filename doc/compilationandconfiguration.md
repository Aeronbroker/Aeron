IoT Broker as Java application
---

Get the compiled IoT Broker
----

[Download](https://catalogue.fiware.org/enablers/iot-broker/downloads) the wanted realease.


Building IoT Broker from Source
----

Last development version
-----

If you want to get the last commit of the code just get the IoT Broker GitHub project onto your machine. If git is installed, then the project can be cloned by opening a command line, navigating to the folder where IoT Broker shall be installed, and typing the command
 
```
git clone https://github.com/Aeronbroker/Aeron.git
```

Alternatively, the project can be downloaded as a [zip file](https://github.com/Aeronbroker/Aeron/archive/master.zip).

Release version
-----
If you want to the source code of a specific release, check the [tags page](https://github.com/Aeronbroker/Aeron/tags).

Compiling the source code
-----
After the code is download we can proceed to compile it. IoT Broker requires the following software to be installed for being built:

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

  This command will generate 14 OSGI bundles inside the *eu.neclab.iotplatform.iotbroker.builder\target* folder:

  (Note that the version numbers might differ from what is
  written in this document.)

```
entitycomposer-5.3.3.jar
iotbroker.client-5.3.3.jar
iotbroker.commons-5.3.3.jar
iotbroker.core-5.3.3.jar
iotbroker.couchdb-5.3.3.jar
iotbroker.embeddediotagent.core-5.3.3.jar
iotbroker.embeddediotagent.couchdb-5.3.3.jar
iotbroker.embeddediotagent.indexer-5.3.3.jar
iotbroker.embeddediotagent.storage-5.3.3.jar
iotbroker.ext.resultfilter-5.3.3.jar
iotbroker.restcontroller-5.3.3.jar
iotbroker.storage-5.3.3.jar
ngsi.api-5.3.3.jar
tomcat-configuration-fragment-5.3.3.jar
```


After having compiled the sources, the resulting jar files can either be run by the pre-configured OSGi platform included by the IoT Broker project (see [README](https://github.com/Aeronbroker/Aeron#quick-start-using-the-runtime-environment-included-by-this-repository)), or by setting up a custom OSGi environment and deploying the compiled IoT Broker bundles and their dependencies there.

Quick Start: Using the runtime environment included by this repository
----

This repository contains a complete runtime environment for the IoT Broker. In the [IoTBroker-runner](https://github.com/Aeronbroker/Aeron/tree/master/IoTBroker-runner) folder you can find the Equinox OSGi environment together with its configuration file. The configuration files contains references to the [fiwareRelease](https://github.com/Aeronbroker/Aeron/tree/master/fiwareRelease) folder, to the bundles in the [targetPlatform](https://github.com/Aeronbroker/Aeron/tree/master/targetPlatform) folder, as well as to the IoT Broker bundles that can be found in the [eu.neclab.iotplatform.iotbroker.builder](https://github.com/Aeronbroker/Aeron/tree/master/eu.neclab.iotplatform.iotbroker.builder)/target* folder after having successfully compiled the bundles.

To arrive at a running IoT Broker installation, the following steps need to be completed:

1. Compile the IoT Broker bundles as described above

2. Open the file *IoTBroker-runner*. Run a *./setup.sh --auto --propagateauto*.

3. Run the IoT Broker by running one of the startup scripts for Windows or Unix in the *IoTBroker-runner* folder.

IoT Broker System Configuration
----

IoT Broker configuration variables are spread among several files. For this reason is highly recommended the usage of the *setup.sh* script (only for Bash shell in UNIX system) available in the *IoTBroker-runner* folder (please note it is important that the structure of the folder needs to be the same as the one in the github repository https://github.com/Aeronbroker/Aeron).

First we have to enter in the *IoTBroker-runner* folder:
```
cd IoTBroker-runner
```

For a very quick configuration of the IoT Broker the following command needs to be run (please make sure the setup.sh has the execute rights with a *chmod +x setup.sh*):
```
./setup.sh --auto
```

The setup script will look at the default configurations in the *iotbroker.conf.default* file and it will manage to automatically figure out the necessary path for the configuration.

The paths will be configured only to IoT Broker configuration files and not in these setup files. In order to run in the future the *setup.sh* script without specifying the *--auto* option, the following command needs to be run:
```
./setup.sh --auto --propagateauto
```

The *--propagateauto* instructs the setup script to create a local configuration file, *iotbroker.conf.local*, with the right path configurations. From now on the next setup action can be done simply by the following command:
```
./setup.sh
```

In order to setup the IoTBroker differently from the default configuration, it is necessary to specify the custom preference in the *iotbroker.conf.local*. If the latter file has not yet been created by the --propagateauto options, it needs to be manually created. There are two possibilities:
* Create and empty file called *iotbroker.conf.local*, add at the first line *#!/bin/bash* and give the execute permissions.
* Copy the *iotbroker.conf.default* into *iotbroker.conf.local* and give the execute permissions.

No differences between the two options, since the setup script will first look into the *iotbroker.conf.default* file and then all the variables will be overwritten by the *iotbroker.conf.local* file.
Now the custom preferences can be set.

Please note: if a *./setup.sh --auto --propagateauto* has not yet been run once, also the setup.sh needs to have the following 4 paths set at the beginning of the script:
```
iotbroker_configini='/opt/Aeron/IoTBroker-runner/configuration/config.ini'
iotbroker_configxml='/opt/Aeron/fiwareRelease/iotbrokerconfig/iotBroker/config/config.xml'
iotbroker_embeddedagent_couchdbxml='/opt/Aeron/fiwareRelease/iotbrokerconfig/embeddedAgent/couchdb.xml'
iotbroker_loggerproperties="$iotbroker_bundlesconfigurationlocation/services/org.ops4j.pax.logging.properties"

```

For having the custom preferences set it is necessary to simply run:
```
./setup.sh
```

After running the setup.sh, the IoT Broker can be started with the provided scripts in *IoTBroker-runner* folder.

The provided scripts are setting parameters in several files:

- *IoTBroker-runner/configuration/config.ini*: OSGI environment configuration file. Amongst the other parameters:

	- The port the IoT Broker NGSI interface listens to. The default is port 8060.
	- The location of the *fiwareRelease* folder where further configuration information is found. As this needs to be an absolute path, setting this parameter is for most installations necessary in order to get the IoT Broker running.

- *fiwareRelease/iotbrokerconfig/iotBroker/config/config.xml*: properties regarding the IoT Broker core behaviour. Amongst the other parameters:
   
	-  **ngsi9Uri** and **pathPreFix_ngsi9:** The URL of the Iot Discovery GE instance the IoT Broker communicates with in order to retrieve the Context Registrations it needs.
 	- **pathPreFix_ngsi10:** The root of the FIWARE NGSI resource tree the IoT Broker exposes.
 	- **pub_sub_addr:** The address of an NGSI component where updates are forwarded to (e.g. a FIWARE Context Broker GE instance).
	- **X-Auth-Token:** The security token for connecting to components secured by the FIWARE access control mechanisms.

- *fiwareRelease/bundleConfigurations/services/org.ops4j.pax.logging.properties*: logging 
properties

- *fiwareRelease/iotbrokerconfig/bigDataRepository/couchdb.xml*: properties regarding the 
BigDataRepository optional feature

- *fiwareRelease/iotbrokerconfig/embeddedAgent/couchdb.xml*: properties regarding the Embedded Historical Agent optional feature.

- *fiwareRelease//iotbrokerconfig/knowledgeBase/knowledgeBase.properties*: properties regarding the Semantic Grounding optiona feature

Please note that the Iot Broker needs to be restarted before any changes will take effect.

Configure the IoT Broker manually (not recommended)
----
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
