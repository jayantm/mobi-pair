package com.orbiworks.mobipair;

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
import android.widget.TextView;

public class DebugFragment extends Fragment
	implements HttpTask.HttpTaskHandler
{
	private ProgressDialog nDialog = null;
	private TextView tvDbgView = null;
	
	public static DebugFragment newInstance(int sectionNumber) {
		DebugFragment fragment = new DebugFragment();
		Bundle args = new Bundle();
		args.putInt("ARG_SECTION_NUMBER", sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public DebugFragment() {
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_debug, container, false);
		nDialog = new ProgressDialog(getActivity());
		nDialog.setMessage("Fetching..");
		nDialog.setTitle("Getting data");
		nDialog.setIndeterminate(false);
		nDialog.setCancelable(true);
		
		getPairedDevices();
		
		tvDbgView=(TextView) rootView.findViewById(R.id.txt_debug);
		
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

	@Override
	public void httpTaskSuccess(String tag, String json) {
		JSONArray arrJson = null;
		String dbgString = "";
		if(json != null) {
			try {
				arrJson = new JSONArray(json);
				for(int i=0; i<arrJson.length(); i++) {
					JSONObject obj = arrJson.getJSONObject(i);
					dbgString += obj.getString("dev_title");
					dbgString += "\n";
				}
				Log.i("DebugFragment", String.valueOf(arrJson.length()));
			} catch (JSONException e) {
				Log.e("DebugFragment", e.getMessage());
			}
		}
		dbgString += "\n";
		dbgString += json;
		tvDbgView.setText(dbgString);
		Log.i(tag, dbgString);
		nDialog.dismiss();
	}

	@Override
	public void httpTaskFail(String tag) {
		nDialog.dismiss();
	}
}
