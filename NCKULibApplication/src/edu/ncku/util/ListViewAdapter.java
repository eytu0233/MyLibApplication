package edu.ncku.util;

import java.util.LinkedList;

import edu.ncku.MessageViewerFragment;
import edu.ncku.R;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListViewAdapter extends BaseAdapter {

	private static final String DEBUG_TAG = ListViewAdapter.class.getName();
	
	private Activity activity;
	private Context context;
	private LinkedList<Message> messages, showMessages;

	private int show;

	public ListViewAdapter(Activity activity, LinkedList<Message> messages,
			int localShow) {
		super();
		this.activity = activity;
		this.context = activity.getApplicationContext();
		this.messages = messages;
		this.show = (localShow > messages.size()) ? messages.size() : localShow;
		
		Log.d(DEBUG_TAG, "The number of messages : " +  messages.size());
		Log.d(DEBUG_TAG, "show : " + show);		

		showMessages = new LinkedList<Message>();
		for (int i = 0; i < show; i++) {
			showMessages.add(messages.get(i));
		}
	}

	private class ViewHolder {
		ImageView imgGroupIcon;
		TextView txtTitle;
		TextView txtDate;
	}

	public int showMoreOldMessaage(int moreShow) {
		try {
			int original = this.getCount();
			if (original == messages.size())
				return 0;// 當沒有舊的訊息時不再更新

			if (original + moreShow >= messages.size()) {
				Log.v("ListViewAdapter", "滿");
				for (int i = original; i < messages.size(); i++) {
					showMessages.addLast(messages.get(i));
				}
				this.notifyDataSetChanged();
				Log.v("ListViewAdapter", "return "
						+ (this.getCount() - original));
				return this.getCount() - original;
			}

			Log.v("ListViewAdapter", "未滿");
			for (int i = original; i < original + moreShow; i++) {
				showMessages.addLast(messages.get(i));
			}

			this.notifyDataSetChanged();
			Log.v("ListViewAdapter", "return " + moreShow);
		} catch (Exception e) {
//			PrintWriter pw = new PrintWriter(new StringWriter());
//			e.printStackTrace(pw);
//			Log.e("ListViewAdapter", pw.toString());
		}
		return moreShow;
	}

	public int getNumShowedMsgs() {
		// TODO Auto-generated method stub
		return showMessages.size();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return showMessages.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return showMessages.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		ViewHolder holder;
		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

		try {
			convertView = mInflater
					.inflate(R.layout.fragment_msglist_item, null);
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					Message msg = (Message) getItem(position);

					Bundle bundle = new Bundle();
					bundle.putString("title", msg.getTitle());
					bundle.putString("date", msg.getDate());
					bundle.putString("unit", msg.getUnit());
					bundle.putString("contents", msg.getContents());
					
					MessageViewerFragment msgViewerFragment = new MessageViewerFragment();
					msgViewerFragment.setArguments(bundle);
					
					FragmentManager fragmentManager = activity.getFragmentManager();
					fragmentManager.beginTransaction()
							.addToBackStack(null)
							.add(R.id.content_frame, msgViewerFragment).commit();
				}
			});

			holder = new ViewHolder();
			holder.txtTitle = (TextView) convertView
					.findViewById(R.id.txtTitle);
			holder.txtDate = (TextView) convertView.findViewById(R.id.txtDate);
			holder.imgGroupIcon =  (ImageView) convertView
					.findViewById(R.id.imgIcon);

			convertView.setTag(holder);

			Message items = (Message) getItem(position);
			String title = items.getTitle(), date = items.getDate(), unit = items.getUnit();
			
			holder.txtTitle.setText((title!=null)?title:"");
			holder.txtDate.setText((date!=null)?date:"");
			String[] unitStrings = convertView.getResources().getStringArray(R.array.unit_array);
			final int SYSTEM_GROUP = convertView.getResources().getInteger(R.integer.index_drawable_system_group),
					COLLECTION_GROUP = convertView.getResources().getInteger(R.integer.index_drawable_collection_group),
					CHIEF_ROOM = convertView.getResources().getInteger(R.integer.index_drawable_chief_room),
					EDITORIAL_GROUP = convertView.getResources().getInteger(R.integer.index_drawable_editorial_group),
					INFORMATION_GROUP = convertView.getResources().getInteger(R.integer.index_drawable_information_group),
					MEDICAL_BRANCH = convertView.getResources().getInteger(R.integer.index_drawable_medical_branch),
					MULTIMEDIA = convertView.getResources().getInteger(R.integer.index_drawable_multimedia),
					PEDROIDICAL_GROUP = convertView.getResources().getInteger(R.integer.index_drawable_periodical_group),
					READING_ROUP = convertView.getResources().getInteger(R.integer.index_drawable_reading_group);
			int id = 0;
				
			if(unit.equals(unitStrings[SYSTEM_GROUP])){
				id = R.drawable.ic_system_group;
			}else if(unit.equals(unitStrings[COLLECTION_GROUP])){
				id = R.drawable.ic_collection_group;
			}else if(unit.equals(unitStrings[CHIEF_ROOM])){
				id = R.drawable.ic_chief_room;
			}else if(unit.equals(unitStrings[EDITORIAL_GROUP])){
				id = R.drawable.ic_editorial_group;
			}else if(unit.equals(unitStrings[INFORMATION_GROUP])){
				id = R.drawable.ic_information_service;
			}else if(unit.equals(unitStrings[MEDICAL_BRANCH])){
				id = R.drawable.ic_medical_branch;
			}else if(unit.equals(unitStrings[MULTIMEDIA])){
				id = R.drawable.ic_multimedia;
			}else if(unit.equals(unitStrings[PEDROIDICAL_GROUP])){
				id = R.drawable.ic_periodical_group;
			}else if(unit.equals(unitStrings[READING_ROUP])){
				id = R.drawable.ic_reading_group;
			}

			holder.imgGroupIcon.setImageResource(id);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return convertView;
	}

}
