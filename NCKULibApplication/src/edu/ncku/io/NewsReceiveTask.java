package edu.ncku.io;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import edu.ncku.R;
import edu.ncku.security.Security;
import edu.ncku.ui.MainActivity;
import edu.ncku.util.Message;
import edu.ncku.util.News;

public class NewsReceiveTask extends TimerTask {

	private static final String DEBUG_FLAG = NewsReceiveTask.class.getName();
	private static final String FILE_NAME = "News";
	private static final String URL = "http://m.lib.ncku.edu.tw/news/news_json.php?item=webNews";
	private static final Object LOCKER = new Object();

	private static NetworkInfo currentNetworkInfo;
	public static boolean isStartSchedulingTask = false;

	private boolean isOnce = false;
	private Context mContext;
	private Intent mIntent = new Intent();

	private IStopSchedulingTask mNonNetworkEvent;

	public NewsReceiveTask(boolean isOnce, Context context) {
		this.isOnce = isOnce;
		this.mIntent.setAction("android.intent.action.MY_RECEIVER");
		this.mContext = context;
	}

	public void setStopSchedulingTaskListener(
			IStopSchedulingTask nonNetworkEvent) {
		mNonNetworkEvent = nonNetworkEvent;
	}

	private int synMsgFile(LinkedList<News> newsList) {

		/* Get internal storage directory */
		File dir = mContext.getFilesDir();
		File newsFile = new File(dir, FILE_NAME);

		ObjectInputStream ois = null;
		ObjectOutputStream oos = null;
		LinkedHashSet<News> readNews = null;

		int updateNum = 0;

		synchronized (LOCKER) {
			try {
				// read news data from file
				if (newsFile.exists()) {
					ois = new ObjectInputStream(new FileInputStream(newsFile));
					readNews = (LinkedHashSet<News>) ois.readObject();
				}

				// record the old number of news
				int oldNum = readNews.size();
				// add the news from network to the news set from file
				for(News news : newsList){
					readNews.add(news);
				}

				updateNum = readNews.size() - oldNum;				

				// overwrite the news data to the file
				oos = new ObjectOutputStream(new FileOutputStream(newsFile));
				oos.writeObject(readNews);
				oos.flush();
				if (oos != null)
					oos.close();
				if (ois != null)
					ois.close();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				Log.e(DEBUG_FLAG, "The read object can't be found.");
				readNews = null;
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return updateNum;
	}

	private void receiveNewsFromNetwork() {
		HttpURLConnection urlConnection = null;
		LinkedList<News> news = null;
		
		int numNews = 0;

		try {
			URL url = new URL(URL);
			urlConnection = (HttpURLConnection) url.openConnection();
			BufferedReader streamReader = new BufferedReader(
					new InputStreamReader(urlConnection.getInputStream()));
			StringBuilder responseStrBuilder = new StringBuilder();

			String inputStr;
			while ((inputStr = streamReader.readLine()) != null)
				responseStrBuilder.append(inputStr);

			JSONArray arr = new JSONArray(responseStrBuilder.toString());

			news = new LinkedList<News>();

			for (int i = 0; i < arr.length(); i++) {
				JSONObject json = arr.getJSONObject(i);
				String relatedLink = json.getString("related_url");
				String att_file_1 = json.getString("att_file_1");
				String att_file_1_des = json.getString("att_file_1_des");
				String att_file_2 = json.getString("att_file_2");
				String att_file_2_des = json.getString("att_file_2_des");
				String att_file_3 = json.getString("att_file_3");
				String att_file_3_des = json.getString("att_file_3_des");
				String content = json.getString("news_text");

				if (relatedLink.length() > 0) {
					content += "<tr><td class=\"newslink\"><img src=\"link.png\" height=\"20\" width=\"20\"><a href="
							+ relatedLink
							+ " target=\"_blank\" class=\"ui-link\">相關連結</a></td></tr>";
				}

				if (att_file_1.length() > 0) {
					content += "<tr><td class=\"newsfile\"><img src=\"file.png\" height=\"20\" width=\"20\"><a href="
							+ att_file_1
							+ " target=\"_blank\" class=\"ui-link\">"
							+ ((att_file_1_des.length() > 0) ? att_file_1_des
									: "相關附件1") + "</a></td></tr>";
				}

				if (att_file_2.length() > 0) {
					content += "<tr><td class=\"newsfile\"><img src=\"file.png\" height=\"20\" width=\"20\"><a href="
							+ att_file_2
							+ " target=\"_blank\" class=\"ui-link\">"
							+ ((att_file_2_des.length() > 0) ? att_file_2_des
									: "相關附件2") + "</a></td></tr>";
				}

				if (att_file_3.length() > 0) {
					content += "<tr><td class=\"newsfile\"><img src=\"file.png\" height=\"20\" width=\"20\"><a href="
							+ att_file_3
							+ " target=\"_blank\" class=\"ui-link\">"
							+ ((att_file_3_des.length() > 0) ? att_file_3_des
									: "相關附件3") + "</a></td></tr>";
				}

				news.add(new News(json.getString("news_title"), json
						.getString("publish_dept"),
						json.getInt("publish_time"), content));
			}

			numNews = synMsgFile(news);

		} catch (ConnectException e) {
			// TODO Auto-generated catch block
			Log.e(DEBUG_FLAG, "網頁連線逾時");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			urlConnection.disconnect();
		}

		mIntent.putExtra("numNews", numNews);
		if (isOnce) {
			mIntent.putExtra("flag", "FinishFlushFlag");
		}
		mContext.sendBroadcast(mIntent);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		if (mContext != null) {
			ConnectivityManager connectivityManager = ((ConnectivityManager) mContext
					.getSystemService(Context.CONNECTIVITY_SERVICE));
			currentNetworkInfo = connectivityManager.getActiveNetworkInfo();
		}

		if (currentNetworkInfo != null && currentNetworkInfo.isConnected()) {

			receiveNewsFromNetwork();

		} else {
			if (isStartSchedulingTask && mNonNetworkEvent != null) {
				mNonNetworkEvent.stopSchedulingTask();
				isStartSchedulingTask = false;
			}

			if (isOnce) {
				mIntent.putExtra("flag", "目前網路尚未開啟,無法更新訊息。");
				mContext.sendBroadcast(mIntent);
			}
		}
	}

}
