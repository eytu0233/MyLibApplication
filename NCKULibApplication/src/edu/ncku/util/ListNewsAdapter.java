package edu.ncku.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import edu.ncku.R;
import edu.ncku.ui.MessageViewerFragment;

public class ListNewsAdapter extends BaseAdapter {

	private static final String DEBUG_TAG = ListNewsAdapter.class.getName();
	
	private Activity activity;
	private Context context;
	private LinkedList<News> AllNews  = new LinkedList<News>(), showNews  = new LinkedList<News>();

	private int show;

	public ListNewsAdapter(Activity activity, LinkedHashSet<News> newsSet,
			int localShow) {
		super();
		this.activity = activity;
		this.context = activity.getApplicationContext();
		this.show = (localShow > newsSet.size()) ? newsSet.size() : localShow;
		
		for(News news : newsSet){
			AllNews.add(news);
		}
		
		Log.d(DEBUG_TAG, "The number of messages : " +  newsSet.size());
		Log.d(DEBUG_TAG, "show : " + show);		
		
		for (int i = 0; i < show; i++) {
			showNews.add(AllNews.get(i));
		}
	}

	private class ViewHolder {
		ImageView imgGroupIcon;
		TextView txtTitle;
		TextView txtDate;
	}

	/**
	 * Update news list which shows on the screen
	 * 
	 * @param moreShow the number of the news which want to show more
	 * @return the number of the news which show more
	 */
	public int showMoreOldMessaage(int moreShow) {
		try {
			int original = this.getCount();
			if (original == AllNews.size())
				return 0;// 當沒有舊的訊息時不再更新

			if (original + moreShow >= AllNews.size()) {
				Log.v(DEBUG_TAG, "全部資料顯示出來");
				for (int i = original; i < AllNews.size(); i++) {
					showNews.addLast(AllNews.get(i));
				}
				this.notifyDataSetChanged();	// 通知更新UI
				Log.v(DEBUG_TAG, "return "
						+ (this.getCount() - original));
				
				return this.getCount() - original;
			}

			Log.v(DEBUG_TAG, "未滿");
			for (int i = original; i < original + moreShow; i++) {
				showNews.addLast(AllNews.get(i));
			}

			this.notifyDataSetChanged();
			Log.v(DEBUG_TAG, "return " + moreShow);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return moreShow;
	}

	public int getNumShowedMsgs() {
		// TODO Auto-generated method stub
		return showNews.size();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return showNews.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return showNews.get(position);
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

					News news = (News) getItem(position);

					Bundle bundle = new Bundle();
					bundle.putString("title", news.getTitle());
					bundle.putString("date", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(news.getTimeStamp()));
					bundle.putString("unit", news.getUnit());
					bundle.putString("contents", news.getContents());
					
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
