What you get
---

IoT Broker is an implementation of the IoT Broker Generic Enabler from [FIWARE catalogue](http://catalogue.fiware.org/enablers/iot-broker).
It is designed as a lightweight and scalable middleware component that separates IoT applications from the underlying device installations.
This implementation satisfies all properties described in [the specification of the FIWARE IoT Broker Generic Enabler](https://forge.fi-ware.org/plugins/mediawiki/wiki/fiware/index.php/FIWARE.OpenSpecification.IoT.Backend.IoTBroker).

The IoT Broker has unique properties that you will not find in other IoT Platforms:

* While IoT Broker decouples applications from underlying IoT device installations, it achieves this going far beyond the
common publish/subscribe paradigm. Instead, the IoT Broker actively communicates simultaneously with multiple IoT gateways
and devices in order to obtain exactly the information that is required by the running IoT applications.
As a result, information only is generated and exchanged when needed. This is in contrast to the state-of-the-art
middleware components where any piece of information - whether needed or not - is stored inside a central
repository (which is still available as an optional component of the IoT Broker).
* The IoT Broker has the ability to automatically translate information to the right abstraction level and therefore closes the
gap between information-centric applications and device-centric IoT installations.
For example, a simple device can typically only deliver values without being aware of the meaning of these values
in the application's context. On the other hand, IoT applications have to be written without consideration of the device
installations in order to be applicable in more than one specific environment. This gap is closed by IoT Broker by the use
of so-called associations between device-level and thing-level information.
* The IoT Broker is based on the simple and powerful information model standardized in OMA Next Generation Service Interface
Context Enabler (FIWARE NGSI). This API has emerged in FIWARE as an important open standard of information
exchange, implemented by a considerable number of FIWARE GEs. In FIWARE NGSI, all objects of the real world,
being it sensor/actuator devices or arbitrary objects (like tables, rooms, cars, ...) are represented as so-called
Context Entities, while information about these objects is expressed in the form of attributes. For more information
about the FIWARE NGSI information model and the related interfaces, please refer to the Open API Specification (https://forge.fi-ware.org/plugins/mediawiki/wiki/fiware/index.php/FI-WARE_NGSI_Open_RESTful_API_Specification).

Why you should get it
---

The main features of IoT Broker are:

* Offering a single point of contact to the user, hiding the complexity of the multi-provider nature of the
  Internet of Things.
* Collecting and aggregating information about thousands of real-world objects on behalf of the user.
* Provide means to assemble lower-level device information (device-centric access) into higher-level thing information
  (information-centric access).
