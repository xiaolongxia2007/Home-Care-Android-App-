'''
Created on 2011-1-18

@author: Administrator
'''
from google.appengine.api import urlfetch
from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app
from google.appengine.ext import db
from model import Registration
from Detector import Detector
import threading
import time
import urllib2
import config
import urllib
import json
import logging

class Sender(webapp.RequestHandler):
    def get(self):
        self.response.headers['Content-Type'] = 'text/plain'
        self.response.set_status( 500,"Server error" )
        self.response.out.write( "GET not supported" )

    def post(self):
        accountName = self.request.get( "accountName" )
        text = self.request.get( "text" )
        itemquery = db.GqlQuery("SELECT * FROM Registration WHERE accountName=:1",
                                accountName )
        items = itemquery.fetch(1)
        self.response.headers['Content-Type'] = 'text/html'
        self.response.set_status( 200,"OK" )
        self.response.out.write('<html>')
        self.response.out.write('<head>')
        self.response.out.write('<title>')
        self.response.out.write('Push')
        self.response.out.write('</title>')
        self.response.out.write('</head>')
        self.response.out.write('<body>')
        if len(items)>0:
            registration=items[0]
            registrationId=registration.registrationId
            status = self.sendMessage( accountName, registrationId, text )
            self.response.out.write('<p>Message sent, status: '+status+'</p>' )
        else:
            self.response.out.write( "<p>No registration for '"+accountName+"'</p>" )
        self.response.out.write('<p><a href=\"/\">Back to start page</a></p>' )
        self.response.out.write('</body>')
        self.response.out.write('</htnl>')
                    
    def sendMessage( self, accountName, registrationId, text ):
        global collapse_key
        global server_account_name
        authToken = self.getAuthToken()
        if authToken=="":
            return "Cannot authenticate "+server_account_name 
        form_fields = {
            "registration_id": registrationId,
            "collapse_key": str(collapse_key),
            "data.message": text
        }
        logging.info( "authToken: "+authToken )
        form_data = urllib.urlencode(form_fields)
        result = urlfetch.fetch(url="https://android.apis.google.com/c2dm/send",
                        payload=form_data,
                        method=urlfetch.POST,
                        headers={'Content-Type': 'application/x-www-form-urlencoded',
                                 'Authorization': 'GoogleLogin auth='+authToken
                                })
        collapse_key=collapse_key+1
        return result.content

    def getAuthToken(self):
        global server_account_name
        global server_account_password
        form_fields = {
            "accountType": "GOOGLE",
            "Email": server_account_name,
            "Passwd": server_account_password,
            "service": "ac2dm",
            "source": "mylifewithandroid-push-2.0"
        }
        form_data = urllib.urlencode(form_fields)
        result = urlfetch.fetch(url="https://www.google.com/accounts/ClientLogin",
                        payload=form_data,
                        method=urlfetch.POST,
                        headers={'Content-Type': 'application/x-www-form-urlencoded'
                                })
        if result.status_code==200:
            logging.info( "Auth response: "+result.content )
            lines=result.content.split('\n')
            authToken=""
            for line in lines:
                if line.startswith( "Auth=" ):
                    authToken=line[5:len(line)]
            return authToken
        logging.error( "error code: "+str(result.status_code)+"; error message: "+result.content )
        return ""

class TokenService(webapp.RequestHandler):
    def get(self):
        self.response.headers['Content-Type'] = 'text/plain'
        self.response.set_status( 500,"Server error" )
        self.response.out.write( "GET not supported" )

    def post(self):
        accountName = self.request.get( "accountName" )
        registrationId = self.request.get( "registrationId" )
        logging.info( "TokenService, accountName: "+accountName+ \
                        "; registrationId: "+registrationId )
        self.updateOrInsertRegistration( accountName, registrationId )
        self.response.headers['Content-Type'] = 'text/plain'
        self.response.set_status( 200,"Registration accepted" )
        self.response.out.write( "Registration accepted" )

    def updateOrInsertRegistration( self, accountName, registrationId ):
        itemquery = db.GqlQuery("SELECT * FROM Registration WHERE accountName=:1",
                                accountName )
        items = itemquery.fetch(1)
        if len(items)>0:
            registration=items[0]
            if registrationId=="":  # unregistration
                registration.delete()
            else:
                registration.registrationId=registrationId
                registration.put()
        else:
            registration=Registration()
            registration.accountName=accountName
            registration.registrationId=registrationId
            registration.put()

class Monitoring(webapp.RequestHandler):
    def get(self):
        link_open = urllib2.build_opener( urllib2.HTTPCookieProcessor() )
        urllib2.install_opener( link_open )
        
        login_params = "data={\"username\":\"Spring\", \"password\":\"zhangrenyutj@gmail.com\", \"mechanism\":\"plain\"}"
        login_result = link_open.open( config.base_url + config.login_url, login_params )
        login_data = login_result.read()
        logging.info("Receive data " + login_data)
        print login_data
        login_json = json.loads(login_data)
        login_result.close()
        self.response.headers['Content-Type'] = 'text/plain'
        #save sessionid for this connection
        sessionid = login_json["sessionid"]
        
        fetchor = Detector( 5, sessionid, login_json['deviceid'] )
        fetchor.start()
        fetchor.run()
        fetchor.join()
    
    def post(self):
        
        return "ok"

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

application = webapp.WSGIApplication(
                                     [('/', Monitoring),
                                      ('/sender',Sender),
                                      ('/token',TokenService)],
                                     debug=True)

def main():
    global collapse_key
    collapse_key=1
    global server_account_name
    server_account_name="gaborpaller@gmail.com"
    global server_account_password
    server_account_password="xxxxxxxxxxx"
    run_wsgi_app(application)

if __name__ == "__main__":
    main()

