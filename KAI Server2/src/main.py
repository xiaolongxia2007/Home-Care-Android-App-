'''
Created on 2011-1-17

@author: Administrator
'''
import urllib2
import Image
import StringIO
import threading
import json
import time
from Detector import Detector
from objectRecognize import objectRecognize


class myTimer(threading.Thread):
    def __init__(self, no, interval):
        threading.Thread.__init__(self)
        self.no = no
        self.interval = interval

    def run(self):
        while True:
            print 'Thread object(%d), Time:%s'%(self.no, time.ctime())
            time.sleep(self.interval)

# Variables
username = 'Spring'
password = 'zhangrenyutj@gamil.com'
base_url = "http://appschallenge.juzz4.com"
login_url = "/api/login"
device_url = "/api/device/search"
snapshot_url = "/api/snapshot"

data = {"username":"Spring", "password":"zhangrenyutj@gmail.com", "address":"Default", "deviceid":"57", "channelid":"0", "boxes": { "left":155, "top":95, "right":208, "bottom":126 }}
print data

content = data
print content['username']
print content['password']
print content['boxes']['top']

print type(content)
link_open = urllib2.build_opener( urllib2.HTTPCookieProcessor() )
urllib2.install_opener( link_open )

try:
    login_params = "data={\"username\":\"Spring\", \"password\":\"zhangrenyutj@gmail.com\", \"mechanism\":\"plain\"}"

    login_result = link_open.open( base_url + login_url, login_params )
    login_data = login_result.read()
    print login_data
    login_json = json.loads(login_data)
    login_result.close()

    #save sessionid for this connection
    sessionid = login_json["sessionid"]
    print sessionid

    '''
    # get devices
    device_params = "data={\"sessionid\":\"" + login_json["sessionid"] + "\"}"
    device_result = link_open.open( base_url + device_url, device_params );
    device_data = device_result.read()
    device_result.close()
    device_json = json.loads(device_data)
    '''

    # save deviceids
    boundbox = (100, 100, 150, 150)
    fetchor = Detector( 5, sessionid, content['deviceid'], boundbox )
    fetchor.start()
    fetchor.join()
    #fetchor.run()
    #threadone = myTimer(1,1)
    #threadtow = myTimer(2,3)
    #threadone.start()
    #threadtow.start()

    print 'OK'
finally:
    print 'Finished'
