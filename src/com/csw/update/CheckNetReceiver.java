package com.csw.update;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.csw.tianlai_ui.MainActivity;
import com.csw.util.NetCheck;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;




import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;

/*
 * 检测网络状态广播
 */
public class CheckNetReceiver extends BroadcastReceiver {
	
	private Context context;
	
	
	private static String update_type="2v9pm2eio8cr";
	
	private static String update_key="uz2o4p6yt0j2h";
	
	private static String TAG="CheckNetReceiver";
	
	public static Context UpdateContext=null;//用于更新的context
	/**
	 * 天籁K歌APP的包名
	 */
	private static final String tlkgappPackageName="com.audiocn.kalaok.tv";
	/**
	 * 主界面UI的包名
	 */
	private static final String uiappPackageName="com.csw.tianlai_ui";
	
	
	 //added 10.28
		SharedPreferences locVersionSharedPreferences;
		SharedPreferences.Editor editor; 
	
		private static String system_Flag="0";
		private static String uiapp_Flag="0";
		private static String tlkgapp_Flag="0";
		
		
		private static String system_url="";
		private static String tlkgapp_url="";
		private static String uiapp_url="";
		
		private static  String updateFlag="0";
	
		/**
		 * 下载的文件名
		 */
		private static String downFileName="download.apk";
		
		
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		 String action = intent.getAction();
		 if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
			System.out.println("开机...");
			
		   }
         if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
        	 this.context=context;
    		 ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
    	     NetworkInfo networkinfo = cm.getActiveNetworkInfo();
    	     if(networkinfo!=null&&networkinfo.isConnected()){//如果联网状态	
    	      checkNetHandler.sendEmptyMessage(CONNECT);
    	     }else{//如果断网状态
    	    	 checkNetHandler.sendEmptyMessage(BREAK);
    	     }
    	     Log.d("检测网络状态广播", "广播开启");
//    	               return;
         }else{
        	// return;
         }
		
		
	}
    
	
	
	private final int CONNECT=1;
	private final int BREAK=2;
	private Handler checkNetHandler=new Handler(){

		@SuppressLint("NewApi")
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what){
			case 1:		
				Log.d("检测到网络打开", "检测到网络打开");


				StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()   // or .detectAll() for all detectable problems
                .penaltyLog()
                .build());
                StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());
				
                
                
                
                
                
                if(MainActivity.updateContext!=null){
                /*	locVersionSharedPreferences=context.getSharedPreferences("updateVersion", 
        					Activity.MODE_PRIVATE); 
        			editor = locVersionSharedPreferences.edit(); 
        		
        		 String systemVersion=GetVersionUtil.getSystemVersion(MainActivity.updateContext);//系统版本号
        		 String uiappVersion=GetVersionUtil.getVersion(MainActivity.updateContext, uiappPackageName);//UI的版本号
        		 String tlkgappVersion=GetVersionUtil.getVersion(MainActivity.updateContext, tlkgappPackageName);//天籁K歌的版本号
        		 
        		 Log.d(TAG, "systemVersion="+systemVersion+"   uiappVersion="+uiappVersion+"  tlkgappVersion"+tlkgappVersion);
        		 
        		 editor.putString("systemVersion", systemVersion);
        		 editor.putString("uiappVersion", uiappVersion);
        		 editor.putString("tlkgappVersion", tlkgappVersion);
        		 editor.commit();
        		 */
                 downFileName="download.apk";
                 updateFlag="0";
              	 update();
                }else{
              	  Log.d("ReceiveDataService.updateContext", "这个对象是空");
                }
				
                 
                
                
                
	    	     
	    	     
	    	     
				break;
			case 2:
				
			
				break;
			default:
					break;
			}
			super.handleMessage(msg);
		}
		
	};
	
	
	/********************************************************************/
	
	/* 下载保存路径 */
	private String mSavePath;
	String url_res;//版本信息
//	String url_res_updateInfo;//更新信息
	URL update_url;//apk升级路径
	
    
	
	
	
	public void update() {
		// wjz 没有网络，就直接�?��
		if (!NetCheck.checkNetWorkStatus(MainActivity.updateContext))
			return;
		
//		String ver = getAppVersionName(ReceiveDataService.updateContext);
		
		PackageManager pm = MainActivity.updateContext.getPackageManager();
		PackageInfo pi = null;
		try {
			pi = pm.getPackageInfo(MainActivity.updateContext.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String versionName = pi.versionName;
		
		System.out.println("版本号："+versionName);
		
		String sign=MD5Util.MD5(update_key);

		try {
			// 获取网站上面的信�?如果没有对应的，就干�? http://oauth.audiocn.org/tian/oauth/versionUpgrade.json
			url_res = sendGet(
					"http://test.audiocn.org/tian/oauth/versionUpgrade.json","type="+update_type+"&sign="+sign);
		
			
			Log.d(TAG,"http://test.audiocn.org/tian/oauth/versionUpgrade.json?type="+update_type+"&sign="+sign);
            Log.d(TAG, url_res);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		
		JSONTokener jsonParser = new JSONTokener(url_res);    
	    // 此时还未读取任何json文本，直接读取就是一个JSONObject对象。    
	    // 如果此时的读取位置在"name" : 了，那么nextValue就是"yuanzhifei89"（String）    
	    JSONObject object1 = null;
		try {
			object1 = (JSONObject) jsonParser.nextValue();
		} catch (JSONException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}    
		
		if(object1!=null){
	    // 接下来的就是JSON对象的操作了    
	    try {
			String time=object1.getString("time");
			String info=object1.getString("info");
			String result=object1.getString("result");

			if(result.equals("0")){
				Log.d(TAG, "请求服务器出错");
				return;
			}
	
			JSONTokener jsonParser2 = new JSONTokener(info);    
			JSONObject object2 = (JSONObject) jsonParser2.nextValue();
			if(object2==null){
				return;
			}
			String uiappJson=object2.getString("uiapp");
			String tlkgappJson=object2.getString("tlkgapp");
			String systemJson=object2.getString("system");
				
			JSONTokener jsonParserSystem = new JSONTokener(systemJson);    
			JSONObject objectSystem = (JSONObject) jsonParserSystem.nextValue();
			if(objectSystem==null){
				return;
			}
			String systemVersion=objectSystem.getString("version").trim();
			String cusystempv=systemVersion.replace(".", "");
			int systemVer=Integer.parseInt(cusystempv);//获取的系统版本号
			Log.d(TAG, "服务器固件版本systemVersion="+systemVersion);
			String systemUrl=objectSystem.getString("url");//获取固件下载地址
			String systemFlag=objectSystem.getString("flag");
			
			
			
			
			String currentSystemVersion=GetVersionUtil.getSystemVersion(MainActivity.updateContext);//当前系统版本号
			String cusystemp=currentSystemVersion.replace(".", "");
			int cusystemVer=Integer.parseInt(cusystemp);//系统当前的版本号
			Log.d(TAG, "服务器固件版本currentSystemVersion="+currentSystemVersion);
//			if(systemVer<cusystemVer){
			if(1>2){
				Log.d(TAG, "固件需要升级");
				system_Flag=systemFlag;
				system_url=systemUrl;
				
				try {
					downFileName="download.apk";
					updateFlag=system_Flag;
					update_url = new URL(system_url);
					
					if(updateFlag.equals("0")){
						return;
					}
					
					downloadApk();//下载文件		
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
				
				
				
			}else{
				Log.d(TAG, "固件不需要升级，检查APP是否需要升级");
				
				JSONTokener jsonParserTlkgapp = new JSONTokener(tlkgappJson);    
				JSONObject objectTlkgapp = (JSONObject) jsonParserTlkgapp.nextValue();
				
				if(objectTlkgapp==null){
					return;
				}
				
				String tlkgappVersion=objectTlkgapp.getString("version").trim();
				Log.d(TAG, "天籁K歌tlkgappVersion="+tlkgappVersion);
				String cutlkgappv=tlkgappVersion.replace(".", "");
				int tlkgappVer=Integer.parseInt(cutlkgappv);//获取的天籁K歌版本号
				String tlkgappUrl=objectTlkgapp.getString("url");//获取天籁K歌下载地址
				String tlkgappFlag=objectTlkgapp.getString("flag");
				
				
				String currentTlkgappVersion=GetVersionUtil.getVersion(MainActivity.updateContext, tlkgappPackageName);//天籁K歌的当前版本号
				
				Log.d(TAG, "天籁K歌currentTlkgappVersion="+currentTlkgappVersion);
				int cuTlkgappVer;
				if(currentTlkgappVersion.equals("")){
					cuTlkgappVer=0;
				}else{
					String cusysTlkgapptemp=currentTlkgappVersion.replace(".", "");
					cuTlkgappVer=Integer.parseInt(cusysTlkgapptemp);//天籁K歌当前的版本号
				}

				if(tlkgappVer>cuTlkgappVer){
//				if(1>2){
					Log.d(TAG, "天籁K歌软件需要升级");
					tlkgapp_Flag=tlkgappFlag;
					tlkgapp_url=tlkgappUrl;
					
					try {
						downFileName="download.apk";
						updateFlag=tlkgapp_Flag;
						update_url = new URL(tlkgapp_url);
						Log.d(TAG, "updateFlag="+updateFlag);
						if(updateFlag.equals("0")){
							return;
						}
						downloadApk();//下载文件		
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
					
				}else {
					Log.d(TAG, "天籁K歌软件不需要升级,检查UI是否需要升级");
					
					
					update_url=null;
					JSONTokener jsonParserUiapp = new JSONTokener(uiappJson);    
					JSONObject objectUiapp = (JSONObject) jsonParserUiapp.nextValue();
					
					if(objectUiapp==null){
						return;
					}
					
					
					String uiappVersion=objectUiapp.getString("version").trim();
					Log.d(TAG, "UIuiappVersion="+uiappVersion);
					String cuuiappv=uiappVersion.replace(".", "");
					
					int uiappVer=Integer.parseInt(cuuiappv);//获取UI版本号
					String uiappUrl=objectUiapp.getString("url");//获取UI下载地址
					String uiappFlag=objectUiapp.getString("flag");
					
					
					String currentUiappVersion=GetVersionUtil.getVersion(MainActivity.updateContext, uiappPackageName);//天籁K歌的当前版本号
					Log.d(TAG, "UI currentUiappVersion="+currentUiappVersion);
					String cuUiapptemp=currentUiappVersion.replace(".", "");
					int cuUiappVer=Integer.parseInt(cuUiapptemp);//天籁K歌当前的版本号
					
					if(uiappVer>cuUiappVer){
						Log.d(TAG, "UI需要升级");
						uiapp_Flag=uiappFlag;
						uiapp_url=uiappUrl;
						System.out.println("uiapp下载地址"+uiapp_url);
						
						try {
							downFileName="download.apk";
							updateFlag=uiapp_Flag;
							update_url = new URL(uiapp_url);
							
							if(updateFlag.equals("0")){
								return;
							}
							
							downloadApk();//下载文件		
						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}	
						
					}else {
						Log.d(TAG, "UI不需要升级");
						return;
					}

				}

			}

		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}else{
		Log.d(TAG, "json解析对象为空");
	}

	}
	
	
	/**
	 * 向指定URL发�?GET方法的请�? *
	 * 
	 * @param url
	 *            发�?请求的URL
	 * @param params
	 *            请求参数，请求参数应该是name1=value1&name2=value2的形式�?
	 * @return URL�?��表远程资源的响应
	 */
	public static String sendGet(String url, String params) {
		String result = "";
		BufferedReader in = null;
		try {
			String urlName = url + "?" + params;
			URL realUrl = new URL(urlName);
			// 打开和URL之间的连�?
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属�?
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			// 建立实际的连�?
			conn.connect();
			// 获取�?��响应头字�?
			Map<String, List<String>> map = conn.getHeaderFields();
			// 遍历�?��的响应头字段
			for (String key : map.keySet()) {
				System.out.println(key + "--->" + map.get(key));
			}
			// 定义BufferedReader输入流来读取URL的响�?
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += "\n" + line;
			}
		} catch (Exception e) {
			System.out.println("发�?GET请求出现异常�?" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输入�?
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	
	/**
	 * 下载apk文件
	 */
	private void downloadApk()
	{
		// 启动新线程下载软�?
		Log.d(TAG, "下载线程开始了....");
		new downloadApkThread().start();
	}

	/**
	 * 下载文件线程
	 * 
	 * @author coolszy
	 *@date 2012-4-26
	 *@blog http://blog.92coding.com
	 */
	/* 记录进度条数�?*/
	private int progress;
	private class downloadApkThread extends Thread
	{
		@Override
		public void run()
		{
			try
			{
				// 判断SD卡是否存在，并且是否具有读写权限
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
				{
					// 获得存储卡的路径
					String sdpath = Environment.getExternalStorageDirectory() + "/";
					mSavePath = sdpath + "download";
					
					
//					update_url= new URL("http://csw-smart.qiniudn.com/s_tianlai_ui0121.apk");
					
					// 创建连接
					HttpURLConnection conn = (HttpURLConnection) update_url.openConnection();
					System.out.println("apk下载地址是："+update_url);
					conn.connect();
					// 获取文件大小
					int length = conn.getContentLength();
					// 创建输入�?
					InputStream is = conn.getInputStream();

					File file = new File(mSavePath);
					// 判断文件目录是否存在
					if (!file.exists())
					{
						file.mkdir();
					}
					File apkFile = new File(mSavePath, downFileName);
					FileOutputStream fos = new FileOutputStream(apkFile);
					int count = 0;
					// 缓存
					byte buf[] = new byte[1024];
					// 写入到文件中
					int numread=0;
					do
					{
						numread = is.read(buf);
						count += numread;
						// 计算进度条位�?
						progress = (int) (((float) count / length) * 100);
//						// 更新进度
//						mHandler.sendEmptyMessage(DOWNLOAD);
//						System.out.println("下载..."+numread);
						if (numread <= 0)
						{
							// 下载完成
//							mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
						   // installApk();
							System.out.println("下载完成,准备安装");
							break;
						}
						// 写入文件
						fos.write(buf, 0, numread);
					} while (numread>0);// 点击取消就停止下�?
					fos.close();
					is.close();
				}
			} catch (MalformedURLException e)
			{
				e.printStackTrace();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	};
	
	
	/**
	 * 安装APK文件
	 */
	private void installApk() {
		
		File apkfile = new File(mSavePath,downFileName);
//		String url1 = apkfile.toString();
		String url1 = mSavePath+"/"+downFileName;
		
		if (!apkfile.exists()) {
			return;
		}
		
		if(downFileName.contains("img")){
			 
		}else if(downFileName.contains(".apk")){
			
			 Intent mBootIntent = new Intent(context, UpdateActivity.class);
			 mBootIntent.putExtra("updateUrl", url1);
			 mBootIntent.putExtra("updateFlag", "2");
			 
			 Log.d(TAG, "updateFlag="+updateFlag);
			 mBootIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		     mBootIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		     context.startActivity(mBootIntent);
		}
		
	     
	     
		// 通过Intent安装APK文件
		/*Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + url1),
				"application/vnd.android.package-archive");
		mContext.startActivity(i);*/
	}
	
}
