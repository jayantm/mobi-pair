package com.orbiworks.mobipair;

import com.orbiworks.mobipair.model.Device;

import android.app.Application;
import android.content.res.Configuration;

public class MobiPairApp extends Application {
	private static MobiPairApp singleton;
	public Device device;
	
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
}
