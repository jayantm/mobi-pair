package com.mukherj.accesscheck;

/**
 * Created by mukherj on 11/11/2014.
 */

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;


public class Getter extends AccessibilityService {
    /*@Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d("Reciever","onEvent function");
        if (event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            //Do something, eg getting packagename
            final String packagename = String.valueOf(event.getPackageName());
            */
    public static String packagename="";

    @Override

    public void onAccessibilityEvent(AccessibilityEvent event) {
        //Log.d("Reciever","onAccessibilityEvent");
        if (event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            packagename = String.valueOf(event.getPackageName());
            if (!packagename.equals("com.mukherj.accesscheck")){
                Log.d("Reciever","notification: " + event.getText());
                Log.d("Reciever","Package:"+packagename);
                NetworkingThread.getStrings(packagename, event.getText().toString());

                Context context = getApplicationContext();
                CharSequence text = packagename + event.getText().toString();
                int duration = Toast.LENGTH_SHORT;
                Intent intent = new Intent("Notification");
                intent.putExtra("pkg", packagename);
                intent.putExtra("msg", event.getText().toString());
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                /*
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                try {
                    Thread.sleep(3000);
                    toast.cancel();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //*/
            }
        }
    }

    @Override
    protected void onServiceConnected() {
        Log.d("Reciever","Connected");
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
        info.notificationTimeout = 100;
        setServiceInfo(info);
    }

    @Override
    public void onInterrupt() {
        Log.d("Reciever","onInterrupt");
    }
}