'''
Created on 2011-1-18

@author: Administrator
'''
import sqlite3 as sql

print 'haha'
connection = sql.connect('monitor.db')
create_sql = "CREATE TABLE if not exists Monitor(id INTEGER PRIMARY KEY, deviceid char(100) NOT NULL, left INTEGER NOT NULL, top INTEGER NOT NULL, right INTEGER NOT NULL, bottom INTEGER NOT NULL, datetime timestamp NOT NULL, status bool NOT NULL)"
create_sql3 = "CREATE TABLE IF not exists Snapshot(id int primary KEY, image blob not null)"
create_sql2 = "CREATE TABLE If not exists Registration(accountName char(100) PRIMARY KEY, RegistrationId char(100) Not NULL)"
connection.execute(create_sql)
connection.execute(create_sql2)
connection.execute(create_sql3)
connection.commit()
connection.close()
