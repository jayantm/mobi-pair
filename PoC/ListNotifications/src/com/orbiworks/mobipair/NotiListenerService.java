package com.orbiworks.mobipair;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;
import android.widget.RemoteViews;
import android.widget.Toast;

public class NotiListenerService extends AccessibilityService {
@SuppressLint("NewApi")
@Override
public void onAccessibilityEvent(AccessibilityEvent event) {
    System.out.println("onAccessibilityEvent");
    if (event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
        //System.out.println("notification: " + event.getText());
    	String message = "";
    	List<CharSequence> notificationList = event.getText();
        for (int i = 0; i < notificationList.size(); i++) {
            message += notificationList.get(i);
        }
    	if(!message.toLowerCase().contains("mobi pair") && !message.isEmpty() ){
	    	Intent intent = new Intent("com.orbiworks.mobipair.NOTIFICATION_LISTENER_EXAMPLE");
	        intent.putExtra("notimessage", message + "");
			NotiListenerService.this.getApplicationContext().sendBroadcast(intent);
    	}
    }
}
@Override
protected void onServiceConnected() {
    System.out.println("onServiceConnected");
    AccessibilityServiceInfo info = new AccessibilityServiceInfo();
    info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
    info.notificationTimeout = 100;
    info.feedbackType = AccessibilityEvent.TYPES_ALL_MASK;
    setServiceInfo(info);
}

@Override
public void onInterrupt() {
    System.out.println("onInterrupt");
}
}