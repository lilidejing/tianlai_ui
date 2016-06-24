package com.csw.csw_wifi;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.net.ConnectivityManagerCompat;
import android.util.Log;

@SuppressLint("NewApi")
public class NetUtil {

	public NetUtil(Context context) {
		super();
	}

	/*
	 * 检查当前WIFI是否连接，两层意思——是否连接，连接是不是WIFI
	 */

	public static boolean isWifiConnected(Context context) {

		ConnectivityManager cm = (ConnectivityManager) context

		.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo info = cm.getActiveNetworkInfo();

		if (info != null && info.isConnected()

		&& ConnectivityManager.TYPE_WIFI == info.getType()) {

			return true;

		}

		return false;

	}

	/**
	 * 检查当前GPRS是否连接，两层意思——是否连接，连接是不是GPRS
	 * 
	 * @param context
	 * @return true表示当前网络处于连接状态，且是GPRS，否则返回false
	 */

	public static boolean isGprsConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info != null && info.isConnected()

		&& ConnectivityManager.TYPE_MOBILE == info.getType()) {
			return true;
		}
		return false;
	}

	/**
	 * 检查当前是否连接
	 * 
	 * @param context
	 * @return true表示当前网络处于连接状态，否则返回false
	 */

	public static boolean isConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			return true;
		}

		return false;

	}

	/**
	 * 对大数据传输时，需要调用该方法做出判断，如果流量敏感，应该提示用户
	 * 
	 * @param context
	 * @return true表示流量敏感，false表示不敏感
	 */

	public static boolean isActiveNetworkMetered(Context context) {

		ConnectivityManager cm = (ConnectivityManager) context

		.getSystemService(Context.CONNECTIVITY_SERVICE);

		return ConnectivityManagerCompat.isActiveNetworkMetered(cm);

	}

	public static Intent registerReceiver(Context context,

	ConnectivityChangeReceiver receiver) {

		return context.registerReceiver(receiver,
				ConnectivityChangeReceiver.FILTER);

	}

	public static void unregisterReceiver(Context context,

	ConnectivityChangeReceiver receiver) {

		context.unregisterReceiver(receiver);

	}

	public static abstract class ConnectivityChangeReceiver extends

	BroadcastReceiver {

		public static final IntentFilter FILTER = new IntentFilter(

		ConnectivityManager.CONNECTIVITY_ACTION);

		@Override
		public final void onReceive(Context context, Intent intent) {

			ConnectivityManager cm = (ConnectivityManager) context

			.getSystemService(Context.CONNECTIVITY_SERVICE);

			NetworkInfo wifiInfo = cm

			.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

			NetworkInfo gprsInfo = cm

			.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

			// 判断是否是Connected事件

			boolean wifiConnected = false;

			boolean gprsConnected = false;

			if (wifiInfo != null && wifiInfo.isConnected()) {

				wifiConnected = true;

			}

			if (gprsInfo != null && gprsInfo.isConnected()) {
				gprsConnected = true;

			}

			if (wifiConnected || gprsConnected) {

				onConnected();

				return;

			}

			// 判断是否是Disconnected事件，注意：处于中间状态的事件不上报给应用！上报会影响体验

			boolean wifiDisconnected = false;

			boolean gprsDisconnected = false;

			if (wifiInfo == null || wifiInfo != null

			&& wifiInfo.getState() == State.DISCONNECTED) {

				wifiDisconnected = true;

			}

			if (gprsInfo == null || gprsInfo != null

			&& gprsInfo.getState() == State.DISCONNECTED) {

				gprsDisconnected = true;

			}

			if (wifiDisconnected && gprsDisconnected) {

				onDisconnected();

				return;

			}

		}

		protected abstract void onDisconnected();

		protected abstract void onConnected();
	}
	
	
	/*******************************获取IP啊mac啊什么的**************************************************************/
	
	/*
	 * 获取IP
	 */
	  private static String intToIp(int i) {
          return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
                          + "." + ((i >> 24) & 0xFF);
  }
  
  protected  String execRootCmd(String paramString) {
          DataInputStream dis = null;
          Runtime r = Runtime.getRuntime();
          try {
                  // r.exec("su"); // get root
                  StringBuilder sb = new StringBuilder();
                  Process p = r.exec(paramString);
                  InputStream input = p.getInputStream();
                  dis = new DataInputStream(input);
                  String content = null;
                  while ((content = dis.readLine()) != null) {
                          sb.append(content).append("\n");
                  }
                  // r.exec("exit"); Log.i("UERY", "sb = " + sb.toString());
                  // localVector.add(sb.toString());
                  return sb.toString();
          } catch (IOException e) {
                  e.printStackTrace();
          } finally {
                  if (dis != null) {
                          try {
                                  dis.close();
                          } catch (IOException e) {
                                  e.printStackTrace();
                          }
                  }
          }
          return null;
  }

  

  /**
   * Get IP address from first non-localhost interface
   * 
   * @param ipv4
   *            true=return ipv4, false=return ipv6
   * @return address or empty string
   */
  public static String getIPAddressWifi(boolean useIPv4,Context context) {
          WifiManager wifimanage = (WifiManager)context.getSystemService(context.WIFI_SERVICE);// 获取WifiManager
          // 检查wifi是否开启

          if (wifimanage.isWifiEnabled()) {

                  WifiInfo wifiinfo = wifimanage.getConnectionInfo();

                  String wifiip = intToIp(wifiinfo.getIpAddress());
                  Log.i("MyTag", "-------wifiip----" + wifiip);
                  return wifiip;
          }
//          else {
//                  String comstr = "ifconfig eth0";
//                  String ip = execRootCmd(comstr);
//                  Log.i("MyTag", "---process ifconfig eth0-----" + ip);
//                  final String myip = ip.substring(ip.indexOf("ip") + 2,
//                                  ip.indexOf("mask")).trim();
//                  return myip;
//                  
//          }
		return null;

  }
  
  /*
   * 插网线获取IP
   */
  public  String getIPAddressLine(boolean useIPv4,Context context) {
	  String comstr = "ifconfig eth0";
      String ip = execRootCmd(comstr);
      Log.i("MyTag", "---process ifconfig eth0-----" + ip);
      final String myip = ip.substring(ip.indexOf("ip") + 2,
                      ip.indexOf("mask")).trim();
      return myip;
  }
	 /* 
*****************************************************************
*                       子函数：获得本地网口MAC地址，插网线用哈,不插网线会获取到wifimac
*****************************************************************                        
*/    
public static String getWangKouMacAddress(){   
  String result = "";     
  String Mac = "";
  result = callCmd("busybox ifconfig","HWaddr");
  
  //如果返回的result == null，则说明网络不可取
  if(result==null){
  	return "网络出错，请检查网络";
  }
  
  //对该行数据进行解析
  //例如：eth0      Link encap:Ethernet  HWaddr 00:16:E8:3E:DF:67
  if(result.length()>0 && result.contains("HWaddr")==true){
  	Mac = result.substring(result.indexOf("HWaddr")+6, result.length()-1);
  	Log.i("test","Mac:"+Mac+" Mac.length: "+Mac.length());

  	if(Mac.length()>1){
  		Mac = Mac.replaceAll(" ", "");
  		result = "";
  		String[] tmp = Mac.split(":");
  		for(int i = 0;i<tmp.length;++i){
  			result +=tmp[i];
  		}
  	}   	
//	return result.toUpperCase();
  }
  return result;
}   


public static String callCmd(String cmd,String filter) {   
  String result = "";   
  String line = "";   
  try {
  	Process proc = Runtime.getRuntime().exec(cmd);
      InputStreamReader is = new InputStreamReader(proc.getInputStream());   
      BufferedReader br = new BufferedReader (is);   
      
      //执行命令cmd，只取结果中含有filter的这一行
      while ((line = br.readLine ()) != null && line.contains(filter)== false) {   
      	//result += line;
      	Log.i("test","line: "+line);
      }
      
      result = line;
      Log.i("test","result: "+result);
  }   
  catch(Exception e) {   
      e.printStackTrace();   
  }   
  return result;   
}
	////////////////////////////////////////////////////////////////

//根据IP获取本地Mac
public static String getLocalMacAddressFromIp(Context context) {
   String mac_s= "";
  try {
       byte[] mac;
       NetworkInterface ne=NetworkInterface.getByInetAddress(InetAddress.getByName(getLocalIpAddress()));
       mac = ne.getHardwareAddress();
       mac_s = byte2hex(mac);
  } catch (Exception e) {
      e.printStackTrace();
  }
  
   return mac_s;
}


@SuppressLint("NewApi")
public static  String byte2hex(byte[] b) {
    StringBuffer hs = new StringBuffer(b.length);
    String stmp = "";
    int len = b.length;
    for (int n = 0; n < len; n++) {
     stmp = Integer.toHexString(b[n] & 0xFF);
     if (stmp.length() == 1)
      hs = hs.append("0").append(stmp);
     else {
      hs = hs.append(stmp);
     }
    }
    return String.valueOf(hs);
   }

/**
 * 获取本地IP地址
 * @return
 */
public static String getLocalIpAddress() {  
      try {  
          for (Enumeration<NetworkInterface> en = NetworkInterface  
                          .getNetworkInterfaces(); en.hasMoreElements();) {  
                      NetworkInterface intf = en.nextElement();  
                     for (Enumeration<InetAddress> enumIpAddr = intf  
                              .getInetAddresses(); enumIpAddr.hasMoreElements();) {  
                          InetAddress inetAddress = enumIpAddr.nextElement();  
                          if (!inetAddress.isLoopbackAddress()) {  
                          return inetAddress.getHostAddress().toString();  
                          }  
                     }  
                  }  
              } catch (SocketException ex) {  
                  Log.e("WifiPreference IpAddress", ex.toString());  
              }  
      
           return null;  
}  
/**
 * 获取本地无线mac地址
 */
public static String getLocalWuxianMacAddress(Context context){
	WifiManager wifiManager=(WifiManager)context.getSystemService(Context.WIFI_SERVICE);
	WifiInfo wifiInfo=wifiManager.getConnectionInfo();
	String mac=wifiInfo.getMacAddress();
	return mac;
}


/**
 * 
 * 获取网口mac地址，如果网口mac地址固定,那就一直获取固定mac
 */
public static String getLocalfinalMacAddress() {  
	String mac = null;
	String result="";
    try{  
        String path="sys/class/net/eth0/address";  
        FileInputStream fis_name = new FileInputStream(path);  
        byte[] buffer_name = new byte[1024*8];  
        int byteCount_name = fis_name.read(buffer_name);  
        if(byteCount_name>0)  
        {  
            mac = new String(buffer_name, 0, byteCount_name, "utf-8");  
        }  
          
        if(mac.length()==0||mac==null){  
            path="sys/class/net/eth0/wlan0";  
            FileInputStream fis = new FileInputStream(path);  
            byte[] buffer = new byte[1024*8];  
            int byteCount = fis.read(buffer);  
            if(byteCount>0)  
            {  
                mac = new String(buffer, 0, byteCount, "utf-8");  
            }  
        }  
          
        if(mac.length()==0||mac==null){  
            return "";  
        }  
    }catch(Exception io){  
          
    }  
    String macStr=mac.trim();  
   /* if(macStr.length()>1){
    	macStr = macStr.replaceAll(" ", "");
  		result = "";
  		String[] tmp = macStr.split(":");
  		for(int i = 0;i<tmp.length;++i){
  			result +=tmp[i];
  		}
  	}   	
	return result.toUpperCase();*/
    return macStr;
   
}  


}