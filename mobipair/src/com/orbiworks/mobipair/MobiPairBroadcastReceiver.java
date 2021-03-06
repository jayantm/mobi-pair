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
					Intent i = new Intent("com.orbiworks.mobipair.GCM_REG");
					i.putExtra("gcmId", registrationId);
					context.sendBroadcast(i);
				} else if (action.equals("com.google.android.c2dm.intent.RECEIVE")) {
					String data = intent.getStringExtra("message");
					Log.i("MobiPairBroadcastReceiver", data);
					Intent i = new Intent("com.orbiworks.mobipair.PAIR_REQUEST");
					i.putExtra("message", data);
					MobiPairApp.getContext().sendBroadcast(i);
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