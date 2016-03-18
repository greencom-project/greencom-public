#!/bin/bash 

## This script checks if the OpenVPN Client daemon is up or not; and if it is not up it brings it up.

#PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/etc/openvpn/scripts

ps -ef | grep -v grep | grep openvpn 
if [ $? -eq 1 ] ; then 
	/etc/init.d/openvpn start
else
	echo "OpenVPN is running already :)" 
fi
date > /tmp/keepalive_date
