package com.orbiworks.mobipair;
import com.orbiworks.mobipair.util.HttpTask;

import android.support.v4.app.Fragment;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PairFragment extends Fragment
implements HttpTask.HttpTaskHandler, OnClickListener
{
	
	private ProgressDialog nDialog = null;
	private MobiPairApp mApplication = null;

	public PairFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_pair, container, false);
		
		nDialog = new ProgressDialog(getActivity());
		nDialog.setMessage("Sending...");
		nDialog.setTitle("Sending pairing request");
		nDialog.setIndeterminate(false);
		nDialog.setCancelable(true);
		
		try {
			mApplication = (MobiPairApp)getActivity().getApplicationContext();
			TextView tView = (TextView) rootView.findViewById(R.id.txtDevToken);
			if (tView != null) {
				tView.setText(mApplication.device.getDeviceToken());
			}
			Button btnRequestPair = (Button) rootView.findViewById(R.id.btnRequestPair);
			btnRequestPair.setOnClickListener(this);
		} catch (Exception ex) {
			Log.e("PairFragment", ex.getMessage());
		}
		return rootView;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnRequestPair:
			//Handle Post request;
			EditText txtPairToken = (EditText) v.getRootView().findViewById(R.id.txtPairToken);
			String tokenPair = txtPairToken.getText().toString();
			HttpTask task = new HttpTask();
			task.setTaskHandler(this);
			String qryStr = String.format("token1=%s&token2=%s", mApplication.device.getDeviceToken(), tokenPair);
			task.execute(HttpTask.POST("/pairs?"+qryStr));
			break;
		}
	}

	@Override
	public void httpTaskBegin(String tag) {
		nDialog.show();
	}

	@Override
	public void httpTaskSuccess(String tag, String json) {
		Log.d(tag, json);
		nDialog.dismiss();
	}

	@Override
	public void httpTaskFail(String tag) {
		nDialog.dismiss();
	}
}
