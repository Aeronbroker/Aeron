#/bin/bash

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

nohup java -jar org.eclipse.osgi_iotbroker.jar >/dev/null 2>&1 &
