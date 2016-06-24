package com.csw.secondwifi;

import java.util.List;



import android.app.Activity;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiManager.WifiLock;
import android.util.Log;

public class WifiAdmin {
	private String TAG;
	// 定义WifiManager对象
	private WifiManager mWifiManager;
	// 定义WifiInfo对象
	private WifiInfo mWifiInfo;
	// 扫描出的网络连接列表
	private List<ScanResult> mWifiList;
	// 网络连接列表
	private List<WifiConfiguration> mWifiConfiguration;
	// 定义一个WifiLock
	WifiLock mWifiLock;

	private boolean debug = false;// 定义一个debug对象
//	public MainActivity mainactivity;// MainActivity
//	ClientMainActivity clientMainActivity;//ClientMainActivity
	
//	MajorFunctionActivity mfactivity;//MajorFunctionActivity
	
	Context context;
	// 构造器
/*	public WifiAdmin(Context context) {
		this.context = (context);// MainActivity对象
		// 取得WifiManager对象
		mWifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		// 取得WifiInfo对象
		mWifiInfo = mWifiManager.getConnectionInfo();
	}*/
	
	// 构造器
		public WifiAdmin(Context context,String test) {
			this.context = ( context);// MajorFunctionActivity对象
			// 取得WifiManager对象
			mWifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			// 取得WifiInfo对象
			mWifiInfo = mWifiManager.getConnectionInfo();
		}

	public WifiAdmin(WifiManager wifiManager){
		this.mWifiManager=wifiManager;
		// 取得WifiInfo对象
		mWifiInfo = mWifiManager.getConnectionInfo();
	}
    
	public String getSSID(){
		return mWifiInfo.getSSID();
	}
	// 打开WIFI
	public void openWifi() {
		if (!mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(true);
		}
	}

	// 关闭WIFI
	public void closeWifi() {
		if (mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(false);
		}
	}

	// 检查当前WIFI状态
	public int checkState() {
		return mWifiManager.getWifiState();
	}

	// 锁定WifiLock
	public void acquireWifiLock() {
		mWifiLock.acquire();
	}

	// 解锁WifiLock
	public void releaseWifiLock() {
		// 判断时候锁定
		if (mWifiLock.isHeld()) {
			mWifiLock.acquire();
		}
	}

	// 创建一个WifiLock
	public void creatWifiLock() {
		mWifiLock = mWifiManager.createWifiLock("Test");
	}

	// 得到配置好的网络
	public List<WifiConfiguration> getConfiguration() {
		return mWifiConfiguration;
	}

	// 指定配置好的网络进行连接
	public void connectConfiguration(int index) {
		// 索引大于配置好的网络索引返回
		if (index > mWifiConfiguration.size()) {
			return;
		}
		// 连接配置好的指定ID的网络
		mWifiManager.enableNetwork(
				((WifiConfiguration) mWifiConfiguration.get(index)).networkId,
				true);
	}

	/**
	 * 添加一个网络连接
	 * 
	 * @param ssid
	 * @param password
	 * @return
	 */
	public boolean addConnect(String ssid, String password) {
		WifiConfiguration config = new WifiConfiguration();

		
		// 如果以前配置过这个网络，移除
		WifiConfiguration tempConfig = this.IsExsits(ssid);
		if (tempConfig != null) {
			mWifiManager.removeNetwork(tempConfig.networkId);
		}
		if(password.length()==0){
			config.allowedKeyManagement.set(KeyMgmt.NONE);
		}
		
		
		config.SSID = "\"" + ssid + "\"";
		config.preSharedKey = "\"" + password + "\""; // 指定密码
		// config.hiddenSSID = true;
		// config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
		// config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
		// config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
		// config.allowedPairwiseCiphers
		// .set(WifiConfiguration.PairwiseCipher.TKIP);
		// config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
		// config.status = WifiConfiguration.Status.ENABLED;

		int netID = mWifiManager.addNetwork(config);
		Log.d("WifiPreference", "add Network returned " + netID);
		// mWifiManager.updateNetwork(config);
		boolean bRet = mWifiManager.enableNetwork(netID, true);// true创建后连接该点，false创建后不连接该点
		Log.d("WifiPreference", "enableNetwork returned " + bRet);
		mWifiManager.saveConfiguration();
		mWifiManager.reconnect();
		return bRet;
	}

	// 查看以前是否也配置过这个网络
	private WifiConfiguration IsExsits(String SSID) {
		List<WifiConfiguration> existingConfigs = mWifiManager
				.getConfiguredNetworks();
		for (WifiConfiguration existingConfig : existingConfigs) {
			if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
				return existingConfig;
			}
		}
		return null;
	}

	// .....................................................................................

	public int getCurrentNetId() {
		if (this.mWifiInfo == null)
			;
		for (Integer localInteger = null;; localInteger = Integer
				.valueOf(this.mWifiInfo.getNetworkId()))
			return localInteger.intValue();
	}

	public boolean DisableNetWordLick(int paramInt) {
		this.mWifiManager.disableNetwork(paramInt);
		return this.mWifiManager.disconnect();
	}

	public boolean removeNetworkLink(int paramInt) {
		return this.mWifiManager.removeNetwork(paramInt);
	}

	// .................................................................................
	public boolean updateConnect(int networkId) {
		boolean bRet = mWifiManager.enableNetwork(networkId, true);
		Log.d("WifiPreference", "enableNetwork returned " + bRet);
		mWifiManager.saveConfiguration();
		mWifiManager.reconnect();
		return bRet;
	}

	public void startScan() {
//		mWifiManager = (WifiManager) mainactivity
//					.getSystemService(Context.WIFI_SERVICE);
		mWifiManager.startScan();
		if(mWifiList!=null){
			mWifiList.clear();
		}
		
//		// 得到扫描结果
//		mWifiList = mWifiManager.getScanResults();
//		// 得到配置好的网络连接
//		mWifiConfiguration = mWifiManager.getConfiguredNetworks();
	}
   
	// 得到网络列表
	public List<ScanResult> getWifiList() {
		// 得到扫描结果
				mWifiList = mWifiManager.getScanResults();
				// 得到配置好的网络连接
				mWifiConfiguration = mWifiManager.getConfiguredNetworks();
		
		return mWifiList;
	}

	// 查看扫描结果
	public StringBuilder lookUpScan() {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < mWifiList.size(); i++) {
			stringBuilder
					.append("Index_" + new Integer(i + 1).toString() + ":");
			// 将ScanResult信息转换成一个字符串包
			// 其中把包括：BSSID、SSID、capabilities、frequency、level
			stringBuilder.append((mWifiList.get(i)).toString());
			stringBuilder.append("/n");
		}
		return stringBuilder;
	}

	// 得到MAC地址
	public String getMacAddress() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
	}

	// 得到接入点的BSSID
	public String getBSSID() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
	}

	// 得到IP地址
	public int getIPAddress() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
	}

	// 得到连接的ID
	public int getNetworkId() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
	}

	// 得到WifiInfo的所有信息包
	public String getWifiInfo() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
	}

	public SupplicantState getSupplicantState() {
		return (mWifiInfo == null) ? null : mWifiInfo.getSupplicantState();
	}

	// 添加一个网络并连接
	public void addNetwork(WifiConfiguration wcg) {
		int wcgID = mWifiManager.addNetwork(wcg);
		mWifiManager.enableNetwork(wcgID, true);
	}

	// 断开指定ID的网络
	public void disconnectWifi(int netId) {
		mWifiManager.disableNetwork(netId);
		mWifiManager.disconnect();
	}

}
