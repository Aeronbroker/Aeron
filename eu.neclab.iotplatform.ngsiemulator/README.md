NGSI Emulator
==================

The Aeron project is including also a NGSI emulator which allows to easily emulate NGSI components in order to let them interworking with IoT Broker and the ConfMan. The puropose of this emulator is two-fold: testing the IoT Broker functionalities and example of implementation of NGSI players.

Requirements
---
JDK: Java 8

NGSI players
----------
The NGSI emulator is implementing and offering three different elements which are playing three different roles:

**IoT Provider**
An IoT data provider which is able to reply to NGSI-10 QueryContext and to handle NGSI-10 SubscribeContext by iniatializing a flow of NGSI-10 NotifyContext to the subscriber.

**IoT Consumer**
An application which opens an HTTP server and consumes NGSI-10 UpdateContext.

**IoT Application**
An application which consumes NGSI-10 NotifyContext


Get the NGSI emulator
-----------
The NGSI emulator is all contained in a Java library that needs to be compiled.

Downloading or cloning the full Aeron project gives you already all what you need. Otherwise the minimum set of code to be downloaded from the Aeron project (https://github.com/Aeronbroker/Aeron) is:
* IoTbrokerParent folder
* eu.neclab.iotplatform.ngsi.api folder
* eu.neclab.iotplatform.iotbroker.commons folder
* eu.neclab.iotplatform.ngsiemulator folder

If you have already compiled the Aeron IoT Broker you can skip the next step. Otherwise you need to make the following (in the command line):
```
cd IoTbrokerParent/
mvn clean install

cd eu.neclab.iotplatform.ngsi.api/
mvn clean install

cd eu.neclab.iotplatform.iotbroker.commons/
mvn clean install
```

Afterwards you can proceed to generate the executable jar file:
```
cd neclab.iotplatform.ngsiemulator/
mvn clean install
```

This will produce the *ngsiemulator-{version}-jar-with-dependencies.jar* executable jar and placed into the *eu.neclab.iotplatform.ngsiemulator/target/* folder.

Usage of the NGSI emulator
-----------

All the NGSI emulated component are suppporting both JSON and XML bindings of NGSI. The response will be coded as JSON or XML depending on the HTTP header "Accept:" of the request (or of the *SubscribeContext* in case of *NotifyContext*).

### IoT Consumer ###
Starting with the easiest component, IoT Consumer, which is a kind of "sink" of NGSI data. Its behaviour is to simply accept NGSI-10 UpdateContext and print the content in the console.
To start an IoT Consumer it is simply necessary to run the following in a command line:
```
java -cp ngsiemulator-$VERSION-jar-with-dependencies.jar \
-Deu.neclab.ioplatform.ngsiemulator.iotconsumer.ports=8001 \
eu.neclab.iotplatform.ngsiemulator.server.MainIoTConsumer
```
This will open a server listening on port 8001 and exposing the *ngsi10/updateContext resource*.

It is possible to open more than one server with only one command (one thread each server), by expressing more than one port. It is allowed ranges (e.g. 8001-8005) and single ports (e.g. 8001) separated by comma. E.g.:

```
-Deu.neclab.ioplatform.ngsiemulator.iotconsumer.ports=8001-8005,8021,8025,8030-8040
```

### IoT Provider ###
The IoT Provider, is the most complex component. It is capable to handle all kind of NGSI-10 data request such as the NGSI-10 queryContext (for synchronous data request) and NGSI-10 subscribeContext (for asyncrhonous data request, following the subscribe/notify paradigm, see https://edu.fiware.org/course/view.php?id=33).

To start an IoT Provider it is necessary to run the following in a command line:
```
java -cp ngsiemulator-$VERSION-jar-with-dependencies.jar \
-Deu.neclab.ioplatform.ngsiemulator.iotprovider.ports=8101 \
eu.neclab.iotplatform.ngsiemulator.server.MainIoTProvider
```

It is possible to open more than one server with only one command (one thread each server), by expressing more than one port. It is allowed ranges (e.g. 8001-8005) and single ports (e.g. 8001) separated by comma. E.g.:

```
# Ports
eu.neclab.ioplatform.ngsiemulator.iotprovider.ports=8001-8005,8021,8025,8030-8040
```

Other necessary settings:
```
# Period between notifyContexts expressed in seconds
eu.neclab.ioplatform.ngsiemulator.iotprovider.notificationPeriod=5

# Default Incoming ContentType if not specified in the HTTP header "ContentType"
# Supported: application/json, application/xml
eu.neclab.iotplaform.ngsiemulator.iotprovider.defaultIncomingContentType=application/json

# Default Outgoing ContentType if not specified in the HTTP header "Accept"
# Supported: application/json, application/xml
eu.neclab.iotplaform.ngsiemulator.iotprovider.defaultOutgoingContentType=application/xml
```


**NGSI-10 Registration**

The first action every NGSI-10 provider should do in order to be officially part of an NGSI network is to register its availability to an IoT Discovery. This action can be done by the IoT Provider at start up time, or it is left to a third actor, e.g.  an explicit user HTTP request. In order to activate the registration it is necessary to specify the following parameters:
```
eu.neclab.ioplatform.ngsiemulator.iotprovider.doRegistration=true/false
eu.neclab.ioplatform.ngsiemulator.iotDiscoveryUrl="http://localhost:8065/"
```

The IoT Provider can operate in two modes: Random mode and FromFile mode.

**Random Mode**

In order to select the Random mode:
```
eu.neclab.ioplatform.ngsiemulator.iotprovider.mode="Random"
```

If it has been specified to do a registration, the IoT Provider will send an NGSI-9 *registerContextAvailability* created from the following parameters:
```
eu.neclab.ioplatform.ngsiemulator.iotprovider.rangesOfEntityIds=1-100
eu.neclab.ioplatform.ngsiemulator.iotprovider.numberOfEntityIdsToSelect=10
eu.neclab.ioplatform.ngsiemulator.iotprovider.rangesOfAttributes=1-15
eu.neclab.ioplatform.ngsiemulator.iotprovider.numberOfAttributesToSelect=3
```
The registration will contain:
* A list of *EntityIds* of size *eu.neclab.ioplatform.ngsiemulator.iotprovider.numberOfEntityIdsToSelect*, of a format like *Entity-<number>* where number is chosen randomly between *eu.neclab.ioplatform.ngsiemulator.iotprovider.rangesOfEntityIds* set of numbers.
* A list of *Attributes* of size *eu.neclab.ioplatform.ngsiemulator.iotprovider.numberOfAttributesToSelect*, of a format like *Attribute-<number>* where number is chosen randomly between *eu.neclab.ioplatform.ngsiemulator.iotprovider.rangesOfAttributes* set of numbers.

The response to an NGSI-10 *queryContext* request is solely based on the incoming request. The *queryContext* is parsed and for *EntityId* requested it will be created a *ContextElement* which will contain a *ContextAttribute* for each attribute specified in the request with a random value.
For a NGSI-10 *subscribeContext*, after a positive response which acknowledges and declares the *subscriptionId*, a stream of NGSI-10 *notifyContexts*, each containing a list of *ContextElement* created from the *subscribeContext* request, is sent to the specified *reference*.

**FromFile Mode**

In order to select the FromFile mode:
```
eu.neclab.ioplatform.ngsiemulator.iotprovider.mode="FromFile"
```

If it has been specified to do a registration, the IoT Provider will send an NGSI-9 *registerContextAvailability* read from a file:
```
eu.neclab.ioplatform.ngsiemulator.iotprovider.registerContextAvailabilityFile="/path/to/registerContextAvailabilityFile"
```
If such parameter is not specified a generic registration, similar to the following, will be made:
```
<?xml version="1.0" encoding="UTF-8"?>
<registerContextRequest>
  <contextRegistrationList>
    <contextRegistration>
      <entityIdList>
        <entityId isPattern="true">
          <id>.*</id>
        </entityId>
      </entityIdList>
      <providingApplication>http://<localip>:<port></providingApplication>
    </contextRegistration>
  </contextRegistrationList>
</registerContextRequest>

```

The response to each NGSI-10 *queryContext* request is simply read from a specified file, not matter what is requested by the *queryContext*.
In case of NGSI-10 *subscribeContext*, after a positive response which acknowledges and declares the *subscriptionId*, a stream of NGSI-10 *notifyContexts*, read from the specified file, is sent to the *reference* specified in the *subscribeContext*.
```
eu.neclab.ioplatform.ngsiemulator.iotprovider.queryContextResponseFile="/path/to/queryContextResponseFile"
eu.neclab.ioplatform.ngsiemulator.iotprovider.notifyContextRequestFile="/path/to/notifyContextRequestFile"
```

### IoT Application ###
The IoT Application is a component which opens a server on a specified port and exposes a resource for NGSI-10 *NotifyContext*: /ngsi10/notifyContext.
Currently it is still in an embryonic stage of development. Its behaviour is to simply accept NGSI-10 *NotifyContext* and print the content in the console.
To start an IoT Application it is simply necessary to run the following in a command line:
```
java -cp ngsiemulator-$VERSION-jar-with-dependencies.jar \
-Deu.neclab.ioplatform.ngsiemulator.iotapplication.ports=8201 \
eu.neclab.iotplatform.ngsiemulator.server.MainIoTApplication
```
This will open a server listening on port 8201 and exposing the *ngsi10/notifyContext resource*.

It is possible to open more than one server with only one command (one thread each server), by expressing more than one port. It is allowed ranges (e.g. 8001-8005) and single ports (e.g. 8001) separated by comma. E.g.:

```
-Deu.neclab.ioplatform.ngsiemulator.iotapplication.ports=8201-8205,8221,8225,8230-8240
```

### Using configuration file ###

It is possible to use also the configuration file:
```
java -cp ngsiemulator-$VERSION-jar-with-dependencies.jar \
-Deu.neclab.ioplatform.ngsiemulator.iotprovider.configurationFile="/path/to/configurationFile.ini"
eu.neclab.iotplatform.ngsiemulator.server.MainIoTProvider
```

with a configuration file similar to:
```
# Here is a comment
eu.neclab.ioplatform.ngsiemulator.iotprovider.ports=8001-8010
eu.neclab.ioplatform.ngsiemulator.iotprovider.rangesOfEntityIds=1-100
eu.neclab.ioplatform.ngsiemulator.iotprovider.numberOfEntityIdsToSelect=10
eu.neclab.ioplatform.ngsiemulator.iotprovider.rangesOfAttributes=1-15
eu.neclab.ioplatform.ngsiemulator.iotprovider.numberOfAttributesToSelect=3
eu.neclab.iotplaform.ngsiemulator.iotprovider.defaultIncomingContentType=application/json
eu.neclab.iotplaform.ngsiemulator.iotprovider.defaultOutgoingContentType=application/json
eu.neclab.ioplatform.ngsiemulator.iotDiscoveryUrl=http://localhost:8065/
eu.neclab.ioplatform.ngsiemulator.iotprovider.mode=random
eu.neclab.ioplatform.ngsiemulator.iotprovider.doRegistration=true
```

or:
```
# Here is a comment
eu.neclab.iotplaform.ngsiemulator.iotprovider.defaultIncomingContentType=application/json
eu.neclab.iotplaform.ngsiemulator.iotprovider.defaultOutgoingContentType=application/xml
eu.neclab.ioplatform.ngsiemulator.iotprovider.mode=fromfile
eu.neclab.ioplatform.ngsiemulator.iotprovider.queryContextResponseFile="/path/to/queryContextResponse.xml"
eu.neclab.ioplatform.ngsiemulator.iotprovider.notifyContextRequestFile="/path/to/notifyContextRequestFile.xml"
eu.neclab.ioplatform.ngsiemulator.iotprovider.registerContextAvailabilityFile="/tmp/registerContextAvailabilityFile.xml"
```


NGSI Single Query bash script
----------
Together with the NGSI Emulator folder, some bash scripts, based on CURL, for sending NGSI single requests are provided.

* ngsi10-querycontext.sh or ngsi10-querycontext-xml.sh

```
Usage: ngsi10-querycontext -e <entityid> -a <attributename> -u <url> [-t <entitytype>] [-p <ispattern>]
Usage: ngsi10-querycontext -u <url> -f <file> [-c <contenttype>]
```

* ngsi10-updatecontext.sh

```
Usage: ngsi10-updatecontext -e <entityid> -a <attributename> -v <value> -u <url>
Usage: ngsi10-updatecontext -u <url> -f <file> [-c <contenttype>]
```

* ngsi10-subscribecontext.sh

```
Usage: ngsi10-subscribecontext -e <entityId[,type][,isPattern]> -r <referenceurl> -u <url> [-a <attributename[,..]>] [-e <entityId[,type][,isPattern]> ...]
Usage: ngsi10-subscribecontext -u <url> -f <file> [-c <contenttype>]

Example: ngsi10-subscribecontext -e e1,,true -e e2,t2 -a temperature,co2 -r http://localhost:8101/ngsi10/notify -u http://localhost:8065/ngsi10/subscribeContext
```

* ngsi9-registercontext.sh

```
Usage: ngsi9-registercontext -e <entityid> -v <providingapplication> -u <url> [-t <entitytype>][-a <attributename>][-y <attributetype>][-p <ispattern>] [-d <duration in the form PnYnMnDTnHnMnS>]
Usage: ngsi9-registercontext -u <url> -f <file> [-c <contenttype>]
```
