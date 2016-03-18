#!/usr/bin/python

# see examples here https://docs.python.org/2/howto/urllib2.html

from PyDwhDumpProperties import *
import cookielib, urllib, urllib2
import json

def PyDwhDumpAuth(props):
	props.ccokiejar = cookielib.CookieJar()
	opener = urllib2.build_opener(urllib2.HTTPCookieProcessor(props.ccokiejar))
	postvalues = {'Username' : props.USER, 'Password' : props.PASS }
	postvalues = json.dumps(postvalues)
	headers = { 'Content-Type': 'application/json' }
	req = urllib2.Request(props.authpath, postvalues,headers)          
	r = opener.open(req)
	#print props.ccokiejar
