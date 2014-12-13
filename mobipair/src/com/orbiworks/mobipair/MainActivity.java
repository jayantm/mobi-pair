package com.orbiworks.mobipair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.orbiworks.mobipair.navdrawer.NavDrawerFragment;
import com.orbiworks.mobipair.util.HttpTask;
import com.orbiworks.mobipair.util.HttpTask.HttpTaskHandler;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
	private static HttpTaskHandler htHandler = null;
	private SharedPreferences sharedPrefs = null;
	
	private BroadcastReceiver gcmRegReceiver = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		init();
		
		populateModel();
		
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

		FragmentManager fragMngr = getSupportFragmentManager();
		
		mNavigationDrawerFragment = (NavDrawerFragment) fragMngr.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();
		
		String deviceId = mApplication.device.getDeviceId();
		Log.i("DeviceID", deviceId);

		HttpTask task = new HttpTask();
		task.setTaskHandler(this);
		task.execute(HttpTask.GET("/devices?id="+deviceId));

		DrawerLayout drwrLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer, drwrLayout);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if (sharedPrefs.getBoolean("firstrun", true)) {
			sharedPrefs.edit().putBoolean("firstrun", false).commit();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(gcmRegReceiver);
		super.onDestroy();
	}
	
	private void init() {
		mApplication = (MobiPairApp)getApplicationContext();
		sharedPrefs = getSharedPreferences("com.orbiworks.mobipair", Context.MODE_PRIVATE);
		MobiPairApp.setContext(this);
		htHandler = this;
		
		mApplication.setNotification("mobipair:appnotification:[{'id':'com.android.phone', 'title':'Missed Call', 'content':'Missed call from Mirang'},{'id':'com.whatsapp', 'title':'Wife', 'content':'Hi'}]");
		
		nDialog = new ProgressDialog(this);
		nDialog.setMessage("Fetching..");
		nDialog.setTitle("Getting data");
		nDialog.setIndeterminate(false);
		nDialog.setCancelable(true);
		
		gcmRegReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (action.equals("com.orbiworks.mobipair.GCM_REG")) {
					String gcmId = intent.getStringExtra("gcmId");
					sharedPrefs.edit().putString("gcmId", gcmId);
					sharedPrefs.edit().commit();
					HttpTask task = new HttpTask();
					if(MainActivity.htHandler != null) {
						task.setTaskHandler(MainActivity.htHandler);
					}
					
					String qryString = String.format("id=%s&gcmId=%s&title=%s&email=%s",
											mApplication.device.getDeviceId(),
											mApplication.device.getGcmId(),
											mApplication.device.getDeviceTitle(),
											mApplication.device.getAccountMail());
					Log.d("MainActivity", qryString);
					task.execute(HttpTask.POST("/devices?"+qryString));
				} else if(action.equals("com.orbiworks.mobipair.PAIR_REQUEST")){
					String message = intent.getStringExtra("message");
					String contentMessage = "EmailId : ";
					if(message.startsWith("mobipair:pairingrequest:")){
						message = message.substring(24);
						JSONObject jsonMessage = null;
						String pairingmessage = "Pairing request from : ";
						try {
							jsonMessage = new JSONObject(message);
							pairingmessage += jsonMessage.getString("name");
							message = pairingmessage;
							contentMessage += jsonMessage.getString("email");
						} catch (JSONException e) {
							Log.e("pairing.request", e.getMessage());
							e.printStackTrace();
						}
					}
					showNotification(message, contentMessage);
				}
			}
		};
		
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.orbiworks.mobipair.GCM_REG");
		registerReceiver(gcmRegReceiver, filter);
	}
	
	private void populateModel() {
		Account[] accounts = AccountManager.get(this).getAccounts();
		for (Account account : accounts) {

			String possibleEmail = account.name;
			String type = account.type;

			if (type.equals("com.google")) {
				mApplication.device.setAccountMail(possibleEmail);
				mApplication.device.setDeviceTitle(possibleEmail.substring(0, possibleEmail.indexOf("@")));
				Log.i("Account", possibleEmail);
				break;
			}
		}
		
		String gcmId = sharedPrefs.getString("gcmId", "");
		if(sharedPrefs.getBoolean("firstrun", true)==false && gcmId != null && gcmId.length()>0) {
			mApplication.device.setGcmId(gcmId);
		} else {
			registerApp();
		}
	}
	
	public void registerApp() {
		PendingIntent pIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 0, new Intent(), 0);
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
			if(arrJson.length()>0) {
				obj = arrJson.getJSONObject(0);
				mApplication.device.setDeviceToken(obj.getString("dev_token"));
			}
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

	private void showNotification(String subject, String content) {
		final int notificationId = 1;
		
		Intent intentApp = new Intent(getBaseContext(), MainActivity.class);
		intentApp.setAction("Dashboard");
		intentApp.putExtra("action_type", "app_notification");
		intentApp.putExtra("notificationId", notificationId);
		PendingIntent pIntentApp = PendingIntent.getActivity(getApplicationContext(), 0, intentApp, PendingIntent.FLAG_UPDATE_CURRENT);

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
			.setSubText(subject)
			.setTicker(subject)
			.setAutoCancel(true)
			.setSmallIcon(R.drawable.ic_launcher)
			.setContentTitle("New Pairing Request")
			.setContentText(content)
			.setContentIntent(pIntentApp)
			.addAction(R.drawable.ic_accept, "Accept", pIntentAccept)
			.addAction(R.drawable.ic_reject, "Reject", pIntentReject);
			//.setContentIntent(pIntentAccept)
			//.setContentIntent(pIntentReject);

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
