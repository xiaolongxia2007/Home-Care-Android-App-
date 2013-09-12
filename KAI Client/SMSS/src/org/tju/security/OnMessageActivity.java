package org.tju.security; 
   
import org.tju.db.DB;

import com.google.c2dm.NetworkCommunication;

import android.app.Activity; 
import android.content.Intent;
import android.content.SharedPreferences;   
import android.graphics.Bitmap; 
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceActivity;   
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;   
import android.widget.ImageView;
import android.widget.TextView;

public class OnMessageActivity extends Activity implements OnClickListener {
	 
	TextView alarmText = null;
	ImageView imView = null; 
	Bitmap bmp = null;
	int sampleRatio = 2; 
	String imID = null;
	Canvas canvas = null;
	Paint paint = null;
	SharedPreferences settings = null; 
	
	String username = null, address=null, deviceID=null, channelID=null; 
    DB db = null;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onmessage_layout);
         
        settings = getSharedPreferences("settings", PreferenceActivity.MODE_PRIVATE);   
        imID = settings.getString("imageid", null); 
        
        imView = (ImageView) findViewById(R.id.alarmImButton);  
        alarmText = (TextView) findViewById(R.id.alarmText);
        alarmText.setText(R.string.alarmText);
        alarmText.setTextSize(20);
        alarmText.setTextColor(Color.RED);
        imView.setOnClickListener(this);  
        
        Bitmap tmp = NetworkCommunication.getImage(imID);
       
        Display currentDisplay = getWindowManager().getDefaultDisplay();
        int dw = currentDisplay.getWidth(); 
        int dh = currentDisplay.getHeight();
        int imw = tmp.getWidth();
    	int imh = tmp.getHeight();
    	int heightRatio = (int)Math.ceil(imh/dh);
   	 	int widthRatio = (int)Math.ceil(imw/dw);
   	 	float scale = ((float)(1.0))/(Math.max(heightRatio, widthRatio));
   	 	  
		Matrix matrix = new Matrix();
	    matrix.postScale(scale, scale);
	    bmp = Bitmap.createBitmap(tmp,5,5,imw-5,imh-5,matrix,true); 
	    imView.setImageBitmap(bmp); 
    }
        
    
    Bitmap drawRegion(Bitmap bitmp){
    	
    	Bitmap alteredBitmap = Bitmap.createBitmap(bitmp.getWidth(),bitmp.getHeight(),bitmp.getConfig());
    	canvas = new Canvas(alteredBitmap);
    	paint = new Paint();
    	paint.setColor(Color.RED);
    	paint.setStrokeWidth(4);
    	Matrix matrix = new Matrix();
    	canvas.drawBitmap(bitmp, matrix, paint);
    	  
    	return alteredBitmap;
    }
     
	@Override
	public void onClick(View v) {  
		Intent intent = new Intent(OnMessageActivity.this, ShowSnapshotActivity.class); 
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
        startActivity(intent);
	}  
	 
}  