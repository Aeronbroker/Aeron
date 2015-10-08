Introduction
==

Welcome to the Installation and Administration Guide for the IoT Broker GE reference implementation. This guide explains how to interact with the IoT Broker GE from the perspective of users and developers.

API walkthrough
==

For more details about the data flows during the different interactions, please refer to the [FIWARE IoT Broker GE Open Specification](https://forge.fiware.org/plugins/mediawiki/wiki/fiware/index.php/FIWARE.OpenSpecification.IoT.Backend.IoTBroker).

FIWARE NGSI 10
--

The IoT Broker GE is a middleware making data from multiple providers accessible for data consumers. The interaction with both data providers and data consumers is taking place via the FIWARE NGSI 10 API (the context data API of FIWARE NGSI).

For using the IoT Broker, the API needs to be contacted via HTTP the server on port 80 (if default configuration applies) with one of the HTTP methods (GET, POST, PUT, DELETE) according to the NGSI-10 specification.

The FIWARE NGSI-10 interactions are

 * context queries (e.g. via HTTP POST to `{IoT Broker IP}/ngsi10/queryContext`)
 * context subscription (e.g. via HTTP POST to `{IoT Broker IP}/ngsi10/subscribeContext`)
 * context updates (e.g. via HTTP POST to `{IoT Broker IP}/ngsi10/updateContext`)

For details on the FIWARE NGSI-10 API exposed by the IoT Broker, please refer to the [API specs](http://aeronbroker.github.io/Aeron/).

FIWARE NGSI 9
--
  
The each Iot Broker instance needs to interact with an instance of the IoT Discovery GE. This interaction is done via the FIWARE NGSI 9 API (the context availability API of FIWARE NGSI). However, this interaction is mostly transparent to the user.


Data Provider Perspective
==
Data providers who want to make their data available to an IoT Broker instance have two possibilities

1. Expose a FIWARE NGSI-10 interface which the IoT Broker can query for data whenever needed. This interface and the data it provides needs to be registered using the FIWARE NGSI-0 *registerContext* operation. Please note that the IoT Broker does not handle registrations by itself. Instead, the registration has to be sent to the instance of the IoT Discovery GE responsible for this FIWARE IoT installation.

2. Push data updates towards the IoT Broker using the *updateContext* method provided by FIWARE NGSI-10. This is the simpler kind of interactions, because data providers do not need to expose an NGSI API. On the downside, this interaction mode potentially uses network resources inefficiently, because the data push takes place regardless of whether the data is needed by the IoT Broker or not.

Data Consumer Perspective
==

Data consumers can retrieve context data from the IoT Broker via the FIWARE NGSI-10 interface as described above.

Additionally, the IoT Broker offers a simple web interface accessible by entering its network address into a browser (e.g. "localhost" when the IoT Broker is running on a local machine). From the home page the user can access a query interface for basic NGSI queries.

Developer Guide
==

The IoT Broker is based on the OSGi framework and has a dedicated extension point for custom data retrieval and data processing plugins. Please see [here]() for details


User Guide
==

The NGSI-10 reference ([[OMA_NGSI-10]]) describes how to use the NGSI-10 API in detail. 

= Developer Guide =

The IoT Broker exposes an NGSI-10 interface, which is a RESTful interface over HTTP. This means that it is possible query the IoT Broker regardless of the programming language.

First, for checking if the IoT Broker GE is running and which operations are supported, is possible to send a GET on <nowiki>http://{IoT Broker IP}/</nowiki> .The response will be like this:

[[Image:index.png|600px|Index Page]]

What is shown is the dashboard of the running IoT Broker instance. From this dashboard, the set of supported operations can be verified by clicking on the "Operations" tab. 

Now for getting a feeling of how NGSI-10 works, let us send a GET request on <nowiki>http://{IoT Broker IP}/ngsi10/contextEntities/Kitchen</nowiki>. The HTTP request and response headers are showed below:

<pre>
<GET /ngsi10/contextEntities/Kitchen HTTP/1.1
<Host: localhost:80
<User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64; rv:13.0) Gecko/20100101 Firefox/13.0.1
<Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8
<Accept-Language: it-it,it;q=0.8,en-us;q=0.5,en;q=0.3
<Accept-Encoding: gzip, deflate
<Connection: keep-alive
> 
<HTTP/1.1 200 OK
<Server: Apache-Coyote/1.1
<Content-Type: application/xml
<Transfer-Encoding: chunked
<Date: Tue, 10 Jul 2012 15:46:49 GMT
 
</pre>

It is important to note that the NGSI-10 API, exposed by IoT Broker GE, required as Content-Type "application/xml", which means that an application can send only xml content and receive only xml as content.

== Accessing the IoT Broker NGSI-10 Interface from a Browser ==

The following example interactions can be executed using the Chrome browser [http://www.google.es/chrome?platform=linux&hl=en-GB] with the REST Client plugin [https://chrome.google.com/webstore/detail/fhjcajmcbmldlhcimfajhfbgofnpcjmb] in order to send http commands to the IoT Broker. You can use it also in Firefox through RESTClient add-ons [https://addons.mozilla.org/en-US/firefox/addon/restclient/].

We give two different example for the GET and POST request. Further examples of NGSI 10 usage can be found in the [[FI-WARE NGSI Open RESTful API Specification|NGSI documentation]].

1. GET request:

[[Image:GET_request.png|900px|Get request]]
<br/>
<br/>
<br/>

2. POST request:

[[Image:POST_request.png|900px|POST request]]
<br/>
The request message body is as follows:

  <?xml version="1.0" encoding="UTF-8"?>
  <queryContextRequest>
  	<entityIdList>
  		<entityId type="Room">
  			<id>Kitchen</id>			
  		</entityId>
  	</entityIdList>
  	<attributeList>
  		<attribute>indoorTemperature</attribute>
  	</attributeList>
  </queryContextRequest>
