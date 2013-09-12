package org.tju.security;
     
import java.util.ArrayList;
import java.util.HashMap;

import org.tju.config.MyConfig;
import org.tju.db.DB;
import org.tju.http.Http;
import org.tju.util.Util;
  

import android.app.Activity;   
import android.app.ActivityManager; 
import android.content.Intent;
import android.content.SharedPreferences;  
import android.graphics.Bitmap; 
import android.os.Bundle; 
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent; 
import android.view.View;   
import android.view.Window;
import android.view.WindowManager; 
import android.widget.AbsoluteLayout;
import android.widget.LinearLayout;
import android.widget.TextView; 

public class ShowSnapshotActivity extends Activity { 
	
	SharedPreferences settings = null;  
	ImView imView = null;
	DrawView drawView = null; 
	float dw=320, dh=480;
	LinearLayout layoutTop, layoutBottom;
	AbsoluteLayout al;
	Boolean isVisable = false;
	TextView homeTextView = null, publishTextView = null;
	TextView clearTextView = null, exitTextView = null;
	
	String accountName = null;
	String username = null;
	String password = null;
	String address = null;
	String deviceID = null;
	String channelID = null;
	  
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState){
    	
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); 
          
        HashMap<String,String> map = new HashMap<String, String>();
        settings = getSharedPreferences("settings", PreferenceActivity.MODE_PRIVATE); 
        accountName = settings.getString("accountName", null); 
        username = settings.getString("username", null); 
        password = settings.getString("password", null);
        address = settings.getString("address", null);
        deviceID = settings.getString("deviceid", null);
        channelID = settings.getString("channelid", null);
        map.put("username", username);
        map.put("address", address);
        map.put("deviceid", deviceID);
        map.put("channelid", channelID);
        
        setSampleRatio();
        
        setLayoutAndListener(map); 
        
        //get the image sample ratio   
	}
    
    @Override 
    protected void onDestroy(){  
    	super.onDestroy();  
    	drawView.finalize();
    	
    	//打开数据库
        DB db = new DB(ShowSnapshotActivity.this); 
        db.open(ShowSnapshotActivity.this);
    	if(MyConfig.sendDataList!=null && MyConfig.sendDataList.size()>0){
        	MyConfig.sendDataList.clear();
        	db.deleteAll(username, address, deviceID, channelID); 
        }
    	String status = (String) homeTextView.getText();
    	if(status.equals("start")){
    		db.updateStatus(username, address, deviceID, channelID, "start");
    	}else if(status.equals("stop")){
    		db.updateStatus(username, address, deviceID, channelID, "stop");
    	}else if(status.equals("^v^")){
    		db.updateStatus(username, address, deviceID, channelID, "^v^");
    	}
    	
    	db.close();
    } 
    
 private void setSampleRatio(){
    	
	 	Bitmap tmp = null;
	 	tmp = Http.getSnapshot(ShowSnapshotActivity.this); 
    	
    	if( tmp!=null){
    		System.out.println("\n\n getSampleRatio initDisplay called \n\n");
    	}   
    	Display currentDisplay = getWindowManager().getDefaultDisplay();
        dw = currentDisplay.getWidth(); 
        dh = currentDisplay.getHeight();
        try{  
        	int imw = tmp.getWidth();
        	int imh = tmp.getHeight();
        	int heightRatio = (int)Math.ceil(imh/dh);
       	 	int widthRatio = (int)Math.ceil(imw/dw); 
       	 	MyConfig.sampleRatio = Math.max(heightRatio, widthRatio);
       	 	  
       	 	MyConfig.viewWidth = imw/MyConfig.sampleRatio;
    	 	MyConfig.viewHeight = imh/MyConfig.sampleRatio;
       	 	Log.i("ratio", heightRatio+"	"+widthRatio+"	"+MyConfig.sampleRatio+"	"+MyConfig.viewWidth+"	"+ MyConfig.viewHeight);
       	 	
        }catch(Exception e){
        	 e.printStackTrace();
        }
         
    } 
       
    private void setLayoutAndListener(HashMap<String,String> map){
    	setContentView(R.layout.snapshot_layout);
    	al = (AbsoluteLayout) findViewById(R.id.al);
        imView = new ImView(this, null);
        drawView = new DrawView(this, null, map);
        MyConfig.viewLeft = (int) ((dw-MyConfig.viewWidth)/2);
        MyConfig.viewTop = (int) ((dh-MyConfig.viewHeight-MyConfig.MENU_HEIGHT)/2);
        System.out.println(MyConfig.viewWidth+"	"+MyConfig.viewHeight+"	"+MyConfig.viewLeft+"	"+MyConfig.viewTop);
        AbsoluteLayout.LayoutParams imLP = new AbsoluteLayout.LayoutParams(MyConfig.viewWidth, MyConfig.viewHeight, MyConfig.viewLeft, MyConfig.viewTop);
        al.addView(imView, imLP);
        al.addView(drawView, imLP);
         
    	layoutTop = (LinearLayout) findViewById(R.id.layoutTop);
        layoutBottom = (LinearLayout) findViewById(R.id.layoutBottom);
        homeTextView = (TextView) findViewById(R.id.homeTextView); 
        publishTextView = (TextView) findViewById(R.id.publishTextView);
        clearTextView = (TextView) findViewById(R.id.clearTextView);
        exitTextView = (TextView) findViewById(R.id.exitTextView);
        
        homeTextView.setOnClickListener(onClickListener);
        publishTextView.setOnClickListener(onClickListener);
        clearTextView.setOnClickListener(onClickListener);
        exitTextView.setOnClickListener(onClickListener);
        
        //设置start/stop按钮
        homeTextView.setText(MyConfig.startStopFlag);

    } 
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    	
	      String action = "up";
	      
	      if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT 
	    		  || keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_DOWN 
	    		  || keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER){
	    	  
	    	if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
	      		action = "left"; 
	      	}  
	      	if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) { 
	      		action = "right";
	      	} 
	      	if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {   
	      		action = "up";
	  	    }
	      	if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN) { 
	      		action = "down";
	      	}
	      	if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER) { 
	      		action = "in";
	      	}
	      	if(keyCode == KeyEvent.KEYCODE_ENTER) { 
	      		action = "out";
	      	}
	      	
	      	Http.controlCamera(this, action);
	      	
	     }else if(keyCode == KeyEvent.KEYCODE_MENU){
	    	 if(isVisable==false){
	    		 layoutTop.setVisibility(View.VISIBLE);
	    		 layoutBottom.setVisibility(View.VISIBLE);
	    		 isVisable = true;
	    	 }else{
	    		 layoutTop.setVisibility(View.INVISIBLE);
	    		 layoutBottom.setVisibility(View.INVISIBLE);
	    		 isVisable = false;
	    	 }
	    	 
	     }else if(keyCode == KeyEvent.KEYCODE_BACK){
	    	 Intent intent = new Intent(ShowSnapshotActivity.this, TreeViewActivity.class); 
			 intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
     		 startActivity(intent);
	     }
	    	
	    return super.onKeyDown(keyCode, event);
	} 
	  
	
  View.OnClickListener onClickListener = new View.OnClickListener() {
	public void onClick(View v) {
		
		if(v instanceof TextView){
			if(v.getId()==R.id.homeTextView){ 
				//
				String str = (String) homeTextView.getText(); 
				if( str!=null && (str.equals("stop")||str.equals("start")) ){
					//打开数据库
			        DB db = new DB(ShowSnapshotActivity.this); 
			        db.open(ShowSnapshotActivity.this);
					if( str.equals("stop") ){ 
						//stop the service 
						Util.showToast(ShowSnapshotActivity.this, "Monitor Service Stoped."); 
					//	ArrayList<Float> existData = db.getAllDrawingData(username, address, deviceID, channelID);
					//	if(existData!=null && existData.size()>0){
							homeTextView.setText("start");
					//		existData.clear();
					//	}else{
					//		homeTextView.setText("^v^");
					//	}
						 
						Http.stopThread(accountName, deviceID, channelID);
						
					}else if( str.equals("start") ){
						//	start the service 
						Util.showToast(ShowSnapshotActivity.this, "Monitor Service Started.");
						homeTextView.setText("stop");
						
						ArrayList<Integer> box = db.getBoundingBox(username, address, deviceID, channelID);
						String data = formData(box); 
						if(box!=null && box.size()>0){
							Http.sendDataToServer(data);
						} 
					} 
					db.close();
				}/*else{
					Util.showToast(ShowSnapshotActivity.this, "No Service Initialed Now.");
				}*/
				
			}else if(v.getId()==R.id.publishTextView){
				if(MyConfig.sendDataList!=null && MyConfig.sendDataList.size()>0){

					String data = formData(MyConfig.sendDataList);
					MyConfig.sendDataList.clear();
					Util.showToast(ShowSnapshotActivity.this, "Monitor Region Published..");
					Http.stopThread(accountName, deviceID, channelID);
					Http.sendDataToServer(data);  
					//设置start/stop按钮
			        homeTextView.setText("stop");
			        
				}else{
					Util.showToast(ShowSnapshotActivity.this, "No Newly Drawed Region, Please Draw Monitor Region");
				}
				
			}else if(v.getId()==R.id.clearTextView){
				drawView.clear();
				String str = (String) homeTextView.getText(); 
				if(str.equals("start")){
					homeTextView.setText("^v^");
				}else if(str.equals("stop")){
					homeTextView.setText("^v^");
					Http.stopThread(accountName, deviceID, channelID);
				}
				
			}else if(v.getId()==R.id.exitTextView){ 
				ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE); 
				am.killBackgroundProcesses(getPackageName());
				Intent intent = new Intent(ShowSnapshotActivity.this, LoginActivity.class);
				startActivity(intent);
			}
		}
		
	}
  }; 
  
  private String formData(ArrayList<Integer> sendDataList){
	  String data = "{ \"accountName\":\""+accountName+"\", \"username\":\""+username+"\", \"password\":\""+password+"\", \"address\":\""+address
		+"\", \"deviceid\":\""+deviceID+"\", \"channelid\":\""+channelID+"\",	" +
		"\"boxes\": [ " ;
	  for(int i=0; i<sendDataList.size(); i+=4){
		  data += "{ \"left\":"+sendDataList.get(i)*MyConfig.sampleRatio+
		  ", \"top\":"+sendDataList.get(i+1)*MyConfig.sampleRatio+
		  ", \"right\":"+sendDataList.get(i+2)*MyConfig.sampleRatio+
		  ", \"bottom\":"+sendDataList.get(i+3)*MyConfig.sampleRatio+" }, ";
		  
		  System.out.println("bounding box   "+sendDataList.get(i)+"  "+sendDataList.get(i+1)+"  "+sendDataList.get(i+2)+"  "+sendDataList.get(i+3)+"  ");
	  }
	  data = data.substring(0, data.length()-2);					
	  data += " ] }";
	  
	  return data;
  }
  
}  
 