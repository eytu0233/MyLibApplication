package edu.ncku.ui;

import edu.ncku.R;
import edu.ncku.R.id;
import edu.ncku.R.layout;
import edu.ncku.io.MessageReaderTask;
import edu.ncku.io.MessageRecieveService;
import edu.ncku.ui.LoadMoreListView.OnLoadMore;
import edu.ncku.util.ListViewAdapter;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.Toast;

public class MessageListFragment extends Fragment implements OnRefreshListener,
		OnLoadMore {
	
	private static final String DEBUG_FLAG = MessageListFragment.class.getName();
	public static final String FINISH_FLUSH_FLAG = "FinishFlushFlag";
	private static int PRELOAD_MSGS_NUM;
	
	private Handler mHandler = new Handler();	
	
	private LoadMoreListView listView;
	private SwipeRefreshLayout swip;
	private MyReceiver receiver;
	private ListViewAdapter listViewAdapter;
	private Context mContext;
	private SharedPreferences sp;
	
	private int numShowedMsgs = 0;
	
	public MessageListFragment(Context context){
		this.mContext = context;
		
		this.sp = PreferenceManager.getDefaultSharedPreferences(context);
		PRELOAD_MSGS_NUM = Integer.valueOf(sp.getString("PRELOAD_MSGS_MAX", "10"));
		
		if(PRELOAD_MSGS_NUM <= 0){
			Log.e(DEBUG_FLAG, "PRELOAD_MSGS_NUM is smaller  than zero");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_msg_list, container,
				false);

		listView = (LoadMoreListView) rootView.findViewById(R.id.listView);
		listView.setLoadMoreListen(this);
		swip = (SwipeRefreshLayout) rootView.findViewById(R.id.swip_index);
		swip.setOnRefreshListener(this);
		swip.setColorSchemeResources(android.R.color.holo_blue_light,
				android.R.color.holo_red_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_green_light);

		/**
		 * register MyReciever
		 */
		receiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.MY_RECEIVER");
		mContext.registerReceiver(receiver, filter);
		
		Log.d(DEBUG_FLAG, "ReaderTask start!");
		updateList();
		return rootView;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mContext.unregisterReceiver(receiver);
	}

	@Override
	public void loadMore() {
		// TODO Auto-generated method stub
		try {
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {

					listViewAdapter.showMoreOldMessaage(Integer.valueOf(sp.getString("LOAD_MSGS_MAX", "10")));
					numShowedMsgs = listViewAdapter	.getNumShowedMsgs();
					Log.v("MessageListActivity", "show : " + numShowedMsgs);

					listView.onLoadComplete();
				}
			}, 2000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		try {
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					onceActiveUpdateMessageData();
				}
			}, 500);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setListInput(final ListAdapter adapter){
		mHandler.post(new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				listView.setAdapter(adapter);
			}			
		});
	}
	
	private void updateList() {
		/* Read data in background and reflesh the listview of this activity */
		try {
			Log.v(DEBUG_FLAG, "want to show : "
					+ (numShowedMsgs + PRELOAD_MSGS_NUM));
			MessageReaderTask msgReaderTask = new MessageReaderTask(this, numShowedMsgs
					+ PRELOAD_MSGS_NUM);
			msgReaderTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			listViewAdapter = msgReaderTask.get();
			if (listViewAdapter != null) {
				numShowedMsgs = listViewAdapter.getNumShowedMsgs();
				Log.v(DEBUG_FLAG, "UpdateList finish : " + numShowedMsgs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * broadcast to update message data once
	 */
	private void onceActiveUpdateMessageData() {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.ONCERCVMSGTASK_RECEIVER");
		mContext.sendBroadcast(intent);
		
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				swip.setRefreshing(false);
			}
		}, 2000);
	}
	
	/**
	 * broadcast receiver
	 * 
	 * @author root
	 */
	private class MyReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				Bundle bundle = intent.getExtras();
				int numMsgs = bundle.getInt("numMsgs");
				if (numMsgs > 0) {
					numShowedMsgs += numMsgs;
					Log.v(DEBUG_FLAG, "Get new messages : " + numMsgs);
					updateList();
				}

				String flag = bundle.getString("flag");
				if (null != flag) {
					if (!FINISH_FLUSH_FLAG.equals(flag)) {
						Toast.makeText(context, flag,
								Toast.LENGTH_SHORT).show();
					}
					swip.setRefreshing(false);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
