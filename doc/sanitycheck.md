Sanity Check Procedures
---

The Sanity Check Procedures are the steps that a System Administrator will take to verify that an installation is ready to be tested. This is therefore a preliminary set of tests to ensure that obvious or basic malfunctioning is fixed before proceeding to unit tests, integration tests and user validation.

First Sanity Check
----

To verify that the IoT Broker is running, access the URL of its hosting machine on the IoT Broker port (default:8060) using a web browser; e.g. *localhost:8060* on the hosting machine. If the IoT Broker home page is not shown, it means that the IoT Broker does not run correctly.
Another way to see wether the IoT Broker is correctly running it is possible to retrieve the version of the IoT Broker by requesting the `sanityCheck` resource:

```
curl http://{IoTBroker_IP}:{IoTBroker_Port}/sanityCheck
```

The response will lok like the following:

```
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<sanityCheck>
  <name>IoT Broker GE</name>
  <type>Sanity Check</type>
  <version>Version: 6.1.0.SNAPSHOT</version>
</sanityCheck>
```

End to End testing
----

An end-to-end test is to send a query to the FIWARE NGSI interface of IoT Broker and receive the response.

It is possible to test one of the supported NGSI-10 resources. For example let us try to send an
HTTP GET to the *contextEntity/EntityId* resource `http://{IoTBroker_IP}:{IoTBroker_Port}/ngsi10/contextEntities/Kitchen`.

You should get back an XML response, with an ERROR CODE, similar to this:
```
  <contextElementResponse>
 	<contextElement>
 		<entityId isPattern="false">
 			<id>Kitchen</id>
 		</entityId>
 	</contextElement>
 	<statusCode>
 		<code>500</code>
 		<reasonPhrase>RECEIVER INTERNAL ERROR</reasonPhrase>
 		<details xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 			xmlns:xs="http://www.w3.org/2001/XMLSchema" xsi:type="xs:string">Error I/O with:
 			http://localhost:8999</details>
 	</statusCode>
  </contextElementResponse>
```
or
```
  <contextElementResponse>
 	<contextElement>
 		<entityId   isPattern="false">
 			<id>Kitchen</id>
 		</entityId>
 	</contextElement>
 	<statusCode>
 		<code>404</code>
 		<reasonPhrase>CONTEXT ELEMENT NOT FOUND</reasonPhrase>
 	</statusCode>
  </contextElementResponse>
```
If you get the first error this means that there is no communication between the IoT Broker GE and an instance of the IoT Discovery GE. In case of the second error this means that the entity that you are requesting is not available. 

List of Running Processes
----

* Only JavaVM process

Network interfaces Up & Open
----

* HTPP standard port (by default 8060) should be opened.
* HTPPS port (by default 9443) should be opened.
