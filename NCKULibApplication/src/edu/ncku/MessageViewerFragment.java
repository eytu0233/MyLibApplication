package edu.ncku;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

public class MessageViewerFragment extends Fragment{
	
	private WebView msgContents;
	private TextView msgTitle, msgUnit, msgDate;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_msg_viewer, container,
				false);
		
		msgTitle = (TextView) rootView.findViewById(R.id.txtMsgTitle);
		msgTitle.setText(getArguments().getString("title"));

		msgUnit = (TextView) rootView.findViewById(R.id.txtMsgUnit);
		msgUnit.setText(getArguments().getString("unit"));

		msgDate = (TextView) rootView.findViewById(R.id.txtMsgDate);
		msgDate.setText(getArguments().getString("date"));

		msgContents = (WebView) rootView.findViewById(R.id.webContesViewer);
		msgContents.loadDataWithBaseURL(null,
				getArguments().getString("contents"), "text/html",
				"utf-8", null);
		
		return rootView;
	}
	
}
