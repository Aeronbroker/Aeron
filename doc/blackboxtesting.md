Testing: Using the Black Box Test
---
While individual classes of the IoT Broker are already tested during compilation with Maven, the project *iotplatform.iotbroker.blackboxtest* is a dedicated black box test for a running instance of the IoT Broker using server mocks. It is based on the JUNIT-Framework.
These tests assume that the IoT Broker is running and listening to port 80, it produces xml messages and it is set to communicates with an IoT Discovery component at port 8002 on localhost. This corresponds to the default configuration. All those IoTBroker settings can be configured with the setup scripts, see (readme.md#configure-the-iot-broker-with-setup-scripts), respectively through iotbroker_tomcatinitport, iotbroker_producedtype and iotbroker_ngsi9uri variables

To run the tests, navigate to the directory *iotplatform.iotbroker.blackboxtest* and compile the project by the command

```
mvn test
```

The IoT Broker instance should be already running at the time of compilation. Please note that a deployment of IoT Broker including at least all bundles loaded by the pre-configured OSGi environment ('IoTBroker-runner' folder) is necessary for the tests to run successfully.

To let the blackboxtest start, first it is necessary to install on the local Maven repository the IoT Broker components. For doing so please run a 'mvn install' in the eu.neclab.iotplatform.iotbroker.builder folder.

In case the IoTBroker is running at different port or the port 8031, 8032 or 8002 is not available in your machine you can set those in the BlackBoxTest with the following command:

```
mvn test -Dblackboxtest.iotbroker.port=8070 -Dblackboxtest.iotdiscoverymock.port=8061 -Dblackboxtest.agentmock1.port=8031 -Dblackboxtest.agentmock2.port=8032
```
