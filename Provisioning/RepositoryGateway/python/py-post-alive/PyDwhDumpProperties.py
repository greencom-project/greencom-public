from configobj import ConfigObj

class PyDwhDumpProperties:
	"""class storing all common properties and status variables for other scripts"""

	### adapting with properties
	cp = ConfigObj(r"/home/pi/.greencom.settings")
	USER =  cp.get('POSTALIVE_USER');
	PASS = cp.get('POSTALIVE_PWD') ;
	
	ccokiejar = 0;
	authpath = cp.get('POSTALIVE_AUTH_PATH');
	listgwpath = cp.get('POSTALIVE_LISTGW_PATH')
	
	#DATE FORMAT: 2015-07-21T00:00:00.0000000Z
	alivegwpath = cp.get('POSTALIVE_ALIVEGW_PATH')

    
	def __init__(self):
		# Just a constructor
		self.data = []
