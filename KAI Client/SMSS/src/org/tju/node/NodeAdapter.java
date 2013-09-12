package org.tju.node;
  
import java.util.ArrayList;

import org.tju.security.R; 
  
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NodeAdapter extends BaseAdapter 
{
	private LayoutInflater mInflater;
	private ArrayList<Node> items;
	
	public NodeAdapter(Context context, ArrayList<Node> it)
	{
		mInflater = LayoutInflater.from(context);
		items = it;
	}

	@Override
	public int getCount() 
	{
		return items.size();
	}

	@Override
	public Object getItem(int position) 
	{
		return items.get(position);
	}

	@Override
	public long getItemId(int position) 
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		ViewHolder holder;
		
		if (convertView == null)
		{
		   convertView = mInflater.inflate(R.layout.node_layout, null);
		   holder = new ViewHolder();
		   holder.pad = (TextView)convertView.findViewById(R.id.nodePad);
		   holder.img = (ImageView)convertView.findViewById(R.id.nodeImg);
		   holder.text = (TextView)convertView.findViewById(R.id.nodeText);
		   convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder)convertView.getTag();
		}
		 
		Node node = items.get(position);
		int nodeType = node.getNodeType();
		if (nodeType == Node.ZONE)
		{  
		   holder.img.setImageResource(R.drawable.zone);
		   holder.text.setText(node.getZoneName()); 
		   holder.text.setTextColor(Color.BLACK);
		}
		else if (nodeType == Node.DEVICE) 
		{   
			holder.pad.setText("	");
			holder.img.setImageResource(R.drawable.device);
			holder.text.setText("Device "+node.getDeviceId()+":  "+node.getDeviceName()); 
			holder.text.setTextColor(Color.BLACK);
		}
		else if (nodeType == Node.CHANNEL) 
		{
			holder.pad.setText("		");
			holder.img.setImageResource(R.drawable.channel);
			holder.text.setText("Channel "+node.getChannel()); 
			holder.text.setTextColor(Color.BLACK);
		}
		if (position == 0)
		{
			holder.pad.setText("");
		}
		
		return convertView;
	}

	private class ViewHolder
	{
		TextView pad;
		ImageView img;
		TextView text;
	}
}
