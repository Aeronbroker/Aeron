IoT Broker Docker file
=======================

IoT Broker is the FIWARE reference implementation of the IoT Broker Generic Enabler provided by NEC.

This dockerfile can be used for building a docker image running the IoT Broker generic enabler. The build process will install the latest version available on GitHub (https://github.com/Aeronbroker/Aeron).

Running the image will create a docker container with the IoT Broker running and listening to port 8060 and the log of the IoTBroker will be shown (you may need root permissions):

```
docker run -p 8060:8060 fiware/iotbroker
```

If you want to run the IoTBroker docker container in background use the following:
```
docker run -p 8060:8060 fiware/iotbroker > /dev/null &
```
