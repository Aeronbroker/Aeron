IoT Broker Docker file
=======================

IoT Broker is the FIWARE reference implementation of the IoT Broker Generic Enabler provided by NEC.

This dockerfile can be used for building a docker image running the IoT Broker generic enabler. The build process will install the latest version available on GitHub (https://github.com/Aeronbroker/Aeron).

Running the image will create a docker container with the IoT Broker running and listening to port 8060 and the log of the IoTBroker will be shown (you may need root permissions):
```
docker run -t -p 8060:8060 fiware/iotbroker:v5.4.3
```


In case you want to have the standalone component of the IoT Broker GE (IoT Broker GEri + NEConfMan), run the following command:
```
docker run -t -p 8065:8065 -p 8060:8060 -p 5984:5984 fiware/iotbroker:v5.4.3-standalone
```
 
Both the IoT Broker GEri and NEConfMan will be accessible respectively at port 8060 and 8065. In addition CouchDB will be exposed to the port 5984.

In order to configure the IoT Broker and/or the NEConfMan, run the docker with the following command:

```
docker run -t -p 8065:8065 -p 8060:8060 fiware/iotbroker:v5.4.3-standalone -p <iotbroker_key>="<value>" -p <confman_key>="<value>" [-p ...]
```

where *iotbroker_key* is one of the parameters available in the *IoTBroker_Runner/iotbroker.conf.default* and *confman_key* one of the parameters available in the *ConfMan_Runner/confman.conf.default*.
Please note that such configurations are runtime properties and they will be forgotten the next time the docker is run.

Permanent Storage: CouchDB, PostgreSQL and HSQLDB
---
In order to handle permanent storage it is necessary to allow the docker image to access the CouchDB server (for the historical agent) and the PostgreSQL (for utility storage) into the docker host (or another machine).

PLEASE NOTE: This configuration will expose postgres and couchdb to everybody, so please take care to fine tuning the configuration for an enhanced security.

Configure **PostgreSQL**:

Lets change the postgres user password.
```
sudo su
su postgres
psql
ALTER USER postgres PASSWORD 'postgres'
\q
exit
exit
```
Then we need to allow the docker image to access the postgreSQL database in the host machine. 
First we need to check which is are the IP addresses of the virtual network created by the docker daemon:
```
$> ifconfig 
docker0   Link encap:Ethernet  HWaddr 02:42:0e:f6:a6:67  
          inet addr:172.17.0.1 Bcast:0.0.0.0  Mask:255.255.255.0
          inet6 addr: fe80::42:eff:fef6:a667/64 Scope:Link
          UP BROADCAST MULTICAST  MTU:1500  Metric:1
          RX packets:157061 errors:0 dropped:0 overruns:0 frame:0
          TX packets:202369 errors:0 dropped:0 overruns:0 carrier:0
          collisions:0 txqueuelen:0 
          RX bytes:10283839 (10.2 MB)  TX bytes:582227340 (582.2 MB)

```
Then we need to instruct postgreSQL to accept requests from this virtual network:
```
echo -e 'host\tall\tall\t172.17.0.0/24\ttrust' >> /etc/postgresql/9.5/main/pg_hba.conf
sed -i "s/^port/listen_addresses = '\*'\nport/g" /etc/postgresql/9.5/main/postgresql.conf
service postgresql restart
```

Configure **CouchDB**:

Similarly we need to allow docker to access couchdb ([CouchDB documentation](http://docs.couchdb.org/en/master/config/http.html)):

```
sudo sed -i "s/;bind_address = 127.0.0.1/bind_address = 0\.0\.0\.0/g" "/etc/couchdb/local.ini"
sudo service couchdb restart
```

Configure **HSQLDB**:

First it is necessarry to copy the HSQLDB directory (https://github.com/Aeronbroker/Aeron/tree/master/SQL_database) that contains the database files somewhere in the host file system. We assume that it is located under /home/user/SQL_database.
At this point we need to mount the HSQLDB folder into the docker container and instruct the IoT Broker in the container to access the mounted folder (with the -p option):

```
sudo docker run -t \
-v /home/user/SQL_database/:/SQL_database \
fiware/iotbroker:standalone-dev \
-p iotbroker_hsqldbdirectory="//SQL_database//database//linkDB"
```

After all the databases are correctly configuerd, you can run the docker with the fowlling parameters (or a subset of it, depending on which storage you want to have permanent):

```
sudo docker run -t -p 8065:8065 -p 8060:8060  \
-v /home/user/SQL_database/:/SQL_database \
fiware/iotbroker:standalone-dev \
-p iotbroker_historicalagent="enabled" \
-p iotbroker_embeddedagent_registrydbname="embeddedagentregistry" \
-p iotbroker_embeddedagent_couchdbname="embeddedagenthistorical" \
-p iotbroker_hsqldbdirectory="//SQL_database//database//linkDB" \
-p iotbroker_embeddedagent_couchdbhost="172.17.0.1" \
-p confman_couchdbipandport="http://172.17.0.1:5984" \
-p confman_couchdbregistercontextdbname="iotdiscoveryregistrations" \
-p confman_couchdbsubscriptiondbname="iotdiscoverysubscriptions" \
-p confman_postgresurl='//172.17.0.1/' \
-p confman_postgresdbname="iotdiscoverypostgres" 
```
