%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
README FOR Security Mobile Security System 
张仁宇 (zhangrenyutj@gmail.com)
刘洪滨 (liuhongbin2007@gmail.com)
01/23/2011
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


LICENSE

Copyright (C) 2011 张仁宇&刘洪滨, Tian Jin University

This software is available for only non-commercial use.  See the attached
license in LICENSE.txt.  


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

Documents SPEC:

SMSS presentation.ppt:		the powerpoint presentation.
SMSS video.mp4:				the video presentation.
org.tju.security-2.apk:		the SMSS Client side installer.
source:						package containing the source code of SMSS Client and SMSS Server.
LINCENSE.txt:				Spring lincense spec.
README.txt:					infomation about this package and SMSS.
 

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

How to RUN:

to run the client,
First, 	ensure your android simulator or real device has equiped with Android2.2+Google Apis.
Second, ensure your simulator or real device has registrated a Google Account, if not yet,
		please go to settings/Accounts & sync/Add account/ to create one.
Third,  in the source code, we used the localhost as default SMSS server address, if you distribute the server
		on another one, please do change the address configutation. In /SMSS/org.tju.config/MyConfig.java, 
		change serverAddress and APP_BASE_URI accordingly. And then recompile it.(We will improve this point later).
Forth,	always ensure your client connect with intenet.
		 

to run the server,
run the following command 
$	cd KAI Server2/src/
$	python ./server.py
 

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

NOTES:

  SMSS Client has only been tested on simulator with Android2.2+Google Apis,
  running on Ubuntu 10.10. We donot guarantee it can run on other plartform
  and operating system.
  
  SMSS Server has not yet distributed on cloud, due to recent free cloud
  infrastructure and plartform, as GAE, with restricted image processing functions.
  If you want to user the security services, you have to run it yourself, 
  please refer to 'How to RUN' above.


(C) 张仁宇&刘洪滨, Tian Jin University, 2011
 
