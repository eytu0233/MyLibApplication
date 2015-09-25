package edu.ncku.io;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartOnceRcvMsgTaskReciever extends BroadcastReceiver {
	
	public StartOnceRcvMsgTaskReciever() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		try {
			Thread onceRcvMsgTask = new Thread(new ReceiveMessageTask(true,
					context));
			onceRcvMsgTask.setDaemon(true);
			onceRcvMsgTask.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
