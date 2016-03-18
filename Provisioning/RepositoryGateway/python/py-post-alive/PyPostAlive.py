#!/usr/bin/python

# see examples here https://docs.python.org/2/howto/urllib2.html

from PyDwhDumpProperties import *
from PyDwhDumpAuth import *
import os, sys

class PyPostAlive:
	
	def __init__(self,props,targetgw):
		self.props = props;
		self.targetgw = targetgw;

	def prettyprint(self,myjson):
		print json.dumps(myjson, indent=4, sort_keys=True)

	def post(self):
		print "posting alive message for: " + self.targetgw
		opener = urllib2.build_opener(urllib2.HTTPCookieProcessor(self.props.ccokiejar));
		headers = { 'Content-Type': 'application/json' };
		mypath = self.props.alivegwpath
		mypath = mypath.replace('SID_TO_BE_REPLACED',self.targetgw)
		#print "posting alive message to: " + mypath
		postvalues = {'Alive' : 'ok'}
		postvalues = json.dumps(postvalues)
		req = urllib2.Request(mypath, postvalues ,headers);
		response = opener.open(req);
		if(response.getcode()==200):
			print "ok"
		else:
			print "error code:" + response.getcode() + " on call to " + mypath

if len(sys.argv) < 2:
    sys.stderr.write('Missing argument. Usage: ./PyPostAlive.py <GWID>\n\n')
    sys.exit(1)

props = PyDwhDumpProperties();
PyDwhDumpAuth(props);
ppa = PyPostAlive(props,sys.argv[1]);
ppa.post()





