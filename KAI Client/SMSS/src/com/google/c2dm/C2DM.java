package com.google.c2dm;

import android.accounts.Account;
import android.accounts.AccountManager; 
import android.content.Context; 
import android.os.Handler;
import android.util.Log;  
import android.widget.Toast; 

import com.google.android.c2dm.C2DMessaging;    
import org.tju.config.MyConfig; 
 

public class C2DM { 
	
    private static final String LOG_TAG = "C2DM";
    private Context context;
    private Handler uiHandler;  
    private boolean registered;
    private static C2DM pushRef;

    
    public C2DM(Context context){ 
        uiHandler = new Handler();
        pushRef = this;
        this.context = context;
    }

  
    public static C2DM getRef() {
        return pushRef;
    }
  
    public Account getUserAccount() {
    	AccountManager accountManager = AccountManager.get( context );
        Account accounts[] = accountManager.getAccountsByType( "com.google" );;
    	if(accounts.length>0){
    		System.out.println(accounts[0].name);
    		return accounts[0];
    	}
    	else{
    		uiHandler.post( new ToastMessage( context, "You must register Google account on you phone..." ) );
    	}
        return null;
    }

    public void onRegistered() {
        Log.d( LOG_TAG, "onRegistered" );
        registered = true; 
        uiHandler.post( new ToastMessage( context,"C2DM Service Registered" ) );
    }

    public void onUnregistered() {
        Log.d( LOG_TAG, "onUnregistered" );
        registered = false; 
        uiHandler.post( new ToastMessage( context, "C2DM Service Unregistered" ) );
    }

    
    public void register() {
        if( registered ){
//            unregister();
        	System.out.println("have registered, man!");
        }
        else {
            Log.d( LOG_TAG, "C2DM register() start" );
            C2DMessaging.register( context, MyConfig.C2DM_SENDER );
            Log.d( LOG_TAG, "C2Dm regester() done" );
        }
    }
    
    private void unregister() {
        if( registered ) {
            Log.d( LOG_TAG, "unregister()" );
            C2DMessaging.unregister( context );
            Log.d( LOG_TAG, "unregister() done" );
        }
    }
 

    class ToastMessage implements Runnable {
    	
    	Context ctx;
    	String msg;
    	
        public ToastMessage( Context ctx, String msg ) {
            this.ctx = ctx;
            this.msg = msg;
        }

        public void run() {
            Toast.makeText( ctx, msg, Toast.LENGTH_SHORT).show();
        }
   
    } 
}


