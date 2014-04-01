Aeron
=====

Aeron is an Internet-of-Things middleware based on the OMA NGSI 9/10 standard. It has been implemented by NEC Laboratories Europe as part of their 
contribution to the European Project FI-WARE.

Aeron Documentation
---

| Document                    | Reference                                                                                                        |
| --------------------------- | ---------------------------------------------------------------------------------------------------------------- |
| FI-WARE wiki                | https://forge.fi-ware.org/plugins/mediawiki/wiki/fiware/index.php/Main_Page                                      |
| IoT Broker GE specification | https://forge.fi-ware.org/plugins/mediawiki/wiki/fiware/index.php/FIWARE.OpenSpecification.IoT.Backend.IoTBroker |
| NGSI 9/10 API specification | https://forge.fi-ware.org/plugins/mediawiki/wiki/fiware/index.php/FI-WARE_NGSI_Open_RESTful_API_Specification    |
| Aeron in FI-WARE catalogue  | http://catalogue.fi-ware.org/enablers/nec-iot-broker                                                             |


What you get
---

Aeron is an implementation of the IoT Broker Generic Enabler from FI-WARE (http://catalogue.fi-ware.org/enablers/nec-iot-broker). 
It is specified as a lightweight and scalable middleware component that separates IoT applications from the underlying device installations. 
This implementation satisfies all properties described in the specification of the FI-WARE Generic Enabler (https://forge.fi-ware.org/plugins/mediawiki/wiki/fiware/index.php/FIWARE.OpenSpecification.IoT.Backend.IoTBroker).

Aeron has unique properties that you will not find in other IoT Platforms:

* While the Aeron decouples applications from underlying IoT device installations, it achieves this going far beyond the 
common publish/subscribe paradigm. Instead, Aeron actively communicates simultaneously with large quantities IoT gateways 
and devices in order to obtain exactly the information that is required by the running IoT applications. 
As a result, information only is generated and exchanged when needed. This is in contrast to the state-of-the-art 
middleware components where any piece of information - whether needed or not - is just ''dumped'' inside a central 
repository.
* Aeron has the ability to automatically translate information to the right abstraction level and therefore closes the 
gap between information-centric applications and device-centric IoT installations. 
For example, a simple device can typically only deliver values without being aware of the meaning of these values 
in the application's context. On the other hand, IoT applications have to written without consideration of the device 
installations in order to be applicable in more than one specific environment. This gap is closed by Aeron by the use 
of so-called associations between device-level and thing-level information.
* Aeron is based on the simple and powerful information model standardized in OMA Next Generation Service Interface 
Context Enabler (NGSI 9 / NGSI 10). This API has emerged in FI-WARE as an important open standard of information 
exchange, implemented by a considerable number of FI-WARE GEs. In NGSI 9/10, all objects of the real world, 
being it sensor/actuator devices or arbitrary objects (like tables, rooms, cars, ...) are represented as so-called 
Context Entities, while information about these objects is expressed in the form of attributes. For more information 
about the OMA NGSI 9/10 information model and the related interfaces, please refer to the Open API Specification (https://forge.fi-ware.org/plugins/mediawiki/wiki/fiware/index.php/FI-WARE_NGSI_Open_RESTful_API_Specification).

Why you should get it 
---

The main features of Aeron are:

* Offering a single point of contact to the user, hiding the complexity of the multi-provider nature of the 
  Internet of Things.
* Collecting and aggregating information about thousands of real-world objects on behalf of the user.
* Provide means to assemble lower-level device information (device-centric access) into higher-level Thing information 
  (information-centric access).
  

Directory Structure
---
.
├── IoTbrokerParent                      Maven parent
├── iotbroker.builder                    Maven builder
├── iotbroker.client                     HTTP client
├── iotbroker.commons                    Commons package
├── iotbroker.core                       Core package
├── iotbroker.resultfilter               Result filter package
├── iotbroker.restcontroller             HTTP REST interface
├── iotbroker.storage                    Internal storage
├── ngsi.api                             NGSI 9/10 API
├── lib								                   Folder contains dependencies
├── tomcat-configuration-fragment 		   Tomcat configuration

Building Aeron Source Code
---

Aeron uses the following libraries as build dependencies:

* JAVA JDK 7 or OpenJDK 7
* MAVEN 3
* httpclient-4.2.0-osgi.jar ( present in the lib folder )
* httpcore-4.2.0-osgi.jar ( present in the lib folder )

The basic procedure for compiling Areon is the following:

* Before to compile the software with Maven, please install the following two libraries 
  httpclient-4.2.0-osgi.jar and httpcore-4.2.0-osgi.jar manually in your local Maven .m2 repository. 
  For doing this, navigate into the lib folder and run the two following commands:

```
mvn install:install-file -DgroupId=org.apache.httpcomponents -DartifactId=httpclient -Dversion=4.2.0-osgi -Dfile=httpclient-4.2.0-osgi.jar -Dpackaging=jar -DgeneratePom=true

mvn install:install-file -DgroupId=org.apache.httpcomponents -DartifactId=httpcore -Dversion=4.2.0-osgi -Dfile=httpclient-4.2.0-osgi.jar -Dpackaging=jar -DgeneratePom=true
```

* After having installed the two libraries, navigate into the IoTBrokerParent folder and compile the pom file as follows:

```
mvn install
```

* The next step is to build Aeron. Navigate into the eu.neclab.iotplatform.iotbroker.builder folder and use Maven for compiling the pom file:

```
mvn install
```

  This command will generate 8 OSGI bundles inside the eu.neclab.iotplatform.iotbroker.builder\target\iotbroker.builder-3.3.3-assembly\bundle folder:

```
iotbroker.client-3.3.3.jar
iotbroker.commons-3.3.3.jar
iotbroker.core-3.3.3.jar
iotbroker.ext.resultfilter-3.3.3.jar
iotbroker.restcontroller-3.3.3.jar
iotbroker.storage-3.3.3.jar
ngsi.api-3.3.3.jar
tomcat-configuration-fragment-3.3.3.jar
```

How to Use the Aeron Bundles
---

The Aeron bundles are OSGI based and can be used with arbitrary OSGI frameworks like EQUINOX, FELIX, etc.
The Aeron OSGI bundles have been tested with the EQUINOX and the FELIX framework.
An example of an OSGI configuration using the EQUINOX framework is shown below:

```
-- EQUINOX OSGI ENVIROMENT -- 
org.eclipse.core.contenttype-3.4.100.v20100505-1235.jar
org.eclipse.equinox.common-3.6.0.v20110506.jar
org.eclipse.core.jobs-3.5.0.v20100515.jar
org.eclipse.equinox.app-1.3.0.v20100512.jar
org.eclipse.equinox.preferences-3.3.0.v20100503.jar
org.eclipse.equinox.registry-3.5.0.v20100503.jar
org.eclipse.osgi.services-3.2.100.v20100503.jar
org.eclipse.equinox.cm_3.2.0.v20070116.jar@

-- PAX LOGGING -- 
pax-confman-propsloader-0.2.2.jar
pax-logging-api-1.7.0-20120710.130402-38.jar
pax-logging-service-1.7.0-20120710.130445-38.jar

-- ADDITIONAL LIBRARIES -- 
com.springsource.javax.activation-1.1.1.jar
com.springsource.org.apache.commons.io-1.4.0.jar
com.springsource.org.apache.commons.codec-1.6.0.jar
com.springsource.javax.annotation-1.0.0.jar
com.springsource.javax.ejb-3.0.0.jar
com.springsource.javax.el-1.0.0.jar
com.springsource.javax.mail-1.4.0.jar
com.springsource.javax.persistence-1.0.0.jar
com.springsource.javax.servlet.jsp.jstl-1.1.2.jar
com.springsource.javax.servlet.jsp-2.1.0.jar
com.springsource.javax.servlet-2.5.0.jar
com.springsource.javax.xml.bind-2.0.0.jar
com.springsource.javax.xml.stream-1.0.1.jar
com.springsource.javax.xml.rpc-1.1.0.jar
com.springsource.javax.xml.soap-1.3.0.jar
com.springsource.javax.xml.ws-2.1.1.jar
com.springsource.org.aopalliance-1.0.0.jar
com.springsource.org.apache.catalina-6.0.18.jar
com.springsource.org.apache.coyote-6.0.18.jar
com.springsource.org.apache.el-6.0.18.jar
com.springsource.org.apache.juli.extras-6.0.18.jar
com.springsource.org.apache.taglibs.standard-1.1.2.jar
jasper.osgi-5.5.23-SNAPSHOT.jar
javax.persistence-2.0.0.jar

-- PROVIDED OSGI BUNDLE LIBRARIES (can be founded inside the lib folder on GitHub)-- 
catalina.start.osgi-1.0.0.jar
jaxb-impl-2.1.5_1.0.0.jar
hsqldb_1.0.0.jar
javamelodybundle_1.0.0.jar
jrobin_1.5.9.1.jar
httpclient-4.2.0-osgi.jar
httpcore-4.2.0-osgi.jar

-- SPRING FRAMEWORK 3.2.3 -- 
org.springframework.aop-3.2.3.RELEASE.jar
org.springframework.aspects-3.2.3.RELEASE.jar
org.springframework.beans-3.2.3.RELEASE.jar
org.springframework.context.support-3.2.3.RELEASE.jar
org.springframework.context-3.2.3.RELEASE.jar
org.springframework.core-3.2.3.RELEASE.jar
org.springframework.expression-3.2.3.RELEASE.jar
org.springframework.jdbc-3.2.3.RELEASE.jar
org.springframework.orm-3.2.3.RELEASE.jar
org.springframework.oxm-3.2.3.RELEASE.jar
org.springframework.transaction-3.2.3.RELEASE.jar
org.springframework.web.servlet-3.2.3.RELEASE.jar
org.springframework.web-3.2.3.RELEASE.jar

-- SPRING SECURITY 3.1.4 -- 
org.springframework.security.config-3.1.4.RELEASE.jar
org.springframework.security.core-3.1.4.RELEASE.jar
org.springframework.security.web-3.1.4.RELEASE.jar

-- SPRING DM 2.0.0.M1 -- 
spring DM/spring-osgi-annotation-2.0.0.M1.jar
spring DM/spring-osgi-core-2.0.0.M1.jar
spring DM/spring-osgi-extender-2.0.0.M1.jar
spring DM/spring-osgi-io-2.0.0.M1.jar
spring DM/spring-osgi-web-2.0.0.M1.jar
spring DM/spring-osgi-web-extender-2.0.0.M1.jar

-- AERON OSGI BUNDLES -- 
iotbroker.client-3.3.3.jar
iotbroker.commons-3.3.3.jar
iotbroker.core-3.3.3.jar
iotbroker.ext.resultfilter-3.3.3.jar
iotbroker.restcontroller-3.3.3.jar
iotbroker.storage-3.3.3.jar
ngsi.api-3.3.3.jar
tomcat-configuration-fragment-3.3.3.jar
```

Installing and Using Aeron
---

Minimum System Requirements:
* Processor: 1 CPU 1.2 GHZ
* RAM: 1 GB
* DISK Spacd:50 MB
* JAVA : Java 7
* Operating System: 32 or 64-bit version Windows or Linux

The administration and programming manuals for Areon can be found on the FI-WARE Catalogue page,
under the "Documentation" tab.

User and Programmers Guide: https://forge.fi-ware.org/plugins/mediawiki/wiki/fiware/index.php/IoT_Broker_-_User_and_Programmers_Guide

Installation and Administration Guide: https://forge.fi-ware.org/plugins/mediawiki/wiki/fiware/index.php/IoT_Broker_-_Installation_and_Administration_Guide

Bugs & Questions
---
Please contact salvatore.longo@neclab.eu or tobias.jacobs@neclab.eu
