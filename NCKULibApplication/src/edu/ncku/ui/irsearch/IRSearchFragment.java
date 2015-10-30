package edu.ncku.ui.irsearch;

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
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class IRSearchFragment extends Fragment {
	
	private static final String URL = "http://m.lib.ncku.edu.tw/catalogs/KeywordSearch.php";
	
	private WebView web;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_personal_borrow, container,
				false);
		
		web = (WebView) rootView.findViewById(R.id.webView);
		web.getSettings().setJavaScriptEnabled(true);
		web.setWebViewClient(new WebViewClient(){

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				 view.loadUrl(url);
			     return true;
			}
			
		});
		web.loadUrl(URL);

		return rootView;
	}

}
