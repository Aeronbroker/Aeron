IoT Broker Docker file
=======================

IoT Broker is the FIWARE reference implementation of the IoT Broker Generic Enabler provided by NEC.

This dockerfile can be used for building a docker image running the IoT Broker generic enabler. The build process will install the latest version available on GitHub (https://github.com/Aeronbroker/Aeron).

Running the image will create a docker container with the IoT Broker running and listening to port 8060 and the log of the IoTBroker will be shown (you may need root permissions):
```
docker run -t -p 8060:8060 fiware/iotbroker:v5.4.3
```


In case you want to have the standalone component of the IoT Broker GE (IoT Broker GEri + NEConfMan), run the following command:
```
docker run -t -p 8065:8065 -p 8060:8060 -p 5984:5984 fiware/iotbroker:v5.4.3-standalone
```
 
Both the IoT Broker GEri and NEConfMan will be accessible respectively at port 8060 and 8065. In addition CouchDB will be exposed to the port 5984.

In order to configure the IoT Broker and/or the NEConfMan, run the docker with the following command:

```
docker run -t -p 8065:8065 -p 8060:8060 fiware/iotbroker:v5.4.3-standalone -p <iotbroker_key>="<value>" -p <confman_key>="<value>" [-p ...]
```

where *iotbroker_key* is one of the parameters available in the *IoTBroker_Runner/iotbroker.conf.default* and *confman_key* one of the parameters available in the *ConfMan_Runner/confman.conf.default*.
Please note that such configurations are runtime properties and they will be forgotten the next time the docker is run.