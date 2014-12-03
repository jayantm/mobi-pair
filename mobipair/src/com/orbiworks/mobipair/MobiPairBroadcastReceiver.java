package com.orbiworks.mobipair;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

public class MobiPairBroadcastReceiver extends BroadcastReceiver {
	
	private MobiPairApp mApplication = null;

	@Override
	public void onReceive(Context context, Intent intent) {
		mApplication = (MobiPairApp) context.getApplicationContext();
		try {
			String action = intent.getAction();
			String actionType = intent.getStringExtra("action_type");
			
			if(actionType!=null && actionType.equals("app_notification")) {
				Log.d("MobiPairBroadcastReceiver", actionType);
				if (action.equals("Accept")) {
					
				} else if (action.equals("Reject")) {
					
				}
				
				int notificationId = intent.getIntExtra("notificationId", 1);
				NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
				notificationManager.cancel(notificationId);
			} else {
				if (action.equals("com.google.android.c2dm.intent.REGISTRATION")) {
					String registrationId = intent.getStringExtra("registration_id");
					mApplication.device.setGcmId(registrationId);
					Log.i("MobiPairBroadcastReceiver", registrationId);
					String error = intent.getStringExtra("error");
					String unregistered = intent.getStringExtra("unregistered");
					Intent i = new Intent("com.orbiworks.mobipair.NOTIFICATION_LISTENER_EXAMPLE");
					i.putExtra("applicationid", registrationId);
					context.sendBroadcast(i);
				} else if (action.equals("com.google.android.c2dm.intent.RECEIVE")) {
					// string dataS
					String data = intent.getStringExtra("message");
					Log.i("MobiPairBroadcastReceiver", data);
					Intent i = new Intent("com.orbiworks.mobipair.NOTIFICATION_LISTENER_EXAMPLE");
					i.putExtra("message", "From GCM : " + data);
					context.sendBroadcast(i);
				} else {
					Log.i("MobiPairBroadcastReceiver", action);
				}
			}
			
		} catch (Exception ex) {
			Log.e("MobiPairBroadcastReceiver", ex.getStackTrace().toString());
			ex.printStackTrace();
		} finally {
			Log.i("MobiPairBroadcastReceiver", "Done");
		}
	}
}