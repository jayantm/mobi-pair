package com.orbiworks.mobipair;
import com.orbiworks.mobipair.util.HttpTask;

import android.support.v4.app.Fragment;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PairFragment extends Fragment
implements HttpTask.HttpTaskHandler
{
	
	private ProgressDialog nDialog = null;
	private MobiPairApp mApplication = null;

	public PairFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_pair, container, false);
		
		try {
			mApplication = (MobiPairApp)getActivity().getApplicationContext();
			TextView tView = (TextView) rootView.findViewById(R.id.txtDevToken);
			if (tView != null) {
				tView.setText(mApplication.device.getDeviceToken());
			}
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
	public void httpTaskBegin(String tag) {
		nDialog.show();
	}

	@Override
	public void httpTaskSuccess(String tag, String json) {
		nDialog.dismiss();
	}

	@Override
	public void httpTaskFail(String tag) {
		nDialog.dismiss();
	}
}
