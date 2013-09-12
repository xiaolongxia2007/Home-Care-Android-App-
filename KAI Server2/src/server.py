'''
    Create at: 2010-01-18
    Author: Zhang Renyu
'''

from bottle import run, route, request, response
from objectRecognize import objectRecognize
from sql import updateOrInsertRegistration
from sql import updateOrInsertMonitor
from sql import getRegistrationId
from sql import saveSnapshot
from sql import getSnapshot
import sql
import threading
import Image
import httplib
import time
import StringIO
import sqlite3
import urllib
import urllib2
import json

global server_account_name
server_account_name = "liuhongbin2007@gmail.com"
global collapse_key
collapse_key = 1
global server_account_password
server_account_password = "1314peipei521"

# Variables
username = 'Spring'
password = 'zhangrenyutj@gamil.com'
base_url = "http://appschallenge.juzz4.com"
login_url = "/api/login"
device_url = "/api/device/search"
snapshot_url = "/api/snapshot"

@route('/')
def index():
    return "Registration accepted"

@route('/sender')
def sender_get():
    response.headers['Content-Type'] = 'text/plain'
    response.out.write( "Get Not supported" )
    
@route('/sender', method="POST")
def sender_post():
    accountName = request.POST['accountName']
    text = request.POST['text']
    connection = sqlite3.connect('monitor.db')
    cur = connection.cursor()
    cur.execute("SELECT * FROM REGISTRATION WHERE accountName=?", accountName)
    items = cur.fetchone()
    if len(items) > 0:
        registration = items[0]
        registrationId = registration[1]
        sendMessage( accountName, registrationId, text )
    else:
        return "No registration for this user"

def sendMessage( accountName, registrationId, text ):
    global collapse_key
    global server_account_name
    authToken = getAuthToken()
    if authToken == "":
        return "Cannot authenticate " + server_account_name
    form_fields = {
                   "registration_id": registrationId,
                   "collapse_key": str(collapse_key),
                   "data.message": text
                   }
    form_data = urllib.urlencode(form_fields)
    
    headers = {
    		   'Content-Type': 'application/x-www-form-urlencoded',
               'Authorization': 'GoogleLogin auth='+authToken
              }
    
    conn = httplib.HTTPSConnection("android.apis.google.com")
    conn.request(method="POST", url="/c2dm/send", body=form_data, headers=headers)
    response = conn.getresponse()
    print response
    print response.status
    data = response.read()
    print data
    
    collapse_key = collapse_key + 1
    return data


def getAuthToken():
    global server_account_name
    global server_account_password
    form_fields = {
                   "accountType": "GOOGLE",
                   "Email": server_account_name,
                   "Passwd": server_account_password,
                   "service": "ac2dm",
                   "source": "Spring Project"
                   }
    form_data = urllib.urlencode(form_fields)
    
    link_open = urllib2.build_opener( urllib2.HTTPCookieProcessor() )
    urllib2.install_opener( link_open )
    
    result = link_open.open("https://www.google.com/accounts/ClientLogin",
                            form_data)
    data = result.read()
    print data
    if data is not None:
        lines = data.split('\n')
        authToken = ""
        for line in lines:
            if line.startswith( "Auth=" ):
                authToken = line[5:len(line)]
        return authToken
    return ""

@route('/token', method="POST")
def token_post():
    accountName = request.POST['accountName']
    registrationId = request.POST['registrationId']
    print registrationId, accountName
    updateOrInsertRegistration( accountName, registrationId )
    return "Registration accepted"

@route('/threadstop', method="POST")
def threadstop_post():
    accountName = request.POST['accountName']
    deviceid = request.POST['deviceid']
    channelid = request.POST['channelid']
    threadName = accountName + deviceid + channelid
    print threadName
    threadlist = threading.enumerate()
    threadcount = threading.activeCount()
    for i in range(1, threadcount):
	    if threadlist[i].getName() == threadName:
		   threadlist[i].exit()

@route('/monitor')
def monitor_get():
    return "Not supported yet"

@route('/monitor', method="POST")
def monitor_post():
    data = request.POST['data']
    content = eval(data)
    print content
    link_open = urllib2.build_opener( urllib2.HTTPCookieProcessor() )
    urllib2.install_opener( link_open )
    
    login_params = "data={\"username\":\"Spring\", \"password\":\"zhangrenyutj@gmail.com\", \"mechanism\":\"plain\"}"

    login_result = link_open.open( base_url + login_url, login_params )
    login_data = login_result.read()
    print login_data
    login_json = json.loads(login_data)
    login_result.close()
    
    #save sessionid for this connection
    sessionid = login_json["sessionid"]
    print sessionid
    
    # save deviceids
    boundbox = (
                content['boxes'][0]['left'],
                content['boxes'][0]['top'],
                content['boxes'][0]['right'],
                content['boxes'][0]['bottom']
                )
    print boundbox
    print type(content['deviceid'])
    print type(content['boxes'][0]['left'])
    updateOrInsertMonitor( content['deviceid'],
                           content['boxes'][0]['left'],
                           content['boxes'][0]['top'],
                           content['boxes'][0]['right'],
                           content['boxes'][0]['bottom']
                          )
    print getRegistrationId(content['accountName'])
    if getRegistrationId(content['accountName']) == "Fail":
        print 'Not registered yet'
        return 'Not Registered yet'
    else:
        fetchor = Detector( 3, sessionid, content['deviceid'], content['channelid'], boundbox, content['accountName'] )
        fetchor.setDaemon(True)
        fetchor.start()
        return 'OK'

class Detector(threading.Thread):
    def __init__(self, interval, sessionid, deviceid, channelid, boundbox, accountName):
        print "init begins"
        threading.Thread.__init__(self)
        self.interval = interval
        self.sessionid = sessionid
        self.deviceid = deviceid
        self.boundbox = boundbox
        self.channelid = channelid
        self.image = None
        self.flag = False
        self.accountName = accountName
        self.exiting = False
        self.setName(accountName + str(deviceid) + str(channelid))
        self.registrationId = getRegistrationId(accountName)
        print "thread %s init is done" %(self.getName())

    def run(self):
        print "in thread"
        link_open = urllib2.build_opener( urllib2.HTTPCookieProcessor() )
        urllib2.install_opener( link_open )
        snapshot_params = "data={\"sessionid\":\"" + self.sessionid + "\",\"deviceid\":\"" + self.deviceid + "\"}"
        snapshot_result = link_open.open( "http://appschallenge.juzz4.com/api/snapshot", snapshot_params )
        snapshot_data = snapshot_result.read()
        snapshot = Image.open(StringIO.StringIO(snapshot_data))
        print "The device is warming up, wait a second"
        time.sleep(10)
        
        while self.exiting == False:
            print "Thread %s is running" %(self.getName())
            snapshot_result = link_open.open( "http://appschallenge.juzz4.com/api/snapshot", snapshot_params )
            snapshot_data = snapshot_result.read()
            snapshot = Image.open(StringIO.StringIO(snapshot_data))
            
            if self.image is not None:
                print "Detecting is ready"
                result = objectRecognize( snapshot, self.image, self.boundbox )
                #result = True
                if result and not self.flag :
                    print "Push a message to client"
                    genId = saveSnapshot( snapshot_data )
                    sendMessage( 
                                self.accountName,
                                self.registrationId,
                                str(self.deviceid) + "," + str(self.channelid) + "," + str(genId)
                                 )
                    self.flag = True
                elif not result and self.flag:
                    self.image = snapshot
                    self.flag = False
                else:
                    self.image = snapshot
            else:
                self.image = snapshot
                self.image_backup = snapshot
            
            #snapshot.show()
            time.sleep(self.interval)
        snapshot_result.close()
        print "Thread %s terminated!" %(self.getName())

    def exit(self):
	    self.exiting = True

@route('/snapshot', method="POST")
def snapshot_post():
    id = request.POST["id"]
    result = str(sql.getSnapshot(id))
    return result

run(host='localhost', port=8080)
