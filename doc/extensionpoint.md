 IoT Broker Extension Point
============================

This document describes how to extend the IoT Broker with custom plugins for  connecting to new kinds of data sources, and for generating FIWARE NGSI entities by means of processing functions.
 

General Principle
-----------------

When the IoT Broker is queried for data by an application, the usual workflow is that the IoT Broker first contacts the IoT Discovery GE to discover the relevant data sources. These data sources are then contacted by the IoT Broker by means of FIWARE NGSI,and the data retrieved from them is returned to the original query issuer. This workflow is hard-wired into the functional core of the IoT Broker.

A new feature with release 4.4 is the possibility to provide custom plugins that can provide data by means other than retrieval from NGSI-compliant data sources. Such plugins implement a so-called *Query Service*. Any Query Service provides a function where the input is a FIWARE NGSI query and a list of FIWARE NGSI context registrations, and the output is FIWARE NGSI data. 

During the query workflow, the IoT Broker core passes the application's data query and the discovered ContextRegistrations to each present Query Service. The data retrieved from these Query Services is then included by the response returned to the application.

Examples Query Services
-------------

Plugins implementing Query Services can be used for
 * retrieving data from databases.
 * retrieving data from non-NGSI data sources.
 * realizing data processing and data translation. One particular example of this usage is the plugin *eu.neclab.iotplatform.entitycomposer* included by IoT Broker from release 4.4.
 
How to Use Existing Query Service Plugins
-------------
Simply deploy the desired plugin  in the OSGi environment where the IoT Broker runs. The plugin will be auto-detected via the OSGi Service Registry; no additional configuration is necessary.
 
How to Write Own Query Service Plugins
-------------
For an OSGi plugin to provide a Query Service, it needs to expose a Service having the interface *eu.neclab.iotplatform.iotbroker.commons.interfaces.QueryService*. This is a java interface which defines exactly one function *queryContext*, taking
as parameter a *QueryContextRequest* object and a list of *ContextRegistration* objects and returning a *QueryContextResponse* object.
 
For a guiding example, please refer to the existing plugin 'eu.neclab.iotplatform.entitycomposer'. This plugin exposes its Query Service by means of Spring OSGi; see the 'eu.neclab.iotplatform.entitycomposer/META-INF/spring' folder. 
The Query Service interface is implemented by the class *eu.neclab.iotplatform.entitycomposer.CompositeEntityQueryService*. This class realizes a Query Service which reads entity composition information (i.e. information how to compose entities from other entities) from context registrations and executes these entity compositions; see the documentation of this plugin for details.

Further Extension Points
---------
Extension points for FIWARE NGSI subscriptions and updates are planned to be realized in future releases.
 
