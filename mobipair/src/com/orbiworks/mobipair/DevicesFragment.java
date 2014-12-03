package com.orbiworks.mobipair;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.orbiworks.mobipair.util.HttpTask;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class DevicesFragment extends Fragment
	implements HttpTask.HttpTaskHandler
{
	private ProgressDialog nDialog = null;
	private ListView listView = null;
	private MobiPairApp mApplication = null;
	
	public static DevicesFragment newInstance(int sectionNumber) {
		DevicesFragment fragment = new DevicesFragment();
		Bundle args = new Bundle();
		args.putInt("ARG_SECTION_NUMBER", sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public DevicesFragment() {
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_devices, container, false);
		
		mApplication = (MobiPairApp)getActivity().getApplicationContext();
		
		nDialog = new ProgressDialog(getActivity());
		nDialog.setMessage("Fetching..");
		nDialog.setTitle("Getting data");
		nDialog.setIndeterminate(false);
		nDialog.setCancelable(true);
		
		getPairedDevices();
		
		listView = (ListView) rootView.findViewById(R.id.lst_paired_devices);

		return rootView;
	}
	
	private void getPairedDevices() {
		HttpTask task = new HttpTask();
		task.setTaskHandler(this);
		task.execute(HttpTask.GET("/pairs?id=ffffffff-e87c-77c8-f589-408533712f03"));
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void httpTaskBegin(String tag) {
		nDialog.show();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void httpTaskSuccess(String tag, String json) {
		JSONArray arrJson = null;
		String dbgString = json;
		final List<String> values = new ArrayList<String>();
		if(json != null) {
			try {
				arrJson = new JSONArray(json);
				for(int i=0; i<arrJson.length(); i++) {
					JSONObject obj = arrJson.getJSONObject(i);
					values.add(obj.getString("dev_title") + "," + obj.getString("status"));
				}
			} catch (JSONException e) {
				Log.e("DebugFragment", e.getMessage());
			}
		}
		ArrayAdapter adapter = null;
		
		adapter = new ArrayAdapter(this.getActivity(), android.R.layout.simple_list_item_2, android.R.id.text1, values) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				TextView text1 = (TextView) view.findViewById(android.R.id.text1);
				TextView text2 = (TextView) view.findViewById(android.R.id.text2);
				String data = values.get(position);
				text1.setText(data.substring(0, data.indexOf(",", 0)));
				text2.setText(data.substring(data.indexOf(",", 0)+1));

				return view;
			}
		};
		listView.setAdapter(adapter); 
		Log.i(tag, dbgString);
		nDialog.dismiss();
	}

	@Override
	public void httpTaskFail(String tag) {
		nDialog.dismiss();
	}
}
