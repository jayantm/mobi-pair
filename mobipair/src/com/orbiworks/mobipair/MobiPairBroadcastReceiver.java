package com.orbiworks.mobipair;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

public class MobiPairBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			String action = intent.getAction();
			String actionType = intent.getStringExtra("action_type");
			
			Log.d("action_type", actionType);
			
			if(actionType.equals("app_notification")) {
				if (action.equals("Accept")) {
					
				} else if (action.equals("Reject")) {
					
				}
				
				int notificationId = intent.getIntExtra("notificationId", 1);
				NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
				notificationManager.cancel(notificationId);
			} else {
				if (action.equals("com.google.android.c2dm.intent.REGISTRATION")) {
					String registrationId = intent.getStringExtra("registration_id");
					Log.i("REC", registrationId);
					String error = intent.getStringExtra("error");
					String unregistered = intent.getStringExtra("unregistered");
					Intent i = new Intent("com.orbiworks.mobipair.NOTIFICATION_LISTENER_EXAMPLE");
					i.putExtra("applicationid", registrationId);
					context.sendBroadcast(i);
				} else if (action.equals("com.google.android.c2dm.intent.RECEIVE")) {
					// string dataS
					String data = intent.getStringExtra("message");
					Intent i = new Intent("com.orbiworks.mobipair.NOTIFICATION_LISTENER_EXAMPLE");
					i.putExtra("message", "From GCM : " + data);
					context.sendBroadcast(i);
				}
			}
			
		} catch (Exception ex) {
			Log.e("BroadcastReceive", ex.getMessage());
		} finally {
			Log.i("BroadcastReceive", "Done");
		}
	}
}