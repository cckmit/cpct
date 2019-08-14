#!/bin/bash
SERVER_PORT=$1
ip=`ifconfig eth0 | grep 'inet ' | awk '{print $2}'`
curl  --connect-timeout 5 "$ESB_UNREGISTER_URI?$ip@$SERVER_PORT"
