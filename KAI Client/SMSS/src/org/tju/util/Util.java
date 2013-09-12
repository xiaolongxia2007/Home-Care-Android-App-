package org.tju.util;
 

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Util {
	
	 
	public static void showToast(Context context, int resId, String str) {
		// 1 创建Toast
		Toast toast = Toast.makeText(context, str, Toast.LENGTH_LONG); 
		// 2 创建Layout，并设置为水平布局
		LinearLayout mLayout = new LinearLayout(context);
		mLayout.setOrientation(LinearLayout.HORIZONTAL);
		ImageView mImage = new ImageView(context); // 用于显示图像的ImageView
		mImage.setImageResource(resId);  
		View toastView = toast.getView(); // 获取显示文字的Toast View 
		mLayout.addView(mImage); // 添加到Layout
		mLayout.addView(toastView); 
		// 3 关键，设置Toast显示的View(上面生成的Layout).
		toast.setView(mLayout);
		toast.show();
	}
	
	public static void showToast(Context context, String str){
		Toast toast = Toast.makeText(context, str, Toast.LENGTH_LONG); 
		toast.show();
	}
}
