#!/bin/bash
PID=`cat pid.file 2> /dev/null`
if [ -z "$PID" ];
then
	echo "IoT Broker is not running"
else
	kill -9 $PID
	rm pid.file
fi
