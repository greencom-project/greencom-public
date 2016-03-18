#!/bin/bash

# this scripts cycles through all gateways and run the same command

LAST=$1

#trap ctrl_c INT

#function ctrl_c() {
#	echo "Interrupted - killing tunnels PID: $PID,$PID2"
#	kill -9 $PID
#
#}


declare -A houses
houses=(\
	["House01"]="10.30.0.22"\
	["House02"]="10.30.0.198"\
	["House03"]="10.30.0.90"\
	["House04"]="10.30.0.30"\
	["House05"]="10.30.0.6"\
	["House06"]="10.30.0.194"\
	["House07"]="10.30.0.38"\
	["House07b"]="10.30.0.174"\
	["House08"]="10.30.0.142"\
	["House09"]="10.30.0.66"\
	["House10"]="10.30.0.166"\
	["House11"]="10.30.0.74"\
	["House12"]="10.30.0.70"\
	["House13"]="10.30.0.154"\
	["House14"]="10.30.0.146"\
	["House16"]="10.30.0.82"\
	["House17"]="10.30.0.86"\
	["House19"]="10.30.0.170"\
	["House20"]="10.30.0.98"\
	["House22"]="10.30.0.150"\
	["House23"]="10.30.0.110"\
	["GWTYND1"]="10.30.0.18"\
	["GWTYND2-PV"]="10.30.0.130"\
	["GWTYND3-ERI"]="10.30.0.122"\
	["GWSENSING"]="10.30.0.54"\
	["GWSCS"]="10.30.0.10"\
	["GWFIT"]="10.30.0.14"\
	["GWISMB1"]="10.30.0.102"\
	["GWISMB1-EAH"]="10.30.0.162"\
	)

	#["House15"]="10.30.0.UNK"
	#["House18"]="10.30.0.UNK"\	
	#["House21"]="10.30.0.UNK"\
	
# direct access example:
# echo "${houses["House01"]}"

# loop example:
# for ip in "${!houses[@]}"; do echo "$ip - ${houses["$ip"]}"; done

if [ -z "$LAST" ]
then
	echo -e "Usage:\\n\\ttunnel-greencom-masscommand.sh <COMMAND>"\\n
	echo -e "Example:\\n\\ttunnel-greencom-masscommand.sh 'df -h'\\t(check space on all Gws)"\\n
	echo -e \\n
else

	# pre-kill ssh just in case
	PID=$(lsof -t -i @localhost:7777 -sTCP:listen)
	kill -9 $PID

	echo "Running command [$LAST] on all gateways."
	# Sort the keys (house IDs)
	houseids=`echo "${!houses[@]}" | tr " " "\n" | sort -V`  
	for houseid in $houseids
	do
		CMD=$LAST
		HOUSEID=$houseid
		IP=${houses["$houseid"]}
		echo -e "\\nRunning [$CMD] on [$HOUSEID] - [$IP]"; 
		# Commented out to make it less verbose
		#echo -e "\\tLaunching tunnel to gateway $IP through vpn center (130.192.85.240). $IP:22 will be available on localhost:7777"
		ssh -N -f -L 7777:$IP:22 greencomuser@greencom.polito.it		
		PID=$(lsof -t -i @localhost:7777 -sTCP:listen)
		
		echo -e "\\t------------------------------------"
		ssh -o ConnectTimeout=8 -p 7777 pi@localhost "$CMD" | sed 's/^/	/'
		echo -e "\\t------------------------------------"

		echo -e "\\tOperation [$CMD] on [$HOUSEID] - [$IP] completed"; 
		kill -9 $PID
	done
fi










