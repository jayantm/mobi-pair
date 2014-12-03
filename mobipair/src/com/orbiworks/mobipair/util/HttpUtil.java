package com.orbiworks.mobipair.util;

import java.net.URL;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONObject;

import com.orbiworks.mobipair.util.HttpTask.HttpTaskHandler;

import android.util.Log;

public class HttpUtil {
	JSONObject returnJSON = null;

	public JSONObject get(String url) {
		/*HttpGet httpGet = new HttpGet("http://techobyte.com/mobipair/devices"); 
		HttpTask task = new HttpTask();
		
		returnJSON = null;
		task.setTaskHandler(new HttpTaskHandler() {
			public void taskSuccessful(JSONObject json) {
				returnJSON = json;
			}

			public void taskFailed() {
				Log.e("", "Task Failed");
				returnJSON = null;
			}
		});
		task.execute(httpGet);*/
		return returnJSON;
	}
}
