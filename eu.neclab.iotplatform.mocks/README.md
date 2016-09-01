Start IoT Provider, mode random:

java -cp mocks-5.4.1-SNAPSHOT-jar-with-dependencies.jar -Deu.neclab.ioplatform.mocks.iotprovider.ports="8001-8010" -Deu.neclab.ioplatform.mocks.iotprovider.rangesOfEntityIds="1-30" -Deu.neclab.ioplatform.mocks.iotprovider.numberOfEntityIdsToSelect="10" -Deu.neclab.ioplatform.mocks.iotprovider.rangesOfAttributes="1-5" -Deu.neclab.ioplatform.mocks.iotprovider.numberOfAttributesToSelect="3" -Deu.neclab.iotplaform.mocks.iotprovider.defaultIncomingContentType="application/json" -Deu.neclab.iotplaform.mocks.iotprovider.defaultOutgoingContentType="application/json" -Deu.neclab.ioplatform.mocks.iotDiscoveryUrl="http://localhost:8065/" eu.neclab.iotplatform.mocks.server.MainIoTProvider


Start IoT Provider with configuration file

java -cp mocks-5.4.1-SNAPSHOT-jar-with-dependencies.jar -Deu.neclab.ioplatform.mocks.iotprovider.configurationFile="/home/cirillo/mycode/eclipseWorkspace/workspace_IoTBroker_git_RemaRepository/IoTBroker-public/eu.neclab.iotplatform.mocks/iotprovider-configuration.ini" eu.neclab.iotplatform.mocks.server.MainIoTProvider


