package com.orbiworks.mobipair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.service.notification.StatusBarNotification;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.widget.DrawerLayout;

public class MainActivity extends ActionBarActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks {
	class MyNotificationReceiver extends BroadcastReceiver{

		@SuppressLint("NewApi")
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			try
			{
			Log.i("MAIN", "Notification Receiver received : ");
			String registrationId = arg1.getStringExtra("applicationid");
			if(registrationId != null && !registrationId.isEmpty()){
				Toast.makeText(arg0, "in regid : ", Toast.LENGTH_SHORT).show();
				//store it in preferences
				SharedPreferences sf = getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
				SharedPreferences.Editor sfEdit = sf.edit();
				sfEdit.putString("regId", registrationId);
				sfEdit.commit();
			}
			else
			{
				SharedPreferences sharedPrefs = getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
				registrationId = sharedPrefs.getString("regId", "");
			}
				String notiMessage = arg1.getStringExtra("notimessage");
				arrayList.add(0,notiMessage);
				adapter.notifyDataSetChanged();
				if( notiMessage != null && !notiMessage.isEmpty()){
					Intent intent = new Intent();
					
					PendingIntent pIntent = PendingIntent.getActivity(arg0, 0, intent, 0);
					
					Notification n = new Notification(R.drawable.ic_launcher, "Mobi Pair", System.currentTimeMillis());
					n.setLatestEventInfo(getApplicationContext(), "Mobi Pair Notifications", notiMessage, pIntent);
					NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
					notificationManager.notify(0, n);
				}
			}
			catch(Exception error){
				Toast.makeText(arg0, error.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}
	}
	private MyNotificationReceiver nReceiver;
	private ListView listView;
	private ArrayList<String> arrayList;
	private ArrayAdapter<String> adapter;
	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();
		listView = (ListView) findViewById(R.id.notoficationsList);
		arrayList = new ArrayList<String>();
        arrayList.add("--Messages--");
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);
		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
		String regId = GetRegistrationID();
		nReceiver = new MyNotificationReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.orbiworks.mobipair.NOTIFICATION_LISTENER_EXAMPLE");
		registerReceiver(nReceiver, filter);
	}
	@SuppressLint("NewApi")
	private String GetRegistrationID()
	{
		SharedPreferences sharedPrefs = getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
		String regId = sharedPrefs.getString("regId", "");
		if(regId.isEmpty()){
			//register for the first time
			registerApp(findViewById(android.R.id.content));
		}
		return regId;
	}
	@SuppressLint("NewApi")
	public void TestNotify(View view){
		arrayList.add(0,"test");
		adapter.notifyDataSetChanged();
		Intent intent = new Intent(this, MainActivity.class);
		
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
		
		Notification n = new Notification(R.drawable.ic_launcher, "ticket text", System.currentTimeMillis());
		n.setLatestEventInfo(getApplicationContext(), "MobiPair notifications", "Sample notification", pIntent);
		
		
		/*Notification.Builder notiBuilder = new Notification.Builder(this);
		notiBuilder.setTicker("MobiPair")
		.setContentTitle("MobiPair Notifications")
		.setContentText("Sample notification")
		.setSmallIcon(R.drawable.ic_launcher)
		.setContentIntent(pIntent);
		Notification notification = notiBuilder.build();*/
		NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		notificationManager.notify(0, n);
	}
	public void registerApp(View view){
		Intent registrationIntent = new Intent("com.google.android.c2dm.intent.REGISTER");
		registrationIntent.putExtra("app",PendingIntent.getBroadcast(view.getContext(), 0, new Intent(), 0));
		registrationIntent.putExtra("sender","644434484811");
		try
		{
		startService(registrationIntent);
		}catch(Exception e){
			Log.e("REC", e.getMessage());
		}
	}
	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager
				.beginTransaction()
				.replace(R.id.container,
						PlaceholderFragment.newInstance(position + 1)).commit();
	}
	public void PostRequest(View view){
		new Connection().execute();
	}
	public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			mTitle = getString(R.string.title_section1);
			break;
		case 2:
			mTitle = getString(R.string.title_section2);
			break;
		case 3:
			mTitle = getString(R.string.title_section3);
			break;
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
	private class Connection extends AsyncTask<String, Void, String> {
		 
        @Override
        protected String doInBackground(String... arg0) {
            try {
				return connect();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            return null;
        }
        @Override
        protected void onPostExecute(String result){
        	//TextView tv = (TextView)findViewById(R.id.txtMessage);
	        //tv.setText(result);
        	arrayList.add(0, "FROM Server Resp :" + result);
			adapter.notifyDataSetChanged();
        }
        private String connect() throws JSONException
    	{
    		HttpClient httpclient = new DefaultHttpClient();
    	    HttpPost httppost = new HttpPost("http://techobyte.com/mobipair/notify");
    	    HttpResponse responseBody = null;
    	    try {
    	        // Add your data
    	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
    	        SharedPreferences sharedPrefs = getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
    	        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
    			String regId = sharedPrefs.getString("regId", "");
    	        nameValuePairs.add(new BasicNameValuePair("id", regId));
    	        nameValuePairs.add(new BasicNameValuePair("msg", currentDateTimeString));
    	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

    	        // Execute HTTP Post Request
    	        responseBody = httpclient.execute(httppost);
    	        BufferedReader reader = new BufferedReader(new InputStreamReader(responseBody.getEntity().getContent(), "UTF-8"));
    	        return reader.readLine();
    	        //JSONTokener tokener = new JSONTokener(json);
    	        //JSONArray finalResult = new JSONArray(tokener);
    	        //parent.runOnUiThread Toast.makeText(getApplicationContext(), json, Toast.LENGTH_SHORT);
    	        /*runOnUiThread(new Runnable(){
    	        	@Override
    	        	public void run(){
    	        		TextView tv = (TextView)findViewById(R.id.tView);
    	    	        tv.setText(json);
    	        	}
    	        });*/
    	        
    	    } catch (ClientProtocolException e) {
    	        // TODO Auto-generated catch block
    	    } catch (IOException e) {
    	        // TODO Auto-generated catch block
    	    }
            return "";
    	}
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

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(
					ARG_SECTION_NUMBER));
		}
	}

}
