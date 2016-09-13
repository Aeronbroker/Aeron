#!/bin/bash
# Use > 1 to consume two arguments per pass in the loop (e.g. each
# argument has a corresponding value to go with it).
# Use > 0 to consume one or more arguments per pass in the loop (e.g.
# some arguments don't have a corresponding value to go with it such
# as in the --default example).
# note: if this is set to > 0 the /etc/hosts part is not recognized ( may be a bug )

REQUEST_TMPFILE="/tmp/subscription-json.ngsi"

##DEFAULT VALUES
DEFAULT_ISPATTERN=false

while [[ $# > 1 ]]
do
key="$1"

case $key in
    
    -e|--entityid)
    ENTITIES+=("$2")
    shift # past argument
    ;;  
    -a|--attribute)
    ATTRIBUTES+=("$2")
    shift # past argument
    ;;
	-c|--contenttype)
    CONTENTTYPE="$2"
    shift # past argument
    ;;
    -u|--url)
    URL="$2"
    shift # past argument
    ;;
    -r|--reference)
    REFERENCE="$2"
    shift # past argument
    ;;   
    -f|--file)
    FILE="$2"
    shift # past argument
    ;;    *)
            # unknown option
    ;;
esac
shift # past argument or value
done

function usage {
	echo "Please specify all the required parameters"
	echo "Usage: ngsi10-subscribecontext -e <entityId[,type][,isPattern]> -r <referenceurl> -u <url> [-a <attributename[,..]>] [-e <entityId[,type][,isPattern]> ...]"
	echo "Usage: ngsi10-subscribecontext -u <url> -f <file> [-c <contenttype>]"
	echo
	echo "Example: ngsi10-subscribecontext -e e1,,true -e e2,t2 -a temperature,co2 -r http://localhost:8101/ngsi10/notify -u http://localhost:8065/ngsi10/subscribeContext"
}

function createEntityList {

	ENTITY_TEMPLATE='{
			"id": "ENTITYID_PLACEHOLDER",
			"type": "ENTITYTYPE_PLACEHOLDER",
			"isPattern": ISPATTERN_PLACEHOLDER
	}'
	
	first="true"
	for i in "${ENTITIES[@]}"
	do
		ENTITY=$ENTITY_TEMPLATE
		
		set -f
		i=${i//,,/,_,}
		idTypeIspattern=(${i//,/ })
		
		if [ -z "${idTypeIspattern[0]}" ];
		then
			continue
		fi
		ENTITY=${ENTITY//ENTITYID_PLACEHOLDER/${idTypeIspattern[0]}}
			
		if [ -z "${idTypeIspattern[1]}" ] || [ "${idTypeIspattern[1]}" == "_" ];
		then
			ENTITY=${ENTITY//\"type\": \"ENTITYTYPE_PLACEHOLDER\",/}
		else
			ENTITY=${ENTITY//ENTITYTYPE_PLACEHOLDER/${idTypeIspattern[1]}}
		fi
		
		if [ -z "${idTypeIspattern[2]}" ];
		then
			ENTITY=${ENTITY//ISPATTERN_PLACEHOLDER/$DEFAULT_ISPATTERN}
			echo "Default isPattern set for ${idTypeIspattern[0]} : $DEFAULT_ISPATTERN"

		else
			ENTITY=${ENTITY//ISPATTERN_PLACEHOLDER/${idTypeIspattern[2]}}
		fi
		
		if [ "$first" == "false" ]; 
		then
			ENTITYLIST+=","
		fi
		ENTITYLIST+=$ENTITY
		first="false"
		
	done
		
}

function createAttributeList {
	first="true"
	for i in "${ATTRIBUTES[@]}"
	do		
		set -f
		attributes=(${i//,/ })
		
		for j in "${attributes[@]}"
		do
			if [ -z "$j" ];
			then
				continue
			fi
			
			if [ "$first" == "false" ]; 
			then
				ATTRIBUTELIST+=","
			fi
			
			ATTRIBUTELIST+="\"$j\""
			first="false"
		done
	done
}


SUBSCRIPTION='{
    "entities": [ENTITYLIST_PLACEHOLDER],
    "attributes": [ATTRIBUTELIST_PLACEHOLDER],
    "reference": "REFERENCE_PLACEHOLDER"
}'

if [ -z "$URL" ];
then
	usage
	exit
fi


if [ -z "$REFERENCE" ];
then
	usage
	exit
fi

if [ -n "$FILE" ] && [ -z "$CONTENTTYPE" ];
then
	CONTENTTYPE="application/json"
fi



if [ -n "$FILE" ] && [ -f "$FILE" ]
then
	echo "SubscribeContext to be sent:"
	cat $FILE
	curl -H "Content-Type: $CONTENTTYPE" -X POST -d @$FILE $URL
	
elif [ -z "$FILE" ] && [ ${#ENTITIES[@]} -eq 0 ]
then
	usage
	exit
else
	createAttributeList
	createEntityList
	
	SUBSCRIPTION=${SUBSCRIPTION//ENTITYLIST_PLACEHOLDER/$ENTITYLIST}
	SUBSCRIPTION=${SUBSCRIPTION//REFERENCE_PLACEHOLDER/$REFERENCE}
	
	if [ -z "$ATTRIBUTELIST" ]
	then
		SUBSCRIPTION=${SUBSCRIPTION//\"attributes\": \[ATTRIBUTELIST_PLACEHOLDER\],/}
	else
		SUBSCRIPTION=${SUBSCRIPTION//ATTRIBUTELIST_PLACEHOLDER/$ATTRIBUTELIST}
	fi

	echo "SubscribeContext to be sent:"
	echo $SUBSCRIPTION
	echo $SUBSCRIPTION > $REQUEST_TMPFILE
	curl -H "Content-Type: application/json" -X POST -d @$REQUEST_TMPFILE $URL
	echo
fi

#~ ESCAPEDUPDATE=${UPDATE//\"/\\\"}
#~ echo $ESCAPEDUPDATE



#curl -H "Content-Type: application/json" -X POST -d @.update.ngsi $URL

#~ curl -H "Content-Type: application/json" -X POST -d '$ESCAPEDUPDATE' $URL
