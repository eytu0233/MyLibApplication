package edu.ncku.io;

import java.util.Timer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkCheckReceiver extends BroadcastReceiver {

	private static final String DEBUG_FLAG = NetworkCheckReceiver.class
			.getName();

	private static final int SCAN_MINUTES = 1;
	private static final int MINISEC_MIN = 60 * 1000;

	private NetworkInfo currentNetworkInfo;
	private Context mContext;
	private Timer messgeReceiveTimer = new Timer();

	public NetworkCheckReceiver() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public synchronized void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		mContext = context;

		try {

			if (mContext != null) {
				ConnectivityManager connectivityManager = ((ConnectivityManager) mContext
						.getSystemService(Context.CONNECTIVITY_SERVICE));
				currentNetworkInfo = connectivityManager.getActiveNetworkInfo();
			}

			if (currentNetworkInfo != null && currentNetworkInfo.isConnected()) {
				// do something when network connected.
				Log.d(DEBUG_FLAG, "連上網路");

				if (!ReceiveMessageTask.isStartSchedulingTask) {
					ReceiveMessageTask rcvMsgTask = new ReceiveMessageTask(false,
							mContext);
					rcvMsgTask.setStopSchedulingTaskListener(new IStopSchedulingTask(){

						@Override
						public void stopSchedulingTask() {
							// TODO Auto-generated method stub
							messgeReceiveTimer.cancel();
							Log.d(DEBUG_FLAG, "關閉Timer");
						}});
					messgeReceiveTimer.schedule(rcvMsgTask, 0, SCAN_MINUTES * MINISEC_MIN);
					ReceiveMessageTask.isStartSchedulingTask = true;
				}
			}
		} catch (RuntimeException e) {
			Log.e(DEBUG_FLAG, "messgeReceiveTimer schedule fail");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
