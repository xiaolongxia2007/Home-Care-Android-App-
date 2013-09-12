'''
Created on 2011-1-18

@author: Administrator
'''
import sqlite3 as sql
import datetime
import StringIO

def createMonitor(deviceid, left, top, right, bottom):
    print type(deviceid)
    print type(left)
    #create_sql = "INSERT INTO Monitor(deviceid, left, top, right, bottom, datetime, status) values(" + deviceid + "," + str(left) + "," + str(top) + "," + str(right) + "," + str(bottom) + "," + str(datetime.datetime.now()) + ",1)"
    #create_sql = "INSERT INTO Monitor(deviceid, left, top, right, bottom, datetime, status) values('44', 100, 100, 150, 150," + str(datetime.datetime.now()) +",1)"
    #create_sql = "INSERT INTO Monitor(deviceid, left, top, right, bottom, timestamp, status) values(?, ?, ?, ?, ?, 1)"
    #print create_sql    
    
    connection = sql.connect("monitor.db")
    connection.execute("INSERT INTO Monitor(deviceid, left, top, right, bottom, datetime, status) values(?,?,?,?,?,?,1)",(deviceid, left, top, right, bottom, datetime.datetime.now()))
    connection.commit()
    connection.close()
    
def updateOrInsertMonitor(deviceid, left, top, right, bottom):
    con = sql.connect("monitor.db")
    cur = con.cursor()
    print type(deviceid)
    print deviceid, left, top, right, bottom
    cur.execute("Select * From Monitor WHERE deviceid=?", (deviceid,))
    print 'executed'
    items = cur.fetchall()
    print items
    if len(items) > 0:
        print 'coming hehe'
        cur.execute("Update Monitor set left=? and top=? and right=? and bottom=? where deviceid=?", (left, top, right, bottom, deviceid))
        print 'no end'
        con.commit()
    else:
        print 'coming haha'
        cur.execute("INSERT INTO Monitor(deviceid, left, top, right, bottom, datetime, status) values(?,?,?,?,?,?,1)",(deviceid, left, top, right, bottom, datetime.datetime.now()))
        print 'no end2'
        con.commit()
    
    con.close()

def updateOrInsertRegistration( accountName, registrationId ):
    con = sql.connect("monitor.db")
    cur = con.cursor()
    print 'INSERTION'
    cur.execute("Select * From Registration Where accountName=?", (accountName,))
    items = cur.fetchall()
    print items
    if len(items) > 0:
    	print items[0]
        registrationId = items[0][1]
        print registrationId
        if registrationId == "":  # unregistration
            cur.execute("Delete From Registration where accountName=?", (accountName,))
            con.commit()
        else:
            #registration.registrationId = registrationId
            print 'Update operation'
            cur.execute("Update Registration set registrationId=? where accountName=?", (registrationId, accountName))
            con.commit()
            print 'operation is done'
    else:
        cur.execute( "INSERT INTO Registration values(?,?)", (accountName, registrationId) )
        con.commit()
    print 'INSERTION DONE'
    con.close()

def getRegistrationId( accountName ):
    con = sql.connect("monitor.db")
    cur = con.cursor()
    print accountName
    cur.execute("Select * From Registration Where accountName=?", (accountName,))
    print 'OYeah'
    items = cur.fetchall()
    if len(items) > 0:
        registration = items[0]
        con.close()
        return registration[1]
    else:
        con.close()
        return "Fail"
    

def saveSnapshot( snapshot ):
    con = sql.connect('monitor.db')
    cur = con.cursor()
    cur.execute("SELECT * From Snapshot")
    items = cur.fetchall()
    id = len(items) + 1
    cur.execute("INSERT INTO Snapshot values(?,?)", (id, sql.Binary(snapshot)) )
    con.commit()
    con.close()
    return id

def getSnapshot( id ):
    con = sql.connect('monitor.db')
    cur = con.cursor()
    print id
    cur.execute("SELECT * FROM snapshot where id=?", (id,))
    items = cur.fetchall()
    con.close()
    if len(items) > 0:
    	print "hehe"
        return items[0][1]
    else:
        return ""
