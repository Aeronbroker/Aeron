# Federation of IoT platforms

## Before to start

####Build the IoT Broker docker

Copy the docker file somewhere in the filesystem (lets assume in ~/AeronIoTBroker/):
```
wget https://github.com/Aeronbroker/Aeron/blob/master/docker/develop-standalone/dockerfile
```

Build the docker:
```
sudo docker build --no-cache=true -t fiware/iotbroker:standalone-dev .
```


####Build the Orion Context Broker docker

Create a file somewhere in the file system (lets assume in ~/Orion/) named “docker-compose.yml” (https://hub.docker.com/r/fiware/orion/) with the following content:

```
mongo:
   image: mongo:3.2
   command: --nojournal
 orion:
   image: fiware/orion
   links:
     - mongo
   ports:
     - "1026:1026"
   command: -dbhost mongo
```

## Orion as Data Exchange Platform, IoT Broker as IoT Single Domain

### NGSI-9 Type Federation: Test Case 1 (queryContext + Context Producer)

####Setting up Orion Context Broker as Data Exchange Platform


Run the following commands:

```
cd ~/Orion/
sudo docker-compose up
```


####Setting up IoT Broker as IoT Single Domain platform


Run the following command:
```
sudo docker run -t \
-p 8060:8060 \
-p 8065:8065 \
--name=iotbroker \
fiware/iotbroker:standalone-dev \
-p iotbroker_historicalagent="enabled"
-p iotbroker_pathprefixngsi9=””
-p iotbroker_ngsi9remoteurl=”http://localhost:8065/ngsi9/,http://localhost:1026/v1/registry/”
```


#### SubscribeContextAvailabilityRequest to IoT Broker on behalf of Orion
Request:
```
POST http://localhost:8065//ngsi9/subscribeContextAvailability
Content-Type: application/json
Accept: application/json
{
	"reference": "http://172.17.0.1:1026/ngsi9/notifyContextAvailability",
    	"entities": [{
        "id": ".*",
        "isPattern": true
    	}]
}
```
NOTE:  172.17.0.1 is the IP address of the docker daemon which is the gateway address for all the docker images. It might change from machine to machine and it can be configurable (see docker documentation).
Response:
```
{
    "duration": null,
    "errorCode": null,
    "subscribeId": "40925-04c2b-0A3C4-e1b17-40c0a-10949be15b6668ee1a1_-_1-e4ae48cf8371d825e25455c379b8a4d1"
}
```

#### UpdateContextRequest to IoT Broker (single domain platform)

Request to IoT Broker:

```
POST http://localhost:8060/ngsi10/updateContext
Content-Type: application/json
Accept: application/json

{
    "contextElements": [{
        "attributes": [{
            "contextValue": "13.48",
            "metadata": [{
                "name": "creation_time",
                "value": "2016.12.14 18:38:08:836 +0100",
                "type": "string"
            }, {
                "name": "Unit",
                "value": "m3-lite:DegreeCelsius",
                "type": "string"
            }],
            "name": "m3-lite:AirTemperature",
            "type": "string"
        }],
        "domainMetadata": [{
            "name": "SimpleGeolocation",
            "value": {
                "latitude": 43.46263,
                "longitude": -3.7988
            },
            "type": "point"
        }, {
            "name": "AbstractionLevel",
            "value": "0",
            "type": "string"
        }],
        "entityId": {
            "isPattern": false,
            "id": "sensor-2"
        }
    }],
    "updateAction": "APPEND"
}

```

Response:

```
{
    "errorCode": {
        "code": 200,
        "reasonPhrase": "OK",
        "details": ""
    },
    "contextResponses": null
}
```
#### QueryContext to Orion (data exchange platform)

Request:
```
POST http://localhost:1026/v1/queryContext
Content-Type: application/json
Accept: application/json

{
    "entities": [
        {
            "isPattern": "true",
            "id": ".*"
        }
    ]
}
```

Response:
```
{
    "contextResponses": [
        {
            "contextElement": {
                "type": "null",
                "isPattern": "false",
                "id": "sensor-2",
                "attributes": [
                    {
                        "name": "m3-lite:AirTemperature",
                        "type": "string",
                        "value": "13.48",
                        "metadatas": [
                            {
                                "name": "creation_time",
                                "type": "string",
                                "value": "2016.12.14 18:39:08:836 +0100"
                            },
                            {
                                "name": "Unit",
                                "type": "string",
                                "value": "m3-lite:DegreeCelsius"
                            }
                        ]
                    },
                    {
                        "name": "position",
                        "type": "geo:point",
                        "value": "43.46263, -3.7988"
                    }
                ]
            },
            "statusCode": {
                "code": "200",
                "reasonPhrase": "OK"
            }
        }
    ]
}
```
