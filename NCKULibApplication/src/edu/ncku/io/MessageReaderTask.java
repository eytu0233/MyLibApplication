package edu.ncku.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.LinkedList;

import edu.ncku.ui.MessageListFragment;
import edu.ncku.util.ListViewAdapter;
import edu.ncku.util.Message;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class MessageReaderTask extends AsyncTask<Void, Void, ListViewAdapter> {

	private static final String INPUT_FILE_NAME = "P76034575";
	private static final Message NO_DATA_MESSAGE = new Message(
			"目前沒有任何訊息資料,請檢查網路。", "", "", "請稍候重新整理。");

	private MessageListFragment msgListFragment;
	private Context context;
	private ListViewAdapter listViewAdapter;

	private int show;

	public ListViewAdapter getListViewAdpater() {
		return listViewAdapter;
	}

	public MessageReaderTask(MessageListFragment msgListFragment, int show) {
		// TODO Auto-generated constructor stub
		this.msgListFragment = msgListFragment;
		this.context = msgListFragment.getActivity().getApplicationContext();
		this.show = show;
	}

	@Override
	protected ListViewAdapter doInBackground(Void... params) {
		// TODO Auto-generated method stub
		LinkedList<Message> readData = null;
		ObjectInputStream ois = null;
		File inputFile = null;

		try {
			inputFile = new File(context
					.getFilesDir(), INPUT_FILE_NAME);

			if (!inputFile.exists()) {
				Log.d("MessageReaderTask", "file is not exist.");
			} else {
				ois = new ObjectInputStream(new FileInputStream(inputFile));
				readData = ((LinkedList<Message>) ois.readObject());
				Log.v("MessageReaderTask",
						"Read msgs from file : " + readData.size());
				if (ois != null)
					ois.close();
			}

			if (readData == null || readData.size() == 0) {
				readData = new LinkedList<Message>();
				readData.add(NO_DATA_MESSAGE);
				Log.d("MessageReaderTask", "No data message");
			}

			listViewAdapter = new ListViewAdapter(msgListFragment.getActivity(), readData, show);
			msgListFragment.setListInput(listViewAdapter);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return listViewAdapter;
	}
}
