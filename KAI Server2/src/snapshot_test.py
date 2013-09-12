import urllib2
import urllib
import Image
import StringIO

link_open = urllib2.build_opener( urllib2.HTTPCookieProcessor() )
urllib2.install_opener( link_open )

params = urllib.urlencode({'id':4})

result = link_open.open( "http://127.0.0.1:8080/snapshot", params )
data = result.read()
snapshot = Image.open(StringIO.StringIO(data))
snapshot.show()
print data

