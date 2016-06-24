package com.csw.update;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import com.csw.tianlai_ui.CustomDialog;
import com.csw.tianlai_ui.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class UpdateActivity extends Activity {

	String updateUrl;
	// String updateInfo;
	String updateFlag;

	Context mContext;
	
	private static File mFileName;

	
	private  ProgressBar  mypro;
	private  Button wanchengBtn;
	private  TextView mTextView;
	
	private int progress=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update);
		mContext = this;
		Intent intent = this.getIntent();
		updateUrl = intent.getStringExtra("updateUrl");
		mFileName=new File(updateUrl);
		updateFlag = intent.getStringExtra("updateFlag");
		// updateInfo=intent.getStringExtra("url_res_updateInfo");
		System.out.println("安装地址为：" + updateUrl);

		showNormalDia(updateUrl, updateFlag);

	}

	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						slientInstall(mFileName);
					}

				}).start();
				break;

			case 1:
//					mHandler.sendEmptyMessage(0);
				    mypro.setProgress(progress);
					mTextView.setText(progress+"%");
				break;
				
			case 2:
				UpdateActivity.this.finish();
				wanchengBtn.setEnabled(true);
				wanchengBtn.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						
						UpdateActivity.this.finish();
					}
					
				});
				break;
				
			case 3:
				
				break;

			default:
				break;

			}

		};
	};

	/* 普通的对话框 */
	private void showNormalDia(final String url, String flag) {
		/*
		 * //AlertDialog.Builder normalDialog=new
		 * AlertDialog.Builder(getApplicationContext()); AlertDialog.Builder
		 * normalDia=new AlertDialog.Builder(UpdateActivity.this);
		 * 
		 * normalDia.setTitle("升级");
		 * 
		 * normalDia.setMessage("升级"); if(flag.equals("1")){
		 * 
		 * normalDia.setPositiveButton("确定", new
		 * DialogInterface.OnClickListener() {
		 * 
		 * @Override public void onClick(DialogInterface dialog, int which) { //
		 * TODO Auto-generated method stub if(updateUrl!=null){ Intent i = new
		 * Intent(Intent.ACTION_VIEW);
		 * i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 * i.setDataAndType(Uri.parse("file://" + url),
		 * "application/vnd.android.package-archive"); startActivity(i);
		 * UpdateActivity.this.finish();
		 * 
		 * }else{
		 * 
		 * UpdateActivity.this.finish(); }
		 * 
		 * } }); normalDia.setNegativeButton("取消", new
		 * DialogInterface.OnClickListener() {
		 * 
		 * @Override public void onClick(DialogInterface dialog, int which) { //
		 * TODO Auto-generated method stub UpdateActivity.this.finish();
		 * 
		 * } }); normalDia.setCancelable(false); normalDia.create().show();
		 * 
		 * 
		 * }else if(flag.equals("2")){
		 * 
		 * normalDia.setPositiveButton("升级", new
		 * DialogInterface.OnClickListener() {
		 * 
		 * @Override public void onClick(DialogInterface dialog, int which) { //
		 * TODO Auto-generated method stub if(updateUrl!=null){ Intent i = new
		 * Intent(Intent.ACTION_VIEW);
		 * i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 * i.setDataAndType(Uri.parse("file://" + url),
		 * "application/vnd.android.package-archive"); startActivity(i);
		 * UpdateActivity.this.finish();
		 * 
		 * 
		 * 
		 * 
		 * }else{
		 * 
		 * UpdateActivity.this.finish(); }
		 * 
		 * } });
		 * 
		 * normalDia.setCancelable(false); normalDia.create().show();
		 * 
		 * 
		 * }
		 */

		

		if (flag.equals("1")) {
			CustomDialog.Builder builder = new CustomDialog.Builder(
					UpdateActivity.this);
			builder.setMessage("已发现新版本，是否升级？");
			builder.setTitle("提示");
			builder.setPositiveButton("立即升级",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							File apkfile = new File(url);
//							slientInstall(apkfile);
							if(updateUrl!=null){ 
							 Intent i = new Intent(Intent.ACTION_VIEW);
							 i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							 i.setDataAndType(Uri.parse("file://" + url),
							 "application/vnd.android.package-archive"); startActivity(i);

							dialog.dismiss();
							UpdateActivity.this.finish();
							}
						}
					});

			builder.setNegativeButton("稍后升级",
					new android.content.DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							UpdateActivity.this.finish();
						}
					});

			builder.create().show();
		} else if (flag.equals("2")) {
			mHandler.sendEmptyMessage(0);
			CustomDialog.Builder builder = new CustomDialog.Builder(
					UpdateActivity.this);
//			builder.setMessage("需要升级");
			builder.setTitle("软件正在升级，请勿切断电源");

			LayoutInflater layoutInflater = LayoutInflater.from(mContext);
	        View dialogView = layoutInflater.inflate(R.layout.update_progress_2, null);
	        
	        mTextView=(TextView)dialogView.findViewById(R.id.jinduText);
			
	    
//	        mTextView.setText("57%");
	        
	        mypro=(ProgressBar)dialogView.findViewById(R.id.myView_ProgressBar);
	        
	        
	        wanchengBtn = (Button) dialogView
					.findViewById(R.id.wanchengBtn);
			
	        wanchengBtn.setEnabled(false);

			builder.setContentView(dialogView);
			builder.createUpdateDownload().show();
//			mHandler.sendEmptyMessage(1);
			updateProgress();
			
			
		}

	}

	
	
	
	private void updateProgress(){
		new updateProgressThread().start();
		
		
	}
	private class updateProgressThread extends Thread{
		@Override
		public void run() {
			
			for(int i=1;i<101;i++){
				try {
					Thread.sleep(100);
//					mypro.incrementProgressBy(1);
					progress=i;
					mHandler.sendEmptyMessage(1);
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
			
			mHandler.sendEmptyMessage(2);
			
		}
	}
	
	
	/**
	 * 静默安装
	 * 
	 * @param file
	 * @return
	 */
	public static boolean slientInstall(File file) {
		boolean result = false;
		Process process = null;
		OutputStream out = null;
		try {
			process = Runtime.getRuntime().exec("su");
			out = process.getOutputStream();
			DataOutputStream dataOutputStream = new DataOutputStream(out);
			dataOutputStream.writeBytes("chmod 777 " + file.getPath() + "\n");
			dataOutputStream
					.writeBytes("LD_LIBRARY_PATH=/vendor/lib:/system/lib pm install -r "
							+ file.getPath());
			// 提交命令
			dataOutputStream.flush();
			// 关闭流操作
			dataOutputStream.close();
			out.close();
			int value = process.waitFor();

			// 代表成功
			if (value == 0) {
				result = true;
				System.out.println("安装结果" + result);
			} else if (value == 1) { // 失败
				result = false;
				System.out.println("安装结果" + result);
			} else { // 未知情况
				result = false;
				System.out.println("安装结果" + result);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return result;
	}

	/*
	 * private class DownloadAsyncTask extends AsyncTask<String, Void, Bitmap> {
	 * 
	 * @Override protected Bitmap doInBackground(String... params) { final
	 * String urlStr = params[0];// 取出execute所传入参数 URL url = null; try { url =
	 * new URL(urlStr); final URLConnection connection = url.openConnection();
	 * final InputStream inputStream = connection.getInputStream(); final Bitmap
	 * bitmap = BitmapFactory.decodeStream(inputStream); if (bitmap != null) {
	 * return bitmap;// 交给onPostExecute()处理 } } catch (Exception e) {
	 * e.printStackTrace(); } return null; }
	 * 
	 * @Override protected void onPostExecute(Bitmap result) { if (result !=
	 * null) { logoIv.setImageBitmap(result);// 取出doInBackground()的计算结果 }
	 * downloadBtn.setEnabled(true); super.onPostExecute(result); }
	 * 
	 * @Override protected void onPreExecute() {
	 * 
	 * downloadBtn.setEnabled(false);// 在整个异步下载期间，不允许重新启动下载任务
	 * super.onPreExecute(); }
	 * 
	 * }
	 */

}
