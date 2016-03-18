#!/bin/bash

# mount a raspberry image, copy and rename the certificates 
# (CA, personal public and personal private key) on the openvpn folder

key_folder="/mnt/sd/rasp/etc/openvpn/keys"
mount -t ext4 -o loop,offset=62914560 $1 /mnt/sd/rasp/
args="$@"
rm -f $key_folder/*
rm -rf /mnt/sd/rasp/home/pi/python-games
for file in $args
do
	echo "Processing file $file"
	if [[ $file != *.img ]]
	then
		echo "Copy file $file"
		cp $file $key_folder
		if [[ $file == *.crt && $file != ISMB_CA.crt ]]
		then
			echo "Rename $file in gw.crt"
			mv "$key_folder/$file" "$key_folder/gw.crt"
		fi
		if [[ $file == *.crt && $file == ISMB_CA.crt ]]
		then
			echo "ISMB_CA.crt will not be renamed"
		fi
		if [[ $file == *.pem ]]
		then
			echo "Rename $file in gw_key.pem"
			mv "$key_folder/$file" "$key_folder/gw_key.pem"
		fi
	fi
done
umount /mnt/sd/rasp/
