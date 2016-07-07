# This is the dockerfile for IoT Broker version 5.3.3, September 2016

FROM ubuntu

MAINTAINER 'NEC Laboratories Europe: iotplatform@neclab.eu, Flavio Cirillo: flavio.cirillo@neclab.eu'

#install CouchDB
RUN apt-get update
RUN apt-get install couchdb -y
RUN mkdir /var/run/couchdb
RUN chown -R couchdb:couchdb /var/run/couchdb

#install Maven, Java and GIT
RUN apt-get install maven default-jdk git -y

#Download the IoTBroker SourceCode
WORKDIR /root
RUN git clone https://github.com/Aeronbroker/Aeron.git

#Compile the SourceCode
WORKDIR /root/Aeron/IoTbrokerParent
RUN mvn install
WORKDIR /root/Aeron/eu.neclab.iotplatform.iotbroker.builder/
RUN mvn install

#Setup the IoTBroker
WORKDIR /root/Aeron/IoTBroker-runner
RUN chmod +x *.sh iotbroker.conf.default
RUN ./setup.sh --auto --propagateauto

#Copy the service file to the init.d folder
RUN printf "#!/bin/bash\n\n/usr/bin/couchdb > /dev/null &\n\n./unix64_exec.sh "$@" && tail -F /root/Aeron/IoTBroker-runner/logs/iotbroker.log 2> /dev/null" > iotbroker_dockerentrypoint.sh
RUN chmod +x iotbroker_dockerentrypoint.sh

#Expose the port
EXPOSE 8060

#Start the IoTBroker
ENTRYPOINT ["./iotbroker_dockerentrypoint.sh"]
