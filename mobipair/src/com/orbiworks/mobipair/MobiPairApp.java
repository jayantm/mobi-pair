package com.orbiworks.mobipair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.orbiworks.mobipair.model.AppNotification;
import com.orbiworks.mobipair.model.Device;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;

public class MobiPairApp extends Application implements OnInitListener {
	private static MobiPairApp singleton;
	public Device device = null;
	private PackageManager pkgMngr = null;

	private HashMap<String, List<AppNotification>> notifications = new HashMap<String, List<AppNotification>>();
	public HashMap<String, List<AppNotification>> getNotifications()
	{
		return notifications;
	}
	public List<AppNotification> getNotificationsForProvider(String providerId)
	{
		return notifications.get(providerId);
	}
	public void setNotification(String actualNotificationString){
		//parsing logic here
		//actualNotificationString = "mobipair:appnotification:[{'id':'com.android.phone', 'title':'Missed Call', 'content':'Missed call from Mirang'},{'id':'com.whatsapp', 'title':'Wife', 'content':'Hi'}]";
		String notificationString = "";
		if(actualNotificationString.startsWith("mobipair:appnotification:")){
			notificationString = actualNotificationString.substring("mobipair:appnotification:".length());
		} else {
			return;
		}
		JSONArray jsonArray = null;
		try {
			jsonArray = new JSONArray(notificationString);
			for(int i = 0 ; i < jsonArray.length() ; i++){
				JSONObject object = jsonArray.getJSONObject(i);
				AppNotification notification = AppNotification.createFromJSONObject(object);
				if(!notifications.containsKey(notification.getPackageName())) {
					List<AppNotification> appNotifications = new ArrayList<AppNotification>();
					appNotifications.add(notification);
					notifications.put(notification.getPackageName(), appNotifications);
				} else {
					List<AppNotification> appNotifications = getNotificationsForProvider(notification.getPackageName());
					appNotifications.add(notification);
				}
			}
		} catch (JSONException e) {
			Log.e("pairing.request", e.getMessage());
			e.printStackTrace();
		}
	}
	
	public String getAppTitle(String packageName) {
		ApplicationInfo ai;
		try {
			ai = pkgMngr.getApplicationInfo(packageName, 0);
		} catch (final NameNotFoundException e) {
			ai = null;
		}
		String applicationName = (String) (ai != null ? pkgMngr.getApplicationLabel(ai) : "(unknown)");
		return applicationName;
	}
	
	public Drawable getAppIcon(String packageName) throws NameNotFoundException {
		return pkgMngr.getApplicationIcon(packageName);
	}
	
	private static Context mContext;
	public static Context getContext() {
		return mContext;
	}
	
	public static void setContext(Context context) {
		mContext = context;
	}

	public MobiPairApp getInstance(){
		return singleton;
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
 
	@Override
	public void onCreate() {
		super.onCreate();
		this.device = new Device(this);
		this.pkgMngr = getPackageManager();
		singleton = this;
	}
 
	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}
 
	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	@Override
	public void onInit(int status) {
		// TODO Auto-generated method stub
	}
}
