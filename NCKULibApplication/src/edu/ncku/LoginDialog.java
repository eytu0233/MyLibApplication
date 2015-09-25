package edu.ncku;

import java.util.HashMap;
import java.util.Map;

import edu.ncku.io.LoginTask;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LoginDialog extends DialogFragment {

	private static final String DEBUG_FLAG = LoginDialog.class.getName();

	private Handler mhandler = new Handler();

	private MainActivity mainActivity;
	private Context context;
	private Button mBtnLogin, mBtnCancel;
	private EditText mEditUsername, mEditPassword;
	private TextView mTxtTip;
	private ProgressBar mPBLogin;

	public LoginDialog(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
		this.context = mainActivity.getApplicationContext();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.fragment_login, null);
		mBtnLogin = (Button) v.findViewById(R.id.btnLogin);
		mBtnCancel = (Button) v.findViewById(R.id.btnCancel);
		mEditUsername = (EditText) v.findViewById(R.id.editTextID);
		mEditPassword = (EditText) v.findViewById(R.id.editTextPassword);
		mTxtTip = (TextView) v.findViewById(R.id.txtTip);
		mPBLogin = (ProgressBar) v.findViewById(R.id.progressBarLogin);

		setEventListenner();

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		return builder.setView(v).create();
	}

	private void setEventListenner() {
		// TODO Auto-generated method stub
		mBtnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mPBLogin.setVisibility(View.VISIBLE);
				mBtnLogin.setVisibility(View.INVISIBLE);

				final String username = mEditUsername.getText().toString(), password = mEditPassword
						.getText().toString();
				ConnectivityManager CM = (ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo info = CM.getActiveNetworkInfo();

				if ((username != null && "".equals(username))
						|| (password != null && "".equals(password))) {
					mTxtTip.setText(R.string.void_account_or_password);
					mPBLogin.setVisibility(View.INVISIBLE);
					mBtnLogin.setVisibility(View.VISIBLE);
					return;
				}

				if (info == null || !info.isConnected()) {
					mTxtTip.setText(R.string.network_disconnected);
					mPBLogin.setVisibility(View.INVISIBLE);
					mBtnLogin.setVisibility(View.VISIBLE);
					return;
				}

				Thread loginThread = new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {

							final Map<String, String> params = new HashMap<String, String>();
							params.put("username", username);
							params.put("password", password);

							LoginTask loginTask = new LoginTask();
							loginTask.executeOnExecutor(
									AsyncTask.THREAD_POOL_EXECUTOR, params);

							final boolean login = loginTask.get();
							Log.d(DEBUG_FLAG, "login : " + login);

							mhandler.post(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									mPBLogin.setVisibility(View.INVISIBLE);
									mBtnLogin.setVisibility(View.VISIBLE);
									if (login) {
										mainActivity
												.changeNavigationLoginList();
										mainActivity.startMessagerService();
									} else {
										mTxtTip.setText(R.string.invalid_account_or_password);
									}
								}

							});

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				});
				loginThread.setDaemon(true);
				loginThread.start();
			}

		});

		mBtnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onStop();
			}

		});
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		Log.d(DEBUG_FLAG, "onStop");
		super.onStop();
	}

}
