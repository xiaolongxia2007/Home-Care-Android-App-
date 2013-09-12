package org.tju.security;
 
import java.util.ArrayList; 

import org.tju.http.Http;
import org.tju.node.Node;
import org.tju.node.NodeAdapter;
import org.tju.node.NodeUtil;
   
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.View; 
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class TreeViewActivity extends Activity  implements OnItemClickListener{
	
	SharedPreferences settings = null; 
	String address = null;
	String deviceID = null;
	String sessionID = null;
	String channelID = null;
	String username = null;
	TextView welcomeMsgTextView = null;
	TextView listMsgTextView = null;
	ListView treeView = null; 
	ArrayList<Node> preitems = null;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tree_layout);
        welcomeMsgTextView = (TextView) findViewById(R.id.loginMsg);
        listMsgTextView = (TextView) findViewById(R.id.listMsg);
        treeView = (ListView) findViewById(R.id.tree);
        
        settings = getSharedPreferences("settings", PreferenceActivity.MODE_PRIVATE);  
        username = settings.getString("username", null); 
        welcomeMsgTextView.setText("Welcome： "+username+"                           ");
        welcomeMsgTextView.setTextColor(Color.MAGENTA);
        listMsgTextView.setText("Your Device List：");
        listMsgTextView.setTextColor(Color.MAGENTA);
        
        if(NodeUtil.nodeList==null || NodeUtil.nodeList.size()==0){
        	NodeUtil.nodeList = Http.getDevices(this); 
        } 
        preitems = NodeUtil.getRootZoneNodes();
        NodeAdapter adapter = new NodeAdapter(this, preitems);
        treeView.setAdapter(adapter); 
         
        treeView.setOnItemClickListener(this);
	} 
  
    
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) { 
		
		ArrayList<Node> items = null;
		
		Node item = preitems.get(position);

		int nodeType = item.getNodeType();
		if(nodeType == Node.ZONE)
		{     
			items = NodeUtil.getZoneDeviceNodes(item.getZoneName()); 
			preitems = items;
			NodeAdapter adapter = new NodeAdapter(TreeViewActivity.this, items);
			treeView.setAdapter(adapter);
		}else if(nodeType == Node.DEVICE){ 
			items = NodeUtil.getDeviceChannelNodes(item.getZoneName(), item.getDeviceId());
			preitems = items;
			NodeAdapter adapter = new NodeAdapter(TreeViewActivity.this, items);
			treeView.setAdapter(adapter);
		}else if(nodeType == Node.CHANNEL)
		{   
			address = item.getZoneName();
			channelID = item.getChannel(); 
			deviceID = item.getDeviceId(); 
			settings = getSharedPreferences("settings", PreferenceActivity.MODE_PRIVATE);
			settings.edit().putString("address", address).commit(); 
			settings.edit().putString("deviceid", deviceID).commit(); 
			settings.edit().putString("channelid", channelID).commit();
    		Intent intent = new Intent();
    		intent.setClass(TreeViewActivity.this, ShowSnapshotActivity.class);
    		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    		startActivity(intent);
		} 	
	}
}  