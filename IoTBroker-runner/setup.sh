#!/bin/bash

# PLEASE SET THE FOLLOWING FILE PATH BEFORE TO RUN
#Configuration files to setup

iotbroker_configini='/opt/Aeron/IoTBroker-runner/configuration//config.ini'
iotbroker_configxml='/opt/Aeron/fiwareRelease/iotbrokerconfig/iotBroker/config/config.xml'
iotbroker_embeddedagent_couchdbxml='/opt/Aeron/fiwareRelease/iotbrokerconfig/embeddedAgent/couchdb.xml'


# Functions
function setPropertyIntoXML {
	if grep -q "<entry key=\"$1\">.*<\/entry>" "$3"; then
		
		sed -i "s/<entry key=\"$1\">.*<\/entry>/<entry key=\"$1\">$2<\/entry>/g" "$3"

	else
		
		sed -i "s/<\/properties>/<entry key=\"$1\">$2<\/entry>\n<\/properties>/g" "$3"		
	fi
}

function setPropertyIntoIni {
	key=$1
	key=${key//\./\\\.}
	key=${key//\//\\/}


	if grep -q "$key=.*" "$3"; then
		
		sed -i "s/$key=.*/$key=$2/g" "$3"

	else
		
		sed -i "s/# End of configurations/$key=$2\n# End of configurations/g" "$3"		
	fi
}

function setPropertyIntoProperties {
	key=$1
	key=${key//\./\\\.}
	key=${key//\//\\/}


	if grep -q "$key=.*" "$3"; then
		
		sed -i "s/$key=.*/$key=$2/g" "$3"

	else
		value=${2//\\/}
		echo "$key=$2" >> "$3"
	fi
}

# Escape characters
sed -e 's/[]\/$*.^|[]/\\&/g' ./platform.conf > ./.platform.conf.escaped
chmod +x .platform.conf.escaped
. ./.platform.conf.escaped


## START OF AUTOMATIC SCRIPT
#IoTBroker setup
setPropertyIntoIni "tomcat.init.port" "$iotbroker_tomcatinitport" "$iotbroker_configini"
setPropertyIntoIni "tomcat.init.httpsport" "$iotbroker_tomcatinithttpsport" "$iotbroker_configini"
setPropertyIntoIni "bundles.configuration.location" "$iotbroker_bundlesconfigurationlocation" "$iotbroker_configini"
setPropertyIntoIni "dir.config" "$iotbroker_dirconfig" "$iotbroker_configini"
setPropertyIntoIni "hsqldb.port" "$iotbroker_hsqldbport" "$iotbroker_configini"
setPropertyIntoIni "hsqldb.silent" "$iotbroker_hsqldbsilent" "$iotbroker_configini"
setPropertyIntoIni "hsqldb.directory" "$iotbroker_hsqldbdirectory" "$iotbroker_configini"

setPropertyIntoXML "schema_ngsi9_operation" "$iotbroker_schemangsi9operation" "$iotbroker_configxml"
setPropertyIntoXML "schema_ngsi10_operation" "$iotbroker_schemangsi10operation" "$iotbroker_configxml"
setPropertyIntoXML "pathPreFix_ngsi9" "$iotbroker_pathprefixngsi9" "$iotbroker_configxml"
setPropertyIntoXML "pathPreFix_ngsi10" "$iotbroker_pathprefixngsi10" "$iotbroker_configxml"
setPropertyIntoXML "ngsi9Uri" "$iotbroker_ngsi9uri" "$iotbroker_configxml"
setPropertyIntoXML "ngsi9RemoteUrl" "$iotbroker_ngsi9remoteurl" "$iotbroker_configxml"
setPropertyIntoXML "default_throttling" "$iotbroker_defaultthrottling" "$iotbroker_configxml"
setPropertyIntoXML "default_duration" "$iotbroker_defaultduration" "$iotbroker_configxml"
setPropertyIntoXML "hsqldb.username" "$iotbroker_hsqldbusername" "$iotbroker_configxml"
setPropertyIntoXML "hsqldb.password" "$iotbroker_hsqldbpassword" "$iotbroker_configxml"
setPropertyIntoXML "ignoreIoTDiscoveryFailure" "$iotbroker_ignoreiotdiscoveryfailure" "$iotbroker_configxml"
setPropertyIntoXML "ignorePubSubFailure" "$iotbroker_ignorepubsubfailure" "$iotbroker_configxml"
setPropertyIntoXML "default_content_type" "$iotbroker_producedtype" "$iotbroker_configxml"

setPropertyIntoXML "couchdb_name" "$iotbroker_embeddedagent_couchdbname" "$iotbroker_embeddedagent_couchdbxml"
setPropertyIntoXML "couchdb_createdb" "$iotbroker_embeddedagent_couchdbcreatedb" "$iotbroker_embeddedagent_couchdbxml"
setPropertyIntoXML "couchdb_protocol" "$iotbroker_embeddedagent_couchdbprotocol" "$iotbroker_embeddedagent_couchdbxml"
setPropertyIntoXML "couchdb_host" "$iotbroker_embeddedagent_couchdbhost" "$iotbroker_embeddedagent_couchdbxml"
setPropertyIntoXML "couchdb_port" "$iotbroker_embeddedagent_couchdbport" "$iotbroker_embeddedagent_couchdbxml" 
