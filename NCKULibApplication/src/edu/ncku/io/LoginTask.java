package edu.ncku.io;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.ncku.security.Security;
import android.os.AsyncTask;
import android.util.Log;

public class LoginTask extends AsyncTask<Map<String, String>, Void, Boolean> {
	
	private static final String DEBUG_FLAG = LoginTask.class.getName();

	private static final String ATHU_URL = "http://reader.lib.ncku.edu.tw/login/index.php";

	@Override
	protected Boolean doInBackground(Map<String, String>... params) {
		// TODO Auto-generated method stub
		Map<String, String> parametersMap;

		boolean result = false;

		if (params.length != 1) {
			return result;
		} else {
			parametersMap = params[0];
		}

		try {
			URL url = new URL(ATHU_URL);
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();
			urlConnection.setRequestMethod("POST");

			// Send post request
			urlConnection.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(
					urlConnection.getOutputStream());
			
			// Set parameters ID and Password with url encode format
			wr.writeBytes(String.format("username=%s&password=%s", URLEncoder
					.encode((String) parametersMap.get("username"), "utf-8"),
					URLEncoder.encode((String) parametersMap.get("password"),
							"utf-8")));

			wr.flush();
			wr.close();

			InputStream input = urlConnection.getInputStream();
			byte[] data = new byte[1024];
			int idx = input.read(data);
			String str = new String(data, 0, idx);
			Log.d(DEBUG_FLAG, "str : " + str);
			if (str.contains("OK")) {
				result = true;
			}
			input.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return result;
	}

}
