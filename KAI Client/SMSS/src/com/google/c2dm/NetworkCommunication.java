package com.google.c2dm;

import android.accounts.Account; 
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log; 
import org.apache.http.HttpResponse;  
import org.apache.http.HttpStatus;     
import org.apache.http.NameValuePair;    
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost; 
import org.apache.http.client.entity.UrlEncodedFormEntity;  
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.tju.config.MyConfig; 

import java.io.BufferedInputStream; 
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class NetworkCommunication {
    private static final int REGISTRATION_TIMEOUT = 30 * 1000; // ms
    private static final String TOKEN_URI = MyConfig.APP_BASE_URI+"/token";
    private static final String IAMGE_URI = MyConfig.APP_BASE_URI+"/snapshot";
    private static final String LOG_TAG = "Push_NetworkComm";

    private static DefaultHttpClient httpClient = null;

    public static boolean sendRegistrationId( Account account, Context context, String registrationId ) {
        String accountName = account.name;
        return sendToken( accountName, registrationId );
    }

    private static boolean sendToken( String accountName, String registrationId ) {
        try {
            maybeCreateHttpClient();
            Log.i(LOG_TAG, "accountName		registrationId:		"+accountName+"	"+registrationId);
            HttpPost post = new HttpPost( TOKEN_URI );
            ArrayList<BasicNameValuePair> parms = new ArrayList<BasicNameValuePair>();
            parms.add( new BasicNameValuePair( "accountName", accountName ) );
            parms.add( new BasicNameValuePair( "registrationId", registrationId ) );
            post.setEntity( new UrlEncodedFormEntity(parms) );
            HttpResponse resp = httpClient.execute( post );
            System.out.println("accountName 	registrationId"+accountName+"	"+registrationId);
            // Execute the POST transaction and read the results
            return resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
        } catch( UnsupportedEncodingException ex ) {
            Log.e( LOG_TAG, "UnsupportedEncodingException", ex );
            return false;
        } catch( IOException ex ) {
            Log.e( LOG_TAG, "IOException", ex );
            return false;
        }
    }

    
    public static Bitmap getImage(String imID){
    	
    	Bitmap bmp = null;
    	 
    	maybeCreateHttpClient();
		HttpPost post = new HttpPost( IAMGE_URI );
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("id", imID)); 
		try {
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
		} 
		
		int res = 0;
		
		try {
			System.out.println("iamge id "+imID);
			res = httpClient.execute(post).getStatusLine().getStatusCode();
		} catch (ClientProtocolException e1) { 
			e1.printStackTrace();
		} catch (IOException e1) { 
			e1.printStackTrace();
		} 
		
		if (res == 200) { 
		    HttpResponse httpResponse = null;
			try {
				httpResponse = httpClient.execute(post);
			} catch (ClientProtocolException e1) {  
				e1.printStackTrace();
			} catch (IOException e1) { 
				e1.printStackTrace();
			} 
		 
			try {
				InputStream is = httpResponse.getEntity().getContent();
				long lens = httpResponse.getEntity().getContentLength();
				System.out.println("image lens = "+lens);
				BufferedInputStream bis = new BufferedInputStream(is);
				bmp = BitmapFactory.decodeStream(bis); 
			}catch(IOException e){
				e.printStackTrace();
			} 
		}else{
			Log.e("error:	", "get alarm iamge connecting failed	"+res);
		}
       
		return bmp;
    }
    
    
    
    private static void maybeCreateHttpClient() {
        if ( httpClient == null) {
            httpClient = new DefaultHttpClient();
            HttpParams params = httpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(params, REGISTRATION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(params, REGISTRATION_TIMEOUT);
            ConnManagerParams.setTimeout(params, REGISTRATION_TIMEOUT);
        }
    }
     

}

