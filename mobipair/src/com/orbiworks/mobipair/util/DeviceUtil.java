package com.orbiworks.mobipair.util;

import java.util.UUID;

import android.content.Context;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

public class DeviceUtil {

	public static String getDeviceId(Context context) {
		final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		String tmDevice, tmSerial, androidId, deviceMobileNo;
		tmDevice = "" + tm.getDeviceId();
		tmSerial = "" + tm.getSimSerialNumber();
		androidId = "" + Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);

		deviceMobileNo = tm.getLine1Number();

		UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
		return deviceUuid.toString();
	}
}
