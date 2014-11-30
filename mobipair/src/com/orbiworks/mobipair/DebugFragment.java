package com.orbiworks.mobipair;

import android.support.v4.app.Fragment;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DebugFragment extends Fragment {
	
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

		return rootView;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
}
