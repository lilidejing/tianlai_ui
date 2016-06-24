package com.csw.update;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.csw.util.NetCheck;





import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;
import android.content.pm.PackageManager;
import android.content.pm.PackageInfo;
import android.content.Context;
/**
 *@author coolszy
 *@date 2012-4-26
 *@blog http://blog.92coding.com
 */
public class update_main 
{
	
	/* 保存解析的XML信息 */
	HashMap<String, String> mHashMap;
	/* 下载保存路径 */
	private String mSavePath;

	private Context mContext;
	private Handler handler;                  // 声明handler
	

	public  update_main(Context context)
	{
		mContext = context;
		// 初始化handler
		//在跑线程的时候，对话框不能在线程里跑。不知道为什么，应该理解成
		//对话框只能在主线程里跑吧！
		handler = new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
	           if(msg.what == 1) // handler接收到相关的消息后
	           {
	   			UpdateManager manager = new UpdateManager(mContext);
	   			// 检查软件更新
	   			manager.checkUpdate("version.xml");
//	     			Intent launchIntent = new Intent();
//	    			launchIntent.setComponent(new ComponentName("com.example.test","com.example.test.FullscreenActivity"));
//	    			mContext.startActivity(launchIntent);
	   			
	           }
	           if(msg.what == 2) // handler接收到相关的消息后
	           {
	   			UpdateManager manager = new UpdateManager(mContext);
	   			// 检查软件更新
	   			manager.checkUpdate("tengxun_version.xml");
	           }
	           
	           if(msg.what == 3) // handler接收到相关的消息后
	           {
	   			UpdateManager manager = new UpdateManager(mContext);
	   			// 检查软件更新
	   			manager.checkUpdate("taijie_version.xml");
	           }
	           
	           if(msg.what == 4) // handler接收到相关的消息后
	           {
	   			UpdateManager manager = new UpdateManager(mContext);
	   			// 检查软件更新
	   			manager.checkUpdate("shichang_version.xml");
	           }
	
			}
		};
	}
	
	public  void update()
	{
		//wjz 没有网络，就直接退出
		if (!NetCheck.checkNetWorkStatus(mContext))
		{
			Looper.prepare();
//			Toast.makeText(mContext,"网络有问题！请检查网络！", Toast.LENGTH_LONG).show();
			Looper.loop(); 
			return;
		}
		xml_parse();
		String ver = getAppVersionName(mContext);
		// 获取网站上面的信息,如果没有对应的，就干掉
		String url_res = sendGet("http://112.124.43.99/apk/tianshang/ts-ver.txt",null);
		if(url_res == "")
			return;
		else
		{
			int len = url_res.indexOf("."); 
			
			url_res= url_res.substring(len-1, url_res.length());
		}
		//string转double
		double ver_dou = Double.parseDouble(ver);
		double http_dou =  Double.parseDouble(url_res);

		//当网络版本高于本地版本，就要进行更新
		if (http_dou>ver_dou) 
		{
			handler.sendEmptyMessage(1);
		}
		else
		{
			Looper.prepare();
		//	Toast.makeText(mContext,"已经是最新版本！", Toast.LENGTH_LONG).show();
			Looper.loop(); 
		}
	}
	
	
	public  void update_tengxun()
	{
		// 把version.xml放到网络上，然后获取文件信息
		InputStream inStream = ParseXmlService.class.getClassLoader().getResourceAsStream("tengxun_version.xml");
		// 解析XML文件。 由于XML文件比较小，因此使用DOM方式进行解析
		ParseXmlService service = new ParseXmlService();
		try
		{
			mHashMap = service.parseXml(inStream);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		handler.sendEmptyMessage(2);
		
	}
	public  void update_taijie()
	{
		// 把version.xml放到网络上，然后获取文件信息
		InputStream inStream = ParseXmlService.class.getClassLoader().getResourceAsStream("taijie_version.xml");
		// 解析XML文件。 由于XML文件比较小，因此使用DOM方式进行解析
		ParseXmlService service = new ParseXmlService();
		try
		{
			mHashMap = service.parseXml(inStream);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		handler.sendEmptyMessage(3);
		
	}
	public  void update_anzhuoshichang()
	{
		// 把version.xml放到网络上，然后获取文件信息
		InputStream inStream = ParseXmlService.class.getClassLoader().getResourceAsStream("shichang_version.xml");
		// 解析XML文件。 由于XML文件比较小，因此使用DOM方式进行解析
		ParseXmlService service = new ParseXmlService();
		try
		{
			mHashMap = service.parseXml(inStream);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		handler.sendEmptyMessage(4);
		
	}
	/**
	 * 下载apk文件
	 */
	private void downloadApk()
	{
		// 启动新线程下载软件
		new downloadApkThread().start();
	}

	/**
	 * 下载文件线程
	 * 
	 * @author coolszy
	 *@date 2012-4-26
	 *@blog http://blog.92coding.com
	 */
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
					URL url = new URL(mHashMap.get("url"));
					// 创建连接
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.connect();
					// 获取文件大小
					int length = conn.getContentLength();
					// 创建输入流
					InputStream is = conn.getInputStream();

					File file = new File(mSavePath);
					// 判断文件目录是否存在
					if (!file.exists())
					{
						file.mkdir();
					}
					File apkFile = new File(mSavePath, mHashMap.get("name"));
					FileOutputStream fos = new FileOutputStream(apkFile);
					int count = 0;
					// 缓存
					byte buf[] = new byte[1024];
					// 写入到文件中
					do
					{
						int numread = is.read(buf);
						count += numread;
						if (numread <= 0)
						{
							// 下载完成
							installApk();
							break;
						}
						// 写入文件
						fos.write(buf, 0, numread);
					} while (true);// 点击取消就停止下载.
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
			// 取消下载对话框显示
			//mDownloadDialog.dismiss();
		}
	};

	/**
	 * 安装APK文件
	 */
	private void installApk()
	{
		File apkfile = new File(mSavePath, mHashMap.get("name"));
		String url1 = apkfile.toString();
		if (!apkfile.exists())
		{
			return;
		}
		// 通过Intent安装APK文件
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + url1), "application/vnd.android.package-archive");
		mContext.startActivity(i);
	}	
	
	/**
	 * 检查软件是否有更新版本
	 * 
	 * @return
	 */
	private boolean xml_parse()
	{

		// 把version.xml放到网络上，然后获取文件信息
		InputStream inStream = ParseXmlService.class.getClassLoader().getResourceAsStream("version.xml");
		// 解析XML文件。 由于XML文件比较小，因此使用DOM方式进行解析
		ParseXmlService service = new ParseXmlService();
		try
		{
			mHashMap = service.parseXml(inStream);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	
	/**
	 * 向指定URL发送GET方法的请求
	 * 
	 * @param url
	 *            发送请求的URL
	 * @param params
	 *            请求参数，请求参数应该是name1=value1&name2=value2的形式。
	 * @return URL所代表远程资源的响应
	 */
	public static String sendGet(String url, String params) {
		String result = "";
		BufferedReader in = null;
		try {
			String urlName = url + "?" + params;
			URL realUrl = new URL(urlName);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			conn.setConnectTimeout(10000);
			conn.setReadTimeout(10000);
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			// 建立实际的连接
			
			conn.connect();

			// 获取所有响应头字段
			Map<String, List<String>> map = conn.getHeaderFields();
			// 遍历所有的响应头字段
			for (String key : map.keySet()) {
				System.out.println(key + "--->" + map.get(key));
			}
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += "\n" + line;
			}
		} catch (Exception e) {
			System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输入流
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
	 * 获取版本号
	 * @param context
	 * @return
	 */
	public static String getAppVersionName(Context context) {   
    String versionName = "";   
    try {   
        // ---get the package info---   
        PackageManager pm = context.getPackageManager();   
        PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);   
        versionName = pi.versionName;   
        if (versionName == null || versionName.length() <= 0) {   
            return "";   
        }   
    } catch (Exception e) {   
        //Log.e("VersionInfo", "Exception", e);   
    }   
    return versionName;   
}  
}