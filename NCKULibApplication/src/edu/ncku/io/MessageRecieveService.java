package edu.ncku.io;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MessageRecieveService extends Service {
	
	private static final String DEBUG_FLAG = MessageRecieveService.class.getName();

	private NetworkCheckReceiver mNetworkStateReceiver = new NetworkCheckReceiver();

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Log.e(DEBUG_FLAG, "onBind");
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		try {
			Log.e(DEBUG_FLAG, "onStart");
			IntentFilter filter = new IntentFilter();
			filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
			registerReceiver(mNetworkStateReceiver, filter);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		Log.e(DEBUG_FLAG, "onDestroy");
		unregisterReceiver(mNetworkStateReceiver);
		super.onDestroy();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.e(DEBUG_FLAG, "onUnbind");
		return super.onUnbind(intent);
	}

	public class MyBinder extends Binder {
		MessageRecieveService getService() {
			return MessageRecieveService.this;
		}
	}

}
