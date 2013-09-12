from bottle import run, route, request, response

@route('/')
def index():
    haha()
    print 'OK'

def haha():
    print "HAHAHAH"
    
run(host='localhost', port=8080)