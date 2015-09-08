package edu.ncku.io;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.ncku.MainActivity;
import edu.ncku.R;
import edu.ncku.security.Security;
import edu.ncku.util.Message;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class NetworkCheckReceiver extends BroadcastReceiver {
	
	private static final String DEBUG_FLAG = NetworkCheckReceiver.class.getName();

	private static final int SCAN_MINUTES = 1;
	private static final int MINISEC_MIN = 60 * 1000;
	private static final String URL = "http://140.116.82.59:8080/MessageSenderSystem/sender.php";
	private static final String ID = "P76034575";
	private static final String PW = "steve123";
	private static final Object LOCKER = new Object();

	private static boolean messageTaskStart = false,
			onceRcvMsgRecieverRegister = false;

	private Context _context;
	private NetworkInfo currentNetworkInfo;
	private ConnectivityManager connectivityManager;

	private Timer messgeReceiveTimer = new Timer();
	private ReceiveMessageTask receiveMessageTask = new ReceiveMessageTask(
			false);
	private startOnceRcvMsgTaskReciever startOnceRcvMsgTaskReciever = new startOnceRcvMsgTaskReciever();

	public NetworkCheckReceiver() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public synchronized void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		_context = context;

		try {
			if (!onceRcvMsgRecieverRegister) {
				Log.d(DEBUG_FLAG,
						"startOnceRcvMsgTaskReciever register!");
				/**
				 * register startOnceRcvMsgTaskReciever
				 */
				IntentFilter filter = new IntentFilter();
				filter.addAction("android.intent.action.ONCERCVMSGTASK_RECEIVER");
				_context.registerReceiver(startOnceRcvMsgTaskReciever, filter);
				onceRcvMsgRecieverRegister = true;
			}

			connectivityManager = ((ConnectivityManager) _context
					.getSystemService(Context.CONNECTIVITY_SERVICE));
			currentNetworkInfo = connectivityManager.getActiveNetworkInfo();
			if (currentNetworkInfo != null && currentNetworkInfo.isConnected()) {
				// do something when network connected.
				Log.d(DEBUG_FLAG, "連上網路");

				if (!messageTaskStart) {
					messgeReceiveTimer.schedule(receiveMessageTask, 0,
							SCAN_MINUTES * MINISEC_MIN);
					messageTaskStart = true;
				}
			}
		} catch (RuntimeException e) {
			Log.e(DEBUG_FLAG, "messgeReceiveTimer schedule fail");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class startOnceRcvMsgTaskReciever extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			try {
				Thread onceRcvMsgTask = new Thread(new ReceiveMessageTask(true));
				onceRcvMsgTask.setDaemon(true);
				onceRcvMsgTask.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	class ReceiveMessageTask extends TimerTask {

		private boolean isOnce = false;
		private Intent intent = new Intent();

		public ReceiveMessageTask(boolean isOnce) {
			this.isOnce = isOnce;
			this.intent.setAction("android.intent.action.MY_RECEIVER");
		}

		private void synMsgFile(LinkedList<Message> msgs) {

			/* Get internal storage directory */
			File dir = _context.getFilesDir();
			File msgData = new File(dir, ID);

			ObjectInputStream ois = null;
			ObjectOutputStream oos = null;
			LinkedList<Message> readMsgs = null;

			synchronized (LOCKER) {
				try {
					if(msgData.exists()){
						ois = new ObjectInputStream(new FileInputStream(msgData));
						readMsgs = (LinkedList<Message>) ois.readObject();
					}

					oos = new ObjectOutputStream(new FileOutputStream(msgData));
					if (readMsgs != null) {
						for (Message msg : readMsgs) {
							msgs.add(msg);
						}
					}
					oos.writeObject(msgs);
					oos.flush();
					if (oos != null)
						oos.close();
					if (ois != null)
						ois.close();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					Log.e(DEBUG_FLAG,
							"The read object can't be found.");
					readMsgs = null;
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}

		private void notificationBroadcast(int num_msgs) {
			/*
			 * when the number of got messages is greater than the number of
			 * past got messages, create a notification and send broadcast to
			 * MainActivity
			 */
			final int mId = 1;
			long[] tVibrate = { 0, 100, 200, 300 };

			try {
				/* Build the content of notification */
				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
						_context).setVibrate(tVibrate).setAutoCancel(true)
						.setSmallIcon(R.drawable.ic_launcher)
						.setContentTitle(String.format("有%d則新訊息", num_msgs));

				/*
				 * Create an intent for notification to start the MainActivity
				 */
				Intent notificationIntent = new Intent(Intent.ACTION_MAIN);
				notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
				notificationIntent.setClass(_context, MainActivity.class);
				notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				notificationIntent.putExtra("menuFragment", "Message Notification");
				
				
				PendingIntent resultPendingIntent = PendingIntent.getActivity(
						_context, PendingIntent.FLAG_CANCEL_CURRENT,
						notificationIntent, 0);

				/* Show the created notification */
				mBuilder.setContentIntent(resultPendingIntent);
				NotificationManager mNotificationManager = (NotificationManager) _context
						.getSystemService(Context.NOTIFICATION_SERVICE);
				mNotificationManager.notify(mId, mBuilder.build());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

			if (currentNetworkInfo != null && currentNetworkInfo.isConnected()) {

				HttpURLConnection urlConnection = null;
				InputStream xmlInputStream = null;
				LinkedList<Message> msgs = null;

				int num_msgs = 0;

				try {
					URL url = new URL(URL);
					urlConnection = (HttpURLConnection) url.openConnection();
					urlConnection.setRequestMethod("POST");

					// Send post request
					urlConnection.setDoOutput(true);
					DataOutputStream wr = new DataOutputStream(
							urlConnection.getOutputStream());

					// Set parameters ID and Password with url encode format
					wr.writeBytes(String.format("ID=%s&Password=%s",
							URLEncoder.encode(Security.encrypt(ID), "utf-8"),
							URLEncoder.encode(Security.encrypt(PW), "utf-8")));
					wr.flush();
					wr.close();

					// Get input stream from http request
					xmlInputStream = urlConnection.getInputStream();

					// Create a new DocumentBuilderFactory
					DocumentBuilderFactory factory = DocumentBuilderFactory
							.newInstance();

					// Use the factory to create a documentbuilder
					DocumentBuilder builder = factory.newDocumentBuilder();

					// Create a new document from input stream
					Document doc = builder.parse(xmlInputStream);

					// Get the first element
					Element element = doc.getDocumentElement();

					// get all child nodes
					NodeList nodes = element.getChildNodes();

					msgs = new LinkedList<Message>();

					// print the text content of each child
					for (int i = 0; i < nodes.getLength(); i += 4) {
						msgs.addFirst(new Message(nodes.item(i)
								.getTextContent(), nodes.item(i + 1)
								.getTextContent(), nodes.item(i + 2)
								.getTextContent(), nodes.item(i + 3)
								.getTextContent()));
						++num_msgs;
					}
					Log.v(DEBUG_FLAG,
							"Get messages from network : " + num_msgs);

					synMsgFile(msgs);

					if (!isOnce)
						notificationBroadcast(num_msgs);

				} catch (ConnectException e) {
					// TODO Auto-generated catch block
					Log.e(DEBUG_FLAG, "網頁連線逾時");
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
					Log.e(DEBUG_FLAG, "XML為空或無法解析XML");
				} catch (Exception e) {
					e.printStackTrace();
				}finally {
					urlConnection.disconnect();
				}

				intent.putExtra("numMsgs", num_msgs);
				if (isOnce) {
					intent.putExtra("flag", "FinishFlushFlag");
				}
				_context.sendBroadcast(intent);
			} else {
				if (messageTaskStart) {
					messgeReceiveTimer.cancel();
					messageTaskStart = false;
				}

				if (isOnce) {
					intent.putExtra("flag", "目前網路尚未開啟,無法更新訊息。");
					_context.sendBroadcast(intent);
				}
			}
		}

	}
}
