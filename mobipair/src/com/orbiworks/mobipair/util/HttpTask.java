package com.orbiworks.mobipair.util;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.util.Log;


public class HttpTask extends AsyncTask<HttpUriRequest, Void, String> {
	private static final String TAG = "HTTP_TASK";
	private static final String HOST = "http://techobyte.com/mobipair";
	private String resource;
	
	private HttpTaskHandler taskHandler;
	
	public static HttpGet GET(String path) {
		String url = HOST + path;
		Log.d("HttpTask-GET", url);
		return new HttpGet(url);
	}
	
	public static HttpPost POST(String path) {
		String url = HOST + path;
		Log.d("HttpTask-POST", url);
		return new HttpPost(url);
	}
	
	public static HttpPut PUT(String path) {
		String url = HOST + path;
		Log.d("HttpTask-PUT", url);
		return new HttpPut(url);
	}

	public void setTaskHandler(HttpTaskHandler taskHandler) {
		this.taskHandler = taskHandler;
	}

	@Override
	//Performed on Background Thread
	protected String doInBackground(HttpUriRequest... params) {
		HttpUriRequest request = params[0];
		HttpClient client = new DefaultHttpClient();
		resource = request.getMethod() + " " + request.getURI().getPath();

		try {
			HttpResponse response = client.execute(request);

			String data = EntityUtils.toString(response.getEntity());
			return data;

		} catch (Exception e) {
			Log.e(TAG, e.toString());
			return null;
		}
	}
	
	@Override
	//Performed on Main/UI Thread
	protected void onPreExecute() {
		super.onPreExecute();
		taskHandler.httpTaskBegin(resource);
	}

	@Override
	//Performed on Main/UI Thread
	protected void onPostExecute(String json) {
		if (json != null) {
			taskHandler.httpTaskSuccess(resource, json);
		} else {
			taskHandler.httpTaskFail(resource);
		}
	}

	public static interface HttpTaskHandler {
		void httpTaskBegin(String tag);
		void httpTaskSuccess(String tag, String json);
		void httpTaskFail(String tag);
	}
}