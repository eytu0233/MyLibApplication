package edu.ncku.ui.personalborrow;

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
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class PersonalBorrowFragment extends Fragment {
	
	private WebView webview;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_personal_borrow, container,
				false);
		
		webview = (WebView) rootView.findViewById(R.id.webView);

		return rootView;
	}

}
