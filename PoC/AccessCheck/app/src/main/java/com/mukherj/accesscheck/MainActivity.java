package com.mukherj.accesscheck;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.TextView;

import java.util.List;


public class MainActivity extends Activity {
    NetworkingThread netThread;
    TextView display;
    String AccServices;
    String LstNotifications;
    public static boolean launched=false;
    public void clearVars(View v){
        NetworkingThread.clear();
    }
    public void clearNotify(View v){
        NetworkingThread.clearNotify();
    }
    public static void changeLaunchedStateTrue(){
        launched=true;
    }
    public static boolean isAccessibilityEnabled(Context context, String id) {

        AccessibilityManager am = (AccessibilityManager) context
                .getSystemService(Context.ACCESSIBILITY_SERVICE);

        List<AccessibilityServiceInfo> runningServices = am
                .getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK);
        for (AccessibilityServiceInfo service : runningServices) {
            if (id.equals(service.getId())) {
                return true;
            }
        }

        return false;
    }

    public static String logInstalledAccessiblityServices(Context context) {
        String str = "";
        AccessibilityManager am = (AccessibilityManager) context
                .getSystemService(Context.ACCESSIBILITY_SERVICE);

        List<AccessibilityServiceInfo> runningServices = am
                .getInstalledAccessibilityServiceList();
        for (AccessibilityServiceInfo service : runningServices) {
            Log.i("accsrv", service.getId());
            str += service.getId().toString() + "\n";
        }
        return str;
    }

    public static String logEnabledAccessiblityServices(Context context) {
        String str = "";
        AccessibilityManager am = (AccessibilityManager) context
                .getSystemService(Context.ACCESSIBILITY_SERVICE);

        List<AccessibilityServiceInfo> runningServices = am
                .getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_SPOKEN);
        for (AccessibilityServiceInfo service : runningServices) {
            Log.i("accsrv", service.getId());
            str += service.getId().toString() + "\n";
        }
        return str;
    }

    @TargetApi(16)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("Reciever","Started");
        if (!launched){
            netThread=new NetworkingThread();
            netThread.start();
            display=new TextView(this);
            display=(TextView)findViewById(R.id.textBox);
            display.setText("Waiting for Notification");
            display.setMovementMethod(new ScrollingMovementMethod());
            AccServices = logInstalledAccessiblityServices(this);
            AccServices += "\n---\n";
            AccServices += logEnabledAccessiblityServices(this);
            display.setText(AccServices);

            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("Notification"));
            //Notify that the app is running
            Log.d("Reciever","Notifying");
            NotificationCompat.Builder mBuilder=new NotificationCompat.Builder(this).setSmallIcon(R.drawable.new_notify).setContentTitle("Running").setContentText("");
            Intent resultIntent =new Intent(this,MainActivity.class);

            TaskStackBuilder stackBuilder=TaskStackBuilder.create(this);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            //resultIntent.setFlags(FLAG_ONGOING_EVENT);
            //PendingIntent notifyIntent=PendingIntent.getActivity(this, 0, notifyIntent, PendingIntent.FLAG_ONGOING_EVENT);
            PendingIntent resultPendingIntent=stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(BIND_ABOVE_CLIENT,mBuilder.build());
            changeLaunchedStateTrue();

        }
    }
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String pkg = intent.getStringExtra("pkg");
            String msg = intent.getStringExtra("msg");
            LstNotifications += action + "- " + pkg + ": " + msg + "\n";
            display.setText(AccServices + "\n###\n" + LstNotifications);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
