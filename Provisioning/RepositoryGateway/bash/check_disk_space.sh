#!/bin/bash

PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/local/games:/usr/games

SETTINGS="/home/pi/.greencom.settings"
# Load email settings
if [ -f "$SETTINGS" ]; then . "$SETTINGS"; else echo "$SETTINGS not found, exiting!"; exit 1; fi

PERC=`df -lk | grep rootfs | sed -e's/  */ /g' | cut -d" " -f 5 | sed -e's/%//g'`
IP_ADDR=`ifconfig | grep "inet addr:10.30" | sed -e's/  */ /g' | cut -d' ' -f3 | sed -e's/addr://g'`


if [[ $PERC -gt $LIMIT_DISK_USAGE_RATIO ]]
then
        MESSAGE="WARNING: Disk space is ending ($PERC% used) on gateway $IP_ADDR"
        sendemail -f "$EMAIL_FROM" -t "$EMAIL_TO" -u "GreenCom noreply : disk space is ending" -m "$MESSAGE" -s smtp.gmail.com:587 -o tls=yes -xu "$EMAIL_FROM" -xp "$SMTP_PWD"        
fi
