Historical Queries  on the IoT Broker
==================

From version 5.3.3 the IoT Broker has an optional feature for saving NGSI data to a database and responding to historical queries on that data.

The following workflow explains how to enable and use this feature.

Enable historical queries
-----------


####As docker
The docker container of the IoT Broker is already embedding a CouchDB server. Therefore just starting the docker container with the following parameter will enable the historical feature:

```
sudo docker run -t -p 8065:8065 -p 8060:8060  \
fiware/iotbroker:standalone-dev \
-p iotbroker_historicalagent="enabled"
```

NOTE: all the data stored in CouchDB will be canceled each time the docker will be stopped. In order to have permanent storage check the following ([Permanent Storage in IoT Broker docker](https://github.com/Aeronbroker/Aeron/blob/master/docker/README.md#permanent-storage-couchdb-postgresql-and-hsqldb))

####As java application

For enabling historical queries, the IoT Broker needs to be connected to an instance of couchDB. This is achieved by the following steps.

* The couchDB database needs to be installed either on the machine where the IoT Broker is running, or on a remote machine.

* The Historical Agent feature of the IoT Broker must be enabled. For doing so add the following line into the `IoTBroker-runner/iotbroker.conf.local`:
```
iotbroker_historicalagent="enabled"
```
then run
```
cd IoTBroker-runner/
./setup.sh
```
and then restart the IoT Broker.

* The above feature needs to be configured such that it knows the address and credentials of couchDB. In order to do so, add the following line in the file `IoTBroker-runner/iotbroker.conf.local`, and change them accordingly:
```
iotbroker_embeddedagent_couchdbname="historicalrepository"
iotbroker_embeddedagent_couchdbcreatedb=true
iotbroker_embeddedagent_couchdbprotocol="http"
iotbroker_embeddedagent_couchdbhost="127.0.0.1"
iotbroker_embeddedagent_couchdbport=5984
iotbroker_embeddedagent_historicallyTrackQueryResponseAndNotifications=false
iotbroker_embeddedagent_storeOnlyLatestValue=false
iotbroker_embeddedagent_localagentid="embeddedagent1"
```


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

```
Reponse:
```
{"errorCode":null,"contextResponses":null}
```

Another update with a different timestamp

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
```
Response:

```
{"errorCode":null,"contextResponses":null}
```

Another update with a different timestamp and different attribute

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
```
Response:
```
{"errorCode":null,"contextResponses":null}
```

The final update does not contain an explicit timestamp:

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
```
Response:
```
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
```
Response:
```
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

The second query asks for  all entities with attributes *temperature* and *noise*, but this time without specifying a time interval. In this case the IoT Broker will respond with the list of the latest values matching the query.

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

```

Response:
```
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

Admitted metadata name and date format
-------
The IoT Broker accepts different metadata name as timestamp metadata:

* "*date*" with time format like "*yyyy-MM-dd HH:mm:ss*" like "2015-08-18 16:54:36"

* "*creation_time*" with time format "*yyyy.MM.dd HH:mm:ss:SSS Z*" like "2017.09.13 10:38:14:838 +0100"

* "*nle:date*" with time format "*yyyy.MM.dd HH:mm:ss:SSS Z*" like "2017.09.13 10:38:14:838 +0100"

* "*endtime*" with time format "*yyyy.MM.dd HH:mm:ss:SSS Z*" like "2017.09.13 10:38:14:838 +0100".
(this is meant for attributes that refer to a timewindow with a starttime and endtime of observation)
