package com.orbiworks.mobipair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.orbiworks.mobipair.navdrawer.NavDrawerFragment;
import com.orbiworks.mobipair.util.HttpTask;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;

public class MainActivity extends ActionBarActivity
	implements NavDrawerFragment.NavigationDrawerCallbacks, HttpTask.HttpTaskHandler
{	
	private ProgressDialog nDialog;
	private String data;
	private NavDrawerFragment mNavigationDrawerFragment;
	private CharSequence mTitle;
	private String[] navMenuTitles;
	private MobiPairApp mApplication = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		registerApp();
		
		mApplication = (MobiPairApp)getApplicationContext();

		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

		FragmentManager fragMngr = getSupportFragmentManager();
		
		Account[] accounts = AccountManager.get(this).getAccounts();
		Log.e("", "Size: " + accounts.length);
		for (Account account : accounts) {

			String possibleEmail = account.name;
			String type = account.type;

			if (type.equals("com.google")) {
				mApplication.device.setAccountMail(possibleEmail);
				Log.i("Account", possibleEmail);
				break;
			}
		}

		mNavigationDrawerFragment = (NavDrawerFragment) fragMngr.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();
		showNotification();
		
		nDialog = new ProgressDialog(this);
		nDialog.setMessage("Fetching..");
		nDialog.setTitle("Getting data");
		nDialog.setIndeterminate(false);
		nDialog.setCancelable(true);
		
		String deviceId = mApplication.device.getDeviceId();
		Log.i("DeviceID", deviceId);

		HttpTask task = new HttpTask();
		task.setTaskHandler(this);
		task.execute(HttpTask.GET("/devices?id="+deviceId));

		DrawerLayout drwrLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer, drwrLayout);
	}
	
	public void registerApp() {
		PendingIntent pIntent = PendingIntent.getBroadcast(this, 0, new Intent(), 0);
		Intent regIntent = new Intent("com.google.android.c2dm.intent.REGISTER");
		regIntent.putExtra("app", pIntent);
		regIntent.putExtra("sender", "644434484811");
		try {
			startService(regIntent);
		} catch (Exception e) {
			Log.e("REC", e.getMessage());
		}
	}
	
	@Override
	public void httpTaskBegin(String tag) {
		Log.d(tag, "Begin");
		nDialog.show();
	}
	
	@Override
	public void httpTaskSuccess(String tag, String json) {
		data = json;
		Log.d(tag, data);
		nDialog.dismiss();
		
		JSONArray arrJson = null;
		JSONObject obj = null;
		try {
			arrJson = new JSONArray(json);
			obj = arrJson.getJSONObject(0);
			mApplication.device.setDeviceToken(obj.getString("dev_token"));
		} catch (JSONException e) {
			Log.e(tag, e.getMessage());
		}
	}

	@Override
	public void httpTaskFail(String tag) {
		Log.e(tag, "Task Failed");
		data = null;
		nDialog.dismiss();
	}

	private void showNotification() {
		final int notificationId = 1;

		Intent intentAccept = new Intent(getBaseContext(), MobiPairBroadcastReceiver.class);
		intentAccept.setAction("Accept");
		intentAccept.putExtra("action_type", "app_notification");
		intentAccept.putExtra("notificationId", notificationId);
		PendingIntent pIntentAccept = PendingIntent.getBroadcast(
				getBaseContext(), 12345, intentAccept, PendingIntent.FLAG_UPDATE_CURRENT);

		Intent intentReject = new Intent(getBaseContext(), MobiPairBroadcastReceiver.class);
		intentReject.setAction("Reject");
		intentReject.putExtra("action_type", "app_notification");
		intentReject.putExtra("notificationId", notificationId);
		PendingIntent pIntentReject = PendingIntent.getBroadcast(
				getBaseContext(), 12345, intentReject, PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationCompat.Builder noti = new NotificationCompat.Builder(getBaseContext())
				.setAutoCancel(true)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("New Pairing Request")
				.setContentText("   from longmailid_reallylonglonglongid@gmail.com")
				.addAction(R.drawable.ic_accept, "Accept", pIntentAccept)
				.setContentIntent(pIntentAccept)
				.addAction(R.drawable.ic_reject, "Reject", pIntentReject)
				.setContentIntent(pIntentReject);

		NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
		notificationManager.notify(notificationId, noti.build());
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		if(navMenuTitles != null) {
			mTitle = navMenuTitles[position];
			setTitle(mTitle);
		}
		displayView(position);
	}

	public void onSectionAttached(int number) {
		mTitle = navMenuTitles[number - 1];
	}

	private void displayView(int position) {
		// update the main content by replacing fragments
		Fragment fragment = null;

		switch (position) {
		case 0:
			fragment = new DashboardFragment();
			break;
		case 1:
			fragment = new DevicesFragment();
			break;
		case 2:
			fragment = new PairFragment();
			break;
		case 3:
			fragment = new DebugFragment();
			break;
		default:
			fragment = new DashboardFragment();
			break;
		}

		if (fragment != null) {
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
		} else { // error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}
	}

	@SuppressWarnings("deprecation")
	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
