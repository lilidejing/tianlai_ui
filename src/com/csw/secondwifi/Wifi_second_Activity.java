package com.csw.secondwifi;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.csw.tianlai_ui.CustomDialog;
import com.csw.tianlai_ui.MainActivity;
import com.csw.tianlai_ui.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;


import com.csw.csw_wifi.NetUtil;
import com.csw.csw_wifi.Wifi_Activity;
import com.csw.secondwifi.*;


import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.rkm.RkmManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Wifi_second_Activity extends Activity {

	private ListView mWifiListView;

	private WifiManager wifiManager = null;
	// private String ssidName;
	// private WifiListAdapter wifiListAdapter;
	public static List<WifiInfo> wfinfoList = new ArrayList<WifiInfo>();
	private String test;
	private WifiAdmin mWifiAdmin;
	// 扫描结果列表
	private List<ScanResult> scanList;

	private WifiListAdapter wifiListAdapter;

	/** 扫描完毕接收器 */
	private WifiReceiver receiverWifi;

	TextView mWifiStadText;
	private Context mContext;

	
	private static String TAG="Wifi_second_Activity";
	
	/**
	 * 当前选的ssid
	 */
	private static String currentSelectSsid="";
	
	/**
	 * 当前输入的密码
	 */
	private static String currentPassword="";
	
	/**
	 * wifi连接状态是否改变状态的flag
	 */
	public static boolean  wifistateFlag=false;
	
	/**
	 * wifi连接是否成功标志,false说明连接失败
	 */
	public static boolean wifiConnectSuccessFlag=true;
	
	
	/**
	 * wifi连接列表第几个要改变状态
	 */
	public static int whichItemChange=1000;
	
	
	
	 /*
     * added  by lgj
     */
    private static   Timer recordTimer=new Timer();
    private  static TimerTask mTimerTask;
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);

		setContentView(R.layout.wifi_second_main);

		mContext = this;
		currentSelectSsid="";
		currentPassword="";
		wifistateFlag=false;
		wifiConnectSuccessFlag=true;
		whichItemChange=1000;
		mWifiListView = (ListView) this.findViewById(R.id.wifi_listview_second);
		mWifiStadText = (TextView) this.findViewById(R.id.wifi_statd_second);

		mHandler.sendEmptyMessage(0);
		wifiManager = (WifiManager) Wifi_second_Activity.this
				.getSystemService(Context.WIFI_SERVICE);
		mWifiAdmin = new WifiAdmin(Wifi_second_Activity.this, test);

		receiverWifi = new WifiReceiver();

		OpenWifi();
		if (wfinfoList != null) {
			wfinfoList.clear();
			wfinfoList=new ArrayList<WifiInfo>();
		}
		mWifiAdmin.startScan();

	}

	/**
	 * 打开WIFI
	 */
	public void OpenWifi() {

		if (!wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(true);
		}

	}

	private static boolean equalsFlag=false;
	
	class WifiReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			 String action = intent.getAction();
			if (action.equals(
					WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {

				
			if(wfinfoList.size()==0){	
				scanList = mWifiAdmin.getWifiList();
				if (scanList != null) {
					for (int i = 0; i < scanList.size(); i++) {
						WifiInfo info = new WifiInfo();
						ScanResult s = scanList.get(i);
						info.setLeved(s.level);
						info.setSsid(s.SSID);
						info.setLeved(s.level);
						info.setSecurityType(s.capabilities);
						System.out.println(s.SSID);
						equalsFlag=false;
						
						if(wfinfoList.size()==0){
							wfinfoList.add(info);
						}else{
							String ssidget=info.getSsid();
							
							for(int j=0;j<wfinfoList.size();j++){
								String wfssid=wfinfoList.get(j).getSsid();
								if(ssidget.equals(wfssid)){
									Log.d(TAG, wfssid+"="+ssidget);
									equalsFlag=true;
									break;
								}
							}
							Log.d(TAG, "keyi");
							if(equalsFlag==false){
								wfinfoList.add(info);
							}
							
						}
						
					}
					
					
				}

				if (wfinfoList.size() == 0) {

					Toast.makeText(Wifi_second_Activity.this, "没有扫描到wifi", 2000)
							.show();

				} else {
					// step1Changed(STATE_COMPLETED);
					mHandler.sendEmptyMessage(1);
					wifiListAdapter = new WifiListAdapter(
							Wifi_second_Activity.this, wfinfoList);
					mWifiListView.setAdapter(wifiListAdapter);
					mWifiListView
							.setOnFocusChangeListener(new OnFocusChangeListener() {

								@Override
								public void onFocusChange(View arg0,
										boolean arg1) {
									// TODO Auto-generated method stub
									if (arg1 == true) {

									}
								}
							});
					mWifiListView
							.setOnItemClickListener(new OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> parent,
										View view, int position, long id) {

									// TODO Auto-generated method stub

									wifiListAdapter.setSelectItem(position);
//									wifiListAdapter.notifyDataSetInvalidated();

									final String ssid = ((TextView) view
											.findViewById(R.id.wifiSsidText))
											.getText().toString();
									System.out.println("当前热点" + ssid);
									String securityType = wfinfoList.get(
											position).getSecurityType();
									final int positiontemp=position;
//									whichItemChange=position;
									
									 ConnectivityManager cm = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
						    	     NetworkInfo networkinfo = cm.getActiveNetworkInfo();
						    	     if(networkinfo!=null&&networkinfo.isConnected()){//如果联网状态
						    	    	 android.net.wifi.WifiInfo mWifiInfo = wifiManager.getConnectionInfo();
											String wifissid = mWifiInfo.getSSID();//当前连接上的wifi名称
											Log.d(TAG, "wifissid=0"+wifissid);
											if(wifissid.equals("\""+ssid+"\"")){
											    return;	
											}	
						    	     }

									if (securityType.contains("SECURITY_NONE")) {
										Wifi_second_Activity.wifistateFlag=false;
										currentSelectSsid=ssid;
										currentPassword="";
										mHandler.sendEmptyMessage(2);
										whichItemChange=position;
										Log.d(TAG, "不需要密码");
									} else {

										final CustomDialog.Builder builder = new CustomDialog.Builder(
												Wifi_second_Activity.this);
//										builder.setMessage(mContext
//												.getResources()
//												.getString(
//														R.string.message_inputpassword_start)
//												+ ssid
//												+ mContext
//														.getResources()
//														.getString(
//																R.string.message_inputpassword_end));
										builder.setTitle(mContext
												.getResources()
												.getString(
														R.string.love_tip_inputpassword));
										
										//动态加载布局生成View对象
								        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
								        View dialogView = layoutInflater.inflate(R.layout.editview, null);
								        
								        final TextView mTextView=(TextView)dialogView.findViewById(R.id.password_message);
								        mTextView.setText(mContext
												.getResources()
												.getString(
														R.string.message_inputpassword_start)
												+ "\""+ssid+"\""
												+ mContext
														.getResources()
														.getString(
																R.string.message_inputpassword_end));
										final EditText mEditText = (EditText) dialogView
												.findViewById(R.id.passwordedit);
//										final EditText mEditText = new EditText(Wifi_second_Activity.this);
										
										builder.setContentView(dialogView);
										builder.setPositiveButton(
												mContext.getResources()
														.getString(
																R.string.confirm_ok),
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int which) {
														Wifi_second_Activity.wifistateFlag=false;
														currentSelectSsid=ssid;
														currentPassword=mEditText.getText().toString();
														whichItemChange=positiontemp;
														mHandler.sendEmptyMessage(2);
														
														if(recordTimer!=null){
												    		recordTimer.cancel();
												    		recordTimer=null;
												    		System.out.println("recordTimer=null;");
												    	}
												    	if(mTimerTask!=null){
												    		mTimerTask.cancel();
												    		mTimerTask=null;
												    		System.out.println("mTimerTask=null;");
												    	}
												    	
												    	 if(recordTimer==null){
												  			recordTimer = new Timer();//注意这里，不然
												  		 }
												          if (mTimerTask == null) {
												  			mTimerTask = new TimerTask() {
												  				@Override
												  				public void run() {
												  					
												  					
												  					if (NetUtil.isWifiConnected(Wifi_second_Activity.this)) {
																		
												  					}else{
												  						wifiConnectSuccessFlag=false;
												  						mHandler.sendEmptyMessage(3);
//																	 wifiListAdapter.notifyDataSetInvalidated();
																	
												  					}
												  				}
												  			};
												  			}
												  		if (recordTimer != null && mTimerTask != null) {
												  			recordTimer.schedule(mTimerTask,12*1000);
												  		}
														
														dialog.dismiss();
													}
												});

										builder.setNegativeButton(
												mContext.getResources()
														.getString(
																R.string.confirm_cancel),
												new android.content.DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int which) {
														whichItemChange=1000;
														dialog.dismiss();
													}
												});
//                                        Dialog mDialog=builder.createWifiDialog();
//										builder.createWifiDialog().show();
                                       
                                        final Dialog mDialog=builder.createWifiDialog();
                                        mDialog.show();
										// 创建一个EditText对象设置为对话框中显示的View对象
										mEditText.setOnEditorActionListener(new OnEditorActionListener() {
											
											@Override
											public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
												// TODO Auto-generated method stub
												
												if(arg1==EditorInfo.IME_ACTION_DONE){
														
													Wifi_second_Activity.wifistateFlag=false;
													currentSelectSsid=ssid;
													currentPassword=mEditText.getText().toString();
													whichItemChange=positiontemp;
													mHandler.sendEmptyMessage(2);
													
													if(recordTimer!=null){
											    		recordTimer.cancel();
											    		recordTimer=null;
											    		System.out.println("recordTimer=null;");
											    	}
											    	if(mTimerTask!=null){
											    		mTimerTask.cancel();
											    		mTimerTask=null;
											    		System.out.println("mTimerTask=null;");
											    	}
											    	
											    	 if(recordTimer==null){
											  			recordTimer = new Timer();//注意这里，不然
											  		 }
											          if (mTimerTask == null) {
											  			mTimerTask = new TimerTask() {
											  				@Override
											  				public void run() {
											  					
											  					
											  					if (NetUtil.isWifiConnected(Wifi_second_Activity.this)) {
																	
											  					}else{
											  						wifiConnectSuccessFlag=false;
											  						mHandler.sendEmptyMessage(3);
//																 wifiListAdapter.notifyDataSetInvalidated();
																
											  					}
											  				}
											  			};
											  			}
											  		if (recordTimer != null && mTimerTask != null) {
											  			recordTimer.schedule(mTimerTask,12*1000);
											  		}
													
//													dialog.dismiss(); 
													
											  		mDialog.dismiss();
												}
												return false;
											}
										});
									}
								}
							});

				}
			}
			}
          if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
	        	 
	    		 ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    	     NetworkInfo networkinfo = cm.getActiveNetworkInfo();
	    	     if(networkinfo!=null&&networkinfo.isConnected()){//如果联网状态
	    	    	 android.net.wifi.WifiInfo mWifiInfo = wifiManager.getConnectionInfo();
						String wifissid = mWifiInfo.getSSID();//当前连接上的wifi名称
						
						Log.d(TAG, "wifissid="+wifissid);
						System.out.println("wifissid:"+wifissid);
						for (int i = 0; i < wfinfoList.size(); i++) {
							if (wifissid.equals("\""+wfinfoList.get(i).getSsid()+"\"")) {
								System.out.println("刷新 ---显示已连接"+wfinfoList.get(i).getSsid());
								whichItemChange=i;
								wifistateFlag=true;
								wifiListAdapter.notifyDataSetInvalidated();
								
							}
						}
						if (wifissid.equals("\""+currentSelectSsid+"\"")) {
							wifistateFlag=true;
							
							wifiListAdapter.notifyDataSetInvalidated();
						}
	    	     }else{//如果断网状态
	    	    	 wifistateFlag=false;

	    	    	 
	    	    	 
	    	     }
	    	     Log.d("检测网络状态广播", "广播开启");
//	    	               return;
	         }

		}
		}
	

	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				mWifiStadText.setVisibility(View.VISIBLE);
				mWifiStadText.setText("正在扫描wifi列表...");
				break;
			case 1:
				mWifiStadText.setVisibility(View.GONE);
				break;
			case 2:
		         new Thread(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						mWifiAdmin.addConnect(currentSelectSsid,currentPassword);
						mHandler.sendEmptyMessage(3);
					}
		        	 
		         }).start();
				
				break;
			case 3:
				wifiListAdapter.notifyDataSetInvalidated();
				break;
			default:
				break;

			}

		};
	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(receiverWifi);// 注销广播
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		IntentFilter mIntentFilter = new IntentFilter();
		
		mIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		mIntentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		mIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		mIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

		registerReceiver(receiverWifi, mIntentFilter);// 注册广播
	}
	/**
	 * 连接wifi广播*******************************************************************************
	 */
	
	
}
