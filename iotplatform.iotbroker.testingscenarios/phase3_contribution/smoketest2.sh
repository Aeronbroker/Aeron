# contributor: watly

HOST=$1
PORT=$2
# check if daemon is running
curl $HOST/version

#create a first "Machine1" Entity with two attributes: temperature and pressure
curl -v -H "Accept: application/json" -H "Content-type: application/json" -X POST -d '
  { "attributes":
    [
      { "name": "temperature",
      "type": "float",
      "value": "23"
      },
      { "name": "pressure",
      "type": "integer",
      "value": "720"
      }
    ]
  }' $HOST/v1/contextEntities/Machine1

#retrieve "Machine1" attributes
curl $HOST/v1/contextEntities

#update the value of the temperature attribute in "Machine1"
curl -v -H "Accept: application/json" -H "Content-type: application/json" -X PUT -d '{
    "value": "18.4"
}' $HOST/v1/contextEntities/Machine1/attributes/temperature

#then check that the new value is correctly updated
curl $HOST/v1/contextEntities
