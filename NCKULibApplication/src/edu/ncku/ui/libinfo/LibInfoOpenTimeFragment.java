package edu.ncku.ui.libinfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.ncku.R;
import edu.ncku.R.array;
import edu.ncku.R.id;
import edu.ncku.R.layout;
import edu.ncku.R.string;
import edu.ncku.util.OpenTimeExpListAdapter;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.TextView;

@SuppressLint("NewApi")
public class LibInfoOpenTimeFragment extends Fragment {

	private String DEBUG_FLAG = LibInfoOpenTimeFragment.class.getName();

	ExpandableListView mOpenTimeExpListView;
	OpenTimeExpListAdapter mOpenTimeExpListAdapter;
	List<String> mListDataHeader; // 標題
	HashMap<String, List<String>> mListDataChild; // 內容

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_info_open_time,
				container, false);

		mOpenTimeExpListView = (ExpandableListView) rootView
				.findViewById(R.id.openTimeExpListView);

		// 列表資料
		prepareListData();

		/* listIv-圖示, listDataHeader-標題, listDataChild-內容 */
		mOpenTimeExpListAdapter = new OpenTimeExpListAdapter(this.getActivity()
				.getApplicationContext(), mListDataHeader, mListDataChild);

		/* 取得螢幕寬度 */
		DisplayMetrics metrics = new DisplayMetrics();  
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int width = metrics.widthPixels;
		
		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
			mOpenTimeExpListView.setIndicatorBounds(width - 100, width);
		} else {
			mOpenTimeExpListView.setIndicatorBoundsRelative(width - 100, width);
		}

		// 將列表資料加入至展開列表單
		mOpenTimeExpListView.setAdapter(mOpenTimeExpListAdapter);
		mOpenTimeExpListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				TextView title = (TextView) v.findViewById(R.id.txtTitle);
				title.setTextColor(Color.rgb(255, 116, 21));
			}

		});
		mOpenTimeExpListView
				.setOnGroupCollapseListener(new OnGroupCollapseListener() {

					@Override
					public void onGroupCollapse(int groupPosition) {
						// TODO Auto-generated method stub

					}

				});

		return rootView;
	}

	private void prepareListData() {
		// TODO Auto-generated method stub
		mListDataHeader = new ArrayList<String>();
		mListDataChild = new HashMap<String, List<String>>();

		for (String openTimeHeader : getResources().getStringArray(
				R.array.lib_open_time_list)) {
			mListDataHeader.add(openTimeHeader);
		}

		ArrayList<String> main_lib = new ArrayList<String>();
		main_lib.add(getString(R.string.open_time_main_lib));
		ArrayList<String> self_studying = new ArrayList<String>();
		self_studying
				.add(getString(R.string.open_time_self_studying_reading_room));
		ArrayList<String> medical_branch = new ArrayList<String>();
		medical_branch.add(getString(R.string.open_time_medical_branch_lib));
		ArrayList<String> departments = new ArrayList<String>();
		departments.add(getString(R.string.open_time_departments));

		mListDataChild.put(mListDataHeader.get(0), main_lib);
		mListDataChild.put(mListDataHeader.get(1), self_studying);
		mListDataChild.put(mListDataHeader.get(2), medical_branch);
		mListDataChild.put(mListDataHeader.get(3), departments);

	}

}
