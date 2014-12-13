package com.orbiworks.mobipair.model;

import org.json.JSONException;
import org.json.JSONObject;

public class AppNotification {
	private String packageName;
	private String title;
	private String content;
	
	
	
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	public static AppNotification createFromJSON(String json) throws JSONException {
		JSONObject obj = new JSONObject(json);
		return AppNotification.createFromJSONObject(obj);
	}
	
	public static AppNotification createFromJSONObject(JSONObject obj) throws JSONException {
		AppNotification apn = new AppNotification();
		String id = obj.getString("id");
		apn.setPackageName(obj.getString("id"));
		apn.setTitle(obj.getString("title"));
		apn.setContent(obj.getString("content"));
		String packageName = id;
		/*
		if(id.contains("android.phone")){
			packageName = "android.phone";
		} else if(id.contains("whatsapp")){
			packageName = "whatsapp";
		} else if(id.contains("facebook")){
			packageName = "whatsapp";
		} else if(id.contains("pairing")){
			packageName = "pairing";
		}
		//*/
		apn.setPackageName(packageName);
		return apn;
	}
}
