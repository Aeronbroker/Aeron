Introduction
==

Welcome to the Installation and Administration Guide for the IoT Broker GE reference implementation. This guide explains how to interact with the IoT Broker GE from the perspective of users and developers.

API walkthrough
==

For more details about the data flows during the different interactions, please refer to the [FIWARE IoT Broker GE Open Specification](https://forge.fiware.org/plugins/mediawiki/wiki/fiware/index.php/FIWARE.OpenSpecification.IoT.Backend.IoTBroker).

FIWARE NGSI 10
--

The IoT Broker GE is a middleware making data from multiple providers accessible for data consumers. The interaction with both data providers and data consumers is taking place via the FIWARE NGSI 10 API (the context data API of FIWARE NGSI).

For using the IoT Broker, the API needs to be contacted via HTTP  on port 8060 (if default configuration applies) with one of the HTTP methods (GET, POST, PUT, DELETE) according to the NGSI-10 specification.

The FIWARE NGSI-10 interactions are

 * context queries (e.g. via HTTP POST to `{IoT Broker IP}/ngsi10/queryContext`)
 * context subscription (e.g. via HTTP POST to `{IoT Broker IP}/ngsi10/subscribeContext`)
 * context updates (e.g. via HTTP POST to `{IoT Broker IP}/ngsi10/updateContext`)

For details on the FIWARE NGSI-10 API exposed by the IoT Broker, please refer to the [API specs](http://aeronbroker.github.io/Aeron/).

FIWARE NGSI 9
--
  
The each Iot Broker instance needs to interact with an instance of the IoT Discovery GE. This interaction is done via the FIWARE NGSI 9 API (the context availability API of FIWARE NGSI). However, this interaction is mostly transparent to the user.


Data Provider Perspective
==
Data providers who want to make their data available to an IoT Broker instance have two possibilities (of course any mix of these possibilities can be applied as well).

* Possibility 1: Expose a FIWARE NGSI-10 interface which the IoT Broker can query for data whenever needed. The network address of this interface and the data it provides need to be registered using the FIWARE NGSI-10 *registerContext* operation. Please note that the IoT Broker does not handle registrations by itself. Instead, the registration has to be sent to the instance of the IoT Discovery GE responsible for this FIWARE IoT installation.

* Possibility 2: Push data updates towards the IoT Broker using the *updateContext* method provided by FIWARE NGSI-10. This is the simpler kind of interactions, because data providers do not need to expose an NGSI API. On the downside, this interaction mode potentially uses network resources inefficiently, because the data push takes place regardless of whether the data is needed by a data consumer or not.

Data Consumer Perspective
==

Data consumers can retrieve context data from the IoT Broker via the FIWARE NGSI-10 interface as described in the [API specs](http://aeronbroker.github.io/Aeron/).

Additionally, the IoT Broker offers a simple web interface, which can be accessed by a web browser under the URL of the hosting machine of the IoT Broker (e.g. "localhost" when accessing from the machine where the IoT Broker is running). From the home page the user can also access a query interface for basic NGSI queries.

In case the BigDataRepository and/or the Embedded Historical Agent is enabled, data can be seen also through the CouchDB interface (if the latter is exposed).

Developer Guide
==


The IoT Broker is based on the OSGi framework and is composed of a number of different plugins. The role of the individual plugins is described in the [README](https://github.com/Aeronbroker/Aeron#directory-structure).

Importantly, the IoT broker core has a dedicated extension point for custom data retrieval and data processing plugins. Please see [here](https://github.com/Aeronbroker/Aeron/blob/master/doc/extensionpoint.md) for details on how to write custom plugins.

NGSI Emulator
==
The Aeron project is including also a NGSI emulator which allows to easily emulate NGSI components in order to let them interworking with IoT Broker and the ConfMan. The puropose of this emulator is two-fold: testing the IoT Broker functionalities and example of implementation of NGSI players. See: https://github.com/Aeronbroker/Aeron/tree/master/eu.neclab.iotplatform.ngsiemulator


