package com.csw.tianlai_ui;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.RemoteException;
import android.rkm.McuBaseInfo;
import android.rkm.McuListener;
import android.rkm.RkmManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.KeyEvent;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.csw.csw_wifi.NetUtil;
import com.csw.csw_wifi.Wifi_Activity;
import com.csw.tianlai_ui.R;
import com.csw.update.GetVersionUtil;
import com.csw.update.UpdateActivity;
import com.csw.update.update_main;
import com.csw.util.RootCmd;
import com.csw.util.ZoomAnimation;
import com.csw.secondwifi.*;

public class MainActivity extends Activity {

	private Button btn_yy;
	private Button btn_mv;
	private Button btn_home_me;
	private Button btn_movable;
	private Button btn_down;
	private Button btn_rank;
	private Button btn_shopping;
	private Button btn_settings;
	private Button btn_manguo;
//	private ImageView img_geren;
	// wifi状态
	private ImageView img_wifi;
	private ConnectivityManager connManager;
	private NetworkInfo mWifi;
	private MediaPlayer mediaPlayer;

	// mic状态
	// private ImageView fisrtMicImage;
	private ImageView secondMicImage;

//	private RkmManager mRkmManager;
	private static final String TAG = "KylinTest";
	Context mContext;

	/**
	 * A麦和B麦都没对上
	 */
	private static final String A0B0 = "A0B0";
	/**
	 * A麦对上，B麦没对上
	 */
	private static final String A1B0 = "A1B0";
	/**
	 * A麦没对上，B麦对上
	 */
	private static final String A0B1 = "A0B1";
	/**
	 * A麦对上，B麦对上
	 */
	private static final String A1B1 = "A1B1";
	/**
	 * A卖没对上，B麦没对上
	 */
	private static final String ERR = "ERR";

	private IntentFilter mWSIntentFilter;

	private BroadcastReceiver mWifiReceiver;

	public static Context updateContext;
	Runnable ReadMicStateRunnable=new Runnable(){
		@Override
		public void run() {
			boolean micState=false;
			// TODO Auto-generated method stub
			/*try {
				micState=mRkmManager.GetMicState();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d(TAG, "read MicState: "+micState);*/
		}
	
	};
	/**
	 * 天籁K歌APP的包名
	 */
	private static final String tlkgappPackageName="com.audiocn.kalaok.tv";
	/**
	 * 主界面UI的包名
	 */
	private static final String uiappPackageName="com.csw.tianlai_ui";
	/* 下载保存路径 */
	private String mSavePath;
	/**
	 * 下载的文件名
	 */
	private  String downFileName="download.apk";
	/**
	 * 安装APK文件
	 */
	private void installApk() {
		
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
		String sdpath = Environment.getExternalStorageDirectory() + "/";
		mSavePath = sdpath + "download";
		File apkfile = new File(mSavePath,downFileName);
		String url1 = mSavePath+"/"+downFileName;
		System.out.println(url1);
		if (!apkfile.exists()) {
			return;
		}
		PackageManager pManager=MainActivity.this.getPackageManager();
		PackageInfo packageInfo =pManager.getPackageArchiveInfo(url1, PackageManager.GET_ACTIVITIES);
		if (packageInfo!=null) {
		      ApplicationInfo appInfo = packageInfo.applicationInfo;  
		      String appname= pManager.getApplicationLabel(appInfo).toString();// 得到应用名 
		      String version =packageInfo.versionName;
		      String pakgename=packageInfo.packageName;
		      System.out.println("appname:"+appname+"\n"+"version:"+version+"\n"+"packgename:"+pakgename);
		      if (uiappPackageName.equals(pakgename)) {
		    		String currentUIgappVersion=GetVersionUtil.getVersion(MainActivity.updateContext, uiappPackageName);//ui的当前版本号
		    		System.out.println("ui当前版本:"+currentUIgappVersion);
		    		int cuUIappVer;
			    	int UIappVer;
					if(currentUIgappVersion.equals("")){
						cuUIappVer=0;
					}else{
						String cusysTlkgapptemp=currentUIgappVersion.replace(".", "");
						cuUIappVer=Integer.parseInt(cusysTlkgapptemp);//天籁K歌当前的版本号
					}
					if(version.equals("")){
						UIappVer=0;
					}else{
						String cusysTlkgapptemp=version.replace(".", "");
						UIappVer=Integer.parseInt(cusysTlkgapptemp);//天籁K歌下载的
					}
					if (cuUIappVer>=UIappVer) {
						System.out.println("版本一样");
						 return;
					}
			   }
		      if (tlkgappPackageName.equals(pakgename)) {
		    	String currentTlkgappVersion=GetVersionUtil.getVersion(MainActivity.updateContext, tlkgappPackageName);//天籁K歌的当前版本号
		    	System.out.println("天籁K歌版本:"+currentTlkgappVersion);
		    	int cuTlkgappVer;
		    	int tlkgappVer;
				if(currentTlkgappVersion.equals("")){
					cuTlkgappVer=0;
				}else{
					String cusysTlkgapptemp=currentTlkgappVersion.replace(".", "");
					cuTlkgappVer=Integer.parseInt(cusysTlkgapptemp);//天籁K歌当前的版本号
				}
				if(version.equals("")){
					tlkgappVer=0;
				}else{
					String cusysTlkgapptemp=version.replace(".", "");
					tlkgappVer=Integer.parseInt(cusysTlkgapptemp);//天籁K歌下载的
				}
				if (cuTlkgappVer>=tlkgappVer) {
					System.out.println("版本一样");
					return;
				}
				
			  }
		      
		}
		
		if(downFileName.contains("img")){
			 
		}else if(downFileName.contains(".apk")){
			
			 Intent mBootIntent = new Intent(MainActivity.this, UpdateActivity.class);
			 mBootIntent.putExtra("updateUrl", url1);
			 mBootIntent.putExtra("updateFlag", "2");
			
			 mBootIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		     mBootIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		     startActivity(mBootIntent);
		}
		}
	}
          
	private void install_run(){
    	new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
			
				installApk();
				
			}
		}).start();
    	
    }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
         
		setContentView(R.layout.activity_main);
	
		updateContext=MainActivity.this;
		play_music();
        handler.postDelayed(ReadMicStateRunnable, 1);
		mContext = this;
//		mRkmManager = new RkmManager(mContext);

		

		intview();
		wifi__check();
		/*
		 * Intent intent = new Intent(this,Wifi_Activity.class);
		 * startActivity(intent);
		 */
		/** 升级 */
//		init_update_run();

		mWSIntentFilter = new IntentFilter();

		mWSIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		mWSIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		mWSIntentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
		mWSIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

		mWifiReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				handleEvent(context, intent);
			}
		};
		handler.sendEmptyMessageDelayed(7, 0);
		
	}

	private update_main update = new update_main(this);

	private void init_update_run() {
		Thread thread = new Thread(runnable_update);
		thread.start();
	}

	Runnable runnable_update = new Runnable() {
		public void run() {
			try {
				Thread.sleep(1000);// 等待系统调度，显示欢迎界面
				// 更新软件
				// mUIHandler.sendEmptyMessage(UPDATE_VER);
				update.update();

			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	};

	private void play_music() {
		// TODO Auto-generated method stub
		mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.null_10ms);
		mediaPlayer.start();
		Log.e("mediaplayer", "开始播放");
	}

	Timer mTimer;
	TimerTask mTimeTask;

	private void intview() {
		// fisrtMicImage = (ImageView) this.findViewById(R.id.firstMic);
		secondMicImage = (ImageView) this.findViewById(R.id.secondMic);
//		img_geren=(ImageView) findViewById(R.id.img_geren);
		img_wifi = (ImageView) findViewById(R.id.img_wifi);
		btn_yy = (Button) findViewById(R.id.btn_yy);
		btn_mv = (Button) findViewById(R.id.btn_mv);

		btn_mv.requestFocus();
		btn_home_me = (Button) findViewById(R.id.btn_home_me);
		btn_movable = (Button) findViewById(R.id.btn_movable);
		btn_down = (Button) findViewById(R.id.btn_down);
		btn_rank = (Button) findViewById(R.id.btn_rank);
		btn_shopping = (Button) findViewById(R.id.btn_shopping);
		btn_settings = (Button) findViewById(R.id.btn_settings);
		btn_manguo = (Button) findViewById(R.id.btn_manguo);
		btn_yy.setOnFocusChangeListener(onFocusChangeListener);
		btn_mv.setOnFocusChangeListener(onFocusChangeListener);
		btn_home_me.setOnFocusChangeListener(onFocusChangeListener);
		btn_movable.setOnFocusChangeListener(onFocusChangeListener);
		btn_down.setOnFocusChangeListener(onFocusChangeListener);
		btn_rank.setOnFocusChangeListener(onFocusChangeListener);
		btn_shopping.setOnFocusChangeListener(onFocusChangeListener);
		btn_settings.setOnFocusChangeListener(onFocusChangeListener);
		btn_manguo.setOnFocusChangeListener(onFocusChangeListener);

		btn_yy.setOnClickListener(onClickListener);
		btn_mv.setOnClickListener(onClickListener);
		btn_home_me.setOnClickListener(onClickListener);
		btn_movable.setOnClickListener(onClickListener);
		btn_down.setOnClickListener(onClickListener);
		btn_rank.setOnClickListener(onClickListener);
		btn_shopping.setOnClickListener(onClickListener);
		btn_settings.setOnClickListener(onClickListener);
		btn_manguo.setOnClickListener(onClickListener);

		if (mTimer == null) {
			mTimer = new Timer();
		}
		if (mTimeTask == null) {
			mTimeTask = new TimerTask() {
				public void run() {
					handler.sendEmptyMessage(6);
				}
			};
		}
		mTimer.schedule(mTimeTask, 900);
	}

	private OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View arg0, boolean arg1) {
			// TODO Auto-generated method stub
			Button btn = (Button) arg0;
			if (arg1) {
				ZoomAnimation.scaleButton_dada(btn);
				btn.bringToFront();
			} else {
				btn.clearAnimation();
			}
			
				
			
		}
	};

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent = MainActivity.this.getPackageManager()
					.getLaunchIntentForPackage("com.audiocn.kalaok.tv");
			switch (arg0.getId()) {
			case R.id.btn_down:
				/*
				 * Intent intent_down = new Intent(); intent_down
				 * .setComponent(new ComponentName( "com.audiocn.kalaok.tv",
				 * "com.audiocn.karaoke.tv.downloadPhone.DownloadPhoneActivity"
				 * )); if (intent == null) { return; }
				 * startActivity(intent_down);
				 */
                 /*
                  *进芒果TV com.hunantv.market/aom.starcor.hunan.SplashActivity   
                  */
				 Intent intent_manguo = MainActivity.this.getPackageManager().getLaunchIntentForPackage("com.hunantv.license");
				 if(intent_manguo == null) { 
					 return; 
					}
				 startActivity(intent_manguo);
				 
				
				
			/*	CustomDialog.Builder builderM = new CustomDialog.Builder(
						MainActivity.this);
				builderM.setMessage(mContext.getResources().getString(
						R.string.confirm_pwoeroff));
				builderM.setTitle(mContext.getResources().getString(
						R.string.bind_mobile));

				builderM.createMobileDownload().show();*/

				break;
			case R.id.btn_manguo:
				/*
				 * Intent intent_manguo = MainActivity.this.getPackageManager()
				 * .getLaunchIntentForPackage("com.starcor.mango"); if
				 * (intent_manguo == null) { return; }
				 * startActivity(intent_manguo);
				 */

				
				 /* new AlertDialog.Builder(mContext)
				  .setTitle(mContext.getResources
				  ().getString(R.string.love_tip) )
				  .setMessage(mContext.getResources
				  ().getString(R.string.confirm_pwoeroff))
				  .setPositiveButton(mContext
				  .getResources().getString(R.string.confirm_ok), new
				  DialogInterface.OnClickListener() {
				  
				  @Override public void onClick(DialogInterface dialog, int
				  which) { try { if (mRkmManager != null) {
				  mRkmManager.Shutdown(); Toast.makeText( MainActivity.this,
				  mContext
				  .getResources().getString(R.string.going_to_poweroff),
				  Toast.LENGTH_LONG) .show(); } else { mRkmManager = new
				  RkmManager( mContext); } } catch (RemoteException e) { //
				  TODO Auto-generated catch block e.printStackTrace(); } }
				  }).setNegativeButton
				  (mContext.getResources().getString(R.string.confirm_cancel),
				  null).show();*/
				 
				CustomDialog.Builder builder = new CustomDialog.Builder(
						MainActivity.this);
				builder.setMessage(mContext.getResources().getString(
						R.string.confirm_pwoeroff));
				builder.setTitle(mContext.getResources().getString(
						R.string.love_tip));
				builder.setPositiveButton(
						mContext.getResources().getString(R.string.confirm_ok),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								/*try {
									if (mRkmManager != null) {
										mRkmManager.Shutdown();
										Toast.makeText(
												MainActivity.this,
												mContext.getResources().getString(R.string.going_to_poweroff),
												Toast.LENGTH_LONG)
												.show();
									} else {
										mRkmManager = new RkmManager(
												mContext);
										mRkmManager.Shutdown();
									}
								} catch (RemoteException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}*/
							}
						});

				builder.setNegativeButton(
						mContext.getResources().getString(
								R.string.confirm_cancel),
						new android.content.DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});

				builder.create().show();

				break;
			case R.id.btn_movable:

				if (!NetUtil.isWifiConnected(MainActivity.this)) {

					CustomDialog.Builder builder_event = new CustomDialog.Builder(
							MainActivity.this);
					builder_event.setMessage(mContext.getResources().getString(
									R.string.no_wifi_connected));
					builder_event.setTitle(mContext.getResources().getString(
							R.string.love_tip));
					builder_event.setPositiveButton(
							mContext.getResources().getString(R.string.gotosetwifi),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									Intent mIntent=new Intent();
									mIntent.setClass(MainActivity.this,Wifi_second_Activity.class);
									startActivity(mIntent);
									dialog.dismiss();
								}
							});

					builder_event.setNegativeButton(
							mContext.getResources().getString(
									R.string.confirm_cancel),
							new android.content.DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});

					builder_event.create().show();
				} else {

					Intent intent_movable = new Intent();
					intent_movable
							.setComponent(new ComponentName(
									"com.audiocn.kalaok.tv",
									"com.audiocn.karaoke.tv.impl.activity.ActivityActivity"));
					if (intent == null) {
						return;
					}
					startActivity(intent_movable);

				}

				break;
			case R.id.btn_mv:
				
					Intent intent_mv = new Intent();
					intent_mv
							.setComponent(new ComponentName(
									"com.audiocn.kalaok.tv",
									"com.audiocn.karaoke.tv.mvlib.CategoryMainActivity"));
					if (intent == null) {
						return;
					}
					startActivity(intent_mv);
				
				break;
			case R.id.btn_rank:

				if (!NetUtil.isWifiConnected(MainActivity.this)) {

					CustomDialog.Builder builder_rank = new CustomDialog.Builder(
							MainActivity.this);
					builder_rank.setMessage(mContext.getResources().getString(
									R.string.no_wifi_connected));
					builder_rank.setTitle(mContext.getResources().getString(
							R.string.love_tip));
					builder_rank.setPositiveButton(
							mContext.getResources().getString(R.string.gotosetwifi),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									Intent mIntent=new Intent();
									mIntent.setClass(MainActivity.this,Wifi_second_Activity.class);
									startActivity(mIntent);
									dialog.dismiss();
								}
							});

					builder_rank.setNegativeButton(
							mContext.getResources().getString(
									R.string.confirm_cancel),
							new android.content.DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});

					builder_rank.create().show();
				} else {
					Intent intent_rank = new Intent();
					intent_rank.setComponent(new ComponentName(
							"com.audiocn.kalaok.tv",
							"com.audiocn.karaoke.tv.impl.rank.RankActivity"));
					if (intent == null) {
						return;
					}
					startActivity(intent_rank);
				}
				break;
			case R.id.btn_settings:
				Intent intent_settings = new Intent();
				intent_settings.setComponent(new ComponentName(
						"com.audiocn.kalaok.tv",
						"com.audiocn.karaoke.tv.setting.SettingActivity"));
				if (intent == null) {
					return;
				}
				startActivity(intent_settings);
				break;
			case R.id.btn_shopping:
				if (!NetUtil.isWifiConnected(MainActivity.this)) {

					CustomDialog.Builder builder_shopping = new CustomDialog.Builder(
							MainActivity.this);
					builder_shopping.setMessage(mContext.getResources().getString(
									R.string.no_wifi_connected));
					builder_shopping.setTitle(mContext.getResources().getString(
							R.string.love_tip));
					builder_shopping.setPositiveButton(
							mContext.getResources().getString(R.string.gotosetwifi),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									Intent mIntent=new Intent();
									mIntent.setClass(MainActivity.this,Wifi_second_Activity.class);
									startActivity(mIntent);
									dialog.dismiss();
								}
							});

					builder_shopping.setNegativeButton(
							mContext.getResources().getString(
									R.string.confirm_cancel),
							new android.content.DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});

					builder_shopping.create().show();
				} else {
					Intent intent_shopping = new Intent();
					intent_shopping.setComponent(new ComponentName(
							"com.audiocn.kalaok.tv",
							"com.audiocn.karaoke.tv.shop.ShopActivity"));
					if (intent == null) {
						return;
					}
					startActivity(intent_shopping);
				}
				break;
			case R.id.btn_home_me:
				if (!NetUtil.isWifiConnected(MainActivity.this)) {

					CustomDialog.Builder builder_home = new CustomDialog.Builder(
							MainActivity.this);
					builder_home.setMessage(mContext.getResources().getString(
									R.string.no_wifi_connected));
					builder_home.setTitle(mContext.getResources().getString(
							R.string.love_tip));
					builder_home.setPositiveButton(
							mContext.getResources().getString(R.string.gotosetwifi),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									Intent mIntent=new Intent();
									mIntent.setClass(MainActivity.this,Wifi_second_Activity.class);
									startActivity(mIntent);
									dialog.dismiss();
								}
							});

					builder_home.setNegativeButton(
							mContext.getResources().getString(
									R.string.confirm_cancel),
							new android.content.DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});

					builder_home.create().show();
				} else {
					Intent intent_home_me = new Intent();
					intent_home_me
							.setComponent(new ComponentName(
									"com.audiocn.kalaok.tv",
									"com.audiocn.karaoke.tv.impl.userinfo.PersonalHomePageActivity"));
					if (intent == null) {
						return;
					}
					startActivity(intent_home_me);
				}
				break;
			case R.id.btn_yy:
				if (!NetUtil.isWifiConnected(MainActivity.this)) {

					CustomDialog.Builder builder_yy = new CustomDialog.Builder(
							MainActivity.this);
					builder_yy.setMessage(mContext.getResources().getString(
									R.string.no_wifi_connected));
					builder_yy.setTitle(mContext.getResources().getString(
							R.string.love_tip));
					builder_yy.setPositiveButton(
							mContext.getResources().getString(R.string.gotosetwifi),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									Intent mIntent=new Intent();
									mIntent.setClass(MainActivity.this,Wifi_second_Activity.class);
									startActivity(mIntent);
									dialog.dismiss();
								}
							});

					builder_yy.setNegativeButton(
							mContext.getResources().getString(
									R.string.confirm_cancel),
							new android.content.DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});

					builder_yy.create().show();
				} else {
					Intent intent_yy = new Intent();
					intent_yy.setComponent(new ComponentName(
							"com.audiocn.kalaok.tv",
							"com.audiocn.karaoke.impls.yy.YYListActivity"));
					if (intent == null) {
						return;
					}
					startActivity(intent_yy);
				}
				break;
			default:
				break;
			}
		}
	};

	private void handleEvent(Context context, Intent intent) {
		String action = intent.getAction();
		if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
			handler.sendEmptyMessage(0);
		} else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
			handler.sendEmptyMessage(0);
		} else if (WifiManager.RSSI_CHANGED_ACTION.equals(action)) {
			handler.sendEmptyMessage(0);
		} else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
			handler.sendEmptyMessage(0);
		}
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				if (isWifiConnect()) {
					WifiManager mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
					WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
					int wifi = mWifiInfo.getRssi();// 获取wifi信号强度
					if (wifi > -50 && wifi < 0)
						image_button_switch(R.id.img_wifi, R.drawable.wifi4);
					if (wifi > -70 && wifi < -50)
						image_button_switch(R.id.img_wifi, R.drawable.wifi3);
					if (wifi > -80 && wifi < -70)
						image_button_switch(R.id.img_wifi, R.drawable.wifi2);
					if (wifi > -100 && wifi < -80)
						image_button_switch(R.id.img_wifi, R.drawable.wifi1);
				} else {
					ConnectivityManager conn = (ConnectivityManager) getApplicationContext()
							.getSystemService(Context.CONNECTIVITY_SERVICE);
					NetworkInfo networkInfo = conn
							.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
					if (networkInfo.isConnected()) {
						image_button_switch(R.id.img_wifi, R.drawable.youxian_c);
					} else {
						image_button_switch(R.id.img_wifi, R.drawable.youxian_b);

					}

				}
				break;
			case 1:// A0B0

				// image_button_switch(R.id.firstMic, R.drawable.mic_grey);
				image_button_switch2(R.id.secondMic, R.drawable.mic_grey);

				break;

			case 2:// A1B0
					// image_button_switch(R.id.firstMic, R.drawable.mic_white);
				image_button_switch2(R.id.secondMic, R.drawable.mic_grey);
				break;
			case 3:// A0B1
					// image_button_switch(R.id.firstMic, R.drawable.mic_grey);
				image_button_switch2(R.id.secondMic, R.drawable.mic_white);
				break;

			case 4:// A1B1
					// image_button_switch(R.id.firstMic, R.drawable.mic_white);
				image_button_switch2(R.id.secondMic, R.drawable.mic_white);
				break;

			case 5:// ERR
					// image_button_switch(R.id.firstMic, R.drawable.mic_grey);
				image_button_switch2(R.id.secondMic, R.drawable.mic_grey);
				break;

			case 6:
				btn_mv.setFocusable(true);
				btn_mv.setFocusableInTouchMode(true);

				btn_mv.requestFocus();
				System.out.println("点歌台聚焦了");

				break;
			case 7:
				install_run();
				break;
			default:
				break;
			}
		};
	};

	/**
	 * 判断wifi是否连接成功
	 * 
	 * @return
	 */
	public boolean isWifiConnect() {
		connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		WifiManager mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
		int wifi = mWifiInfo.getRssi();// 获取wifi信号强度
		return mWifi.isConnected();
	}

	/**
	 * 放横幅的image，不过这个函数，可以适用所有的imagebuttom放图像
	 * 
	 * @param id
	 *            要换的位置
	 * @param id2
	 *            要更换的图片
	 */
	private void image_button_switch(int id, int id2) {
		final ImageView image_shoucang = (ImageView) findViewById(id);
		image_shoucang.setImageBitmap(BitmapFactory.decodeResource(
				getResources(), id2));
	}

	/**
	 * 放横幅的image，不过这个函数，可以适用所有的imagebuttom放图像
	 * 
	 * @param id
	 *            要换的位置
	 * @param id2
	 *            要更换的图片
	 */
	private void image_button_switch2(int id, int id2) {
		final ImageView image_shoucang = (ImageView) findViewById(id);
		image_shoucang.setBackgroundResource(id2);
	}

	/**
	 * wiif信号检查
	 */
	void wifi__check() {
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				// TODO 自动生成的方法存根
				try {
					Thread.sleep(1000);
					handler.sendEmptyMessage(0);
				} catch (InterruptedException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}

			}
		};
		Thread thread = new Thread(runnable);
		thread.start();
	}

	/**
	 * 获取mic状态监听
	 *//*
	private McuListener mMcuListener = new McuListener() {

		@Override
		public void onMcuInfoChanged(McuBaseInfo mcuBaseInfo, int infoType) {

			Log.d(TAG, "callback exe.. onMcuInfoChanged()");
		}
		@Override
		public void onMcuMICState(McuBaseInfo arg0, int arg1) { // TODOAuto-generated
																// method stub
			String micState = arg0.getMicState();
			Log.d(TAG, "callback exe.. micState=" + micState + "type=" + arg1);
			
			  System.out.println(("first"+ micState.contains("0")+"second"+ micState.contains("1")));
			if (micState.contains("0")  || micState.contains("1")) {
				Log.d(TAG, "hava contains 0 or 1: " + micState);
				handler.sendEmptyMessage(3);//更新主界面mic图片为白色
			} else {
				handler.sendEmptyMessage(1);//更新主界面mic图片为灰色
			}
		}
	};
*/
	private void updateMicImage(final int what) {
		Runnable micrunnable = new Runnable() {
			@Override
			public void run() {
				// TODO 自动生成的方法存根
				handler.sendEmptyMessage(what);
			}
		};
		Thread thread = new Thread(micrunnable);
		thread.start();
	}

	/*
	 * @Override public boolean onKeyDown(int arg0, KeyEvent arg1) { // TODO
	 * Auto-generated method stub return super.onKeyDown(arg0, arg1);
	 * 
	 * if(arg0==KeyEvent.KEYCODE_BACK){ return false; }
	 * 
	 * }
	 */

	private int sumFlag = 0;// 到6了就打开adb调试
	private int secondSumFlag = 0;// 到6了就打开adb调试

	/**
	 * 打开adb的命令
	 */
	String openAdbStr = "adb shell" + "\n" + "su" + "\n"
			+ "chmod 777 /sys/bus/platform/drivers/usb20_otg/force_usb_mode"
			+ "\n"
			+ "echo  2 > /sys/bus/platform/drivers/usb20_otg/force_usb_mode"
			+ "\n" + "exit" + "\n" + "exit";
	/**
	 * 关闭adb的命令
	 */
	String closeAdbStr = "adb shell" + "\n" + "su" + "\n"
			+ "chmod 777 /sys/bus/platform/drivers/usb20_otg/force_usb_mode"
			+ "\n"
			+ "echo  1 > /sys/bus/platform/drivers/usb20_otg/force_usb_mode"
			+ "\n" + "exit" + "\n" + "exit";

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) { // 监控/拦截/屏蔽返回键

			return true;
		}

		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_LEFT:
			sumFlag = 1;
			Log.d(TAG, "adb调试KEYCODE_DPAD_LEFT  " + sumFlag);
			break;

		case KeyEvent.KEYCODE_DPAD_RIGHT:

			if (sumFlag == 1 || sumFlag == 2) {
				sumFlag++;
			} else {
				sumFlag = 0;
			}
			Log.d(TAG, "adb调试KEYCODE_DPAD_RIGHT  " + sumFlag);
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			if (sumFlag == 3) {
				sumFlag++;
			} else {
				sumFlag = 0;
			}
			Log.d(TAG, "adb调试KEYCODE_DPAD_UP  " + sumFlag);
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			if (sumFlag == 4 || sumFlag == 5) {
				sumFlag++;
			} else {
				sumFlag = 0;
			}
			Log.d(TAG, "adb调试KEYCODE_DPAD_DOWN  " + sumFlag);
			break;
		case KeyEvent.KEYCODE_MENU:
			if (sumFlag == 6) {

				secondSumFlag++;
				int sendCmd = secondSumFlag % 2;

				// /sys/bus/platform/drivers/usb20_otg/force_usb_mode
				Log.d(TAG, "adb----KEYCODE_MENU  " + sumFlag);

				if (RootCmd.haveRoot()) {

					if (sendCmd == 1) {

						if (RootCmd.execRootCmdSilent(closeAdbStr) == -1) {

							Log.d(TAG, "adb closed failed");
						} else {
							Log.d(TAG, "adb closed success");
						}
					} else {
						if (RootCmd.execRootCmdSilent(openAdbStr) == -1) {

							Log.d(TAG, "adb opened failed");
						} else {
							Log.d(TAG, "adb opened success");
						}

					}

				} else {
					Log.d(TAG, "没有root权限");
				}

				sumFlag = 0;

			} else {
				sumFlag = 0;
			}
			Log.d(TAG, "adb调试  ----" + sumFlag);

			break;

		default:
			Log.d(TAG, "adb调试  default" + sumFlag);
			sumFlag = 0;
			break;

		}

		if (keyCode == KeyEvent.KEYCODE_MENU) { // 监控/拦截/屏蔽MENU键
			Log.d(TAG, "KEYCODE_MENU------被屏蔽了");
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		/*try {
			mRkmManager.RPC_RequestMcuInfoChangedListener(mMcuListener);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		registerReceiver(mWifiReceiver, mWSIntentFilter);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		/*try {
			mRkmManager.RPC_RemoveMcuInfoChangedListener(mMcuListener);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		unregisterReceiver(mWifiReceiver);

	}

}
