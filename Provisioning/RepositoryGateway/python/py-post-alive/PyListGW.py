#!/usr/bin/python

# see examples here https://docs.python.org/2/howto/urllib2.html

from PyDwhDumpProperties import *
from PyDwhDumpAuth import *
import cookielib, urllib, urllib2
import json

class PyListGW:
	
	def __init__(self,props):
		self.props = props;
		self.gws = {}

	def prettyprint(self,myjson):
		print json.dumps(myjson, indent=4, sort_keys=True)

	def load(self):
		opener = urllib2.build_opener(urllib2.HTTPCookieProcessor(self.props.ccokiejar));
		headers = { 'Content-Type': 'application/json' };
		req = urllib2.Request(self.props.listgwpath, None ,headers);
		urllib2.install_opener(opener)
		response = opener.open(req);
		jsonpayloads = response.read();
		#print jsonpayloads
		jsonpayload = json.loads(jsonpayloads);
		#print jsonpayload
		jsonpayload= jsonpayload['Gateways']
		#print self.prettyprint(jsonpayload)
		
		for gw in jsonpayload:
			#self.prettyprint(gw)
			idgw = gw['Id'];
			self.gws[idgw] = idgw;
			#print idgw
	
	def print_all(self):
		print "Available GWs:"
		for key in self.gws:
			print "\t" + self.gws[key]
			
	def get_all(self):
		return self.installations



# run stand-alone

props = PyDwhDumpProperties();
PyDwhDumpAuth(props);

lgw = PyListGW(props);
lgw.load();
lgw.print_all();


