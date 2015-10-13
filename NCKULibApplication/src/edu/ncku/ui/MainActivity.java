package edu.ncku.ui;

import edu.ncku.R;
import edu.ncku.R.array;
import edu.ncku.R.drawable;
import edu.ncku.R.id;
import edu.ncku.R.layout;
import edu.ncku.R.menu;
import edu.ncku.R.string;
import edu.ncku.io.MessageRecieveService;
import edu.ncku.ui.libinfo.LibInfoListFragment;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ImageView;

public class MainActivity extends Activity {

	private static final String DEBUG_FLAG = MainActivity.class.getName();

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] mNavigationNormalList;
	private String[] mNavigationLoginList;

	private Fragment mMsgListFragment;
	private Fragment mHomePageFragment;
	private Fragment mSettingFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initUI();

		initComponent();
		
		String parameters = getIntent().getStringExtra("mainActivityParameter");			

		if (parameters == null) {
			selectItem(0);
		}else if("Message Notification".equals(parameters)){
			selectItem((isLogin())?1:0);
		}else{
			Log.e(DEBUG_FLAG, "parameters undefined!");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action buttons
		switch (item.getItemId()) {
		case R.id.action_setting:
			getFragmentManager().beginTransaction().addToBackStack(null)
					.addToBackStack(null).add(R.id.content_frame, mSettingFragment).commit();
			setTitle(R.string.setting);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void startMessagerService() {

		Intent serviceIntent = new Intent(this, MessageRecieveService.class);

		if (startService(serviceIntent) != null) {
			Log.d(DEBUG_FLAG, "MessageRecieveService start!");
		} else {
			Log.e(DEBUG_FLAG, "MessageRecieveService start fail!");
		}
		
	}

	public void stopMessagerService() {

		Intent serviceIntent = new Intent(this, MessageRecieveService.class);

		if (stopService(serviceIntent)) {
			Log.d(DEBUG_FLAG, "MessageRecieveService stop!");
		} else {
			Log.e(DEBUG_FLAG, "MessageRecieveService stop fail!");
		}
	}

	public void changeNavigationNormalList() {
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_list_item, mNavigationNormalList));
		selectItem(0);
	}

	public void changeNavigationLoginList() {
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_list_item, mNavigationLoginList));
		selectItem(0);
	}

	private void initUI() {

		mTitle = mDrawerTitle = getTitle();
		mNavigationNormalList = getResources().getStringArray(
				R.array.Navigation_normal_array);
		mNavigationLoginList = getResources().getStringArray(
				R.array.Navigation_login_array);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_list_item, (isLogin())?mNavigationLoginList:mNavigationNormalList));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		/* Set the padding of the actionBar icon */
		ImageView actionBarIconView = (ImageView) findViewById(android.R.id.home);
		actionBarIconView.setPadding(30, 0, 30, 0);

		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	private void initComponent() {

		mHomePageFragment = new HomePageFragment(this);
		mMsgListFragment = new MessageListFragment(getApplicationContext());
		mSettingFragment = new PrefFragment();
		
	}

	private boolean isLogin() {
		
		final SharedPreferences SP = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		String username = SP.getString("username", ""), password = SP.getString("password",
				"");
		
		Log.d(DEBUG_FLAG, "username : " + username);
		Log.d(DEBUG_FLAG, "password : " + password);

		if ("".equals(username) || "".equals(password)) {
			return false;
		} else {
			return true;
		}

	}
	
	private void logout(){
		final SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		final SharedPreferences.Editor SPE = SP.edit();
		
		SPE.putString("username", "");
		SPE.putString("password", "");
		SPE.apply();
	}
	
	private void clearBackStackFragment() {
		FragmentManager fragmentManager = getFragmentManager();
		// clear the back stack for fragmentManager
		for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
			fragmentManager.popBackStack();
		}
	}
	
	/**
	 * Check the service is alive or not
	 * 
	 * @param serviceClass
	 * @return Alive state of the service
	 */
	private boolean isServiceRunning(Class<?> serviceClass) {
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (serviceClass.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {
		// update the main content by replacing fragments
		String navigationTilte = mDrawerList.getAdapter().getItem(position)
				.toString();
		Fragment fragment = null;

		if (getResources().getString(R.string.home_page)
				.equals(navigationTilte)) {
			fragment = mHomePageFragment;
			setTitle(navigationTilte);
		} else if (getResources().getString(R.string.messager).equals(
				navigationTilte)) {
			fragment = mMsgListFragment;
			setTitle(navigationTilte);
			if(!isServiceRunning(MessageRecieveService.class)){
				startMessagerService();
			}
		} else if (getResources().getString(R.string.login).equals(
				navigationTilte)) {
			(new LoginDialog(this)).show(getFragmentManager(), "Dialog");
			mDrawerLayout.closeDrawer(mDrawerList);
			return;
		} else if (getResources().getString(R.string.logout).equals(
				navigationTilte)) {
			changeNavigationNormalList();
			if(isServiceRunning(MessageRecieveService.class)){
				stopMessagerService();
			}
			logout();
			mDrawerLayout.closeDrawer(mDrawerList);
			return;
		} else {
			Log.e(DEBUG_FLAG, "Select no definition fragment!");
			mDrawerList.setItemChecked(position, true);
			setTitle(mNavigationNormalList[position]);
			mDrawerLayout.closeDrawer(mDrawerList);
			return;
		}

		if (fragment != null) {
			clearBackStackFragment();
			getFragmentManager().beginTransaction()
					.replace(R.id.content_frame, fragment).commit();
		}

		// update selected item and title, then close the drawer
		mDrawerList.setItemChecked(position, true);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	public static class HomePageFragment extends Fragment {

		private Fragment mLibInfoListFragment;
		
		private Activity mMainActivity;
		private ImageView mLibInfoImageView;
		
		public HomePageFragment(Activity mainActivity) {

			mMainActivity = mainActivity;
			mLibInfoListFragment = new LibInfoListFragment();
			
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_homepage,
					container, false);
			mLibInfoImageView = (ImageView) rootView.findViewById(R.id.libInfoImgBtn);
			mLibInfoImageView.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					FragmentManager fragmentManager = getActivity()
							.getFragmentManager();
					fragmentManager.beginTransaction().addToBackStack(null)
							.add(R.id.content_frame, mLibInfoListFragment).commit();
					mMainActivity.setTitle(R.string.homepage_ic_info);
				}
				
			});
			return rootView;
		}

	}

	
}
