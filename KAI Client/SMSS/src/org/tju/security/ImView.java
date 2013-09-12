package org.tju.security;

import org.tju.config.MyConfig;
import org.tju.http.Http; 

import android.content.Context;  
import android.graphics.Bitmap; 
import android.graphics.Canvas;   
import android.graphics.Matrix;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log; 
import android.view.KeyEvent;
import android.view.SurfaceHolder;  
import android.view.SurfaceView;   
   
public class ImView extends SurfaceView implements SurfaceHolder.Callback {  
   
     //定义SurfaceHolder对象     
     Handler mHandler = null;
     private Bitmap bmp = null;
     private SurfaceHolder sfh;
       
     public ImView(Context context) {  
         super(context);   
     }  
     
    public ImView(Context context, AttributeSet attrs) {//备注1
     	super(context, attrs);

     	sfh = this.getHolder();
		sfh.addCallback(this);
		this.setKeepScreenOn(false);
		setFocusable(true);
		setFocusableInTouchMode(false);
		
		Log.i("ImSurfaceView", "ImSurfaceView constractor called...");
    }
    
    //surface创建时激发 此方法在主线程总执行  
    @Override  
    public void surfaceCreated(SurfaceHolder holder) {  
        //开启绘图线程  
    	Log.i("ImSurfaceView", "ImageSusfaceView crated...."); 
        
        mHandler = new Handler();
        mHandler.post(mRunnable);
    }  
    
    
     //在surface的大小发生改变时激发  
     @Override  
     public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    	 
     }  
        
     //在surface销毁时激发  
     @Override  
     public void surfaceDestroyed(SurfaceHolder holder) {   

    	 mHandler.removeCallbacks(mRunnable);  
     }  
	
	 private Runnable mRunnable = new Runnable() {
	     
	    	public void run() {
	    		//至于这里为什么同步这就像一块画布 你不能让两个人同时往上边画画  
	            synchronized( sfh ){  
	           	 Log.i("ImSurfaceView", "ImageSusfaceView running drawing image....");
	                draw();  
	            } 
	             
	    		mHandler.postDelayed(mRunnable, MyConfig.INTERVAL_TIME);
	    	} 
	    };
	 
     
     //绘图方法  注意这里是另起一个线程来执行绘图方法了不是在UI 线程了  
     public void draw(){  
    	 Bitmap tmp = null;
    	 Log.i("ImSurfaceView", "the draw method called..");
    	 //获取IP Camera图片
    	 try{ 
    		 tmp = Http.getSnapshot(this.getContext());  
    		 if(tmp==null){
    			 return;
    		 }
    	     float scale = ((float)(1.0))/MyConfig.sampleRatio; 
    		 int bmpWidth = tmp.getWidth(); 
    		 int bmpHeight = tmp.getHeight();
    		 Matrix matrix = new Matrix();
    	     matrix.postScale(scale, scale); 
    	     bmp = Bitmap.createBitmap(tmp,0,0,bmpWidth,bmpHeight,matrix,true); 
    	 }catch(Exception e){
    		 e.printStackTrace();
    	 }
    	//锁定画布，得到canvas 用SurfaceHolder对象的lockCanvas方法  
		 Canvas canvas = sfh.lockCanvas();  
		 if (sfh==null || canvas == null || bmp==null) {  
			 return;  
		 } 
		 Matrix matrix = new Matrix(); 
		 canvas.drawBitmap(bmp, matrix, null);
         //绘制后解锁，绘制后必须解锁才能显示  
         sfh.unlockCanvasAndPost(canvas);  
     }  
    
  	@Override
 	public boolean onKeyDown(int key, KeyEvent event) {

 		return super.onKeyDown(key, event);
 	}
  
 }  