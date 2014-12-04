package com.orbiworks.mobipair;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

public class NotificationListenerService extends AccessibilityService {

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		Log.d("Reciever", "onAccessibilityEvent");
		if (event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
			String packagename = String.valueOf(event.getPackageName());
			if (!packagename.equals("com.mukherj.accesscheck")) {
				Log.d("Reciever", "notification: " + event.getText());
				Log.d("Reciever", "Package:" + packagename);

				// Context context = getApplicationContext();
				Intent intent = new Intent("Notification");
				intent.putExtra("pkg", packagename);
				intent.putExtra("msg", event.getText().toString());
				LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
			}
		}
	}

	@Override
	protected void onServiceConnected() {
		Log.d("Reciever", "Connected");
		AccessibilityServiceInfo info = new AccessibilityServiceInfo();
		info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
		info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
		info.notificationTimeout = 100;
		setServiceInfo(info);
	}

	@Override
	public void onInterrupt() {
		Log.d("Reciever", "onInterrupt");
	}

}
