package org.tju.config;

import java.util.ArrayList;

public class MyConfig {

	public final static int MENU_HEIGHT = 80;
	public static final int INTERVAL_TIME = 4500;
//	public static final int UNSHOW_INTERVAL_TIME = 500;
//	public static final int SHOW_INTERVAL_TIME = 1200;
	public static int sampleRatio = 2;
	public static int viewWidth = 320;
	public static int viewHeight = 240;
	public static int viewLeft = 0;
	public static int viewTop = 0;
	public static ArrayList<Integer> sendDataList = null;
	
	public static String startStopFlag = "^v^";
	
	public static String serverAddress = "http://10.0.2.2:8080/monitor";
    public static final String APP_BASE_URI = "http://10.0.2.2:8080";
 
    public static final String C2DM_SENDER = "liuhongbin2007@gmail.com";
    
//    public static final String ALARM_IMAGE_FILENAME = "/sdcard/alarmImage.jpg";

}

