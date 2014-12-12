package com.orbiworks.mobipair;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.RemoteViews;

public class NotificationListenerService extends AccessibilityService {

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		Log.d("Reciever", "onAccessibilityEvent");
		if (event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
			Notification notification = (Notification) event.getParcelableData();
			List<String> lstNoti = getNotiText(notification);
			Log.d("Noti", lstNoti.toString());
			
			String packagename = String.valueOf(event.getPackageName());
			Log.d("Receiver:", packagename + event.getText());
			if (!packagename.equals("com.orbiworks.mobipair")) {
				Intent intent = new Intent("Notification");
				intent.putExtra("pkg", packagename);
				String msg = lstNoti.toString();
				if(lstNoti.size() == 2) {
					msg = msg.replace(", ", ": ");
				} else {
					msg = msg.replace(", ", "\n");
				}
				intent.putExtra("msg", msg);
				LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
			}
		}
	}
	
	@SuppressLint("NewApi")
	public static List<String> getNotiText(Notification notification) {
		// We have to extract the information from the view
		RemoteViews views = null;
		views = notification.bigContentView;
		if (views == null)
			views = notification.contentView;
		if (views == null)
			return null;

		// Use reflection to examine the m_actions member of the given RemoteViews object.
		// It's not pretty, but it works.
		List<String> text = new ArrayList<String>();
		try {
			Field field = views.getClass().getDeclaredField("mActions");
			field.setAccessible(true);

			@SuppressWarnings("unchecked")
			ArrayList<Parcelable> actions = (ArrayList<Parcelable>) field
					.get(views);

			// Find the setText() and setTime() reflection actions
			for (Parcelable p : actions) {
				Parcel parcel = Parcel.obtain();
				p.writeToParcel(parcel, 0);
				parcel.setDataPosition(0);

				// The tag tells which type of action it is (2 is ReflectionAction, from the source)
				int tag = parcel.readInt();
				if (tag != 2)
					continue;

				// View ID
				parcel.readInt();

				String methodName = parcel.readString();
				if (methodName == null)
					continue;

				// Save strings
				else if (methodName.equals("setText")) {
					// Parameter type (10 = Character Sequence)
					parcel.readInt();

					// Store the actual string
					String t = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel).toString().trim();
					if(t.matches("\\d+\\smessages\\sfrom\\s\\d+\\sconversations.")) {
						text.clear();
					} else if(t.matches("\\d+\\snew\\smessages?")) {
						text.get(0);
					} else {
						text.add(t);
					}
				} /*else if (methodName.equals("setTime")) {
					// Parameter type (5 = Long)
					parcel.readInt();

					String t = new SimpleDateFormat("h:mm a").format(new Date(parcel.readLong()));
					text.add(t);
				} //*/

				parcel.recycle();
			}
		}

		// It's not usually good style to do this, but then again, neither is
		// the use of reflection...
		catch (Exception e) {
			Log.e("NotificationClassifier", e.toString());
		}

		return text;
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
