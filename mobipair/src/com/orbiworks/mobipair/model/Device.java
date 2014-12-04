package com.orbiworks.mobipair.model;

import android.content.Context;

import com.orbiworks.mobipair.util.DeviceUtil;

public class Device {
	private String deviceId;
	private String gcmId;
	private String accountMail;
	private String deviceToken;
	private String deviceTitle;

	public Device(Context cntxt) {
		deviceId = DeviceUtil.getDeviceId(cntxt);
	}
	
	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getGcmId() {
		return gcmId;
	}

	public void setGcmId(String gcmId) {
		this.gcmId = gcmId;
	}

	public String getAccountMail() {
		return accountMail;
	}
	
	public void setAccountMail(String accountMail) {
		this.accountMail = accountMail;
	}

	public String getDeviceToken() {
		return deviceToken;
	}

	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}

	public String getDeviceTitle() {
		return deviceTitle;
	}

	public void setDeviceTitle(String deviceTitle) {
		this.deviceTitle = deviceTitle;
	}

}
