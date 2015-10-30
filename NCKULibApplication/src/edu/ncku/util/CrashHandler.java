package edu.ncku.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

/**
 * 捕獲android程序崩溃日志<br>
 * UncaughtException處理類,當程序發生Uncaught異常的時候,有該類来接管程序.
 * 
 * @author PMTOAM
 * 
 */
@SuppressLint("SimpleDateFormat")
public class CrashHandler implements UncaughtExceptionHandler {

	public static final String TAG = CrashHandler.class.getCanonicalName();

	// 系统默認的UncaughtException處理類
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	// CrashHandler實例
	private static CrashHandler INSTANCE = new CrashHandler();
	// 程序的Context对象
	private Context mContext;
	// 用来存储設備信息和異常信息
	private Map<String, String> infos = new HashMap<String, String>();

	// 用於格式化日期,作為日志文件名的一部分
	private DateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");

	/**
	 * 保證只有一个實例
	 */
	private CrashHandler() {
	}

	/**
	 * 獲取實例 ，單例模式
	 */
	public static CrashHandler getInstance() {
		return INSTANCE;
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 */
	public void init(Context context) {
		mContext = context;
		// 獲取系统默認的UncaughtException處理器
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		// 設置該CrashHandler為程序的默認處理器
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 當UncaughtException發生時會轉入該函数来處理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			// 如果用户没有處理则讓系统默認的異常處理器来處理
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				Log.e(TAG, "error : ", e);
			}

			// 退出程序
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(1);
		}
	}

	/**
	 * 自定義错误處理,收集错误信息 發送错误报告等操作均在此完成.
	 * 
	 * @param ex
	 * @return true:如果處理了該異常信息;否则返回false.
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}

		// 使用Toast来显示異常信息
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				Toast.makeText(mContext, "很抱歉，程序出现異常。", Toast.LENGTH_LONG)
						.show();
				Looper.loop();
			}
		}.start();

		// 收集設備参数信息
		collectDeviceInfo(mContext);

		// 保存日志文件
		String str = saveCrashInfo2File(ex);
		Log.e(TAG, str);

		return false;
	}

	/**
	 * 收集設備参数信息
	 * 
	 * @param ctx
	 */
	public void collectDeviceInfo(Context ctx) {
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				String versionName = pi.versionName == null ? "null"
						: pi.versionName;
				String versionCode = pi.versionCode + "";
				infos.put("versionName", versionName);
				infos.put("versionCode", versionCode);
			}

		} catch (NameNotFoundException e) {
			Log.e(TAG, "an error occured when collect package info", e);
		}
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				infos.put(field.getName(), field.get(null).toString());
				Log.d(TAG, field.getName() + " : " + field.get(null));
			} catch (Exception e) {
				Log.e(TAG, "an error occured when collect crash info", e);
			}
		}
	}

	/**
	 * 保存错误信息到文件中
	 * 
	 * @param ex
	 * @return 返回文件名称,便於将文件傳送到伺服器
	 */
	private String saveCrashInfo2File(Throwable ex) {

		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : infos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append("[" + key + ", " + value + "]\n");
		}

		sb.append("\n" + getStackTraceString(ex));

		try {
			String time = formatter.format(new Date());

			TelephonyManager mTelephonyMgr = (TelephonyManager) mContext
					.getSystemService(Context.TELEPHONY_SERVICE);
			String imei = mTelephonyMgr.getDeviceId();
			if (TextUtils.isEmpty(imei)) {
				imei = "unknownimei";
			}

			String fileName = "CRS_" + time + "_" + imei + ".txt";

			File sdDir = null;

			if (Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED))
				sdDir = Environment.getExternalStorageDirectory();

			File cacheDir = new File(sdDir + File.separator + "dPhoneLog");
			if (!cacheDir.exists())
				cacheDir.mkdir();

			File filePath = new File(cacheDir + File.separator + fileName);

			FileOutputStream fos = new FileOutputStream(filePath);
			fos.write(sb.toString().getBytes());
			fos.close();

			return fileName;
		} catch (Exception e) {
			Log.e(TAG, "an error occured while writing file...", e);
		}
		return null;
	}

	/**
	 * 獲取捕捉到的異常的字符串
	 */
	public static String getStackTraceString(Throwable tr) {
		if (tr == null) {
			return "";
		}

		Throwable t = tr;
		while (t != null) {
			if (t instanceof UnknownHostException) {
				return "";
			}
			t = t.getCause();
		}

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		tr.printStackTrace(pw);
		return sw.toString();
	}
}