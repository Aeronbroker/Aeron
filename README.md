Aeron
=====

Aeron is an Internet-of-Things middleware based on the OMA NGSI 9/10 standard.   

What you get
------------

Aeron is an implementation of the IoT Broker Generic Enabler from FI-WARE. It is specified as a lightweight and scalable 
middleware component that separates IoT applications from the underlying device installations. This implementation 
satisfies all properties described in the specification of the Generic Enabler.

Aeron has unique properties that you will not find in other IoT Platforms:

While the Aeron decouples applications from underlying IoT device installations, it achieves this going far beyond the 
common publish/subscribe paradigm. Instead, Aeron actively communicates simultaneously with large quantities IoT gateways 
and devices in order to obtain exactly the information that is required by the running IoT applications. 
As a result, information only is generated and exchanged when needed. This is in contrast to the state-of-the-art 
middleware components where any piece of information - whether needed or not - is just ''dumped'' inside a central 
repository.
Aeron has the ability to automatically translate information to the right abstraction level and therefore closes the 
gap between information-centric applications and device-centric IoT installations. 
For example, a simple device can typically only deliver values without being aware of the meaning of these values 
in the application's context. On the other hand, IoT applications have to written without consideration of the device 
installations in order to be applicable in more than one specific environment. This gap is closed by Aeron by the use 
of so-called associations between device-level and thing-level information.
Aeron is based on the simple and powerful information model standardized in OMA Next Generation Service Interface 
Context Enabler (NGSI 9 / NGSI 10). This API has emerged in FI-WARE as an important open standard of information 
exchange, implemented by a considerable number of FI-WARE GEs. In NGSI 9/10, all objects of the real world, 
being it sensor/actuator devices or arbitrary objects (like tables, rooms, cars, ...) are represented as so-called 
Context Entities, while information about these objects is expressed in the form of attributes. For more information 
about the OMA NGSI 9/10 information model and the related interfaces, please refer to the Open API Specification.

Why should you get it 
---------------------

The main features of Aeron are:

* Offering a single point of contact to the user, hiding the complexity of the multi-provider nature of the 
  Internet of Things.
* Collecting and aggregating information about thousands of real-world objects on behalf of the user.
* Provide means to assemble lower-level device information (device-centric access) into higher-level Thing information 
  (information-centric access).
  
