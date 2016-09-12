#!/bin/bash
# Use > 1 to consume two arguments per pass in the loop (e.g. each
# argument has a corresponding value to go with it).
# Use > 0 to consume one or more arguments per pass in the loop (e.g.
# some arguments don't have a corresponding value to go with it such
# as in the --default example).
# note: if this is set to > 0 the /etc/hosts part is not recognized ( may be a bug )

REQUEST_TMPFILE="/tmp/update-json.ngsi"

DEFAULT_LATITUDE="1.283333"
DEFAULT_LONGITUDE="103.833333"
DEFAULT_RADIUS="2"

while [[ $# > 1 ]]
do
key="$1"

case $key in
    
    -e|--entityid)
    ENTITYID="$2"
    shift # past argument
    ;;
    -a|--attribute)
    ATTRIBUTENAME="$2"
    shift # past argument
    ;;
    -v|--value)
    VALUE="$2"
    shift # past argument
    ;;
    -y|--latitude)
    LATITUDE="$2"
    shift # past argument
    ;;
    -x|--longitude)
    LONGITUDE="$2"
    shift # past argument
    ;;
    -r|--radius)
    RADIUS="$2"
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
	echo "Please specify all the required parameters"
	echo "Usage: ngsi10-updatecontext -e <entityid> -a <attributename> -v <value> -u <url>"
	echo "Usage: ngsi10-updatecontext -u <url> -f <file> [-c <contenttype>]"
}

#UPDATE='{"updateAction": "UPDATE","contextElements": [{"entityId": {"id": "ENTITYID_PLACEHOLDER","isPattern": false},"attributes": [{"name": "ATTRIBUTENAME_PLACEHOLDER","type": "ATTRIBUTENAME_PLACEHOLDER","contextValue": "CONTEXTVALUE_PLACEHOLDER","metadata": [{"name": "date","type": "date","value": "DATE_PLACEHOLDER"}]}]}]}'

UPDATE='{
    "updateAction": "UPDATE",
    "contextElements": [{
        "entityId": {
            "id": "ENTITYID_PLACEHOLDER",
            "isPattern": false
        },
        "domainMetadata": [
       {
           "name": "SimpleGeoLocation",
           "type": "SimpleGeoLocation",
           "value": { "centerLatitude" :"CENTERLATITUDE_PLACEHOLDER", "centerLongitude" :"CENTERLONGITUDE_PLACEHOLDER","radius" :"RADIUS_PLACEHOLDER"}
           }],
        "attributes": [{
            "name": "ATTRIBUTENAME_PLACEHOLDER",
            "type": "ATTRIBUTENAME_PLACEHOLDER",
            "contextValue": "CONTEXTVALUE_PLACEHOLDER",
            "metadata": [{
                "name": "date",
                "type": "date",
                "value": "DATE_PLACEHOLDER"
            }]
        }]
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


if [ -z "$LATITUDE" ]
then
	LATITUDE=$DEFAULT_LATITUDE
	echo "Default Latitude set: $LATITUDE"
fi
if [ -z "$LONGITUDE" ]
then
	LONGITUDE=$DEFAULT_LONGITUDE
	echo "Default Latitude set: $LONGITUDE"
fi
if [ -z "$RADIUS" ]
then
	RADIUS=$DEFAULT_RADIUS
	echo "Default Radius set: $RADIUS"
fi


if [ -n "$FILE" ] && [ -f "$FILE" ]
then
	echo "UpdateContext to be sent:"
	cat $FILE
	curl -H "Content-Type: $CONTENTTYPE" -X POST -d @$FILE $URL
	
elif [ -z "$FILE" ] && ([ -z "$ENTITYID" ] || [ -z "$ATTRIBUTENAME" ] || [ -z "$VALUE" ]);
then
	usage
	exit
else 
	UPDATE=${UPDATE//ENTITYID_PLACEHOLDER/$ENTITYID}
	UPDATE=${UPDATE//ATTRIBUTENAME_PLACEHOLDER/$ATTRIBUTENAME}
	UPDATE=${UPDATE//CONTEXTVALUE_PLACEHOLDER/$VALUE}
	UPDATE=${UPDATE//CENTERLATITUDE_PLACEHOLDER/$LATITUDE}
	UPDATE=${UPDATE//CENTERLONGITUDE_PLACEHOLDER/$LONGITUDE}
	UPDATE=${UPDATE//RADIUS_PLACEHOLDER/$RADIUS}
	DATE=`date "+%Y-%m-%d %H:%M:%S"`
	UPDATE=${UPDATE//DATE_PLACEHOLDER/$DATE}
	echo "UpdateContext to be sent:"
	echo $UPDATE
	echo $UPDATE > $REQUEST_TMPFILE
	curl -H "Content-Type: application/json" -X POST -d @$REQUEST_TMPFILE $URL
	echo
fi

#~ ESCAPEDUPDATE=${UPDATE//\"/\\\"}
#~ echo $ESCAPEDUPDATE



#curl -H "Content-Type: application/json" -X POST -d @.update.ngsi $URL

#~ curl -H "Content-Type: application/json" -X POST -d '$ESCAPEDUPDATE' $URL
