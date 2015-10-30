package edu.ncku.ui.news;

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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import edu.ncku.R;
import edu.ncku.io.NewsReaderTask;
import edu.ncku.ui.LoadMoreListView;
import edu.ncku.ui.LoadMoreListView.OnLoadMore;
import edu.ncku.util.ListNewsAdapter;
import edu.ncku.util.ListViewAdapter;

public class NewsFragment extends Fragment implements OnRefreshListener,
		OnLoadMore {

	private static final String DEBUG_FLAG = NewsFragment.class.getName();
	public static final String FINISH_FLUSH_FLAG = "FinishFlushFlag";
	private static int PRELOAD_MSGS_NUM;

	private Handler mHandler = new Handler();

	private ProgressBar progressBar;
	private TextView textView;
	private LoadMoreListView listView;
	private SwipeRefreshLayout swip;
	private NewsReceiver receiver;
	private ListNewsAdapter listViewAdapter;
	private Context mContext;
	private SharedPreferences sp;

	private int numShowedMsgs = 0;

	public NewsFragment(Context context) {
		this.mContext = context;

		this.sp = PreferenceManager.getDefaultSharedPreferences(context);
		PRELOAD_MSGS_NUM = Integer.valueOf(sp.getString("PRELOAD_MSGS_MAX",
				"10"));

		if (PRELOAD_MSGS_NUM <= 0) {
			Log.e(DEBUG_FLAG, "PRELOAD_MSGS_NUM is smaller  than zero");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_news_list,
				container, false);

		progressBar = (ProgressBar) rootView.findViewById(R.id.newsTip);
		textView = (TextView) rootView.findViewById(R.id.newsProgressBar);
		listView = (LoadMoreListView) rootView.findViewById(R.id.listView);
		listView.setLoadMoreListen(this);
		swip = (SwipeRefreshLayout) rootView.findViewById(R.id.swip_index);
		swip.setOnRefreshListener(this);
		swip.setColorSchemeResources(android.R.color.holo_blue_light,
				android.R.color.holo_red_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_green_light);

		/**
		 * register NewsReciever
		 */
		receiver = new NewsReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.MY_RECEIVER");
		mContext.registerReceiver(receiver, filter);

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				try {
					Log.d(DEBUG_FLAG, "ReaderTask start!");
					if (updateList()) {
						progressBar.setVisibility(View.INVISIBLE);
					} else {
						progressBar.setVisibility(View.INVISIBLE);
						textView.setVisibility(View.VISIBLE);
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, 1000);

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

					listViewAdapter.showMoreOldMessaage(Integer.valueOf(sp
							.getString("LOAD_MSGS_MAX", "10")));
					numShowedMsgs = listViewAdapter.getNumShowedMsgs();
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

	public void setListAdapter(final ListAdapter adapter) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				listView.setAdapter(adapter);
			}
		});
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

	private boolean updateList() throws Exception {
		/* Read data in background and reflesh the listview of this activity */
		Log.v(DEBUG_FLAG, "want to show : "
				+ (numShowedMsgs + PRELOAD_MSGS_NUM));
		NewsReaderTask msgReaderTask = new NewsReaderTask(this, numShowedMsgs
				+ PRELOAD_MSGS_NUM);
		msgReaderTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		listViewAdapter = msgReaderTask.get();

		if (listViewAdapter != null) {
			numShowedMsgs = listViewAdapter.getNumShowedMsgs();
			Log.v(DEBUG_FLAG, "UpdateList finish : " + numShowedMsgs);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * broadcast receiver
	 * 
	 * @author root
	 */
	private class NewsReceiver extends BroadcastReceiver {
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
						Toast.makeText(context, flag, Toast.LENGTH_SHORT)
								.show();
					}
					swip.setRefreshing(false);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
