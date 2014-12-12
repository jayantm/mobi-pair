package com.orbiworks.mobipair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.support.v4.app.Fragment;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
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

	public DashboardFragment() {
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
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

		// Adding child data
		listDataHeader.add("Pairing Requests");
		listDataHeader.add("Missed Calls");
		listDataHeader.add("Facebook");
		listDataHeader.add("Whatsapp");
		
		listDataHeaderIcons.add("ic_link");
		listDataHeaderIcons.add("ic_misscall");
		listDataHeaderIcons.add("ic_facebook");
		listDataHeaderIcons.add("ic_whatsapp");

		// Adding child data
		List<String> pairReq = new ArrayList<String>();
		pairReq.add("Request from: Mirang");

		List<String> missCalls = new ArrayList<String>();
		missCalls.add("Call from +918X3X4X8X9X");
		missCalls.add("Call from +919A7A5A4A3A");
		missCalls.add("Call from +9177E3E7E0E1");

		List<String> facebook = new ArrayList<String>();
		facebook.add("mirang shared so-so post");
		facebook.add("mirang liked your post");
		facebook.add("virang liked your post");
		
		List<String> whatsapp = new ArrayList<String>();
		whatsapp.add("mirang:Interactively benchmark high-quality ROI through mission-critical methods of empowerment.");
		whatsapp.add("mirang:Progressively maintain team building scenarios rather than premier e-services.");
		whatsapp.add("virang:Collaboratively incubate process-centric potentialities with viral platforms. Holisticly supply.");
		whatsapp.add("tanmay:Synergistically pontificate web-enabled imperatives vis-a-vis impactful technologies.");
		whatsapp.add("japan:Objectively integrate magnetic collaboration and idea-sharing rather than competitive.");

		listDataChild.put(listDataHeader.get(0), pairReq); // Header, Child data
		listDataChild.put(listDataHeader.get(1), missCalls);
		listDataChild.put(listDataHeader.get(2), facebook);
		listDataChild.put(listDataHeader.get(3), whatsapp);
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
			lblListHeader.setText(headerTitle);
			
			ImageView imgAppIcon = (ImageView) convertView.findViewById(R.id.app_icon);
			String iconName = listDataHeaderIcons.get(groupPosition);
			int id = getResources().getIdentifier(iconName, "drawable", this._context.getPackageName());
			imgAppIcon.setImageResource(id);
			
			TextView lblListCount = (TextView) convertView.findViewById(R.id.noti_count);
			lblListCount.setTypeface(null, Typeface.BOLD);
			lblListCount.setText(String.valueOf(this._listDataChild.get(this._listDataHeader.get(groupPosition)).size()));

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
