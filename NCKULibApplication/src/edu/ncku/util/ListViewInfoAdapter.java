package edu.ncku.util;

import edu.ncku.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListViewInfoAdapter extends BaseAdapter{

	private Context context;
	private String[] info_list;
	
	public ListViewInfoAdapter(Context context, String[] info_list) {
		super();
		this.context = context;
		this.info_list = info_list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return info_list.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return info_list[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		 if (convertView == null) {
	            convertView = LayoutInflater.from(context).inflate(R.layout.fragment_lib_info_item, null);
	        }

	        TextView textView = (TextView) convertView.findViewById(R.id.txtTitle);
	        textView.setText(info_list[position]);
	        
	        return convertView;
	}

}
