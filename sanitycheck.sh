#!/bin/bash -ex

#check whether port 80 is accessable on target machine
wget http://$IP -O /dev/null
