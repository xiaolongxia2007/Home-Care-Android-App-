package org.tju.db;
 
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;  
import android.database.Cursor;  
import android.database.sqlite.SQLiteDatabase;  
import android.util.Log;  
  
public class DB{  
     private static final String TAG = "UserDB";  
     private static final String DataBaseName = "UserDB";  
     SQLiteDatabase db;  
     Context context;  
   
     public DB(Context context) {  
         this.open(context);  
     }  
     
     public void createTabel(String username) {  

    	 String sql = "";  
    	 String sql2 = "";
    	 String tableName = username+"status";
         try {  
             sql = "CREATE TABLE IF NOT EXISTS '"+username+   //tablename is username
             		"' (id INTEGER PRIMARY KEY autoincrement,"+
             		"owner TEXT,"+              //owner is address+deviceid+channelid
             		"startx FLOAT,"+
             		"starty FLOAT,"+
             		"endx FLOAT,"+
             		"endy FLOAT)";  
             this.db.execSQL(sql);   
             sql2 = "CREATE TABLE IF NOT EXISTS '"+tableName+"' "+   //tablename is username+status 
      				"(owner TEXT PRIMARY KEY,"+              //owner is address+deviceid+channelid
      				"flag TEXT)";
             this.db.execSQL(sql2);
             
         } catch (Exception e) {   
             e.printStackTrace();
         } finally {   
             Log.v(TAG, "Create Table "+username);  
         }  
     }  
   
     public boolean insert(String username, String address, String device, String channel, 
    		 				float startx, float starty, float endx, float endy){   
         String sql = "insert into "+username+" values(null, '" +address+device+channel+"', '"+startx+
         				"', '"+starty+"', '"+endx+"', '"+endy+"')";  
         try {  
             this.db.execSQL(sql);    
             return true;  
         } catch (Exception e) {  
     
             return false;  
         } finally {   
         }  
     }  
     
       
     private Cursor selectAll(String username, String address, String device, String channel) {  
    	 Log.i(TAG, "db selectAll called...");
         Cursor cur = db.query(username, new String[] { "startx","starty","endx", "endy"}, "owner='"+address+device+channel+"'",  
                 null, null, null, null);  
   
         return cur;  
     }  
     
     //获取该channel用户保存的所有drawing数据
     public ArrayList<Float> getAllDrawingData(String username, String address, String deviceID, String channelID){

     	ArrayList<Float> list = new ArrayList<Float>();
     	Cursor cur = selectAll(username, address, deviceID, channelID);  
 		cur.moveToFirst();  
 		while(!cur.isAfterLast()) {  
 			list.add(cur.getFloat(0));
 			list.add(cur.getFloat(1));
 			list.add(cur.getFloat(2));
 			list.add(cur.getFloat(3));
 		    cur.moveToNext();  
 		}
 		cur.close();
 		
 		return list;
     }
     
     public ArrayList<Integer> getBoundingBox(String username, String address, String deviceID, String channelID){
    	 ArrayList<Float> list = getAllDrawingData(username, address, deviceID, channelID);
    	 ArrayList<Integer> re = new ArrayList<Integer>();
    	 float minx=10000, miny=10000, maxx=0, maxy=0;
    	 boolean tag = false;
    	 for(int i=0; i<list.size(); i+=2){
    		 if(minx>list.get(i)){
    			 minx = list.get(i); tag=true;
    		 }else if(maxx<list.get(i)){
    			 maxx = list.get(i); tag=true;
    		 }
    		 if(miny>list.get(i+1)){
    			 miny = list.get(i+1); tag=true;
    		 }else if(maxy<list.get(i+1)){
    			 maxy = list.get(i+1); tag=true;
    		 }
    	 }
    	 if(tag==true){
    		 re.add((int) minx);
        	 re.add((int) miny);
        	 re.add((int) maxx);
        	 re.add((int) maxy);
    	 }
    	  
    	 return re;
     }
     
     
     public void deleteAll(String username, String address, String device, String channel){
    	Log.i(TAG, "db deleteAll called...");
    	try{
    		this.db.delete(username, "owner='"+address+device+channel+"'", null);
    	}catch(Exception e){ 
    		e.printStackTrace();
    	}
    }
    
    public String getStatus(String username, String address, String device, String channel){
    	Log.i(TAG, "db getStatus called...");
    	String status = "^v^";
        Cursor cur = db.query(username+"status", new String[] { "flag" }, "owner='"+address+device+channel+"'",  
                null, null, null, null);  
        cur.moveToFirst();
        if(cur.isAfterLast()){
        	addStatusItem(username, address, device, channel);
        }else{
        	status = cur.getString(0);
        }
        cur.close();
        return status;
    }
    
    public boolean addStatusItem(String username, String address, String device, String channel){
    	 String status = "^v^";
    	 String sql = "insert into "+username+"status"+" values('" +address+device+channel+"', '"+status+"')";  
    	 try {  
    		 this.db.execSQL(sql);    
    		 return true;  
    	 } catch (Exception e) {  
    		 return false;  
    	 } finally {   
    	 }  
    }
    
    public void updateStatus(String username, String address, String device, String channel, String status){
    	ContentValues contentValues = new ContentValues(); 
    	contentValues.put("flag", status);  
    	db.update(username+"status", contentValues, "owner=?", new String[]{address+device+channel}); 
    }
     
    public void open(Context context){  
        if(null == db || !this.db.isOpen()){  
            this.context = context;  
            this.db = context.openOrCreateDatabase(this.DataBaseName, context.MODE_PRIVATE, null); 
        }  
    }  
    
    public void close() {  
        db.close();  
    }  
   
 }  