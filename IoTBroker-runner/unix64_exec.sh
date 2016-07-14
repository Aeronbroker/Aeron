#!/bin/bash

CONFIGINI="configuration/config.ini"

RESETAGENT=false
RESETIOTBROKER=false
CONSOLE=false

# READING THE 
while [[ $# > 0 ]]
do
key="$1"
case $key in
    --resetagent)
		RESETAGENT=true
		;;
    --resetiotbroker)
		RESETIOTBROKER=true
		;;
	--console)
		CONSOLE=true
		;;
    -p|--property)
		if [[ "$2" != *=* ]]
		then
			echo "WARN: wrong property specification $1 $2. Please use -p|--property <property_name>=\"<property_value>\""
		else
			#PROPERTIES[${#PROPERTIES[*]}+1]="$2"
			value=${2/=/=\'}
			value="$value'"
			PROPERTIES+=($value)
			shift # past argument
		fi
		;;
    *)
		echo "WARN: Unknown option $key"
        # unknown option
    ;;
esac
shift # past argument or value
done

if [ $CONSOLE == false ]
then
	if [ -f pid.file ];
	then
		if [ -z "`ps -ef | grep org.eclipse.osgi_iotbroker.jar | grep -v grep`" ]; 
		then 
			rm pid.file
		else
			HERE=`pwd`
			echo "An instance of the IoT Broker seems already running. If you are sure that this is not the case, delete the file $HERE/pid.file"
			exit 1
		fi
	fi
fi

if [ -n "$PROPERTIES" ]
then
	echo "RunTime properties set:"
	printf '%s\n' "${PROPERTIES[@]}"
	echo "#!/bin/bash" > .iotbroker.conf.runtime 
	printf '%s\n' "${PROPERTIES[@]}" >> .iotbroker.conf.runtime
else 
	rm -f .iotbroker.conf.runtime
fi

./setup.sh

. iotbroker_functions.sh


if [ $RESETAGENT == true ]
then
    setPropertyIntoIni "agent.reset" "true" "$CONFIGINI"
else
    setPropertyIntoIni "agent.reset" "false" "$CONFIGINI"
fi

if [ $RESETIOTBROKER == true ]
then
    setPropertyIntoIni "iotbroker.reset" "true" "$CONFIGINI"
else
    setPropertyIntoIni "iotbroker.reset" "false" "$CONFIGINI"
fi

if [ $CONSOLE == true ]
then
	java -jar org.eclipse.osgi_iotbroker.jar -console
else
	nohup java -jar org.eclipse.osgi_iotbroker.jar >/dev/null 2>&1 & echo $! > pid.file
fi
