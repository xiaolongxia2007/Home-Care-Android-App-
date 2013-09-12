package org.tju.security;

import java.util.ArrayList;
import java.util.HashMap;

import org.tju.config.MyConfig;
import org.tju.db.DB; 

import android.content.Context;  
import android.database.Cursor;
import android.graphics.Bitmap; 
import android.graphics.Canvas;   
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.AttributeSet; 
import android.widget.ImageView;
   
public class AlarmImView extends ImageView {  
   
     //定义SurfaceHolder对象     
    Handler mHandler = null;
    String username = null, address=null, deviceID=null, channelID=null; 
    DB db = null;
    Canvas canvas = null;
    Paint paint = null;
    Bitmap alteredBitmap, resizedBmp, bmp;
    ArrayList<Float> regionList = null;
    
    public AlarmImView(Context context) { 
  	    super(context); 
  	} 
    public AlarmImView(Context context, AttributeSet attrs, int defStyle) { 
  	    super(context, attrs, defStyle); 
  	} 
     
  	public AlarmImView(Context context, AttributeSet attrs, HashMap<String, String> map){ 
  	    super(context, attrs); 
  	    
  	    this.setKeepScreenOn(true);
 		setFocusable(false);
 		setFocusableInTouchMode(false);
 		
 		//获取数据库连接参数
 		username = map.get("username"); 
        address = map.get("address");
        deviceID = map.get("deviceid");
        channelID = map.get("channelid");
 		//打开数据库
        db = new DB(this.getContext()); 
        db.open(this.getContext()); 
        
        regionList = db.getAllDrawingData(username, address, deviceID, channelID);
        setPaint(); 
         
        
        setCanvas();
        drawSavedData();
  	} 
  	
  	
  	private void setCanvas(){
  		
  	    //设置画布
  		bmp = ((BitmapDrawable)(this.getResources().getDrawable(R.drawable.canvas))).getBitmap();
  		/* 计算出缩小的比例 */ 
        float scaleWidth=(float)bmp.getWidth()/MyConfig.viewWidth; 
        float scaleHeight=(float)bmp.getHeight()/MyConfig.viewHeight; 
         /* 产生reSize后的Bitmap对象 */
        Matrix scaleMatrix = new Matrix();
        scaleMatrix.postScale(scaleWidth, scaleHeight);
        resizedBmp = Bitmap.createBitmap(bmp,0,0,bmp.getWidth(),bmp.getHeight(),scaleMatrix,true); 
  		alteredBitmap = Bitmap.createBitmap(MyConfig.viewWidth, MyConfig.viewHeight, Config.ARGB_8888);
  		canvas = new Canvas(alteredBitmap);
  		
  		Matrix matrix = new Matrix();
  		canvas.drawBitmap(resizedBmp, matrix, paint);
  		this.setImageBitmap(alteredBitmap);
  	}
  	
  	private void setPaint(){
  		paint = new Paint();
  		paint.setAntiAlias(true);
  		paint.setColor(Color.RED);
  		paint.setStrokeWidth(2);
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
  	
  
    private void drawSavedData(){
      	
      	System.out.println("drawing region data...");
  		if(regionList!=null && regionList.size()>0){
  	//		System.out.println("region data nums		"+ regionList.size());
  			for(int i=0; i<regionList.size(); i+=4){
  	    		canvas.drawLine(regionList.get(i), regionList.get(i+1), regionList.get(i+2), regionList.get(i+3), paint);
  	    	}
  		}
    }
  	
  	 
     
}  