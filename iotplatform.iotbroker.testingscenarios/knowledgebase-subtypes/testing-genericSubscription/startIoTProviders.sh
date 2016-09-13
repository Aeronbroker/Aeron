IOTDISCOVERY_URL="http://localhost:8065"
NGSIEMULATOR_HOME="/opt/Aeron/eu.neclab.iotplatform.ngsiemulator/target/"
VERSION="5.4.1-SNAPSHOT"

#Start First Provider
echo -e "eu.neclab.ioplatform.ngsiemulator.iotprovider.ports=8001\neu.neclab.ioplatform.ngsiemulator.iotprovider.mode=random\neu.neclab.ioplatform.ngsiemulator.iotprovider.doRegistration=false" > /tmp/iotprovider1.ini
gnome-terminal --working-directory=$NGSIEMULATOR_HOME -x sh -c "java -cp ngsiemulator-$VERSION-jar-with-dependencies.jar -Deu.neclab.ioplatform.ngsiemulator.iotprovider.configurationFile=\"/tmp/iotprovider1.ini\" eu.neclab.iotplatform.ngsiemulator.server.MainIoTProvider"

ngsi9-registercontext -e Bus1 -t http://www.semanticweb.org/neclab/smartsantander/NGSI#BusSensor -a temperature -v http://localhost:8001/ngsi10/ -u $IOTDISCOVERY_URL/ngsi9/registerContext



#Start Second Provider
echo -e "eu.neclab.ioplatform.ngsiemulator.iotprovider.ports=8002\neu.neclab.ioplatform.ngsiemulator.iotprovider.mode=random\neu.neclab.ioplatform.ngsiemulator.iotprovider.doRegistration=false" > /tmp/iotprovider2.ini
gnome-terminal --working-directory=$NGSIEMULATOR_HOME -x sh -c "java -cp ngsiemulator-$VERSION-jar-with-dependencies.jar -Deu.neclab.ioplatform.ngsiemulator.iotprovider.configurationFile=\"/tmp/iotprovider2.ini\" eu.neclab.iotplatform.ngsiemulator.server.MainIoTProvider"

ngsi9-registercontext -e Node1 -t http://www.semanticweb.org/neclab/smartsantander/NGSI#Node -a temperature -v http://localhost:8002/ngsi10 -u $IOTDISCOVERY_URL/ngsi9/registerContext


#Start First Application
gnome-terminal --working-directory=$NGSIEMULATOR_HOME -x sh -c "java -cp ngsiemulator-$VERSION-jar-with-dependencies.jar eu.neclab.iotplatform.ngsiemulator.server.MainApplication"


