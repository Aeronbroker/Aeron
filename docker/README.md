IoT Broker Docker file
=======================

IoT Broker is the FIWARE reference implementation of the IoT Broker Generic Enabler provided by NEC.

This dockerfile can be used for building a docker image running the IoT Broker generic enabler. The build process will install the latest version available on GitHub (https://github.com/Aeronbroker/Aeron).

Running the image will create a docker container with the IoT Broker running and listening to port 80. When running the docker image in attached mode, the user will be dropped into a console of the OSGi environment hosting the IoT Broker.