IOTDISCOVERY_URL="http://localhost:8065"
MOCKS_HOME="/home/cirillo/mycode/eclipseWorkspace/workspace_IoTBroker_git_RemaRepository/IoTBroker-public/eu.neclab.iotplatform.mocks/target/"
VERSION="5.4.1-SNAPSHOT"

#Start First Provider
echo -e "eu.neclab.ioplatform.mocks.iotprovider.ports=8001\neu.neclab.ioplatform.mocks.iotprovider.mode=random\neu.neclab.ioplatform.mocks.iotprovider.doRegistration=false" > /tmp/iotprovider1.ini
gnome-terminal --working-directory=$MOCKS_HOME -x sh -c "java -cp mocks-$VERSION-jar-with-dependencies.jar -Deu.neclab.ioplatform.mocks.iotprovider.configurationFile=\"/tmp/iotprovider1.ini\" eu.neclab.iotplatform.mocks.server.MainIoTProvider"

#ngsi9-registercontext -e Bus1 -t http://www.semanticweb.org/neclab/smartsantander/NGSI#BusSensor -a temperature -v http://localhost:8001/ngsi10/ -u $IOTDISCOVERY_URL/ngsi9/registerContext



#Start Second Provider
echo -e "eu.neclab.ioplatform.mocks.iotprovider.ports=8002\neu.neclab.ioplatform.mocks.iotprovider.mode=random\neu.neclab.ioplatform.mocks.iotprovider.doRegistration=false" > /tmp/iotprovider2.ini
gnome-terminal --working-directory=$MOCKS_HOME -x sh -c "java -cp mocks-$VERSION-jar-with-dependencies.jar -Deu.neclab.ioplatform.mocks.iotprovider.configurationFile=\"/tmp/iotprovider2.ini\" eu.neclab.iotplatform.mocks.server.MainIoTProvider"

ngsi9-registercontext -e Node1 -t http://www.semanticweb.org/neclab/smartsantander/NGSI#Node -a temperature -v http://localhost:8002/ngsi10 -u $IOTDISCOVERY_URL/ngsi9/registerContext


#Start First Application
gnome-terminal --working-directory=$MOCKS_HOME -x sh -c "java -cp mocks-$VERSION-jar-with-dependencies.jar eu.neclab.iotplatform.mocks.server.MainApplication"


