Semantic in the IoT Broker
==================

The IoT Broker has an optional feature for having semantic capabilities. The current status is the support for checking **subtypes** of a certain type when a query or subscription is done.

NOTE: This feature will work only if the same feature is enable in the NEConfMan: https://github.com/Aeronbroker/NEConfMan/doc/semanticgrounding.md

The following workflow explains how to enable and use this feature.

Enable the Semantic feature
-----------
For enabling the semantic feature, the IoT Broker needs to be connected to an instance of the **NECKnowledgeBase** server (https://github.com/Aeronbroker/NECKnowledgeBase). This is achieved by the following steps.
* The NECKnowledgeBase server needs to be installed either on the machine where the IoT Broker is running, or on a remote machine.
* The IoT Broker plugin eu.neclab.iotplatform.knowledgebase needs to be activated. This can be done by setting the following configuration in the IoTBroker-runner/iotbroker.conf.local:
```
iotbroker_semantic="enabled"
```
If not set, the default value in IoTBroker-runner/iotbroker.conf.deafult is *enabled*. Then run
```
./setup.sh
```
In orde to better understand the configuration procedure please read: https://github.com/Aeronbroker/Aeron#configure-the-iot-broker-with-setup-scripts.
* Configure the activated plugin by adding the following configurations in the IoTBroker-runner/iotbroker.conf.local:
```
# Knowledge Base configuration
iotbroker_knowledgebaseaddress='http://127.0.0.1'
iotbroker_knowledgebaseport=8015
```
If not set, the default values in IoTBroker-runner/iotbroker.conf.deafult are * http://127.0.0.1 * and *8015*. Then run
```
./setup.sh
```
