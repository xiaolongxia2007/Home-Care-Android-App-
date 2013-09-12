import sqlite3 as sql
import Image
import StringIO

con = sql.connect("monitor.db")
cur = con.cursor()

#cur.execute("INSERT INTO Registration values(?, ?)", ('zhangrenyuggg#gmail.com', 'XXXXX-XXX'))
#con.commit()
accountName = "liuhongbin2007@gmail.com"
cur.execute('select * from snapshot ')
items = cur.fetchall()
snap_buffer = str(items[len(items) - 4][1])
#print snap_buffer
snapshot = Image.open(StringIO.StringIO(snap_buffer))
snapshot.show()
print len(items)
cur.close()
#if len(items) > 0:
#    print items
#else:
#    print "nothing"
#
