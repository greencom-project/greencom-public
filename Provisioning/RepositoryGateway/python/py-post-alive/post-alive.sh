#!/bin/bash

# note: in future we may a better place to resolve the installation_id
# At the moment we get it from /home/pi/greencom_installation_id

inst_id=$(cat /home/pi/greencom_installation_id)

python /home/pi/greencom/python/py-post-alive/PyPostAlive.py $inst_id
date > /home/pi/.py-post-alive-last.log
