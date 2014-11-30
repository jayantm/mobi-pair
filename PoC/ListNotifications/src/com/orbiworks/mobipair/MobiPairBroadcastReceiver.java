package com.orbiworks.mobipair;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MobiPairBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		try
		{
			String action = arg1.getAction();
			if(action.equals("com.google.android.c2dm.intent.REGISTRATION")){
				String registrationId = arg1.getStringExtra("registration_id");
				Log.i("REC", registrationId);
				String error = arg1.getStringExtra("error");
				String unregistered = arg1.getStringExtra("unregistered");
				Intent i = new Intent("com.orbiworks.mobipair.NOTIFICATION_LISTENER_EXAMPLE");
				i.putExtra("applicationid", registrationId);
				arg0.sendBroadcast(i);
			}
			else if(action.equals("com.google.android.c2dm.intent.RECEIVE")){
				//string dataS
				String data = arg1.getStringExtra("message");
				Intent i = new Intent("com.orbiworks.mobipair.NOTIFICATION_LISTENER_EXAMPLE");
				i.putExtra("message", "From GCM : " + data);
				arg0.sendBroadcast(i);
			}
		}finally{
			
		}
	}	
}