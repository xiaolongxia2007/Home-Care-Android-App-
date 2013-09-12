package org.tju.node;

import java.util.ArrayList;


public class NodeUtil {
	
	public static ArrayList<Node> nodeList;
	private static ArrayList<Node> items = new ArrayList<Node>();
	
	
	public static ArrayList<Node> getRootZoneNodes(){
		
		items.clear();
		for(Node n : nodeList){
			if(n.getNodeType()==Node.ZONE){
				items.add(n);
			}
		}
		return items;
	}
	
	public static ArrayList<Node> getZoneDeviceNodes(String zonename){
		
		items.clear();
		for(Node n : nodeList){
			if(n.getNodeType()==Node.ZONE){
				items.add(n);
				if( n.getZoneName().equals(zonename) ){
					for(Node k : nodeList){
						if( (k.getNodeType()==Node.DEVICE&&k.getZoneName().equals(n.getZoneName())) ){
							items.add(k);
						}
					}
				}
				
			} 
		}
		
		return items;
	}
	
	public static ArrayList<Node> getDeviceChannelNodes(String zonename, String deviceid){
		
		items.clear();
		for(Node n : nodeList){
			if( n.getNodeType()==Node.ZONE ) {
				items.add(n);
				if( n.getZoneName().equals(zonename) ){
					for(Node k : nodeList){
						if( k.getNodeType()==Node.DEVICE&&k.getZoneName().equals(zonename) ){
							items.add(k);
							if( k.getDeviceId().equals(deviceid) ){
								for(Node l : nodeList){
									if( l.getNodeType()==Node.CHANNEL&&l.getDeviceId().equals(deviceid) ){
										items.add(l);
									}
								}
							}
						}
					}
				} 
			}
		} 
			
		return items;
	}
	
}
