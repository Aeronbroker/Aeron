Introduction
==

Welcome to the Installation and Administration Guide for the IoT Broker GE reference implementation. This guide explains how to interact with the IoT Broker GE from the perspective of users and developers.

API walkthrough
==

For a API walkthrough please refer to [IoT Broker Apiary](http://docs.iotbrokerngsiinterface.apiary.io/#).

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
  
Each Iot Broker instance needs to interact with an instance of the IoT Discovery GE. This interaction is done via the FIWARE NGSI 9 API (the context availability API of FIWARE NGSI). However, this interaction is mostly transparent to the user.


Data Provider Perspective
==
Data providers who want to make their data available to an IoT Broker instance have two possibilities (of course any mix of these possibilities can be applied as well).

* Possibility 1 (**data provider**): Expose a FIWARE NGSI-10 interface which the IoT Broker can query for data whenever needed. The network address of this interface and the data it provides need to be registered using the FIWARE NGSI-10 *registerContext* operation. Please note that the IoT Broker does not handle registrations by itself. Instead, the registration has to be sent to the instance of the IoT Discovery GE responsible for this FIWARE IoT installation.

* Possibility 2 (**data producer**): Push data updates towards the IoT Broker using the *updateContext* method provided by FIWARE NGSI-10. This is the simpler kind of interactions, because data providers do not need to expose an NGSI API. On the downside, this interaction mode potentially uses network resources inefficiently, because the data push takes place regardless of whether the data is needed by a data consumer or not.

Data Consumer (aka IoT Application) Perspective
==

Data consumers can retrieve context data from the IoT Broker via the FIWARE NGSI-10 interface as described in the [API specs](http://aeronbroker.github.io/Aeron/).

Additionally, the IoT Broker offers a simple web interface, which can be accessed by a web browser under the URL of the hosting machine of the IoT Broker (e.g. "localhost" when accessing from the machine where the IoT Broker is running). From the home page the user can also access a query interface for basic NGSI queries.

In case the BigDataRepository and/or the Embedded Historical Agent is enabled, data can be seen also through the CouchDB interface (if the latter is exposed).


Playing with IoT Broker middleware: NGSI Emulator
==

A common question is: "**how do I play with the IoT Broker platform?**"

Since this is a middleware, it is not easy to imagine how to use the platform. A simple testing can be done with a HTTP client application. We often use "[HTTP Requester](https://addons.mozilla.org/en-US/firefox/addon/httprequester/)" extension fo Firefox browser or "[Postman](https://chrome.google.com/webstore/detail/postman/fhbjgbiflinjbdggehcddcbncdddomop?hl=en)" plugin for Chromium browser.

But those applications are not enough to fully test the IoT Broker middleware and the potentiality of the NGSI inferface (e.g. the asynchronous notifications). The Aeron project is including also a [NGSI emulator](https://github.com/Aeronbroker/Aeron/tree/master/eu.neclab.iotplatform.ngsiemulator) which allows to easily emulate NGSI components in order to let them interworking with IoT Broker and the ConfMan. The puropose of this emulator is two-fold: testing the IoT Broker functionalities and example of implementation of NGSI players. 

Historical agent
==

Recent versions of the IoT Broker (after 5.3.3) are embedding a system for historically store context information and responding to historical queries on that data.

In orde to enable it and to understand how it works, please refer to the following documentation: [Historical Storage](https://github.com/Aeronbroker/Aeron/blob/master/doc/historyqueries.md);
 

Multiple values of the same attribute of the same entity
==

IoT Broker allows to have multiple values for the same attribute of the same entity. The data can come from multiple IoT providers or from the internal historical storage (Historical Agent, see above).

In case multiple IoT providers are providing the same attribute of the same entity, when this is requested with a query or subscription from an IoT consumer, the IoT Broker simply retrieve all of them in the same response (or notification in case of subscription).

In case the IoT Broker stores the data historically in the database, when it is requested to get an EntityId only the latest values of all the attributes are retrieved.

When data is pushed without a specific timestamp, the data is stored with the local timestamp of the IoT Broker. If the same attribute is pushed with two different values in the same request, it will be taken into consideration only one of them (most likely the last in the list): e.g.:

```
POST http://localhost:8060/ngsi10/updateContext
Content-Type: application/json
Accept: application/json
{
    "contextElements": [{
        "entityId": {
            "id": "TestEntity",
            "isPattern": false
        },
        "attributes": [{
            "name": "Activity_Walk",
            "type": "String",
            "contextValue": "TestWalk1"
        }, {
            "name": "Activity_Walk",
            "type": "String",
            "contextValue": "TestWalk2"
        }]
    }],
    "updateAction": "APPEND"
}
```

and if retrieve the entityId we will havedo , request:
```
POST http://localhost:8060/ngsi10/queryContext
Content-Type: application/json
Accept: application/json
{
    "entities": [{
        "isPattern": false,
        "id": "TestEntity"
    }]
}
```

response:
```
{
    "contextResponses": [{
        "contextElement": {
            "entityId": {
            "id": "TestEntity",
            "isPattern": false
            },
            "attributeDomainName": null,
            "domainMetadata": [],
            "attributes": []{
                "name": "Activity_Walk",
                "type": "String",
                "contextValue": "TestWalk1",
                "metadata": null
            }]
        },
        "statusCode": {
            "code": 200,
            "reasonPhrase": "OK",
            "details": "OK"
        }
    }]
}

```

In order to have them stored correctly there are two possible methods:

**Method 1.** Send two different updateContext for the two attributes.
**Method 2.** Send one updateContext with a different timestamp for each of the attributeValues (one single millisecond of difference is enough), e.g.:

```
{
    "contextElements": [{
        "entityId": {
            "id": "TestEntity",
            "isPattern": false
        },
        "attributes": [{
            "name": "Activity_Walk",
            "type": "String",
            "contextValue": "TestWalk2",
            "metadata": [{
                "name": "creation_time",
                "value": "2017.09.13 10:38:14:837 +0100",
                "type": "string"
            }]
        }, {
            "name": "Activity_Walk",
            "type": "String",
            "contextValue": "TestWalk1",
            "metadata": [{
                "name": "creation_time",
                "value": "2017.09.13 10:38:14:838 +0100",
                "type": "string"
            }]
        }]
    }],
    "updateAction": "APPEND"
}
```
 
For retrieving back the different values it necessary to make an historical query like this:

```
POST http://155.54.210.176:8060/ngsi10/queryContext
Content-Type: application/json
Accept: application/json

{
    "entities": [{
        "isPattern": false,
        "id": "TestEntity"
    }],
    "restriction": {
        "attributeExpression": "",
        "scopes": [{
            "scopeType": "ISO8601TimeInterval",
            "scopeValue": "2017-09-13T10:30:00+0100/2017-09-13T11:30:00+0100"
        }]
    }
}
```

The response will be different for push method 1. or method 2. for the insertion of the values:

**Method 1. **response:

```
{
    "contextResponses": [{
        "contextElement": {
            "entityId": {
                "id": "TestEntity",
                "type": "TestForAttributesNames",
                "isPattern": false
            },
            "attributeDomainName": null,
            "domainMetadata": [],
            "attributes": [{
                "name": "Activity_Walk",
                "type": "String",
                "contextValue": "TestWalk2",
                "metadata": null
            }, {
                "name": "Activity_Walk",
                "type": "String",
                "contextValue": "TestWalk1",
                "metadata": null
            }]
        },
        "statusCode": {
            "code": 200,
            "reasonPhrase": "OK",
            "details": "OK"
        }
    }]
}
```

**Method 2.** response:

```
{
    "contextResponses": [{
        "contextElement": {
            "entityId": {
                "id": "TestEntity",
                "type": "TestForAttributesNames",
                "isPattern": false
            },
            "attributeDomainName": null,
            "domainMetadata": [],
            "attributes": [{
                "name": "Activity_Walk",
                "type": "String",
                "contextValue": "TestWalk2",
                "metadata": [{
                    "name": "creation_time",
                    "type": "string",
                    "value": "2017.09.13 10:38:14:837 +0100"
                }]
            }, {
                "name": "Activity_Walk",
                "type": "String",
                "contextValue": "TestWalk1",
                "metadata": [{
                    "name": "creation_time",
                    "type": "string",
                    "value": "2017.09.13 10:38:14:838 +0100"
                }]
            }]
        },
        "statusCode": {
            "code": 200,
            "reasonPhrase": "OK",
            "details": "OK"
        }
    }]
}
```


Method 2 is suggested because you have more control on the timestamp and how they will be historically put in a timeserie. Method 1 relies on the clock of the host machine of the IoT Broker.

Developer Guide
==

The IoT Broker is based on the OSGi framework and is composed of a number of different plugins. The role of the individual plugins is described in the [README](https://github.com/Aeronbroker/Aeron#directory-structure).

Importantly, the IoT broker core has a dedicated extension point for custom data retrieval and data processing plugins. Please see [here](https://github.com/Aeronbroker/Aeron/blob/master/doc/extensionpoint.md) for details on how to write custom plugins.

### Use IoT Broker libraries into your (JAVA) code

If you are implementing you own IoT Application, Context Provider or Context Producer you either start from the [NGSI emulator](https://github.com/Aeronbroker/Aeron/tree/master/eu.neclab.iotplatform.ngsiemulator) project or you can create from scratch your Maven project and uses the IoT Broker libraries.

The basic library to be used would be:

* [eu.neclab.iotplatform.ngsi.api](https://github.com/Aeronbroker/Aeron/tree/master/eu.neclab.iotplatform.ngsi.api): which is comprehensive of all the POJO classes that model the NGSI data model (very useful for parsing from JSON and from XML and to serialize from JSON and to XML).
* [eu.neclab.iotplatform.iotbroker.commons](https://github.com/Aeronbroker/Aeron/tree/master/eu.neclab.iotplatform.iotbroker.commons): which is comprehensive of several useful methods, interfaces related to NGSI and enumators related to NGSI
* [eu.neclab.iotplatform.iotbroker.client](https://github.com/Aeronbroker/Aeron/tree/master/eu.neclab.iotplatform.iotbroker.client): which is useful for sending NGSI query (automatically establishes connection to URL and check error messages)

In order to include the libraries into your project you can

**Method 1**: Include it as an external jar library (you need to compile the IoT Broker [see here](https://github.com/Aeronbroker/Aeron#building-iot-broker-source-code))

**Method 2** Compile the full IoT Broker project ([see here](https://github.com/Aeronbroker/Aeron#building-iot-broker-source-code)) and install the maven library on your local maven repository into your machine (*mvn install*) and add the following dependencies into your pom.xml:

```
    <dependency>
                <groupId>eu.neclab.iotplatform</groupId>
                <artifactId>iotbroker.client</artifactId>  
                <version>6.1-SNAPSHOT</version>
    </dependency>
    
    <dependency>
                <groupId>eu.neclab.iotplatform</groupId>
                <artifactId>iotbroker.commons</artifactId>  
                <version>6.1-SNAPSHOT</version>
    </dependency>
    
    <dependency>
                <groupId>eu.neclab.iotplatform</groupId>
                <artifactId>ngsi.api</artifactId>  
                <version>6.1-SNAPSHOT</version>
    </dependency>
```
NOTE: the version may differ

**Method 3** Import it as a maven project into your JAVA IDE together with the needed other maven projects (see the list below) and add the dependency to your pom.xml (see above)

* IoTbrokerParent
* eu.neclab.iotplatform.ngsi.api
* eu.neclab.iotplatform.iotbroker.commons
* eu.neclab.iotplatform.iotbroker.client


