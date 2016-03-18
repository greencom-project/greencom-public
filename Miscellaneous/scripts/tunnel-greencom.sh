#!/bin/bash

# this scripts cycles through all gateways and run the same command

LAST=$1

trap ctrl_c INT

function ctrl_c() {
	echo "Interrupted - killing tunnels PID: $PID,$PID2"
	kill -9 $PID
	kill -9 $PID2
}

# loop example:
# for ip in "${!houses[@]}"; do echo "$ip - ${houses["$ip"]}"; done

if [ -z "$LAST" ]
then
	echo -e "\nGreenCom tunnel script\n"\\n
	echo -e "Usage:\\n\\ttunnel-greencom.sh <LAST-TWO-NUMBERS-OF-THE-IP-ADDRESS>"\\n
	echo -e "Example:\\n\\ttunnel-greencom.sh 102\\n\\t(connects to ISMB Gateway)"\\n
	echo -e 'Current list of GreenCom GWs'\\n

	#for houseid in "${!houses[@]}";
	#	do echo -e "\\t [$houseid]:\\t ${houses["$houseid"]}"; 
	#done
	
	echo -e "\t\tHouse01 \t\t 10.30.0.22"
	echo -e "\t\tHouse01b \t\t 10.30.0.238"
	echo -e "\t\tHouse02 \t\t 10.30.1.138"
	echo -e "\t\tHouse02b \t\t 10.30.1.122"
	echo -e "\t\tHouse03 \t\t 10.30.0.90"
	echo -e "\t\tHouse03b \t\t 10.30.1.134"
	echo -e "\t\tHouse04 \t\t 10.30.0.30"
	echo -e "\t\tHouse04b \t\t 10.30.1.18"
	echo -e "\t\tHouse05 \t\t 10.30.1.2"
	echo -e "\t\tHouse06 \t\t 10.30.0.194"
	echo -e "\t\tHouse06b \t\t 10.30.0.226"
	echo -e "\t\tHouse07 \t\t 10.30.0.38"
	echo -e "\t\tHouse07b \t\t 10.30.1.114"
	echo -e "\t\tHouse09 \t\t 10.30.0.66"
	echo -e "\t\tHouse10 \t\t 10.30.0.166"
	echo -e "\t\tHouse11 \t\t 10.30.0.74"
	echo -e "\t\tHouse12 \t\t 10.30.0.70"
	echo -e "\t\tHouse13 \t\t 10.30.0.154"
	echo -e "\t\tHouse14 \t\t 10.30.0.146"
	echo -e "\t\tHouse16 \t\t 10.30.0.82"
	echo -e "\t\tHouse17 \t\t 10.30.0.86"
	echo -e "\t\tHouse19 \t\t 10.30.0.170"
	echo -e "\t\tHouse20 \t\t 10.30.0.98"
	echo -e "\t\tHouse22 \t\t 10.30.0.150"
	echo -e "\t\tHouse24b \t\t 10.30.0.222"	
	echo -e "\t\tHouse25b \t\t 10.30.1.102"
	echo -e "\t\tHouse26b \t\t 10.30.1.70"
	echo -e "\t\tGWFIT \t\t\t 10.30.0.14"
	echo -e "\t\tGWISMB1 \t\t 10.30.0.102"
	echo -e "\t\tGWISMB1-EAH \t\t 10.30.0.162"
	echo -e "\t\tGWISMB2-TestFossano \t 10.30.0.202"
	echo -e "\t\tGWTYND1 \t\t 10.30.0.18"
	echo -e "\t\tGWTYND2-PV \t\t 10.30.0.130"
	echo -e "\t\tGWTYND3-ERI \t\t 10.30.0.122"
	echo -e "\t\tGWSENSING \t\t 10.30.0.54"
	echo -e "\t\tGWSCS \t\t\t 10.30.0.10"
	echo -e "\t\tHouse23-INJET \t\t 10.30.0.110"

	echo -e \\n
else
	echo -e \\nConnecting to 10.30.$LAST
	echo -e "\\tLaunching tunnel 1 to gateway 10.30.$LAST through vpn center (130.192.86.240). 10.30.$LAST:22 will be available on localhost:7777"
	ssh -N -f -L 7777:10.30.$LAST:22 greencomuser@greencom.polito.it
	
	echo -e "\\tLaunching tunnel 2 to gateway 10.30.$LAST through vpn center (130.192.86.240). 10.30.$LAST:8080 will be available on localhost:8888"
	ssh -N -f -L 8888:10.30.$LAST:8080 greencomuser@greencom.polito.it

	PID=$(lsof -t -i @localhost:7777 -sTCP:listen)
	PID2=$(lsof -t -i @localhost:8888 -sTCP:listen)

	echo -e "*** launching SSH connection through tunnel (PID=$PID, PID2=$PID2) ...\n\n"

	ssh -p 7777 pi@localhost

	echo "Exited - killing tunnels PID: $PID,$PID2"

	kill -9 $PID
	kill -9 $PID2

	echo -e "\n\n*** OK."
fi










