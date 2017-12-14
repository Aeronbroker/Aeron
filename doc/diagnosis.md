Diagnosis Procedures
---
The Diagnosis Procedures are the first steps that a System Administrator will take to locate the source of an error in a GE. Once the nature of the error is identified with these tests, the system admin will very often have to resort to more concrete and specific testing to pinpoint the exact point of error and a possible solution. Such specific testing is out of the scope of this section.


Resource availability
----

The minimum of RAM needed for running the IoT Broker is 256MB, however our recommandation is to have 512MB of available RAM.
IoT Broker binaries need about 30MB of hard disk space. However, as the IoT Broker will use an internal database for storing subscription information, it is recommended to have at least 100MB of space available on the disk.

Remote Service Access
----

Depending on the setup, the IoT Broker communicates via the FIWARE NGSI interface with the following FIWARE components.

* An instance of the IoT Discovery GE. This is mandatory, as otherwise the IoT Broker cannot determine which data sources are available and thus cannot deliver any data. The address of this component is configurable in the IoT Broker config files (see above).
* A default NGSI data consumer/broker where NGSI data updates are forwarded to. This is typically an instance of the Context Broker GE. However, this can be in principle any FIWARE NGSI-compliant component, and such a component is not present in all installations. The address of this component is configurable in the IoT Broker config files (see above).
* One or more NGSI-compliant data sources, e.g. instances of the Data Handling GE, or Context Broker GE instances. 

**Please make sure the chosen port (by deafult 8060) is accessible.**

Resource consumption
----

In the idle State the IoT Broker consumption is:

* Memory consumption = 20MB (idle state)
* CPU Usage = 0% (idle state)
* Thread runnning = 21 (idle state)

If the IoT Broker process uses more then 500MB and the CPU usage is above 40% in the idle state, this can be considered abnormal.

I/O flows
----
The only expected I/O flow is of type HTTP, on the chosen port (by deafult 8060) (Tomcat Server).
