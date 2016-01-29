Historical Queries  on the IoT Broker
==================

The IoT Broker has an optional feature for saving NGSI data to a database and answering historical queries on that data.

The following workflow explains how to enable and use this feature.

Enable historical queries
-----------
For enabling historical queries, the IoT Broker needs to be connected to an instance of couchDB. This is achieved by the following steps.
* The couchDB database needs to be installed either on the machine where the IoT Broker is running, or on a remote machine.
* The IoT Broker plugin eu.neclab.iotplatform.couchdb needs to be activated. This is done in the OSGi runtime environment; please refer to the [Installation and Administration guide](installadminguide.md) for details.
* The above plugin needs to be configured such that it knows the address and credentials of couchDB. This is done in the file `fiwareRelease/iotbrokerconfig/iotBroker/config/couchdb.properties`.


Send some updates
-----------

Before history can be queried, it needs to exist. Therefore, the second step of the workflow is to send data to the IoT Broker in the form of *updateContextRequest* messages.

In this example, a time stamp is explicity specified in the form of NGSI metadata. While this makes the example reproducible, it is not necessary in real life to specify timestamps of updates - the IoT Broker will automatically add them whenever timestamps are not present.

The following examples assume that the IoT Broker is running on a machine with IP address `10.0.2.199` on port 8060.

```
HTTP POST to http://10.0.2.199:8060/ngsi10/updateContext
Content-Type:application/json
Accept:application/json

Body:
{
    "updateAction": "UPDATE",
    "contextElements": [{
        "entityId": {
            "id": "urn:x-iot:testbed1:sensor1",
            "isPattern": false
        },
        "attributes": [{
            "name": "temperature", 
            "type": "temperature ", 
            "contextValue": "26.00",
            "metadata": [{
                "name": "date",
                "type": "date",
                "value": "2015-08-18 16:54:36"
            }]
        }]
    }]
}

Reponse:
{"errorCode":null,"contextResponses":null}
```

```
HTTP POST to http://10.0.2.199:8060/ngsi10/updateContext
Content-Type:application/json
Accept:application/json

Body:
{
    "updateAction": "UPDATE",
    "contextElements": [{
        "entityId": {
            "id": "urn:x-iot:testbed1:sensor1",
            "isPattern": false
        },
        "attributes": [{
            "name": "temperature", 
            "type": "temperature ", 
            "contextValue": "27.00",
            "metadata": [{
                "name": "date",
                "type": "date",
                "value": "2015-08-18 16:54:37"
            }]
        }]
    }]
}

Response:
{"errorCode":null,"contextResponses":null}
```

```
HTTP POST to http://10.0.2.199:8060/ngsi10/updateContext
Content-Type:application/json
Accept:application/json

Body:
{
    "updateAction": "UPDATE",
    "contextElements": [{
        "entityId": {
            "id": "urn:x-iot:testbed1:sensor2",
            "isPattern": false
        },
        "attributes": [{
            "name": "noise", 
            "type": "noise ", 
            "contextValue": "55.00",
            "metadata": [{
                "name": "date",
                "type": "date",
                "value": "2015-08-18 16:54:38"
            }]
        }]
    }]
}

Response:
{"errorCode":null,"contextResponses":null}
```

The final update will not contain an explicit timestamp:

```
HTTP POST to http://10.0.2.199:8060/ngsi10/updateContext
Content-Type:application/json
Accept:application/json

Body:
{
    "updateAction": "UPDATE",
    "contextElements": [{
        "entityId": {
            "id": "urn:x-iot:testbed1:sensor1",
            "isPattern": false
        },
        "attributes": [{
            "name": "temperature", 
            "type": "temperature ", 
            "contextValue": "28.00"
        }]
    }]
}

Response:
{"errorCode":null,"contextResponses":null}
```

Query the history
-------

Now that the database is populated, the history can be queried using the *queryContext* operation.

The first query will ask for all entities with attributes *temperature* and *noise*; within the time interval *2015-08-
18T16:54:00+0200/2015-08-18T16:55:00+0200*.

```
HTTP POST to http://10.0.2.199:8060/ngsi10/queryContext
Content-Type:application/json
Accept:application/json

Body:
{
    "entities": [{
        "id": ".*",
        "isPattern": true
    }],
    "attributes": ["temperature","noise"],
    "restriction": {
        "attributeExpression": "",
        "scopes": [{
            "scopeType": "ISO8601TimeInterval",
            "scopeValue": "2015-08-18T16:54:00+0200/2015-08-18T16:55:00+0200"
        }]
    }
}

Response:
{
    "contextResponses": [{
        "contextElement": {
            "entityId": {
                "id": "urn:x-iot:testbed1:sensor2",
                "type": null,
                "isPattern": false
            },
            "attributeDomainName": null,
            "domainMetadata": [],
            "attributes": [{
                "name": "noise",
                "type": "noise",
                "contextValue": "55.00",
                "metadata": [{
                    "name": "date",
                    "type": "date",
                    "value": "2015-08-18 16:54:38"
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
                "id": "urn:x-iot:testbed1:sensor1",
                "type": null,
                "isPattern": false
            },
            "attributeDomainName": null,
            "domainMetadata": [],
            "attributes": [{
                "name": "temperature",
                "type": "temperature",
                "contextValue": "26.00",
                "metadata": [{
                    "name": "date",
                    "type": "date",
                    "value": "2015-08-18 16:54:36"
                }]
            }, {
                "name": "temperature",
                "type": "temperature",
                "contextValue": "27.00",
                "metadata": [{
                    "name": "date",
                    "type": "date",
                    "value": "2015-08-18 16:54:39"
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

The second query asks for  all entities with attributes *temperature* and *noise*, but this time without specifying a time interval. In this case the IoT Broker will respond with 
the list of the latest values matching the query.

```
HTTP POST to http://10.0.2.199:8060/ngsi10/queryContext
Content-Type:application/json
Accept:application/json

Body:
{
    "entities": [{
        "id": ".*",
        "isPattern": true
    }],
    "attributes": ["temperature","noise"]
}

Response:
{
    "contextResponses": [{
        "contextElement": {
            "entityId": {
                "id": "urn:x-iot:testbed1:sensor2",
                "type": null,
                "isPattern": false
            },
            "attributeDomainName": null,
            "domainMetadata": [],
            "attributes": [{
                "name": "noise",
                "type": "noise",
                "contextValue": "55.00",
                "metadata": [{
                    "name": "date",
                    "type": "date",
                    "value": "2015-08-18 16:54:38"
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
                "id": "urn:x-iot:testbed1:sensor1",
                "type": null,
                "isPattern": false
            },
            "attributeDomainName": null,
            "domainMetadata": [],
            "attributes": [{
                "name": "temperature",
                "type": "temperature",
                "contextValue": "28.00",
                "metadata": [{
                    "name": "creation_time",
                    "type": "creation_time",
                    "value": "2016.01.25 13:50:39:002+01:00"
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
