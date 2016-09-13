#!/bin/bash
# Use > 1 to consume two arguments per pass in the loop (e.g. each
# argument has a corresponding value to go with it).
# Use > 0 to consume one or more arguments per pass in the loop (e.g.
# some arguments don't have a corresponding value to go with it such
# as in the --default example).
# note: if this is set to > 0 the /etc/hosts part is not recognized ( may be a bug )

REQUEST_TMPFILE="/tmp/query-xml.ngsi"

##DEFAULT VALUES
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
    -a|--attribute)
    ATTRIBUTENAME="$2"
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
	echo "Usage: ngsi10-querycontext -e <entityid> -a <attributename> -u <url> [-t <entitytype>] [-p <ispattern>]"
	echo "Usage: ngsi10-querycontext -u <url> -f <file> [-c <contenttype>]"
}

QUERY='{
    "entities": [{
        "id": "ENTITYID_PLACEHOLDER",
        "type": "ENTITYTYPE_PLACEHOLDER",
        "isPattern": ISPATTERN_PLACEHOLDER
    }],
    "attributes": ["ATTRIBUTENAME_PLACEHOLDER"]
}'

QUERY='<?xml version="1.0" encoding="UTF-8"?>
<queryContextRequest>
  <entityIdList>
    <entityId type="ENTITYTYPE_PLACEHOLDER" isPattern="ISPATTERN_PLACEHOLDER">
      <id>ENTITYID_PLACEHOLDER</id>
    </entityId>
  </entityIdList>
  <attributeList>
    <attribute>ATTRIBUTENAME_PLACEHOLDER</attribute>
  </attributeList>
</queryContextRequest>'

if [ -z "$URL" ];
then
	usage
	exit
fi

	

if [ -n "$FILE" ] && [ -z "$CONTENTTYPE" ];
then
	CONTENTTYPE="application/xml"
fi


if [ -z "$ISPATTERN" ]
then
	ISPATTERN=$DEFAULT_ISPATTERN
	echo "Default isPattern set: $ISPATTERN"
fi


if [ -n "$FILE" ] && [ -f "$FILE" ]
then
	echo "QueryContext to be sent:"
	cat $FILE
	curl -H "Content-Type: $CONTENTTYPE" -X POST -d @$FILE $URL
	
elif [ -z "$FILE" ] && ([ -z "$ENTITYID" ] || [ -z "$ATTRIBUTENAME" ]);
then
	usage
	exit
	
else 
	QUERY=${QUERY//ENTITYID_PLACEHOLDER/$ENTITYID}
	if [ -z "$ENTITYTYPE" ]
	then
		QUERY=${QUERY//type=\"ENTITYTYPE_PLACEHOLDER\"/}
	else
		QUERY=${QUERY//ENTITYTYPE_PLACEHOLDER/$ENTITYTYPE}
	fi
	QUERY=${QUERY//ISPATTERN_PLACEHOLDER/$ISPATTERN}
	QUERY=${QUERY//ATTRIBUTENAME_PLACEHOLDER/$ATTRIBUTENAME}
	echo "QueryContext to be sent:"
	echo $QUERY
	echo $QUERY > $REQUEST_TMPFILE
	curl -H "Content-Type: application/xml" -X POST -d @$REQUEST_TMPFILE $URL
	echo 
fi



#~ ESCAPEDUPDATE=${UPDATE//\"/\\\"}
#~ echo $ESCAPEDUPDATE



#curl -H "Content-Type: application/json" -X POST -d @.update.ngsi $URL

#~ curl -H "Content-Type: application/json" -X POST -d '$ESCAPEDUPDATE' $URL
