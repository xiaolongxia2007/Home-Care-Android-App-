'''
Created on 2011-1-18

@author: Administrator
'''
import threading
import urllib2
import Image
import time
import StringIO
from objectRecognize import objectRecognize

class Detector(threading.Thread):
    def __init__(self, interval, sessionid, deviceid, boundbox):
        print "init begins"
        threading.Thread.__init__(self)
        self.interval = interval
        self.sessionid = sessionid
        self.deviceid = deviceid
        self.boundbox = boundbox
        self.image = None
        self.flag = False
        print "init is done"

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
        
        while True:
            print "Thread is running"
            snapshot_result = link_open.open( "http://appschallenge.juzz4.com/api/snapshot", snapshot_params )
            snapshot_data = snapshot_result.read()
            snapshot = Image.open(StringIO.StringIO(snapshot_data))
            
            if self.image is not None:
                print "Detecting is ready"
                result = objectRecognize( snapshot, self.image, self.boundbox )
                if result and not self.flag :
                    print "Push a message to client"
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
            