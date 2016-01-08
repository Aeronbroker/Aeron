#!/bin/bash -ex

#install puppet
sudo apt-get update -q
sudo apt-get install -y puppet

#download puppet install script
wget  https://raw.githubusercontent.com/Aeronbroker/Aeron/master/puppet/install$

#apply puppet install script
sudo puppet apply install.pp