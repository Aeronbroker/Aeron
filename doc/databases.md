Databases
---

HSQLDB
----

The IoT Broker uses an [HSQLDB](http://hsqldb.org/) database running on port 9001. This database is embedded by the IoT Broker and therefore requires no particular configuration or installation procedure (username and password can be changed in the config.xml file). For checking the status of the database it is possible to send an SQL query using the hsqldb driver on port 9001 with username *NEC* and password *neclab*.

The database logs are in the folder *SQL_database* and the file name is *database.txt*.

At startup the IoT Broker automatically starts the database instance as shown by the IoT Broker logger: 

```
  [Server@3b2d38f6]: [Thread[SpringOsgiExtenderThread-8,5,spring-osgi-extender[1c57a236]-threads]]: checkRunning(false) entered
  [Server@3b2d38f6]: [Thread[SpringOsgiExtenderThread-8,5,spring-osgi-extender[1c57a236]-threads]]: checkRunning(false) exited
  [Server@3b2d38f6]: Initiating startup sequence...
  [Server@3b2d38f6]: Server socket opened successfully in 1 ms.
  Jul 29, 2013 9:17:23 AM org.hsqldb.persist.Logger logInfoEvent
  INFO: checkpointClose start
  Jul 29, 2013 9:17:23 AM org.hsqldb.persist.Logger logInfoEvent
  INFO: checkpointClose end
  [Server@3b2d38f6]: Database [index=0, id=0, db=file:E:\SQL_database\database, alias=linkdb] opened sucessfully in 738 ms.
  [Server@3b2d38f6]: Startup sequence completed in 750 ms.
  [Server@3b2d38f6]: 2013-07-29 09:17:23.347 HSQLDB server 2.2.9 is online on port 9001
  [Server@3b2d38f6]: To close normally, connect and execute SHUTDOWN SQL
  [Server@3b2d38f6]: From command line, use [Ctrl]+[C] to abort abruptly
```


Historical Database: CouchDB
----

The IoT Broker has a feature for historically store data into a noSQL database such CouchDB.

In order to enable the feature, check [here](https://github.com/Aeronbroker/Aeron/blob/master/doc/historyqueries.md).

Furthermore, the data can be accessed also directly from the CouchDB interface (if you have access directly to the machine or if your CouchDB has been enabled to allow any machine to access it, see [CouchDB documentation](http://docs.couchdb.org/en/master/config/http.html)).

For example, to check all the entity stored into the db you can make the following query:

```
curl 'http://localhost:5984/historicalrepository/_all_docs?startkey=%22entity%22&endkey=%22entity%C3%BF%22'
```
NOTE: change localhost with the actual IP if needed. In case of docker you can map CouchDB of the docker container to a host port.

The result will look like:

```
{"total_rows":20295,"offset":2,"rows":[
{"id":"entity__Room1~Room:::pressure","key":"entity__Room1~Room:::pressure","value":{"rev":"1-0661ad8aedb94aeab067eee62a7ff666"}},
{"id":"entity__Room1~Room:::temperature","key":"entity__Room1~Room:::temperature","value":{"rev":"1-73916d2d5f92dbda275e9eff610f8bf7"}}
]}
```

The key is in the form "*entity__ENTITYID[~ENTITYTYPE]:::ATTRIBUTENAME*". In square brackets (i.e. "[ ]") are optional fields, in capital letters the variable part.


In order to check all historical data of a specific entity, the query to make to CouchDB is:

```
curl 'http://localhost:5984/historicalrepository/_all_docs?startkey=%22obs__Room1~Room%22&endkey=%22obs__Room1~Room%C3%BF%22'
```
The result will look like:

```
{"total_rows":20295,"offset":5404,"rows":[
{"id":"obs__Room1~Room:::pressure|2017-09-15 13:45:57.352","key":"obs__Room1~Room:::pressure|2017-09-15 13:45:57.352","value":{"rev":"1-0661ad8aedb94aeab067eee62a7ff666"}},
{"id":"obs__Room1~Room:::pressure|2017-09-15 13:50:55.352","key":"obs__Room1~Room:::pressure|2017-09-15 13:50:55.352","value":{"rev":"1-0661ad8aedb94aeab067eee62a7ff666"}},
{"id":"obs__Room1~Room:::temperature|2017-09-15 13:45:57.352","key":"obs__Room1~Room:::temperature|2017-09-15 13:45:57.352","value":{"rev":"1-73916d2d5f92dbda275e9eff610f8bf7"}},
{"id":"obs__Room1~Room:::temperature|2017-09-15 13:48:22.352","key":"obs__Room1~Room:::temperature|2017-09-15 13:48:22.352","value":{"rev":"1-73916d2d5f92dbda275e9eff610f8bf7"}}
]}

```
The key is in the form "*obs__ENTITYID[~ENTITYTYPE]:::ATTRIBUTENAME|TIMESTAMP*". In square brackets (i.e. "[ ]") are optional fields, in capital letters the variable part.
