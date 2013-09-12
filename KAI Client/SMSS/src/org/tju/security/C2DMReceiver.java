package org.tju.security;

import android.accounts.Account; 
import android.content.Context;
import android.content.Intent; 
import android.content.SharedPreferences;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.c2dm.C2DMBaseReceiver; 
import com.google.c2dm.C2DM;
import com.google.c2dm.NetworkCommunication;

import java.io.IOException;

import org.tju.config.MyConfig; 
 

/**
 * Broadcast receiver that handles Android Cloud to Data Messaging (AC2DM) messages, initiated
 * by the JumpNote App Engine server and routed/delivered by Google AC2DM servers. The
 * only currently defined message is 'sync'.
 */
public class C2DMReceiver extends C2DMBaseReceiver {
    static final String LOG_TAG = "C2DMRECEIVER";
    SharedPreferences settings = null;
    
    public C2DMReceiver() {
        super( MyConfig.C2DM_SENDER);
    }

    @Override
    public void onError(Context context, String errorId) {
        Toast.makeText(context, "Messaging registration error: " + errorId,
                Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
    	//message contains the deviceid and channelid.
        String message = intent.getExtras().getString( "message" );
        Log.d( LOG_TAG, "onMessage: "+message );
//        C2DM p = C2DM.getRef();
        //TODO 解析message，启动activity
        String[] splitMessage = message.split(",");
        String deviceID = splitMessage[0];
        String channelID = splitMessage[1];
        String imageID = splitMessage[2];
        
        SharedPreferences settings = getSharedPreferences("settings", PreferenceActivity.MODE_PRIVATE);
		settings.edit().putString("deviceid", deviceID).commit();
		settings.edit().putString("channelid", channelID).commit();
		settings.edit().putString("imageid", imageID).commit();
		
		Intent i = new Intent(context, OnMessageActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(i);
        
    }

    public void onRegistered(Context context, String registrationId) throws IOException {
        Log.d( LOG_TAG, "onRegistered()" );
        C2DM p = C2DM.getRef();
        Account acc = p.getUserAccount();
        
        String accountName = acc.name;
        settings = getSharedPreferences("settings", PreferenceActivity.MODE_PRIVATE);
		settings.edit().putString("accountName", accountName).commit(); 
		
        Log.d( LOG_TAG, "onRegistered() sendRegistrationId start" );
        NetworkCommunication.sendRegistrationId( acc, context, registrationId );
       
        Log.d( LOG_TAG, "onRegistered() p.onRegistered() sendRegisteredId end" );
        p.onRegistered();
        Log.d( LOG_TAG, "onRegistered() done" );
    }

    public void onUnregistered(Context context) {
        C2DM p = C2DM.getRef();
        Account acc = p.getUserAccount();
        NetworkCommunication.sendRegistrationId( acc, context, "" );
        p.onUnregistered();
    }

}
