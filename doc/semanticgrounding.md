Semantic in the IoT Broker
==================

From version 5.4.3 the IoT Broker has an optional feature which brings semantic capabilities. The current status is the support for checking **subtypes** of a certain type when a query or subscription is done.

NOTE: This feature will work only if the same feature is enable in the NEConfMan: https://github.com/Aeronbroker/NEConfMan/doc/semanticgrounding.md

The following workflow explains how to enable and use this feature.

Enable the Semantic feature
-----------
For enabling the semantic feature, the IoT Broker needs to be connected to an instance of the **NECKnowledgeBase** server (https://github.com/Aeronbroker/NECKnowledgeBase). This is achieved by the following steps.
* The NECKnowledgeBase server needs to be installed either on the machine where the IoT Broker is running, or on a remote machine.
* The IoT Broker plugin eu.neclab.iotplatform.knowledgebase needs to be activated. This can be done by setting the following configuration in the IoTBroker-runner/iotbroker.conf.local:
```
iotbroker_semantic="enabled"
```
If not set, the default value in IoTBroker-runner/iotbroker.conf.deafult is *enabled*. Then run
```
./setup.sh
```
In orde to better understand the configuration procedure please read: https://github.com/Aeronbroker/Aeron#configure-the-iot-broker-with-setup-scripts.
* Configure the activated plugin by adding the following configurations in the IoTBroker-runner/iotbroker.conf.local:
```
# Knowledge Base configuration
iotbroker_knowledgebaseaddress='http://127.0.0.1'
iotbroker_knowledgebaseport=8015
```
If not set, the default values in IoTBroker-runner/iotbroker.conf.deafult are * http://127.0.0.1 * and *8015*. Then run
```
./setup.sh
```

Testing the entity subtype scenario
-----------


Assumption for this example:
* IoT Broker is running on localhost on port 8060. 
* IoT Broker has the embedded Historical Agent enabled (https://github.com/Aeronbroker/Aeron/blob/master/doc/historyqueries.md#enable-historical-queries).
* All the databases are empty.
* It is used the NGSI ontology developed for the SmartSantander IoT deployment (see https://github.com/Aeronbroker/NECIoTKnowledge/blob/master/NGSI_Sparql_Examples/SmartSantanderNGSI-RDF.owl).


First we push data of an entity of type NoiseSensor

```
HTTP POST to http://localhost:8060/ngsi10/updateContext
Content-Type:application/json
Accept:application/json

Body:
{
    "updateAction": "APPEND",
    "contextElements": [{
        "entityId": {
            "id": "noiseSensor1",
            "type": "NoiseSensor",
            "isPattern": false
        },
        "attributes": [{
            "name": "noiselevel",
            "type": "float",
            "contextValue": "65",
            "metadata": [{
                "name": "units",
                "type": "units",
                "value": "dB"
            }]
        }]
    }]
}

```

```
{"errorCode":null,"contextResponses":null}
```

And we try to retrieve what has just been stored by using the convenience method for getting all data for all sensors of a certain type (http://docs.iotbrokerngsiinterface.apiary.io/#reference/0/convenience-interaction-with-entity-type/retrieve-all-context-information-about-entities-of-specified-type):

```
HTTP GET to http://localhost:8060/ngsi10/contextEntityTypes/NoiseSensor
Accept:application/json
```
the exact same query can be executed by send a query request (http://docs.iotbrokerngsiinterface.apiary.io/#reference/0/standard-context-query):
```
HTTP POST to http://localhost:8060/ngsi10/queryContext
Content-Type:application/json
Accept:application/json

Body:
{
    "entities": [{
        "id": ".*",
        "isPattern": true,
        "type" : "NoiseSensor"
    }]
}

```

The response to both will look like:

```
{
    "contextResponses": [{
        "contextElement": {
            "entityId": {
                "id": "noiseSensor1",
                "type": "NoiseSensor",
                "isPattern": false
            },
            "attributeDomainName": null,
            "domainMetadata": [],
            "attributes": [{
                "name": "noiselevel",
                "type": "float",
                "contextValue": "66",
                "metadata": [{
                    "name": "units",
                    "type": "units",
                    "value": "dB"
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

Since the ontology we are using is specifying that NoiseSensor is a subclass of Node, with the semantic feature enabled, we can request data of all entity of type Node:

```
HTTP GET to http://localhost:8060/ngsi10/contextEntityTypes/Node
Accept:application/json
```
The response will look like the previous one:

```
{
    "contextResponses": [{
        "contextElement": {
            "entityId": {
                "id": "noiseSensor1",
                "type": "NoiseSensor",
                "isPattern": false
            },
            "attributeDomainName": null,
            "domainMetadata": [],
            "attributes": [{
                "name": "noiselevel",
                "type": "float",
                "contextValue": "66",
                "metadata": [{
                    "name": "units",
                    "type": "units",
                    "value": "dB"
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

Let's now push data of a different kind of entity, in particual LightSensor which is a subclass of Node as well:

```
HTTP POST to http://localhost:8060/ngsi10/updateContext
Content-Type:application/json
Accept:application/json

Body:
{
    "updateAction": "APPEND",
    "contextElements": [{
        "entityId": {
            "id": "lightSensor1",
            "type": "LightSensor",
            "isPattern": false
        },
        "attributes": [{
            "name": "lightlevel",
            "type": "float",
            "contextValue": "4567",
            "metadata": [{
                "name": "units",
                "type": "units",
                "value": "lux"
            }]
        }]
    }]
}

```

```
{"errorCode":null,"contextResponses":null}
```

Let's try to request data only of entity of type LightSensor:


```
HTTP GET to http://localhost:8060/ngsi10/contextEntityTypes/LightSensor
Accept:application/json
```

```
{
    "contextResponses": [{
        "contextElement": {
            "entityId": {
                "id": "lightSensor1",
                "type": "LightSensor",
                "isPattern": false
            },
            "attributeDomainName": null,
            "domainMetadata": [],
            "attributes": [{
                "name": "lightlevel",
                "type": "float",
                "contextValue": "4567",
                "metadata": [{
                    "name": "units",
                    "type": "units",
                    "value": "lux"
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

Now only of NoiseSensor again:
```
HTTP GET to http://localhost:8060/ngsi10/contextEntityTypes/NoiseSensor
Accept:application/json
```
```
{
    "contextResponses": [{
        "contextElement": {
            "entityId": {
                "id": "noiseSensor1",
                "type": "NoiseSensor",
                "isPattern": false
            },
            "attributeDomainName": null,
            "domainMetadata": [],
            "attributes": [{
                "name": "noiselevel",
                "type": "float",
                "contextValue": "66",
                "metadata": [{
                    "name": "units",
                    "type": "units",
                    "value": "dB"
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

And finally of the superclass Node:

```
HTTP GET to http://localhost:8060/ngsi10/contextEntityTypes/Node
Accept:application/json
```
```
{
    "contextResponses": [{
        "contextElement": {
            "entityId": {
                "id": "lightSensor1",
                "type": "LightSensor",
                "isPattern": false
            },
            "attributeDomainName": null,
            "domainMetadata": [],
            "attributes": [{
                "name": "lightlevel",
                "type": "float",
                "contextValue": "4567",
                "metadata": [{
                    "name": "units",
                    "type": "units",
                    "value": "lux"
                }]
            }]
        },
        "statusCode": {
            "code": 200,
            "reasonPhrase": "OK",
            "details": "OK"
        }
    }, {
        "contextElement": {
            "entityId": {
                "id": "noiseSensor1",
                "type": "NoiseSensor",
                "isPattern": false
            },
            "attributeDomainName": null,
            "domainMetadata": [],
            "attributes": [{
                "name": "noiselevel",
                "type": "float",
                "contextValue": "66",
                "metadata": [{
                    "name": "units",
                    "type": "units",
                    "value": "dB"
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
