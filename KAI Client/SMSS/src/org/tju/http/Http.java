package org.tju.http;

import java.io.BufferedInputStream;
import java.io.BufferedReader; 
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList; 
import java.util.List; 

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject; 
import org.tju.config.MyConfig;
import org.tju.node.Node;
import org.tju.security.R;  

import android.content.Context; 
import android.content.SharedPreferences; 
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceActivity;
import android.util.Log;

public class Http {
	
	static Bitmap bmp=null;
	
	public static String checkUser(Context context, String username, String password){
		
		String sessionID = getSessionID(context, username, password);
		
		return sessionID;
	}
	
	private static String getSessionID(Context context, String username, String password) {
    	
    	HttpClient httpClient = new DefaultHttpClient();
    	HttpPost httpPost = null;
    	String sessionID = null;
    	
        try {
        	String url = context.getResources().getString(R.string.login_url); 
            httpPost = new HttpPost(url); 
            
            System.out.println("\n\n\n url username password "+url+username+password+"\n\n\n");
            
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
            String data = "{\"username\": \""+username+"\", \"password\": \""+password+"\", \"mechanism\": \"plain\"}"; 
            nameValuePair.add(new BasicNameValuePair("data", data));
 
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        }catch(Exception e){
        	e.printStackTrace();
        }
    	
        int res = 0;
		try {
			res = httpClient.execute(httpPost).getStatusLine().getStatusCode();
		} catch (ClientProtocolException e) { 
			e.printStackTrace();
		} catch (IOException e) { 
			e.printStackTrace();
		}
        if (res == 200) { 
            HttpResponse httpResponse = null;
			try {
				httpResponse = httpClient.execute(httpPost);
			} catch (ClientProtocolException e) { 
				e.printStackTrace();
			} catch (IOException e) { 
				e.printStackTrace();
			}
            StringBuilder builder = new StringBuilder();
            BufferedReader bufferedReader2 = null;
			try {
				bufferedReader2 = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
			} catch (IllegalStateException e) { 
				e.printStackTrace();
			} catch (IOException e) { 
				e.printStackTrace();
			} 
            try {
				for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine()) {
				    builder.append(s);
				}
			} catch (IOException e) { 
				e.printStackTrace();
			}
//            Log.i("cat", "\n\n>>>>>>" + builder.toString()); 
            JSONObject jsonObject = null;
			try {
				jsonObject = new JSONObject(builder.toString());
			} catch (JSONException e) {  
				e.printStackTrace();
			} 
            try {
				sessionID = jsonObject.getString("sessionid");
			} catch (JSONException e) { 
				e.printStackTrace();
			}
            Log.i("cat", sessionID);
        } 
        
        httpClient.getConnectionManager().shutdown();
        return sessionID;
    }
	
	public static ArrayList<Node> getDevices(Context context){  
		
    	HttpClient httpClient = new DefaultHttpClient();
    	HttpPost httpPost = null;  
    	SharedPreferences settings = null;   
    	String sessionID = null;
    	ArrayList<Node> deviceList= new ArrayList<Node>();
    	 
		settings = context.getSharedPreferences("settings", PreferenceActivity.MODE_PRIVATE);  
	    sessionID = settings.getString("sessionid", null); 
    	
        try {
        	String url = context.getResources().getString(R.string.search_url); 
            httpPost = new HttpPost(url); 
            
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
            String data = "{\"sessionid\":	\"" +sessionID+ "\"}"; 
            nameValuePair.add(new BasicNameValuePair("data", data));
 
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        }catch(Exception e){
        	e.printStackTrace();
        }
        
        int res = 0;
		try {
			res = httpClient.execute(httpPost).getStatusLine().getStatusCode();
		} catch (ClientProtocolException e) { 
			e.printStackTrace();
		} catch (IOException e) { 
			e.printStackTrace();
		}
        if (res == 200) {
            /*
             * 当返回码为200时，做处理
             * 得到服务器端返回json数据，并做处理
             * */
            HttpResponse httpResponse = null;
			try {
				httpResponse = httpClient.execute(httpPost);
			} catch (ClientProtocolException e) { 
				e.printStackTrace();
			} catch (IOException e) { 
				e.printStackTrace();
			}
            StringBuilder builder = new StringBuilder();
            BufferedReader bufferedReader2 = null;
			try {
				bufferedReader2 = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
			} catch (IllegalStateException e) { 
				e.printStackTrace();
			} catch (IOException e) { 
				e.printStackTrace();
			} 
            try {
				for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine()) {
				    builder.append(s);
				}
			} catch (IOException e) { 
				e.printStackTrace();
			}
//            Log.i("cat", "\n\n>>>>>>" + builder.toString());   
            JSONObject jsonObject = null;
			try {
				jsonObject = new JSONObject(builder.toString());
			} catch (JSONException e) { 
				e.printStackTrace();
			}
            JSONArray jsonArray = null;
			try {
				jsonArray = jsonObject.getJSONArray("devices");
			} catch (JSONException e) { 
				e.printStackTrace();
			}
			boolean tag = false;
            for(int i=0; i<jsonArray.length(); i++){
            	//deviceIDList.add( ((JSONObject)jsonArray.opt(i)).getString("deviceid") );
            	Node node = null;
            	try {  
            		String deviceid = ((JSONObject)jsonArray.opt(i)).getString("deviceid");
            		String devicename = ((JSONObject)jsonArray.opt(i)).getString("devicename");
            		String channel = ((JSONObject)jsonArray.opt(i)).getJSONObject("customfields").getString("channel");
            		String address = ((JSONObject)jsonArray.opt(i)).getJSONObject("customfields").getString("address");
            		if(address==null || address.length()==0){
            			address = "Default"; 
            		}
            		node = new Node(address);
            		if(!address.equals("Default")){
            			deviceList.add(node);
            		}else if(address.equals("Default") && tag==false){
            			deviceList.add(node);
            			tag = true;
            		} 
            		node = new Node(address, deviceid, devicename);
            		deviceList.add(node);
            		node = new Node(address, deviceid, devicename, channel);
            		deviceList.add(node);
				} catch (JSONException e) { 
					e.printStackTrace();
				}  
            }
             
        } 
    	
        httpClient.getConnectionManager().shutdown();
        
        return deviceList;
    }
	
	public static Bitmap getSnapshot(Context context){
		 
		if(bmp!=null && !bmp.isRecycled()){
		    bmp.recycle();  
		    bmp = null;
		}
	//	System.gc();
		
		SharedPreferences settings = null;
		String sessionID = null;
		String deviceID = null;
		
		settings = context.getSharedPreferences("settings", PreferenceActivity.MODE_PRIVATE);  
	    sessionID = settings.getString("sessionid", null);
	    deviceID = settings.getString("deviceid", null);
	          
    	HttpClient httpClient = new DefaultHttpClient();
    	HttpPost httpPost = null;     
    	
        try {
        	String url = context.getResources().getString(R.string.getSnapshot_url); 
            httpPost = new HttpPost(url); 
            
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
            String data = "{\"deviceid\": " +deviceID+", \"sessionid\":	\"" +sessionID+ "\"}"; 
            nameValuePair.add(new BasicNameValuePair("data", data));
 
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        }catch(Exception e){
        	e.printStackTrace();
        }
        
        int res = 0;
		try {
			res = httpClient.execute(httpPost).getStatusLine().getStatusCode();
		} catch (ClientProtocolException e1) { 
			e1.printStackTrace();
		} catch (IOException e1) { 
			e1.printStackTrace();
		} 
        if (res == 200) { 
            HttpResponse httpResponse = null;
			try {
				httpResponse = httpClient.execute(httpPost); 
			} catch (ClientProtocolException e1) {  
				e1.printStackTrace();
			} catch (IOException e1) { 
				e1.printStackTrace();
			} 
             
			try {
				InputStream is = httpResponse.getEntity().getContent(); 
				BufferedInputStream bis = new BufferedInputStream(is);
				bmp = BitmapFactory.decodeStream(bis); 
				is.close();
				bis.close();
			}catch(IOException e){
				e.printStackTrace();
			}
			
        }else{
        	Log.e("error:	", "connecting failed	"+res);
        }
        
        httpClient.getConnectionManager().shutdown(); 
        
        return bmp;
    }
	
	
	public static void controlCamera(Context context, String action){
		
		HttpClient httpClient = new DefaultHttpClient();
    	HttpPost httpPost = null; 
    	SharedPreferences settings = null;
    	String sessionID = null;
		String deviceID = null;
		String channelID = null;
		
		settings = context.getSharedPreferences("settings", PreferenceActivity.MODE_PRIVATE);  
	    sessionID = settings.getString("sessionid", null);
	    deviceID = settings.getString("deviceid", null);
	    channelID = settings.getString("channelid", null);
	    
        try {
        	String url = context.getResources().getString(R.string.cameraControl_url); 
            httpPost = new HttpPost(url); 
            
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
            //channelID = "1";
            
            String data = "{\"deviceid\": " +deviceID+", \"sessionid\": \"" +sessionID+"\", \"channelid\": " +channelID+", \"action\": \"" +action+ "\"}"; 
//            System.out.println( data );
            nameValuePair.add(new BasicNameValuePair("data", data));
 
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        }catch(Exception e){
        	e.printStackTrace();
        }
    	
        int res = 0;
		try {
			res = httpClient.execute(httpPost).getStatusLine().getStatusCode();
		} catch (ClientProtocolException e) { 
			e.printStackTrace();
		} catch (IOException e) { 
			e.printStackTrace();
		}
        if (res == 200) { 
            Log.i("control", "	controler works well....	");            
        } 
      	  
	} 
	
	public static void sendDataToServer(String data){
		
		HttpClient httpClient = new DefaultHttpClient();
    	HttpPost httpPost = null; 
    	
        try {
        	String serverAddress = MyConfig.APP_BASE_URI+"/monitor";
            httpPost = new HttpPost(serverAddress); 
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
            nameValuePair.add(new BasicNameValuePair("data", data));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        }catch(Exception e){
        	e.printStackTrace();
        }
    	
        int res = 0;
		try {
			res = httpClient.execute(httpPost).getStatusLine().getStatusCode();
		} catch (ClientProtocolException e) { 
			e.printStackTrace();
		} catch (IOException e) { 
			e.printStackTrace();
		}
        if (res == 200) { 
			Log.i("sendToServer", "send data success!");
        } 
        
        httpClient.getConnectionManager().shutdown();
	}
    
	
	public static void startstopService(String data){
		
		HttpClient httpClient = new DefaultHttpClient();
    	HttpPost httpPost = null; 
    	
        try {
        	String startstopAddress = MyConfig.APP_BASE_URI+"/threadstop"; 
            httpPost = new HttpPost(startstopAddress); 
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
            nameValuePair.add(new BasicNameValuePair("data", data));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        }catch(Exception e){
        	e.printStackTrace();
        }
    	
        int res = 0;
		try {
			res = httpClient.execute(httpPost).getStatusLine().getStatusCode();
		} catch (ClientProtocolException e) { 
			e.printStackTrace();
		} catch (IOException e) { 
			e.printStackTrace();
		}
        if (res == 200) { 
			Log.i("sendToServer", "send start/stop success!");
        } 
        
        httpClient.getConnectionManager().shutdown();
	}
	 
	
	public static void stopThread(String accountName, String deviceid, String channelid){
    	
		String stopAddress = MyConfig.APP_BASE_URI+"/threadstop";
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost post = new HttpPost( stopAddress );
		
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
		nameValuePairs.add(new BasicNameValuePair("accountName", accountName));
		nameValuePairs.add(new BasicNameValuePair("deviceid", deviceid));
		nameValuePairs.add(new BasicNameValuePair("channelid", channelid));
		try {
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
		} 
		
		int res = 0;
		try {
			res = httpClient.execute(post).getStatusLine().getStatusCode();
		} catch (ClientProtocolException e) { 
			e.printStackTrace();
		} catch (IOException e) { 
			e.printStackTrace();
		}
        if (res == 200) { 
			Log.i("sendToServer", "send stop success!");
        } 
        
        httpClient.getConnectionManager().shutdown();
    }

}
 