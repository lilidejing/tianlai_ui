package com.csw.update;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
/**
 * 获取应用程序的版本号
 * @author json_data
 *
 */
public class GetVersionUtil {

	public static String getVersion(Context context, String packageName) {

		PackageManager pckMan = context.getPackageManager();
		
		List<PackageInfo> packageInfoList = pckMan.getInstalledPackages(0);

		String versionName="";
		for (int i=0;i<packageInfoList.size();i++) {
			PackageInfo packageInfoTemp = packageInfoList.get(i);   
			String packageNameTemp=packageInfoTemp.packageName;
			
			if(packageName.equals(packageNameTemp)){
				
				versionName=packageInfoTemp.versionName;
				
				break;
			}
			
		}

		return versionName;
	}

	public static String getSystemVersion(Context mContext) {
		// 要执行的命令行
		// String ret = "cat /sys/class/net/eth0/address";
		String ret = "getprop ro.product.version";
		String con = "";
		String result = "";
		Process p;
		try {
			p = Runtime.getRuntime().exec(ret);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			while ((result = br.readLine()) != null) {
				con += result;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//con = "GGECMG0001193822522443LRLD43";
		// 可以打出命令执行的结果
		System.out.println("==========================con:" + con);
		
		// ret = do_command(con);
		return con;
	}
	
	
}
