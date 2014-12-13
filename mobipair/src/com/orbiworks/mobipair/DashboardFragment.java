package com.orbiworks.mobipair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.orbiworks.mobipair.model.AppNotification;

import android.support.v4.app.Fragment;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

public class DashboardFragment extends Fragment {
	ExpandableListView expListView;
	ExpandableListAdapter listAdapter;
	
	List<String> listDataHeader;
	HashMap<String, List<String>> listDataChild;
	List<String> listDataHeaderIcons;
	
	private MobiPairApp app;
	
	public DashboardFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
		app = (MobiPairApp)getActivity().getApplicationContext();
		
		expListView = (ExpandableListView) rootView.findViewById(R.id.exlst_dashboard);
		prepareListData();
		listAdapter = new ExpandableListAdapter(this.getActivity(), listDataHeader, listDataChild);
		expListView.setAdapter(listAdapter);
		setGroupIndicatorToRight();
		
		return rootView;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	private void setGroupIndicatorToRight() {
		/* Get the screen width */
		DisplayMetrics dm = new DisplayMetrics();
		this.getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;

		expListView.setIndicatorBounds(width - getDipsFromPixel(35), width - getDipsFromPixel(5));
	}
	
	public int getDipsFromPixel(float pixels) {
		// Get the screen's density scale
		final float scale = getResources().getDisplayMetrics().density;
		// Convert the dps to pixels, based on density scale
		return (int) (pixels * scale + 0.5f);
	}
	
	private void prepareListData() {
		listDataHeader = new ArrayList<String>();
		listDataHeaderIcons = new ArrayList<String>();
		listDataChild = new HashMap<String, List<String>>();
		
		HashMap<String,List<AppNotification>> allNotifications = app.getNotifications();
		for(Map.Entry<String, List<AppNotification>> notificationsMap : allNotifications.entrySet()){
			String packageName = notificationsMap.getKey();
			String appName = packageName;
			List<AppNotification> nots = notificationsMap.getValue();
			listDataHeader.add(appName);
			List<String> notificationData = new ArrayList<String>();
			for(int i = 0 ; i < nots.size() ; i++){
				AppNotification data = nots.get(i);
				String displayData = data.getTitle() + "\n" + data.getContent();
				notificationData.add(displayData);
			}
			listDataChild.put(appName, notificationData); // Header, Child data
        }
	}
	public class ExpandableListAdapter extends BaseExpandableListAdapter {

		private Context _context;
		private List<String> _listDataHeader; // header titles
		private HashMap<String, List<String>> _listDataChild;

		public ExpandableListAdapter(Context context, List<String> listDataHeader, HashMap<String, List<String>> listChildData) {
			this._context = context;
			this._listDataHeader = listDataHeader;
			this._listDataChild = listChildData;
		}

		@Override
		public Object getChild(int groupPosition, int childPosititon) {
			return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosititon);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public View getChildView(int groupPosition, final int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {

			final String childText = (String) getChild(groupPosition, childPosition);

			if (convertView == null) {
				LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = infalInflater.inflate(R.layout.dbd_exlist_row_item, null);
			}

			TextView txtListChild = (TextView) convertView.findViewById(R.id.noti_info);

			txtListChild.setText(childText);
			return convertView;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return this._listDataChild.get(
					this._listDataHeader.get(groupPosition)).size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return this._listDataHeader.get(groupPosition);
		}

		@Override
		public int getGroupCount() {
			return this._listDataHeader.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			String headerTitle = (String) getGroup(groupPosition);
			if (convertView == null) {
				LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = infalInflater.inflate(R.layout.dbd_exlist_group_item, null);
			}

			TextView lblListHeader = (TextView) convertView.findViewById(R.id.app_name);
			lblListHeader.setTypeface(null, Typeface.BOLD);
			lblListHeader.setText(app.getAppTitle(headerTitle));
			
			ImageView imgAppIcon = (ImageView) convertView.findViewById(R.id.app_icon);
			try {
				imgAppIcon.setImageDrawable(app.getAppIcon(headerTitle));
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			TextView lblListCount = (TextView) convertView.findViewById(R.id.noti_count);
			lblListCount.setTypeface(null, Typeface.BOLD);
			if(this._listDataChild.get(this._listDataHeader.get(groupPosition)) != null){
				lblListCount.setText(String.valueOf(this._listDataChild.get(this._listDataHeader.get(groupPosition)).size()));
			} else {
				lblListCount.setText("0");
			}
			return convertView;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	}
	
}
