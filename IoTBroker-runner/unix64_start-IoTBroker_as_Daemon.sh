#/bin/bash

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

CONFIGINI="configuration/config.ini"

RESETAGENT=false
RESETIOTBROKER=false

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
    *)
            # unknown option
    ;;
esac
shift # past argument or value
done


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

nohup java -jar org.eclipse.osgi_iotbroker.jar >/dev/null 2>&1 & echo $! > pid.file
