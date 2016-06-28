#!/bin/bash

# PLEASE SET THE FOLLOWING FILE PATH BEFORE TO RUN
#Configuration files to setup

iotbroker_configini='/opt/Aeron/IoTBroker-runner/configuration//config.ini'
iotbroker_configxml='/opt/Aeron/fiwareRelease/iotbrokerconfig/iotBroker/config/config.xml'
iotbroker_embeddedagent_couchdbxml='/opt/Aeron/fiwareRelease/iotbrokerconfig/embeddedAgent/couchdb.xml'
iotbroker_loggerproperties="$iotbroker_bundlesconfigurationlocation/services/org.ops4j.pax.logging.properties"

iotbroker_version="5.1.3"

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

function setConfiguration {
	
	key=$1
	key=${key//\./\\\.}
	key=${key//\//\\/}
	
	value=$2
	value=${value//\./\\\.}
	value=${value//\//\\/}

	
	if grep -q "$key=.*" "$3"; then
		sed -i "s/$key=.*/$key=\'$value\'/g" "$3"
	else 
		echo "$1='$2'" >> "$3"
	fi

}

function disableBundle {

	sed -i "s/.*$1-$iotbroker_version.jar.*//g" $iotbroker_configini

}

function enableBundle {
	
	found=`grep $1-$iotbroker_version.jar configuration/config.ini`
	if [ -n "$found" ];
	then
		return
	fi
	
	builder_target=`dirname $iotbroker_configini`
	builder_target=`dirname $builder_target`
	builder_target=`dirname $builder_target`
	builder_target="$builder_target/eu.neclab.iotplatform.iotbroker.builder/target/iotbroker.builder-$iotbroker_version-assembly/bundle/"
	
	if [ ! -e $builder_target/$1-$iotbroker_version.jar ];
	then
		echo "WARNING bundle not found: $builder_target/$1-$iotbroker_version.jar"
		return;
	fi
	
	
	lastline=`awk '/./{line=$0} END{print line}' $iotbroker_configini`
	lastline=${lastline//\./\\\.}
	lastline=${lastline//\//\\/}
	if [[ $lastline != *,* ]] ; then
		sed -i "s/$lastline/$lastline, \\\/g" $iotbroker_configini
	fi
	
	if [ "$2" == "nostart" ];
	then
		echo "../eu.neclab.iotplatform.iotbroker.builder/target/iotbroker.builder-$iotbroker_version-assembly/bundle/$1-$iotbroker_version.jar, \\" >> $iotbroker_configini
	else
		echo "../eu.neclab.iotplatform.iotbroker.builder/target/iotbroker.builder-$iotbroker_version-assembly/bundle/$1-$iotbroker_version.jar@start, \\" >> $iotbroker_configini
	fi
}

function correctConfigIni {
	
	lastline=`awk '/./{line=$0} END{print line}' $iotbroker_configini`
	correctlastline=${lastline/, \\/}
	correctlastline=${correctlastline//\./\\\.}
	correctlastline=${correctlastline//\//\\/}
	sed -i "s/$correctlastline, \\\/$correctlastline/g" $iotbroker_configini
	
	sed -i '/^$/d' $iotbroker_configini
	
}

# Escape characters
sed -e 's/[]\/$*.^|[]/\\&/g' ./iotbroker.conf.default > ./.iotbroker.conf.default.escaped
chmod +x .iotbroker.conf.default.escaped
. ./.iotbroker.conf.default.escaped

if [ -e iotbroker.conf.local ];
then
	echo "Reading default preferences from iotbroker.conf.local"
	sed -e 's/[]\/$*.^|[]/\\&/g' ./iotbroker.conf.local > ./.iotbroker.conf.local.escaped
	chmod +x .iotbroker.conf.local.escaped
	. ./.iotbroker.conf.local.escaped
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


	iotbroker_version_auto=`grep -m1 "<version>" $iotbrokerdir/eu.neclab.iotplatform.iotbroker.builder/pom.xml`;
	if [ -z "$iotbroker_version_auto" ];
	then
		echo "WARNING: impossible to read the version from the $iotbrokerdir/eu.neclab.iotplatform.iotbroker.builder/pom.xml. Please set it in the setup.sh manually"
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

		setConfiguration "iotbroker_configini" "$iotbroker_configini_auto" "setup.sh"
		setConfiguration "iotbroker_configxml" "$iotbroker_configxml_auto" "setup.sh"
		setConfiguration "iotbroker_embeddedagent_couchdbxml" "$iotbroker_embeddedagent_couchdbxml_auto" "setup.sh"
		setConfiguration "iotbroker_loggerproperties" "$iotbroker_loggerproperties_auto" "setup.sh"

		if [ -n "$iotbroker_version_auto" ];
		then
				setConfiguration "iotbroker_version" "$iotbroker_version_auto" "setup.sh"
		fi

	fi
	
	iotbroker_configini=$iotbroker_configini_auto
	iotbroker_configxml=$iotbroker_configxml_auto
	iotbroker_embeddedagent_couchdbxml=$iotbroker_embeddedagent_couchdbxml_auto
	iotbroker_version=$iotbroker_version_auto
	iotbroker_loggerproperties=$iotbroker_loggerproperties_auto

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

setPropertyIntoXML "couchdb_name" "$iotbroker_embeddedagent_couchdbname" "$iotbroker_embeddedagent_couchdbxml"
setPropertyIntoXML "couchdb_createdb" "$iotbroker_embeddedagent_couchdbcreatedb" "$iotbroker_embeddedagent_couchdbxml"
setPropertyIntoXML "couchdb_protocol" "$iotbroker_embeddedagent_couchdbprotocol" "$iotbroker_embeddedagent_couchdbxml"
setPropertyIntoXML "couchdb_host" "$iotbroker_embeddedagent_couchdbhost" "$iotbroker_embeddedagent_couchdbxml"
setPropertyIntoXML "couchdb_port" "$iotbroker_embeddedagent_couchdbport" "$iotbroker_embeddedagent_couchdbxml" 

setPropertyIntoProperties "log4j.appender.ReportFileAppender.File" "$iotbroker_logfile" "$iotbroker_loggerproperties"

##ENABLE BASIC BUNDLE
enableBundle iotbroker.commons
enableBundle iotbroker.storage
enableBundle iotbroker.client
enableBundle iotbroker.core
enableBundle iotbroker.restcontroller
enableBundle ngsi.api
enableBundle iotbroker.ext.resultfilter
enableBundle tomcat-configuration-fragment nostart


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
else
	disableBundle iotbroker.embeddediotagent.core
	disableBundle iotbroker.embeddediotagent.couchdb
	disableBundle iotbroker.embeddediotagent.indexer
	disableBundle iotbroker.embeddediotagent.storage
fi

##ENABLE ENTITY COMPOSER BUNDLES
if [ "$iotbroker_entitycomposer" == "enabled" ]
then
	enableBundle entitycomposer
else
	disableBundle entitycomposer
fi


correctConfigIni
