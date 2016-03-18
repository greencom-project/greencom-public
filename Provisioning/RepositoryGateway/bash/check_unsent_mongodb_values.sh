#!/bin/bash
# Mail part copied from check_disk_space.sh
PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/local/games:/usr/games



SETTINGS="/home/pi/.greencom.settings"
# Load email settings
if [ -f "$SETTINGS" ]; then . "$SETTINGS"; else echo "$SETTINGS not found, exiting!"; exit 1; fi

# Locate $Mongo/bin 
MONGO_BIN=`find /opt -type d -path "*/mongo*/bin"`
UNSENT=`$MONGO_BIN/mongo gatewaydb --quiet --eval 'db.SampledValue.count({"sent":false})'`
IP_ADDR=`ifconfig | grep "inet addr:10.30" | sed -e's/  */ /g' | cut -d' ' -f3 | sed -e's/addr://g'`


if [[ "$UNSENT" -gt "$LIMIT_UNSENT_VALUES" ]]
then
	MESSAGE="WARNING: Large number of unsent values ($UNSENT) on gateway $IP_ADDR"
	sendemail -f "$EMAIL_FROM" -t "$EMAIL_TO" -u "GreenCom noreply:  Large number of unsent values" -m "$MESSAGE" -s smtp.gmail.com:587 -o tls=yes -xu "$EMAIL_FROM" -xp "$SMTP_PWD"	
fi

