#!/bin/bash
# Use > 1 to consume two arguments per pass in the loop (e.g. each
# argument has a corresponding value to go with it).
# Use > 0 to consume one or more arguments per pass in the loop (e.g.
# some arguments don't have a corresponding value to go with it such
# as in the --default example).
# note: if this is set to > 0 the /etc/hosts part is not recognized ( may be a bug )

REQUEST_TMPFILE="/tmp/registration-json.ngsi"

DEFAULT_ISPATTERN=false

while [[ $# > 1 ]]
do
key="$1"

case $key in
    
    -e|--entityid)
    ENTITYID="$2"
    shift # past argument
    ;;
    -t|--entitytype)
    ENTITYTYPE="$2"
    shift # past argument
    ;;
    -p|--ispattern)
    ISPATTERN="$2"
    shift # past argument
    ;;    
    -a|--attributename)
    ATTRIBUTENAME="$2"
    shift # past argument
    ;;
    -y|--attributetype)
    ATTRIBUTETYPE="$2"
    shift # past argument
    ;;
    -v|--providingapplication)
    PROVIDINGAPPLICATION="$2"
    shift # past argument
    ;;
    -d|--duration)
    DURATION="$2"
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
	echo "Please specify the url"
	echo "Usage: ngsi9-registercontext -e <entityid> -v <providingapplication> -u <url> [-t <entitytype>][-a <attributename>][-y <attributetype>][-p <ispattern>] [-d <duration in the form PnYnMnDTnHnMnS>] "
	echo "Usage: ngsi9-registercontext -u <url> -f <file> [-c <contenttype>]"
}

REGISTRATION='{
    "duration": "DURATION_PLACEHOLDER",
    "contextRegistrations": [{
        "entities": [{
            "id": "ENTITYID_PLACEHOLDER",
            "type": "ENTITYTYPE_PLACEHOLDER",
            "isPattern": ISPATTERN_PLACEHOLDER
        }],
        "attributes": [{
            "name": "ATTRIBUTENAME_PLACEHOLDER",
            "type": "ATTRIBUTETYPE_PLACEHOLDER",
            "isDomain": "false"}],
        "providingApplication": "PROVIDINGAPPLICATION_PLACEHOLDER"
    }]
}'

if [ -z "$URL" ];
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
	echo "RegisterContext to be sent:"
	cat $FILE
	curl -H "Content-Type: $CONTENTTYPE" -H "Accept: $CONTENTTYPE" -X POST -d @$FILE $URL
	
elif [ -z "$FILE" ] && ([ -z "$ENTITYID" ] || [ -z "$PROVIDINGAPPLICATION" ]);
then
	usage
	exit
	
else

	if [ -z "$ISPATTERN" ]
	then
		ISPATTERN=$DEFAULT_ISPATTERN
		echo "Default isPattern set: $ISPATTERN"
	fi

	if [ -z "$DURATION" ]
	then
		REGISTRATION=${REGISTRATION//\"duration\": \"DURATION_PLACEHOLDER\",/}
	else
		REGISTRATION=${REGISTRATION//DURATION_PLACEHOLDER/$DURATION}
	fi
	
	if [ -z "$ENTITYTYPE" ]
	then
		REGISTRATION=${REGISTRATION//\"type\": \"ENTITYTYPE_PLACEHOLDER\",/}
	else
		REGISTRATION=${REGISTRATION//ENTITYTYPE_PLACEHOLDER/$ENTITYTYPE}
	fi
	
	if [ -z "$ATTRIBUTENAME" ]
	then
		REGISTRATION=${REGISTRATION//\"attributes\": [\{/}
		REGISTRATION=${REGISTRATION//\"name\": \"ATTRIBUTENAME_PLACEHOLDER\",/}
		REGISTRATION=${REGISTRATION//\"type\": \"ATTRIBUTETYPE_PLACEHOLDER\",/}
		REGISTRATION=${REGISTRATION//\"isDomain\": \"false\"\}],/}
	else
		REGISTRATION=${REGISTRATION//ATTRIBUTENAME_PLACEHOLDER/$ATTRIBUTENAME}
		
		if [ -z "$ATTRIBUTETYPE" ]
		then
			REGISTRATION=${REGISTRATION//\"type\": \"ATTRIBUTETYPE_PLACEHOLDER\",/}
		else
			REGISTRATION=${REGISTRATION//ATTRIBUTETYPE_PLACEHOLDER/$ATTRIBUTETYPE}
		fi
		
	fi
	
	REGISTRATION=${REGISTRATION//ENTITYID_PLACEHOLDER/$ENTITYID}
	REGISTRATION=${REGISTRATION//PROVIDINGAPPLICATION_PLACEHOLDER/$PROVIDINGAPPLICATION}
	REGISTRATION=${REGISTRATION//ISPATTERN_PLACEHOLDER/$ISPATTERN}

	echo "RegisterContext to be sent:"
	echo $REGISTRATION
	echo $REGISTRATION > $REQUEST_TMPFILE
	curl -H "Content-Type: application/json" -H "Accept: application/json" -X POST -d @$REQUEST_TMPFILE $URL
	echo
fi

#~ ESCAPEDUPDATE=${UPDATE//\"/\\\"}
#~ echo $ESCAPEDUPDATE



#curl -H "Content-Type: application/json" -X POST -d @.update.ngsi $URL

#~ curl -H "Content-Type: application/json" -X POST -d '$ESCAPEDUPDATE' $URL
