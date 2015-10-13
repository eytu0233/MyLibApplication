package edu.ncku.ui.libinfo;

import edu.ncku.R;
import edu.ncku.R.array;
import edu.ncku.R.id;
import edu.ncku.R.layout;
import edu.ncku.util.ListViewInfoAdapter;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class LibInfoListFragment extends Fragment {

	private ListView listview;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_lib_info, container,
				false);
		listview = (ListView) rootView.findViewById(R.id.infoListView);

		String[] lib_info_list = getResources().getStringArray(
				R.array.lib_info_list);
		
		listview.setAdapter(new ListViewInfoAdapter(this
				.getActivity().getApplicationContext(), lib_info_list));
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Fragment fragment = null;
				switch (position) {
				case 0:
					fragment = new LibInfoOpenTimeFragment();
					break;
				default:
					break;
				}

				if (fragment != null) {
					FragmentManager fragmentManager = getActivity()
							.getFragmentManager();
					fragmentManager.beginTransaction().addToBackStack(null)
							.add(R.id.content_frame, fragment).commit();
				}
			}

		});

		return rootView;
	}

}
