package com.csw.csw_wifi;

import static android.net.wifi.WifiConfiguration.INVALID_NETWORK_ID;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.security.Credentials;
import android.security.KeyStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.internal.util.AsyncChannel;
import com.csw.tianlai_ui.R;

public class Wifi_Activity extends Activity implements View.OnClickListener,
		DialogInterface.OnClickListener {

	private final String TAG = "Wifi_Activity";
	List<ScanResult> mWFlist = null;
	private WifiManager mWifiManager;

	private WifiManager.ActionListener mConnectListener;
	private WifiManager.ActionListener mSaveListener;
	private WifiManager.ActionListener mForgetListener;
	// private TextView text_tepy;
	private TextView wifi_statd;
	private ListView mListView;
	private LinearLayout eth_linear;
	private ImageView wifi_connect;
	private TextImageAdapter mTextImageAdapter;
	private final Scanner mScanner;
	private DetailedState mLastState;
	private WifiInfo mLastInfo;
	private WifiDialog mDialog;
	private WifiInfo mWifiInfo;
	private ArrayList<AccessPoint> accessPoints;
	private IntentFilter mWSIntentFilter;
	private final BroadcastReceiver mWifiReceiver;

	private static final int WIFI_DIALOG_ID = 1;
	private boolean mInXlSetupWizard;
	private boolean mDlgEdit;
	private AccessPoint mDlgAccessPoint;
	private Bundle mAccessPointSavedState;
	private AccessPoint mSelectedAccessPoint;
	private int mKeyStoreNetworkId = -1;
	private int security = 0;
	private static final String SAVE_DIALOG_EDIT_MODE = "edit_mode";
	private static final String SAVE_DIALOG_ACCESS_POINT_STATE = "wifi_ap_state";
	// Combo scans can take 5-6s to complete - set to 10s.
	private static final int WIFI_RESCAN_INTERVAL_MS = 10 * 1000;
	private static final int[] STATE_NONE = {};
	private static final int[] STATE_SECURED = { R.attr.state_encrypted };

	private AtomicBoolean mConnected = new AtomicBoolean(false);

	static final int SECURITY_NONE = 0;
	static final int SECURITY_WEP = 1;
	static final int SECURITY_PSK = 2;
	static final int SECURITY_EAP = 3;

	private Context mcontext;

	// added 11.4
	/*
	 * private TextView netTypeText; private String netType; private TextView
	 * netStatusText; private String netStatus; private TextView netIpText;
	 * private String netIp; private TextView netMacText; private String netMac;
	 */

	private IntentFilter mConnectIntentFilter;
	private  BroadcastReceiver mWifiConnectReceiver;
	
	public Wifi_Activity() {
		accessPoints = new ArrayList();

		mWSIntentFilter = new IntentFilter();

		mWSIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		mWSIntentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		mWSIntentFilter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
		mWSIntentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
		mWSIntentFilter
				.addAction(WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION);
		mWSIntentFilter
				.addAction(WifiManager.LINK_CONFIGURATION_CHANGED_ACTION);
		mWSIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		mWSIntentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
		// mWSIntentFilter.addAction(WifiManager.ERROR_ACTION);

		// added 11.4
		mWSIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

		mWifiReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				handleEvent(context, intent);
			}
		};
		mScanner = new Scanner();

		mConnectIntentFilter=new IntentFilter();
		mConnectIntentFilter.addAction("dianhuichela_connect");
		mWifiConnectReceiver=new BroadcastReceiver(){

			@Override
			public void onReceive(Context arg0, Intent arg1) {
				// TODO Auto-generated method stub
				
				if(arg1.getAction().equals("dianhuichela_connect")){
					Log.d(TAG, "收到广播，连接wifi，dianhuichela_connect");
					submit(mDialog.getController());
					mDialog.dismiss();
				}
			}
			
		};
	}

	private static String currentConnectSsid = "";

	@Override
	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.wifi_main);
		// netStatusText=(TextView)this.findViewById(R.id.netStatusContent);
		// netTypeText=(TextView)this.findViewById(R.id.netTypeContent);
		// netIpText=(TextView)this.findViewById(R.id.netIpContent);
		// netMacText=(TextView)this.findViewById(R.id.netMacContent);
		wifi_statd = ((TextView) findViewById(R.id.wifi_statd));
		wifi_statd.setVisibility(8);
		mListView = ((ListView) findViewById(R.id.wifi_listview));

		eth_linear = ((LinearLayout) findViewById(R.id.eth_linear));
		eth_linear.setOnClickListener(this);
		eth_linear.setFocusable(true);

		wifi_connect = ((ImageView) findViewById(R.id.eth_connect));

		if (paramBundle != null) {
			mDlgEdit = paramBundle.getBoolean("edit_mode");
			mAccessPointSavedState = paramBundle.getBundle("wifi_ap_state");
		}

		mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		switchFlag=true;
		if (NetUtil.isWifiConnected(Wifi_Activity.this)) {
			WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
			currentConnectSsid = mWifiInfo.getSSID();
		} else {
			currentConnectSsid = "";
		}

		// mWifiManager.asyncConnect(this, new WifiServiceHandler());
		mConnectListener = new WifiManager.ActionListener() {
			public void onSuccess() {
			}

			public void onFailure(int reason) {
				Activity activity = Wifi_Activity.this;
				if (activity != null) {
					Toast.makeText(activity,
							R.string.wifi_failed_connect_message,
							Toast.LENGTH_SHORT).show();
				}
			}
		};

		mSaveListener = new WifiManager.ActionListener() {
			public void onSuccess() {
			}

			public void onFailure(int reason) {
				Activity activity = Wifi_Activity.this;
				if (activity != null) {
					Toast.makeText(activity, R.string.wifi_failed_save_message,
							Toast.LENGTH_SHORT).show();
				}
			}
		};

		mForgetListener = new WifiManager.ActionListener() {
			public void onSuccess() {
			}

			public void onFailure(int reason) {
				Activity activity = Wifi_Activity.this;
				if (activity != null) {
					Toast.makeText(activity,
							R.string.wifi_failed_forget_message,
							Toast.LENGTH_SHORT).show();
				}
			}
		};

		mWFlist = mWifiManager.getScanResults();
		mTextImageAdapter = new TextImageAdapter(this);
		mListView.setAdapter(mTextImageAdapter);
		mListView.setOnItemClickListener(new mOnItemClickListener());

		if (mWifiManager.isWifiEnabled()) {
			wifi_connect.setImageDrawable(getResources().getDrawable(
					R.drawable.eth_connect));
		} else {
			wifi_connect.setImageDrawable(getResources().getDrawable(
					R.drawable.eth_unconnect));
		}

	}

	/** Returns sorted list of access points */
	private List<AccessPoint> constructAccessPoints() {
		// ArrayList<AccessPoint> accessPoints = new ArrayList<AccessPoint>();
		accessPoints.clear();
		/**
		 * Lookup table to more quickly update AccessPoints by only considering
		 * objects with the correct SSID. Maps SSID -> List of AccessPoints with
		 * the given SSID.
		 */
		Multimap<String, AccessPoint> apMap = new Multimap<String, AccessPoint>();

		final List<WifiConfiguration> configs = mWifiManager
				.getConfiguredNetworks();
		if (configs != null) {
			for (WifiConfiguration config : configs) {
				AccessPoint accessPoint = new AccessPoint(Wifi_Activity.this,
						config);
				accessPoint.update(mLastInfo, mLastState);
				accessPoints.add(accessPoint);
				apMap.put(accessPoint.ssid, accessPoint);
			}
		}

		final List<ScanResult> results = mWifiManager.getScanResults();
		if (results != null) {
			for (ScanResult result : results) {
				// Ignore hidden and ad-hoc networks.
				if (result.SSID == null || result.SSID.length() == 0
						|| result.capabilities.contains("[IBSS]")) {
					continue;
				}

				boolean found = false;
				for (AccessPoint accessPoint : apMap.getAll(result.SSID)) {
					if (accessPoint.update(result))
						found = true;
				}
				if (!found) {
					AccessPoint accessPoint = new AccessPoint(
							Wifi_Activity.this, result);
					accessPoints.add(accessPoint);
					apMap.put(accessPoint.ssid, accessPoint);
				}
			}
		}

		// Pre-sort accessPoints to speed preference insertion
		Collections.sort(accessPoints);
		
		mTextImageAdapter.notifyDataSetChanged();
		return accessPoints;
	}

	/** A restricted multimap for use in constructAccessPoints */
	private class Multimap<K, V> {
		private HashMap<K, List<V>> store = new HashMap<K, List<V>>();

		/** retrieve a non-null list of values with key K */
		List<V> getAll(K key) {
			List<V> values = store.get(key);
			return values != null ? values : Collections.<V> emptyList();
		}

		void put(K key, V val) {
			List<V> curVals = store.get(key);
			if (curVals == null) {
				curVals = new ArrayList<V>(3);
				store.put(key, curVals);
			}
			curVals.add(val);
		}
	}

	private class Scanner extends Handler {
		private int mRetry = 0;

		void resume() {
			if (!hasMessages(0)) {
				sendEmptyMessage(0);
			}
		}

		void forceScan() {
			removeMessages(0);
			sendEmptyMessage(0);
		}

		void pause() {
			mRetry = 0;
			removeMessages(0);
		}

		@Override
		public void handleMessage(Message message) {
			// if (mWifiManager.startScanActive()) {
			if (mWifiManager.startScan()) {
				mRetry = 0;
			} else if (++mRetry >= 3) {
				mRetry = 0;
				Toast.makeText(Wifi_Activity.this, R.string.wifi_fail_to_scan,
						Toast.LENGTH_LONG).show();
				return;
			}
			sendEmptyMessageDelayed(0, WIFI_RESCAN_INTERVAL_MS);
		}
	}

	private class WifiServiceHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case AsyncChannel.CMD_CHANNEL_HALF_CONNECTED:
				if (msg.arg1 == AsyncChannel.STATUS_SUCCESSFUL) {
					// AsyncChannel in msg.obj
				} else {
					// AsyncChannel set up failure, ignore
					Log.e(TAG, "Failed to establish AsyncChannel connection");
				}
				break;
			case WifiManager.WPS_COMPLETED:
				WpsResult result = (WpsResult) msg.obj;
				if (result == null)
					break;
				AlertDialog.Builder dialog = new AlertDialog.Builder(
						Wifi_Activity.this).setTitle(
						R.string.wifi_wps_setup_title).setPositiveButton(
						android.R.string.ok, null);
				switch (result.status) {
				case FAILURE:
					dialog.setMessage(R.string.wifi_wps_failed);
					dialog.show();
					break;
				case IN_PROGRESS:
					dialog.setMessage(R.string.wifi_wps_in_progress);
					dialog.show();
					break;
				default:
					if (result.pin != null) {
						dialog.setMessage(getResources().getString(
								R.string.wifi_wps_pin_output, result.pin));
						dialog.show();
					}
					break;
				}
				break;
			// TODO: more connectivity feedback
			default:
				// Ignore
				break;
			}
		}
	}

	/**
	 * Shows the latest access points available with supplimental information
	 * like the strength of network and the security for it.
	 */
	private void updateAccessPoints() {
		final int wifiState = mWifiManager.getWifiState();
		
		switch (wifiState) {
		case WifiManager.WIFI_STATE_ENABLED:
			
			constructAccessPoints();
			mListView.setVisibility(0);
			wifi_statd.setVisibility(8);
			break;

		case WifiManager.WIFI_STATE_ENABLING:

			
			mListView.setVisibility(8);
			wifi_statd.setVisibility(0);
			wifi_statd.setText(R.string.wifi_starting);

			break;

		case WifiManager.WIFI_STATE_DISABLING:
			mListView.setVisibility(8);
			wifi_statd.setVisibility(0);
			wifi_statd.setText(R.string.wifi_stopping);
			break;

		case WifiManager.WIFI_STATE_DISABLED:
			mListView.setVisibility(8);
			wifi_statd.setVisibility(0);
			wifi_statd.setText(R.string.wifi_empty_list_wifi_off);
			wifi_connect.setImageDrawable(getResources().getDrawable(
					R.drawable.eth_unconnect));
			break;
		}
	}

	private void updateWifiState(int state) {

		switch (state) {
		case WifiManager.WIFI_STATE_ENABLED:
			mScanner.resume();
			mListView.setVisibility(0);
			wifi_statd.setVisibility(8);
			wifi_connect.setImageDrawable(getResources().getDrawable(
					R.drawable.eth_connect));
			return; // not break, to avoid the call to pause() below

		case WifiManager.WIFI_STATE_ENABLING:
			mListView.setVisibility(8);
			wifi_statd.setVisibility(0);
			wifi_statd.setText(R.string.wifi_starting);

			break;

		case WifiManager.WIFI_STATE_DISABLED:
			mListView.setVisibility(8);
			wifi_statd.setVisibility(0);
			wifi_statd.setText(R.string.wifi_empty_list_wifi_off);
			wifi_connect.setImageDrawable(getResources().getDrawable(
					R.drawable.eth_unconnect));
			break;
		}
		mScanner.pause();
		mLastInfo = null;
		mLastState = null;
	}

	private void updateConnectionState(DetailedState state) {
		/* sticky broadcasts can call this when wifi is disabled */
		if (!mWifiManager.isWifiEnabled()) {
			mScanner.pause();
			return;
		}

		if (state == DetailedState.OBTAINING_IPADDR) {
			mScanner.pause();
			return;
		} else {
			mScanner.resume();
		}

		mLastInfo = mWifiManager.getConnectionInfo();

		if (state != null) {
			mLastState = state;
		}

		
		mTextImageAdapter.notifyDataSetChanged();
	}

	private void handleEvent(Context context, Intent intent) {
		String action = intent.getAction();
		Log.d("Wifi_Activity", "action=" + action);
		if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
			updateWifiState(intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
					WifiManager.WIFI_STATE_UNKNOWN));

		} else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)
				|| WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION
						.equals(action)
				|| WifiManager.LINK_CONFIGURATION_CHANGED_ACTION.equals(action)) {

			
			updateAccessPoints();
			// Toast.makeText(Wifi_Activity.this, "wifi连接中", 2000).show();
		} else if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {
			if (!mConnected.get()) {
				updateConnectionState(WifiInfo
						.getDetailedStateOf((SupplicantState) intent
								.getParcelableExtra(WifiManager.EXTRA_NEW_STATE)));
			}
		} else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {

			NetworkInfo info = (NetworkInfo) intent
					.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			mConnected.set(info.isConnected());
			
			updateAccessPoints();
			updateConnectionState(info.getDetailedState());
		} else if (WifiManager.RSSI_CHANGED_ACTION.equals(action)) {
			updateConnectionState(null);
		} /*
		 * else if (WifiManager.ERROR_ACTION.equals(action)) { int errorCode =
		 * intent.getIntExtra(WifiManager.EXTRA_ERROR_CODE, 0); switch
		 * (errorCode) { case WifiManager.WPS_OVERLAP_ERROR:
		 * Toast.makeText(MainActivity.this, R.string.wifi_wps_overlap_error,
		 * Toast.LENGTH_SHORT).show(); break; } }
		 */
		else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
			connectivityManager = (ConnectivityManager)

			getSystemService(Context.CONNECTIVITY_SERVICE);
			networkInfo = connectivityManager.getActiveNetworkInfo();
			/*
			 * if(networkInfo != null && networkInfo.isAvailable()) { String
			 * name = networkInfo.getTypeName(); Log.d("mark", "当前网络名称：" +
			 * name);
			 * 
			 * NetworkInfo netInfo =
			 * connectivityManager.getNetworkInfo(ConnectivityManager
			 * .TYPE_ETHERNET); if(netInfo.isConnected()){ Log.d("mark", "有线");
			 * UiHander.sendEmptyMessage(ETHNETWORK); }else{ Log.d("mark",
			 * "无线"); UiHander.sendEmptyMessage(WIIFNETWORK); //
			 * UiHander.sendEmptyMessage(ETHNETWORK); }
			 * 
			 * } else { Log.d("mark", "没有可用网络");
			 * UiHander.sendEmptyMessage(NONETWORK); }
			 */
		}
	}

	// added 11.4
	private ConnectivityManager connectivityManager;
	private NetworkInfo networkInfo;

	private static boolean switchFlag = true;

	private class TextImageAdapter extends BaseAdapter {
		LayoutInflater inflater;

		public TextImageAdapter(Context context) {
			// TODO Auto-generated constructor stub
			inflater = LayoutInflater.from(context);
			mcontext = context;
		}

		public int getCount() {
			return accessPoints.size();
		}

		public Object getItem(int paramInt) {
			return paramInt;
		}

		public long getItemId(int paramInt) {
			return paramInt;
		}

		public View getView(int paramInt, View paramView,
				ViewGroup paramViewGroup) {
			View view = inflater.inflate(R.layout.wifi_listview_addapte, null);

			AccessPoint accessPoint = accessPoints.get(paramInt);
			/*
			 * String currentssid=accessPoint.getTitle().toString();
			 * 
			 * if(currentssid.equals(currentConnectSsid)){ accessPoint }
			 */
			WifiConfiguration config = accessPoint.getConfig();

			// Log.d(TAG,"getView +++++++++name:"+accessPoint.ssid);
			// wifi name
			TextView nameTextView = (TextView) view
					.findViewById(R.id.wifi_point_name);
			nameTextView.setText(accessPoint.ssid);

			// wifi signal
			ImageView signal = (ImageView) view.findViewById(R.id.wifi_image);
			signal.setImageLevel(accessPoint.getLevel());
			signal.setImageResource(R.drawable.ic_wifi_signal);// 本来是ic_wifi_signal
			// Log.d(TAG,"getView +++++++++000");
			// signal.setImageState(STATE_SECURED,true);
			// Log.d(TAG,"getView +++++++++1111");
			signal.setImageState(
					(accessPoint.security != SECURITY_NONE) ? STATE_SECURED
							: STATE_NONE, true);

			// ========================================
			// wifi wsp
			TextView wpsTextView = (TextView) view
					.findViewById(R.id.wifi_point_wps);

			mWifiInfo = mWifiManager.getConnectionInfo();

			if (accessPoint.getState() != null) { // This is the active
													// connection
				wpsTextView.setText(Summary.get(mcontext,
						accessPoint.getState()));
				
			} else if (accessPoint.getLevel() == Integer.MAX_VALUE) { // Wifi
																		// out
																		// of
																		// range
				wpsTextView.setText(mcontext
						.getString(R.string.wifi_not_in_range));
				signal.setImageDrawable(null);
			} else if (config != null
					&& config.status == WifiConfiguration.Status.DISABLED) {
				switch (config.disableReason) {
				case WifiConfiguration.DISABLED_AUTH_FAILURE:
					wpsTextView
							.setText(mcontext
									.getString(R.string.wifi_disabled_password_failure));
					break;
				case WifiConfiguration.DISABLED_DHCP_FAILURE:
				case WifiConfiguration.DISABLED_DNS_FAILURE:
					wpsTextView.setText(mcontext
							.getString(R.string.wifi_disabled_network_failure));
					break;
				case WifiConfiguration.DISABLED_UNKNOWN_REASON:
					wpsTextView.setText(mcontext
							.getString(R.string.wifi_disabled_generic));
				}
			} else { // In range, not disabled.
				StringBuilder summary = new StringBuilder();
				if (config != null) { // Is saved network
					Log.d(TAG, mcontext.getString(R.string.wifi_remembered));
					summary.append(mcontext.getString(R.string.wifi_remembered));
				}

				if (accessPoint.security != SECURITY_NONE) {
					String securityStrFormat;
					if (summary.length() == 0) {
						securityStrFormat = mcontext
								.getString(R.string.wifi_secured_first_item);
					} else {
						securityStrFormat = mcontext
								.getString(R.string.wifi_secured_second_item);
					}
					summary.append(String.format(securityStrFormat,
							accessPoint.getSecurityString(true)));
				}

				if (config == null && accessPoint.wpsAvailable) { // Only list
																	// WPS
																	// available
																	// for
																	// unsaved
																	// networks
					if (summary.length() == 0) {
						summary.append(mcontext
								.getString(R.string.wifi_wps_available_first_item));
					} else {
						summary.append(mcontext
								.getString(R.string.wifi_wps_available_second_item));
					}
				}

				wpsTextView.setText(summary.toString());

//				Log.d(TAG, "switchFlag="+switchFlag);
				if (switchFlag ==true) {

					if (NetUtil.isWifiConnected(Wifi_Activity.this)) {
						WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
						String wifissid = mWifiInfo.getSSID();
						if (wifissid.equals(currentConnectSsid)) {
							wpsTextView.setText(Wifi_Activity.this.getResources().getString(R.string.hasbeenconnected));
							switchFlag = false;
						}

					}
				}
//				Log.d(TAG, summary.toString());
			}
			// ========================================

			return view;
		}
	}

	public void onClick(View paramView) {
		switch (paramView.getId()) {
		case R.id.eth_linear:
			final int wifiState = mWifiManager.getWifiState();
			int wifiApState = mWifiManager.getWifiApState();

			if (WifiManager.WIFI_STATE_ENABLED == wifiState) {
				mWifiManager.setWifiEnabled(false);
				wifi_connect.setImageDrawable(getResources().getDrawable(
						R.drawable.eth_unconnect));
			} else {
				mWifiManager.setWifiEnabled(true);
				wifi_connect.setImageDrawable(getResources().getDrawable(
						R.drawable.eth_connect));

				if (wifiApState == WifiManager.WIFI_AP_STATE_ENABLED) {
					mWifiManager.setWifiApEnabled(null, false);// if wifi
																// enable,disable
																// Apwifi
				}
			}
			break;
		default:
			break;
		}
	}

	private class mOnItemClickListener implements
			AdapterView.OnItemClickListener {
		private mOnItemClickListener() {
		}

		public void onItemClick(AdapterView paramAdapterView, View paramView,
				int paramInt, long paramLong) {
			mSelectedAccessPoint = accessPoints.get(paramInt);

			/** Bypass dialog for unsecured, unsaved networks */
			if (mSelectedAccessPoint.security == AccessPoint.SECURITY_NONE
					&& mSelectedAccessPoint.networkId == INVALID_NETWORK_ID) {
				mSelectedAccessPoint.generateOpenNetworkConfig();
				mWifiManager.connect(mSelectedAccessPoint.getConfig(),
						mConnectListener);
			} else {
				showDialog(mSelectedAccessPoint, false);
				// Toast.makeText(MainActivity.this, "hehheheh",
				// Toast.LENGTH_SHORT).show();
			}

			return;
		}
	}

	private void showDialog(AccessPoint accessPoint, boolean edit) {
		if (mDialog != null) {
			removeDialog(WIFI_DIALOG_ID);
			mDialog = null;
		}
		mDlgAccessPoint = accessPoint;
		mDlgEdit = edit;
		showDialog(WIFI_DIALOG_ID);
	}

	@Override
	public Dialog onCreateDialog(int dialogId) {
		AccessPoint ap = mDlgAccessPoint;
		if (ap == null) {
			if (mAccessPointSavedState != null) {
				ap = new AccessPoint(this, mAccessPointSavedState);
				mDlgAccessPoint = ap;
			}
		}
		mSelectedAccessPoint = ap;
		mDialog = new WifiDialog(this, this, ap, mDlgEdit);
		return mDialog;
	}

	public void onClick(DialogInterface dialogInterface, int button) {

		if (button == WifiDialog.BUTTON_FORGET && mSelectedAccessPoint != null) {
			forget();
		} else if (button == WifiDialog.BUTTON_SUBMIT) {
			submit(mDialog.getController());
		}
	}

	void submit(WifiConfigController configController) {

		final WifiConfiguration config = configController.getConfig();

		if (config == null) {
			if (mSelectedAccessPoint != null
			/* && !requireKeyStore(mSelectedAccessPoint.getConfig()) */
			&& mSelectedAccessPoint.networkId != INVALID_NETWORK_ID) {
				mWifiManager.connect(mSelectedAccessPoint.networkId,
						mConnectListener);
			}
		} else if (config.networkId != INVALID_NETWORK_ID) {
			if (mSelectedAccessPoint != null) {
				mWifiManager.save(config, mSaveListener);
			}
		} else {
			if (configController.isEdit() /*|| requireKeyStore(config)*/ ) {
				mWifiManager.save(config, mSaveListener);
			} else {
				mWifiManager.connect(config, mConnectListener);
			}
		}

		if (mWifiManager.isWifiEnabled()) {
			mScanner.resume();
		}
		
		updateAccessPoints();
	}

	void forget() {
		mWifiManager.forget(mSelectedAccessPoint.networkId, mSaveListener);

		if (mWifiManager.isWifiEnabled()) {
			mScanner.resume();
		}
		
		updateAccessPoints();

		// We need to rename/replace "Next" button in wifi setup context.
		// changeNextButtonState(false);
	}

	/*
	 * private boolean requireKeyStore(WifiConfiguration config) { if
	 * (WifiConfigController.requireKeyStore(config) &&
	 * KeyStore.getInstance().state() != KeyStore.State.UNLOCKED) {
	 * mKeyStoreNetworkId = config.networkId;
	 * Credentials.getInstance().unlock(MainActivity.this); return true; }
	 * return false; }
	 */
	@Override
	public void onResume() {
		super.onResume();
		registerReceiver(mWifiReceiver, mWSIntentFilter);

		registerReceiver(mWifiConnectReceiver, mConnectIntentFilter);
		
		if (mWifiManager.isWifiEnabled()) {
			wifi_connect.setImageDrawable(getResources().getDrawable(
					R.drawable.eth_connect));
		} else {
			wifi_connect.setImageDrawable(getResources().getDrawable(
					R.drawable.eth_unconnect));
		}

		if (mKeyStoreNetworkId != INVALID_NETWORK_ID
				&& KeyStore.getInstance().state() == KeyStore.State.UNLOCKED) {
			mWifiManager.connect(mKeyStoreNetworkId, mSaveListener);
		}
		mKeyStoreNetworkId = INVALID_NETWORK_ID;

		
		updateAccessPoints();
	}

	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(mWifiReceiver);
		unregisterReceiver(mWifiConnectReceiver);
		mScanner.pause();
	}
	/*
	 * private final int ETHNETWORK=0;//有线网络 private final int
	 * WIIFNETWORK=1;//无线网络 private final int NONETWORK=2;//没有网络
	 *//**
	 * update UI
	 */
	/*
	 * private Handler UiHander=new Handler(){
	 * 
	 * @Override public void handleMessage(Message msg) { // TODO Auto-generated
	 * method stub super.handleMessage(msg); switch(msg.what){ case ETHNETWORK:
	 * 
	 * NetUtil netUtil = new NetUtil(Wifi_Activity.this); netType=(String)
	 * Wifi_Activity.this.getResources().getText(R.string.eth_net_work);
	 * netStatus=(String)
	 * Wifi_Activity.this.getResources().getText(R.string.net_work_online); try
	 * { netIp=netUtil.getIPAddressLine(true, Wifi_Activity.this);
	 * netMac=NetUtil.getLocalfinalMacAddress(); } catch (Exception e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); } initConnectionInfo();
	 * 
	 * break; case WIIFNETWORK:
	 * 
	 * netType=(String)
	 * Wifi_Activity.this.getResources().getText(R.string.wifi_net_work);
	 * netStatus=(String)
	 * Wifi_Activity.this.getResources().getText(R.string.net_work_online); try
	 * { netIp=NetUtil.getIPAddressWifi(true, Wifi_Activity.this);
	 * netMac=NetUtil.getLocalWuxianMacAddress(Wifi_Activity.this); } catch
	 * (Exception e) { // TODO Auto-generated catch block e.printStackTrace(); }
	 * initConnectionInfo();
	 * 
	 * break; case NONETWORK:
	 * 
	 * netType=(String)
	 * Wifi_Activity.this.getResources().getText(R.string.no_net_work);
	 * netStatus=(String)
	 * Wifi_Activity.this.getResources().getText(R.string.no_net_work_please);
	 * try { netIp="";
	 * netMac=NetUtil.getLocalWuxianMacAddress(Wifi_Activity.this);
	 * if(netMac==null||netMac.equals("")){
	 * netMac=NetUtil.getLocalfinalMacAddress(); } } catch (Exception e) { //
	 * TODO Auto-generated catch block e.printStackTrace(); }
	 * initConnectionInfo();
	 * 
	 * break; }
	 * 
	 * }
	 * 
	 * };
	 *//**
	 * 初始化网络信息
	 */
	/*
	 * private void initConnectionInfo(){
	 * 
	 * netStatusText.setText(""); netTypeText.setText("");
	 * netIpText.setText(""); netMacText.setText("");
	 * netStatusText.setText(netStatus); netTypeText.setText(netType);
	 * netIpText.setText(netIp); netMacText.setText(netMac); }
	 */
}
