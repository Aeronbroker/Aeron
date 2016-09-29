Semantic Grounding tutorial
============================

This tutorial is meant to demonstrate the capabilities of the Semantic Grounding feature of the IoT Broker.

Enable Semantic Grounding
----
see https://github.com/Aeronbroker/Aeron/blob/master/doc/semanticgrounding.md
 
Simple Query
---

Start the IoT Knowledge server
```
cd NECIoTKnowledge/
mvn spring-boot:run
```

Start the IoT Broker.
```
cd Aeron/IoTBroker-runner
./unix64_start-IoTBroker_as_Daemon.sh
```

Reset (please note that this will delete all the Registration and Subscription previously made) and start the NEConfMan as IoT Discovery (please note that only the NEConfMan component support this feature).
```
cd NEConfMan/ConfMan_Runner
./startConfigurationMananager.sh -r
```

Start the NGSI emulator
```
cd Aeron/iotplatform.iotbroker.testingscenarios/knowledgebase-subtypes/testing-query/
./startIoTProviders.sh
```
which will start 2 IoT Provider (one for each newly opened terminal), one for a Context Entity of type Node and one of type BusSensor (which is a subtype of Node).

Now we query a specific Bus Sensor:

```
Aeron/eu.neclab.iotplatform.ngisemulator/ngsi10-querycontext -e ".*" -a temperature -t http://www.semanticweb.org/neclab/smartsantander/NGSI#Node -p true -u http://localhost:8070/ngsi10/queryContext
```

and we will have as reply:
```
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<queryContextResponse>
  <contextResponseList>
    <contextElementResponse>
      <contextElement>
        <entityId type="http://www.semanticweb.org/neclab/smartsantander/NGSI#Node" isPattern="false">
          <id>Node1</id>
        </entityId>
        <contextAttributeList>
          <contextAttribute>
            <name>temperature</name>
            <contextValue>-2135021789</contextValue>
          </contextAttribute>
          <contextAttribute>
            <name>temperature</name>
            <contextValue>-2135021789</contextValue>
          </contextAttribute>
        </contextAttributeList>
        <domainMetadata/>
      </contextElement>
      <statusCode>
        <code>200</code>
        <reasonPhrase>OK</reasonPhrase>
        <details/>
      </statusCode>
    </contextElementResponse>
  </contextResponseList>
</queryContextResponse>
```


And now we try to query to a more generic type which is a supertype of BusSensor
```
Aeron/eu.neclab.iotplatform.ngisemulator/ngsi10-querycontext -e ".*" -a temperature -t http://www.semanticweb.org/neclab/smartsantander/NGSI#BusSensor -p true -u http://localhost:8070/ngsi10/queryContext
```

and we will have two contextElements, one for Bus Sensor and one for Node sensors.

```
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<queryContextResponse>
  <contextResponseList>
    <contextElementResponse>
      <contextElement>
        <entityId type="http://www.semanticweb.org/neclab/smartsantander/NGSI#Node" isPattern="false">
          <id>Node1</id>
        </entityId>
        <contextAttributeList>
          <contextAttribute>
            <name>temperature</name>
            <contextValue>2025636933</contextValue>
          </contextAttribute>
          <contextAttribute>
            <name>temperature</name>
            <contextValue>2025636933</contextValue>
          </contextAttribute>
        </contextAttributeList>
        <domainMetadata/>
      </contextElement>
      <statusCode>
        <code>200</code>
        <reasonPhrase>OK</reasonPhrase>
        <details/>
      </statusCode>
    </contextElementResponse>
    <contextElementResponse>
      <contextElement>
        <entityId type="http://www.semanticweb.org/neclab/smartsantander/NGSI#BusSensor" isPattern="false">
          <id>Bus1</id>
        </entityId>
        <contextAttributeList>
          <contextAttribute>
            <name>temperature</name>
            <contextValue>-2058854939</contextValue>
          </contextAttribute>
          <contextAttribute>
            <name>temperature</name>
            <contextValue>-2058854939</contextValue>
          </contextAttribute>
        </contextAttributeList>
        <domainMetadata/>
      </contextElement>
      <statusCode>
        <code>200</code>
        <reasonPhrase>OK</reasonPhrase>
        <details/>
      </statusCode>
    </contextElementResponse>
  </contextResponseList>
</queryContextResponse>
```

Specific Subscription
---
Reset (please note that this will delete all the subscription in the local HSQLDB of the IoT Broker) and start the IoT Broker.
```
cd Aeron/IoTBroker-runner
./unix64_start-IoTBroker_as_Daemon.sh --resetIoTBroker
```

Reset (please note that this will delete all the Registration and Subscription previously made) and start the NEConfMan as IoT Discovery (please note that only the NEConfMan component support this feature).
```
cd NEConfMan/ConfMan_Runner
./startConfigurationMananager.sh -r
```

Start the NGSI emulator
```
cd Aeron/iotplatform.iotbroker.testingscenarios/knowledgebase-subtypes/testing-specificSubscription/
./startIoTProviders.sh
```
which will starts again 2 IoT Provider, one for a Context Entity of type Node and one of type BusSensor (which is a subtype of Node) and an IoT Application. Each on a newly opened terminal.

Subscribe to a specific kind of sensor: BusSensor
```
Aeron/eu.neclab.iotplatform.ngisemulator/ngsi10-subscribecontext -e ".*",http://www.semanticweb.org/neclab/smartsantander/NGSI#BusSensor,true -a temperature -r http://localhost:8101/ngsi10/notify -u http://localhost:8070/ngsi10/subscribeContext
```

At this point a stream of NotifyConditions of ContextElement regarding ContextEntity of type BusSensor will be displayed in the terminal of the IoT Application.

Generic Subscription
---
Reset (please note that this will delete all the subscription in the local HSQLDB of the IoT Broker) and start the IoT Broker.
```
cd Aeron/IoTBroker-runner
./unix64_start-IoTBroker_as_Daemon.sh --resetIoTBroker
```

Reset (please note that this will delete all the Registration and Subscription previously made) and start the NEConfMan as IoT Discovery (please note that only the NEConfMan component support this feature).
```
cd NEConfMan/ConfMan_Runner
./startConfigurationMananager.sh -r
```

Start the NGSI emulator
```
cd Aeron/iotplatform.iotbroker.testingscenarios/knowledgebase-subtypes/testing-genericSubscription/
./startIoTProviders.sh
```
which will starts again 2 IoT Provider, one for a Context Entity of type Node and one of type BusSensor (which is a subtype of Node) and the IoT Application. Each on a newly opened terminal.

But now we will subscribe for to a more generic ContextEntity type, Node, which is a superclass of BusSensor.
```
Aeron/eu.neclab.iotplatform.ngisemulator/ngsi10-subscribecontext -e ".*",http://www.semanticweb.org/neclab/smartsantander/NGSI#Node,true -a temperature -r http://localhost:8101/ngsi10/notify -u http://localhost:8070/ngsi10/subscribeContext
```

Now a stream ContextElement related to ContextEntity of both Node and BusSensor (even if not specifically requested in the subscription) flow to the IoT Application and displayed in the IoT Application terminal.

Generic Subscription with a late Context Entity Registration
---
Reset (please note that this will delete all the subscription in the local HSQLDB of the IoT Broker) and start the IoT Broker.
```
cd Aeron/IoTBroker-runner
./unix64_start-IoTBroker_as_Daemon.sh --resetIoTBroker
```

Reset (please note that this will delete all the Registration and Subscription previously made) and start the NEConfMan as IoT Discovery (please note that only the NEConfMan component support this feature).
```
cd NEConfMan/ConfMan_Runner
./startConfigurationMananager.sh -r
```

Start the NGSI emulator
```
cd Aeron/iotplatform.iotbroker.testingscenarios/knowledgebase-subtypes/testing-genericSubscription/
./startIoTProviders.sh
```
which will starts again 2 IoT Provider, one for a Context Entity of type Node and one of type BusSensor (which is a subtype of Node) and the IoT Application. Each on a newly opened terminal.
In this case, differently than before, one of the provider (responsible for the ContextEntity of BusSensor type)  are not automatically registered, so the IoT platform is not aware of its existence.

If we now make a subscription:
```
Aeron/eu.neclab.iotplatform.ngisemulator/ngsi10-subscribecontext -e ".*",http://www.semanticweb.org/neclab/smartsantander/NGSI#Node,true -a temperature -r http://localhost:8101/ngsi10/notify -u http://localhost:8070/ngsi10/subscribeContext
```

a stream of ContextElement related to ContextEntity of  Node type flow to the IoT Application. As shown in the IoT Application terminal.

If then we declare the availability of the ContextEntity of BusSensor type with a registration:
```
Aeron/eu.neclab.iotplatform.ngisemulator/ngsi9-registercontext -e Bus1 -t http://www.semanticweb.org/neclab/smartsantander/NGSI#BusSensor -a temperature -v http://localhost:8001/ngsi10/ -u http://localhost:8065/ngsi9/registerContext
```

Also the IoT Provider starts to issue NotifyContext to the IoT Application, although the latter has not specifically request for such type of ContextEntity.
