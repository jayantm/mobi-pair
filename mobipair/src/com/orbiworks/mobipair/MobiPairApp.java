package com.orbiworks.mobipair;

import com.orbiworks.mobipair.model.Device;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.speech.tts.TextToSpeech.OnInitListener;

public class MobiPairApp extends Application implements OnInitListener {
	private static MobiPairApp singleton;
	public Device device;

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
