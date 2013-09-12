package org.tju.security;


 import java.util.ArrayList; 
import java.util.HashMap;
 
import org.tju.db.DB;
import org.tju.config.MyConfig; 

import android.content.Context;  
import android.database.Cursor;
import android.graphics.Bitmap; 
import android.graphics.Canvas;   
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;  
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView; 
   
 public class DrawView extends ImageView {  
    
	Bitmap resizedBmp, alteredBitmap, bmp;
	Canvas canvas;
	Paint paint;
	float scaleWidth, scaleHeight;
	Matrix scaleMatrix;
	int upx, upy, downx, downy; 
	int minx=1000, miny=1000, maxx=0, maxy=0;
    String username = null, address=null, deviceID=null, channelID=null; 
    DB db = null;

    

 	public DrawView(Context context) { 
 	    super(context); 
 	} 
    public DrawView(Context context, AttributeSet attrs, int defStyle) { 
 	    super(context, attrs, defStyle); 
 	} 
    
 	public DrawView(Context context, AttributeSet attrs, HashMap<String, String> map){ 
 	    super(context, attrs); 
 	    
 	    this.setKeepScreenOn(true);
		setFocusable(false);
		setFocusableInTouchMode(true);
		
 	    //设置画布
 		bmp = ((BitmapDrawable)(this.getResources().getDrawable(R.drawable.canvas))).getBitmap();
 		/* 计算出缩小的比例 */ 
        scaleWidth=(float)bmp.getWidth()/MyConfig.viewWidth; 
        scaleHeight=(float)bmp.getHeight()/MyConfig.viewHeight; 
        /* 产生reSize后的Bitmap对象 */
        scaleMatrix = new Matrix();
        scaleMatrix.postScale(scaleWidth, scaleHeight);
        resizedBmp = Bitmap.createBitmap(bmp,0,0,bmp.getWidth(),bmp.getHeight(),scaleMatrix,true); 
 		alteredBitmap = Bitmap.createBitmap(MyConfig.viewWidth, MyConfig.viewHeight, Config.ARGB_8888);
 		canvas = new Canvas(alteredBitmap);
 		paint = new Paint();
 		paint.setAntiAlias(true);
 		paint.setColor(Color.RED);
 		paint.setStrokeWidth(2);
 		Matrix matrix = new Matrix();
 		canvas.drawBitmap(resizedBmp, matrix, paint);
 		this.setImageBitmap(alteredBitmap);
 		
 		//获取数据库连接参数
 		username = map.get("username"); 
        address = map.get("address");
        deviceID = map.get("deviceid");
        channelID = map.get("channelid"); 
        //创建数据库
        db = new DB(this.getContext()); 
        db.open(this.getContext());
        db.createTabel(username);
        
        MyConfig.sendDataList = new ArrayList<Integer>();
        
        //draw the saved data
        ArrayList<Float> list = db.getAllDrawingData(username, address, deviceID, channelID); 
        if(list!=null && list.size()>0){
        	drawSavedData(list); 
        }
        String status = db.getStatus(username, address, deviceID, channelID);
        setStartStopFlag(status);
        
 	} 
 	 
 	@Override 
    protected void finalize(){
      try {
    	  super.finalize();
      } catch (Throwable e) {
		e.printStackTrace();
      }
      db.close();
      
      if(alteredBitmap!=null && !alteredBitmap.isRecycled()){
    	  alteredBitmap.recycle();  
    	  alteredBitmap = null;
	  }
      if(resizedBmp!=null && !resizedBmp.isRecycled()){
    	  resizedBmp.recycle();  
    	  resizedBmp = null;
	  }
      if(bmp!=null && !bmp.isRecycled()){
    	  bmp.recycle();  
    	  bmp = null;
	  }
    }
 	
 	
 	@Override 
 	public void draw(Canvas canvas){ 
 	    super.draw(canvas);    
 	}
 	
 	@Override 
 	public boolean onTouchEvent(MotionEvent event){
 		
 		int action = event.getAction();
 		switch (action) {
 			case MotionEvent.ACTION_DOWN:
 				downx = (int) event.getX();
 				downy = (int) event.getY();
 				updateBoundingBox(downx, downy);
 				break;
 		 
 			case MotionEvent.ACTION_MOVE:
 				upx = (int) event.getX();
 				upy = (int) event.getY();
 		//		Log.i("drawing", upx+"	"+upy+" in class...");
 				canvas.drawLine(downx, downy, upx, upy, paint);
 				this.invalidate(); 
 				updateBoundingBox(downx, downy);
 				db.insert(username, address, deviceID, channelID, downx, downy, upx, upy);
 				downx = upx;
 				downy = upy;
 				break;
 		  
 			case MotionEvent.ACTION_UP:
 				upx = (int) event.getX();
 				upy = (int) event.getY();
 				canvas.drawLine(downx, downy, upx, upy, paint);
 				this.invalidate(); 
 				updateBoundingBox(downx, downy);
 				MyConfig.sendDataList.add(minx);		MyConfig.sendDataList.add(miny);
 				MyConfig.sendDataList.add(maxx);		MyConfig.sendDataList.add(maxy);
 				db.insert(username, address, deviceID, channelID, downx, downy, upx, upy);
 				minx=1000; miny=1000; maxx=0; maxy=0;
 				break;

 			case MotionEvent.ACTION_CANCEL:
 				break;
 		
 			default:
 				break;
 		}
 		
 		return true;
 	}   
     
     private void drawSavedData(ArrayList<Float> list){
     	 
     	System.out.println("drawing saved data..."); 
 		System.out.println("saved data nums		"+ list.size());
 		for(int i=0; i<list.size(); i+=4){
 	    	canvas.drawLine(list.get(i), list.get(i+1), list.get(i+2), list.get(i+3), paint);
 	    }
     }
     
     private void setStartStopFlag(String str){ 
    	 //TODO
    	 MyConfig.startStopFlag = str;
     }
     
     /*//获取该channel用户保存的所有drawing数据
     public ArrayList<Float> getAllDrawingData(){

     	ArrayList<Float> list = new ArrayList<Float>();
     	Cursor cur = db.selectAll(username, address, deviceID, channelID);  
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
     }*/
     
    public void clear(){
    	Log.i("DrawSurfaceView", "DrawSurfaceView clear called...");
    	if(alteredBitmap!=null && !alteredBitmap.isRecycled()){
      	  alteredBitmap.recycle();  
      	  alteredBitmap = null;
  	    }
    	alteredBitmap = Bitmap.createBitmap(MyConfig.viewWidth, MyConfig.viewHeight, Config.ARGB_8888);
 		canvas = new Canvas(alteredBitmap);
 		Matrix matrix = new Matrix();
 		canvas.drawBitmap(resizedBmp, matrix, paint);
 		this.setImageBitmap(alteredBitmap);
 		
 		MyConfig.sendDataList.clear();
 		db.deleteAll(username, address, deviceID, channelID);
    } 
    
    private void updateBoundingBox(int x, int y){
    	if(x<minx){
    		minx = x;
    	}else if(x>maxx){
    		maxx = x;
    	}
    	if(y<miny){
    		miny = y;
    	}else if(y>maxy){
    		maxy = y;
    	}
    }
 }  