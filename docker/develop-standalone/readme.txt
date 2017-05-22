#######################################
How to use me?
#######################################

1) Run "docker build --no-cache=true -t fiware/iotbroker:standalone-dev ."



#######################################
How to run?
#######################################

-) With no option:
docker run -t -p 8065:8065 -p 8060:8060 fiware/iotbroker:standalone-dev

-) With options (the same contained in the confman.conf.default and iotbroker.conf.default)

docker run -t -p 8065:8065 -p 8060:8060 fiware/iotbroker:standalone-dev -p <iotbroker_key>="<value>" -p <confman_key>="<value>" [-p ...]

-) Possibility to access to the CouchDB server
docker run -t -p 8065:8065 -p 8060:8060 -p 5987:5984 -i fiware/iotbroker:standalone-dev ....
