package org.tju.node;

public class Node {

	public static final int ZONE = 1;    	//区域
	public static final int DEVICE = 2; 	//设备
	public static final int CHANNEL = 3;	//设备通道
	
	private String zoneName;
	private String deviceId;
	private String deviceName; 
	private String channel;
	private int nodeType;  //可取1、2、3
	
	public Node(){
		nodeType = ZONE;
	}
	
	public Node(String zonename){
		zoneName = zonename;
		nodeType = ZONE;
	}
	
	public Node(String zonename, String deviceid, String devicename){
		zoneName = zonename;
		deviceId = deviceid;
		deviceName = devicename;
		nodeType = DEVICE;	
	}
	
	public Node(String zonename, String deviceid, String devicename, String channel){
		zoneName = zonename;
		deviceId = deviceid;
		deviceName = devicename;
		this.channel = channel;
		nodeType = CHANNEL;
	}

	public String getZoneName() {
		return zoneName;
	}

	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	
	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public int getNodeType() {
		return nodeType;
	}

	public void setNodeType(int nodeType) {
		this.nodeType = nodeType;
	}
 
}
