#GreenCom Python Post Alive Script

Tested with Python 2.7.x

## Main Script

## Sub-scripts

- **PyListGW**: posts the current list of GW
- **PyPostAlive**: posts an alive message for this gateway
- **PyDwhDumpProperties.py** : class storing all common properties for other scripts
- **PyDwhDumpAuth.py** : generates authentication cookie from user/password

## you will need

- python 2.7.x (NOT 3.x)
- python dateutil
	- to install in ubuntu: sudo apt-get install python-pip; sudo pip install python-dateutil

## Notes

- List of gateways can be taken by doing:
	- GET http://greencom.cloudapp.net:80/api/gateways
- Availability can be taken by doing:
	http://greencom.cloudapp.net/api/gateways/00000000-0000-0000-0000-000000000002/alive?format=json

# cron
In order to run every 20 minutes add the following line:

*/20 * * * * /home/pi/scripts/py-post-alive/post-alive.sh 
