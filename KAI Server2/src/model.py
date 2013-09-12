'''
    Definition of model
'''
from google.appengine.ext import db

class Registration(db.Model):
    accountName = db.StringProperty()
    registrationId = db.StringProperty()


class Monitor(db.Model):
    m_id = db.IntegerProperty()
    deviceid = db.StringProperty();
    left = db.IntegerProperty()
    top = db.IntegerProperty()
    right = db.IntegerProperty()
    bottom = db.IntegerProperty()
    datetime = db.DateTimeProperty()