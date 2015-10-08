= Introduction =
Welcome the IoT Broker GE User and Programmer Guide. The IoT Broker is the component implemented by NEC in the IoT WP (WP5). It is built in JAVA over OSGI using standard interface NGSI 9/10 to comunicate with the other components/GEs. The online documents are being continuously updated and improved, and the FI-WARE wiki will be the appropriate place to get the most up-to-date information on this GE.

= User Guide =

The NGSI-10 reference ([[OMA_NGSI-10]]) describes how to use the NGSI-10 API in detail. 
For using the IoT Broker you need to contact via HTTP the server on port 80 with one of the REST HTTP METHOD (GET, POST, PUT, DELETE) according to the NGSI-10 reference document.

In addition to the basic OMA NGSI 10 interface, the NEC IoT Broker also implements full support of the NGSI entity-to-entity association concept; please follow this [https://forge.fi-ware.eu/plugins/mediawiki/wiki/fiware/index.php/NGSI_association link] for documentation.

In the following section it is detailed how to use the Iot Broker GE from a user or developer perspective.

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
