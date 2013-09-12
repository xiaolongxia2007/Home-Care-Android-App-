package org.tju.security;
       
import org.tju.http.Http;
import org.tju.util.Util;

import com.google.c2dm.C2DM;

import android.app.Activity; 
import android.content.Intent;
import android.content.SharedPreferences;  
import android.os.Bundle;
import android.preference.PreferenceActivity;   
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox; 
import android.widget.EditText; 

public class LoginActivity extends Activity implements OnClickListener {
	
	String username = null;
	String password = null;
	String sessionID = null;
	SharedPreferences settings = null;
	EditText usernameEdit;
	EditText passwordEdit;
	Button loginButton;
	CheckBox savepwdCheckBox;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginpage);
        usernameEdit = (EditText) findViewById(R.id.login_edit_account);
        passwordEdit = (EditText) findViewById(R.id.login_edit_pwd);
        loginButton = (Button) findViewById(R.id.login_btn_login);
        savepwdCheckBox = (CheckBox) this.findViewById(R.id.login_cb_savepwd);
        
        settings = getSharedPreferences("settings", PreferenceActivity.MODE_PRIVATE);  
        username = settings.getString("username", null);
        password = settings.getString("password", null); 
        
        if(username!=null && password!=null){ 
        	usernameEdit.setText(username);
        	passwordEdit.setText(password);            
        } 
        
        loginButton.setOnClickListener(this);
        
    }

	@Override
	public void onClick(View v) {
		username = usernameEdit.getText().toString();
		password = passwordEdit.getText().toString();
		if(username==null || password==null || username.length()==0 || password.length()==0 ){
			Util.showToast(this, R.drawable.error, "Account and Password cannot be Empty！");
		}else{
			sessionID = Http.checkUser(this, username, password);
			if(sessionID==null){
				Util.showToast(this, R.drawable.fail, "Login Fail!");
			}else{ 
				if(savepwdCheckBox.isChecked()){ 
					settings.edit().putString("username", username).commit();
					settings.edit().putString("password", password).commit();
				} 
				
				//start C2DM service
				C2DM c2dm = new C2DM(this);
				c2dm.register();
				
				settings.edit().putString("sessionid", sessionID).commit(); 
				Intent intent = new Intent(LoginActivity.this, TreeViewActivity.class); 
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        		startActivity(intent);
			}
		}
	}  
	 
}  