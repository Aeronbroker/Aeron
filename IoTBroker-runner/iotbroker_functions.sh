# PLEASE SET THE FOLLOWING FILE PATH BEFORE TO RUN
#Configuration files to setup
iotbroker_configini='/opt/Aeron/IoTBroker-runner/configuration//config.ini'
iotbroker_configxml='/opt/Aeron/fiwareRelease/iotbrokerconfig/iotBroker/config/config.xml'
iotbroker_embeddedagent_couchdbxml='/opt/Aeron/fiwareRelease/iotbrokerconfig/embeddedAgent/couchdb.xml'
iotbroker_loggerproperties="$iotbroker_bundlesconfigurationlocation/services/org.ops4j.pax.logging.properties"
iotbroker_knowledgebaseproperties="/opt/Aeron/fiwareRelease/iotbrokerconfig/knowledgeBase/knowledgeBase.properties"

iotbroker_version="5.3.3"

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

function setFirstPropertyValueOverMultipleValuesIntoProperties {
	
	key=$1
	key=${key//\./\\\.}
	key=${key//\//\\/}

	
	sed -i "s/$key=[^,]*/$key=$2/g" "$3"

	
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
