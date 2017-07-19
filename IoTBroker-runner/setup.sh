#!/bin/bash

AUTOSETUP='false'
PROPAGATEAUTO='false'

while [[ $# > 0 ]]
do
key="$1"
case $key in
    --auto)
    AUTOSETUP=true
    ;;
    --propagateauto)
    PROPAGATEAUTO=true
    ;;
    *)
            # unknown option
    ;;
esac
shift # past argument or value
done

# Functions
. iotbroker_functions.sh

# Escape characters
sed -e 's/[]\/$*.^|[]/\\&/g' ./iotbroker.conf.default > ./.iotbroker.conf.default.escaped
chmod +x .iotbroker.conf.default.escaped
. ./.iotbroker.conf.default.escaped


if [ -e iotbroker.conf.local ];
then
	echo "Reading custom preferences from iotbroker.conf.local"
	sed -e 's/[]\/$*.^|[]/\\&/g' ./iotbroker.conf.local > ./.iotbroker.conf.local.escaped
	chmod +x .iotbroker.conf.local.escaped
	. ./.iotbroker.conf.local.escaped
fi

if [ -e .iotbroker.conf.runtime ];
then
	echo "Reading runtime preferences from .iotbroker.conf.runtime"
	sed -e 's/[]\/$*.^|[]/\\&/g' ./.iotbroker.conf.runtime > ./.iotbroker.conf.runtime.escaped
	chmod +x .iotbroker.conf.runtime.escaped
	. ./.iotbroker.conf.runtime.escaped
fi


if [ "$AUTOSETUP" = true ]; 
then
	iotbrokerdir="$(dirname `pwd`)"
	
	iotbroker_dir_doubleslash=${iotbrokerdir//\///\/}
	iotbroker_hsqldbdirectory="$iotbroker_dir_doubleslash//SQL_database//database//linkDB"
	
	iotbroker_dirconfig="$iotbrokerdir/fiwareRelease"
	iotbroker_bundlesconfigurationlocation="$iotbrokerdir/fiwareRelease/bundleConfigurations"
	iotbroker_logfile="$iotbrokerdir/IoTBroker-runner/logs/iotbroker.log"	

	iotbroker_configini_auto="$iotbrokerdir/IoTBroker-runner/configuration/config.ini"
	iotbroker_configxml_auto="$iotbrokerdir/fiwareRelease/iotbrokerconfig/iotBroker/config/config.xml"
	iotbroker_embeddedagent_couchdbxml_auto="$iotbrokerdir/fiwareRelease/iotbrokerconfig/embeddedAgent/couchdb.xml"
	iotbroker_loggerproperties_auto="$iotbroker_bundlesconfigurationlocation/services/org.ops4j.pax.logging.properties"
	iotbroker_knowledgebaseproperties_auto="$iotbrokerdir/fiwareRelease/iotbrokerconfig/knowledgeBase/knowledgeBase.properties"


	iotbroker_version_auto=`grep -m1 "<version>" $iotbrokerdir/eu.neclab.iotplatform.iotbroker.builder/pom.xml`;
	if [ -z "$iotbroker_version_auto" ];
	then
		echo "WARNING: impossible to read the version from the $iotbrokerdir/eu.neclab.iotplatform.iotbroker.builder/pom.xml. Please set it in the iotbroker_functions.sh manually"
	else
		iotbroker_version_auto=${iotbroker_version_auto/<version>};
		iotbroker_version_auto=${iotbroker_version_auto/<\/version>};
		iotbroker_version_auto=${iotbroker_version_auto//	};
		iotbroker_version_auto=${iotbroker_version_auto// };
	fi
	
	if [ "$PROPAGATEAUTO" = true ];
	then
	
		if [ ! -e iotbroker.conf.local ];
		then
			touch iotbroker.conf.local
			echo "#!/bin/bash" >> iotbroker.conf.local
		fi

		setConfiguration "iotbroker_dirconfig" "$iotbroker_dirconfig" "iotbroker.conf.local"
		setConfiguration "iotbroker_bundlesconfigurationlocation" "$iotbroker_bundlesconfigurationlocation" "iotbroker.conf.local"
		setConfiguration "iotbroker_hsqldbdirectory" "$iotbroker_hsqldbdirectory" "iotbroker.conf.local"
		setConfiguration "iotbroker_logfile" "$iotbroker_logfile" "iotbroker.conf.local"

		setConfiguration "iotbroker_configini" "$iotbroker_configini_auto" "iotbroker_functions.sh"
		setConfiguration "iotbroker_configxml" "$iotbroker_configxml_auto" "iotbroker_functions.sh"
		setConfiguration "iotbroker_embeddedagent_couchdbxml" "$iotbroker_embeddedagent_couchdbxml_auto" "iotbroker_functions.sh"
		setConfiguration "iotbroker_loggerproperties" "$iotbroker_loggerproperties_auto" "iotbroker_functions.sh"
		setConfiguration "iotbroker_knowledgebaseproperties" "$iotbroker_knowledgebaseproperties_auto" "iotbroker_functions.sh"

		if [ -n "$iotbroker_version_auto" ];
		then
				setConfiguration "iotbroker_version" "$iotbroker_version_auto" "iotbroker_functions.sh"
		fi

	fi
	
	iotbroker_configini=$iotbroker_configini_auto
	iotbroker_configxml=$iotbroker_configxml_auto
	iotbroker_embeddedagent_couchdbxml=$iotbroker_embeddedagent_couchdbxml_auto
	iotbroker_version=$iotbroker_version_auto
	iotbroker_loggerproperties=$iotbroker_loggerproperties_auto
	iotbroker_knowledgebaseproperties=$iotbroker_knowledgebaseproperties_auto

	iotbroker_dirconfig=${iotbroker_dirconfig//\./\\\.}
	iotbroker_dirconfig=${iotbroker_dirconfig//\//\\/}
	
	iotbroker_bundlesconfigurationlocation=${iotbroker_bundlesconfigurationlocation//\./\\\.}
	iotbroker_bundlesconfigurationlocation=${iotbroker_bundlesconfigurationlocation//\//\\/}
	
	iotbroker_hsqldbdirectory=${iotbroker_hsqldbdirectory//\./\\\.}
	iotbroker_hsqldbdirectory=${iotbroker_hsqldbdirectory//\//\\/}

	iotbroker_logfile=${iotbroker_logfile//\./\\\.}
	iotbroker_logfile=${iotbroker_logfile//\//\\/}

fi

## START OF AUTOMATIC SCRIPT
#IoTBroker setup
setPropertyIntoIni "tomcat.init.port" "$iotbroker_tomcatinitport" "$iotbroker_configini"
setPropertyIntoIni "tomcat.init.port.ssl" "$iotbroker_tomcatinithttpsport" "$iotbroker_configini"
setPropertyIntoIni "bundles.configuration.location" "$iotbroker_bundlesconfigurationlocation" "$iotbroker_configini"
setPropertyIntoIni "dir.config" "$iotbroker_dirconfig" "$iotbroker_configini"
setPropertyIntoIni "hsqldb.url" "$iotbroker_hsqldburl" "$iotbroker_configini"
setPropertyIntoIni "hsqldb.port" "$iotbroker_hsqldbport" "$iotbroker_configini"
setPropertyIntoIni "hsqldb.silent" "$iotbroker_hsqldbsilent" "$iotbroker_configini"
setPropertyIntoIni "hsqldb.directory" "$iotbroker_hsqldbdirectory" "$iotbroker_configini"

setPropertyIntoXML "schema_ngsi9_operation" "$iotbroker_schemangsi9operation" "$iotbroker_configxml"
setPropertyIntoXML "schema_ngsi10_operation" "$iotbroker_schemangsi10operation" "$iotbroker_configxml"
setPropertyIntoXML "isMaster" "$iotbroker_ismaster" "$iotbroker_configxml"
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
setPropertyIntoXML "pub_sub_addr_ngsiv1" "$iotbroker_consumers_ngsiv1" "$iotbroker_configxml"
setPropertyIntoXML "pub_sub_addr_ngsiv1_orion" "$iotbroker_consumers_ngsiv1_orion" "$iotbroker_configxml"
setPropertyIntoXML "timestampContextElement" "$iotbroker_timestampcontextelement" "$iotbroker_configxml"
setPropertyIntoXML "trackContextSource" "$iotbroker_trackcontextsource" "$iotbroker_configxml"
setPropertyIntoXML "exposedAddress" "$iotbroker_exposedAddress" "$iotbroker_configxml"
setPropertyIntoXML "historicallyTrackQueryResponseAndNotifications" "$iotbroker_embeddedagent_historicallyTrackQueryResponseAndNotifications" "$iotbroker_configxml"
setPropertyIntoXML "updateThreadPoolSize" "$iotbroker_updatethreadpoolsize" "$iotbroker_configxml"
setPropertyIntoXML "historicallyTrackQueryResponseAndNotifications" "$iotbroker_embeddedagent_historicallyTrackQueryResponseAndNotifications" "$iotbroker_configxml" 

setPropertyIntoXML "couchdb_name" "$iotbroker_embeddedagent_couchdbname" "$iotbroker_embeddedagent_couchdbxml"
setPropertyIntoXML "couchdb_createdb" "$iotbroker_embeddedagent_couchdbcreatedb" "$iotbroker_embeddedagent_couchdbxml"
setPropertyIntoXML "couchdb_protocol" "$iotbroker_embeddedagent_couchdbprotocol" "$iotbroker_embeddedagent_couchdbxml"
setPropertyIntoXML "couchdb_host" "$iotbroker_embeddedagent_couchdbhost" "$iotbroker_embeddedagent_couchdbxml"
setPropertyIntoXML "couchdb_port" "$iotbroker_embeddedagent_couchdbport" "$iotbroker_embeddedagent_couchdbxml" 
setPropertyIntoXML "storeOnlyLatestValue" "$iotbroker_embeddedagent_storeOnlyLatestValue" "$iotbroker_embeddedagent_couchdbxml" 
setPropertyIntoXML "registrydb_name" "$iotbroker_embeddedagent_registrydbname" "$iotbroker_embeddedagent_couchdbxml" 
setPropertyIntoXML "embeddedAgentId" "$iotbroker_embeddedagent_localagentid" "$iotbroker_embeddedagent_couchdbxml" 

setPropertyIntoProperties "log4j.appender.ReportFileAppender.File" "$iotbroker_logfile" "$iotbroker_loggerproperties"
setFirstPropertyValueOverMultipleValuesIntoProperties "log4j.rootLogger" "$iotbroker_loglevel" "$iotbroker_loggerproperties"

setPropertyIntoProperties "knowledgebase_address" "$iotbroker_knowledgebaseaddress" "$iotbroker_knowledgebaseproperties"
setPropertyIntoProperties "knowledgebase_port" "$iotbroker_knowledgebaseport" "$iotbroker_knowledgebaseproperties"


##ENABLE BASIC BUNDLE
enableBundle iotbroker.commons
enableBundle iotbroker.storage
enableBundle iotbroker.client
enableBundle iotbroker.core
enableBundle iotbroker.restcontroller
enableBundle ngsi.api
enableBundle iotbroker.ext.resultfilter
enableBundle tomcat-configuration-fragment nostart

##ENABLE/DISABLE ASSOCIATION
if [ "$iotbroker_association" == "enabled" ]
then
	setPropertyIntoXML "associationsEnabled" "true" "$iotbroker_configxml"
else
	setPropertyIntoXML "associationsEnabled" "false" "$iotbroker_configxml"
fi


##ENABLE BIG DATA REPOSITORY BUNDLES
if [ "$iotbroker_bigdatarepository" == "enabled" ]
then
	enableBundle iotbroker.couchdb
else
	disableBundle iotbroker.couchdb
fi

##ENABLE HISTORICAL AGENT BUNDLES
if [ "$iotbroker_historicalagent" == "enabled" ]
then
	enableBundle iotbroker.embeddediotagent.core
	enableBundle iotbroker.embeddediotagent.couchdb
	enableBundle iotbroker.embeddediotagent.indexer
	enableBundle iotbroker.embeddediotagent.storage
	enableBundle iotbroker.embeddediotagent.registry
else
	disableBundle iotbroker.embeddediotagent.core
	disableBundle iotbroker.embeddediotagent.couchdb
	disableBundle iotbroker.embeddediotagent.indexer
	disableBundle iotbroker.embeddediotagent.storage
	disableBundle iotbroker.embeddediotagent.registry
fi

##ENABLE ENTITY COMPOSER BUNDLES
if [ "$iotbroker_entitycomposer" == "enabled" ]
then
	enableBundle entitycomposer
else
	disableBundle entitycomposer
fi

##ENABLE SEMANTIC BUNDLES
if [ "$iotbroker_semantic" == "enabled" ]
then
	enableBundle iotbroker.knowledgebase
else
	disableBundle iotbroker.knowledgebase
fi


correctConfigIni
