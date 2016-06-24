/*$_FOR_ROCKCHIP_RBOX_$*/
/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.csw.rkm_hdmi_display;

import static android.provider.Settings.System.SCREEN_OFF_TIMEOUT;

import android.app.ActivityManagerNative;
import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;

import java.util.ArrayList;

import com.csw.tianlai_ui.R;
import com.csw.util.SharedPreferencesUtils;
//$_rbox_$_modify_$_by cx
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.DisplayOutputManager;

//$_rbox_$_modify_$_end

public class DisplaySettings extends SettingsPreferenceFragment implements
		Preference.OnPreferenceChangeListener, OnPreferenceClickListener {
	private static final String TAG = "DisplaySettings";

	/** If there is no setting in the provider, use this. */
	private static final int FALLBACK_SCREEN_TIMEOUT_VALUE = 30000;

	// private static final String KEY_SCREEN_TIMEOUT = "screen_timeout";
	// private static final String KEY_ACCELEROMETER = "accelerometer";
	// private static final String KEY_FONT_SIZE = "font_size";
	// private static final String KEY_NOTIFICATION_PULSE =
	// "notification_pulse";
	// private static final String KEY_SCREEN_SAVER = "screensaver";
	// $_rbox_$_modify_$_chenxiao begin
	// private static final String KEY_WIFI_DISPLAY = "wifi_display";
	// private static final String KEY_BRIGHTNESS = "brightness";
	// $_rbox_$_modify_$_chenxiao end

	private static final int DLG_GLOBAL_CHANGE_WARNING = 1;

	// private CheckBoxPreference mAccelerometer;
	// private WarnedListPreference mFontSizePref;
	// private CheckBoxPreference mNotificationPulse;

	private final Configuration mCurConfig = new Configuration();

	// private ListPreference mScreenTimeoutPreference;
	// private Preference mScreenSaverPreference;
	private final boolean DBG = true;
	private static final String KEY_MAIN_DISPLAY_INTERFACE = "main_screen_interface";
	private static final String KEY_MAIN_DISPLAY_MODE = "main_screen_mode";
	private static final String KEY_AUX_DISPLAY_INTERFACE = "aux_screen_interface";
	private static final String KEY_AUX_DISPLAY_MODE = "aux_screen_mode";

	private static final String m1920_1080p_60 = "main_screen_1920_1080p_60";
	private static final String m1920_1080p_50 = "main_screen_1920_1080p_50";
	private static final String m1920_1080i_60 = "main_screen_1920_1080i_60";
	private static final String m1920_1080i_50 = "main_screen_1920_1080i_50";
	private static final String m1280_720p_60 = "main_screen_1280_720p_60";
	private static final String m1280_720p_50 = "main_screen_1280_720p_50";
	private static final String m720_576i_60 = "main_screen_720_576i_60";
	private static final String m720_576p_60 = "main_screen_720_576p_60";
	private static final String m720_480p_60 = "main_screen_720_480p_60";
	private static final String m720_480i_60 = "main_screen_720_480i_60";

	private Preference m1920_1080p_60_Display;
	private Preference m1920_1080p_50_Display;
	private Preference m1920_1080i_60_Display;
	private Preference m1920_1080i_50_Display;
	private Preference m1280_720p_60_Display;
	private Preference m1280_720p_50_Display;
	private Preference m720_576i_60_Display;
	private Preference m720_576p_60_Display;
	private Preference m720_480p_60_Display;
	private Preference m720_480i_60_Display;

	private static String setMode="";
	
	private ListPreference mMainDisplay;
	private ListPreference mMainModeList;
	private ListPreference mAuxDisplay;
	private ListPreference mAuxModeList;
	private DisplayOutputManager mDisplayManagement = null;
	private int mMainDisplay_last = -1;
	private int mMainDisplay_set = -1;
	private String mMainMode_last = null;
	private String mMainMode_set = null;
	private int mAuxDisplay_last = -1;
	private int mAuxDisplay_set = -1;
	private String mAuxMode_last = null;
	private String mAuxMode_set = null;
	private static final int DIALOG_ID_RECOVER = 2;
	private AlertDialog mDialog;
	private static int mTime = -1;
	private Handler mHandler;
	private Runnable mRunnable;

	/*
	 * private final RotationPolicy.RotationPolicyListener
	 * mRotationPolicyListener = new RotationPolicy.RotationPolicyListener() {
	 * 
	 * @Override public void onChange() { updateAccelerometerRotationCheckbox();
	 * } };
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ContentResolver resolver = getActivity().getContentResolver();

		addPreferencesFromResource(R.xml.display_settings);
        
		try {
			mDisplayManagement = new DisplayOutputManager();
		} catch (RemoteException doe) {

		}

		int[] main_display = mDisplayManagement
				.getIfaceList(mDisplayManagement.MAIN_DISPLAY);
		if (main_display == null) {
			Log.e(TAG, "Can not get main display interface list");
			return;
		}
		int[] aux_display = mDisplayManagement
				.getIfaceList(mDisplayManagement.AUX_DISPLAY);

		mMainDisplay = (ListPreference) findPreference(KEY_MAIN_DISPLAY_INTERFACE);
//		mMainDisplay.setOnPreferenceChangeListener(this);
		mMainModeList = (ListPreference) findPreference(KEY_MAIN_DISPLAY_MODE);
//		mMainModeList.setOnPreferenceChangeListener(this);

		m1920_1080p_60_Display = (Preference) findPreference(m1920_1080p_60);
		m1920_1080p_50_Display = (Preference) findPreference(m1920_1080i_60);
		m1920_1080i_60_Display = (Preference) findPreference(m1920_1080p_50);
		m1920_1080i_50_Display = (Preference) findPreference(m1920_1080i_50);
		m1280_720p_60_Display = (Preference) findPreference(m1280_720p_60);
		m1280_720p_50_Display = (Preference) findPreference(m1280_720p_50);
		m720_576i_60_Display = (Preference) findPreference(m720_576i_60);
		m720_576p_60_Display = (Preference) findPreference(m720_576p_60);
		m720_480p_60_Display = (Preference) findPreference(m720_480p_60);
		m720_480i_60_Display = (Preference) findPreference(m720_480i_60);

		m1920_1080p_60_Display.setOnPreferenceChangeListener(this);
		m1920_1080p_50_Display.setOnPreferenceChangeListener(this);
		m1920_1080i_60_Display.setOnPreferenceChangeListener(this);
		m1920_1080i_50_Display.setOnPreferenceChangeListener(this);
		m1280_720p_60_Display.setOnPreferenceChangeListener(this);
		m1280_720p_50_Display.setOnPreferenceChangeListener(this);
		m720_576i_60_Display.setOnPreferenceChangeListener(this);
		m720_576p_60_Display.setOnPreferenceChangeListener(this);
		m720_480p_60_Display.setOnPreferenceChangeListener(this);
		m720_480i_60_Display.setOnPreferenceChangeListener(this);
		
		int intmode=SharedPreferencesUtils.getsum(getActivity(),"intmode");
		if (intmode!=0) {
			showhddme(intmode);
		}else {
			
			int curIfaceD = mDisplayManagement
					.getCurrentInterface(mDisplayManagement.MAIN_DISPLAY);
			
			String currentMode = mDisplayManagement.getCurrentMode(
					mDisplayManagement.MAIN_DISPLAY, curIfaceD);
			Log.d(TAG, "cur mode = " + currentMode);
			int intmodeTemp=5;
			if(currentMode.equals("1920x1080p-60")){
				intmodeTemp=1;
			}else if(currentMode.equals("1920x1080p-50")){
				intmodeTemp=3;
			}else if(currentMode.equals("1920x1080i-60")){
				intmodeTemp=2;
			}else if(currentMode.equals("1920x1080i-50")){
				intmodeTemp=4;
			}else if(currentMode.equals("1280x720p-60")){
				intmodeTemp=5;
			}else if(currentMode.equals("1280x720p-50")){
				intmodeTemp=6;
			}else if(currentMode.equals("720x576p-50")){
				intmodeTemp=8;
			}else if(currentMode.equals("720x576i-50")){
				intmodeTemp=7;
			}else if(currentMode.equals("720x480p-60")){
				intmodeTemp=9;
			}else if(currentMode.equals("720x480i-60")){
				intmodeTemp=10;
			}

			showhddme(intmodeTemp);
		}
		
		
		
		int curIface = mDisplayManagement
				.getCurrentInterface(mDisplayManagement.MAIN_DISPLAY);
		
	    Log.d(TAG, "============+++++======"+curIface);
		mMainDisplay_last = curIface;

		if (aux_display == null) {
			mMainDisplay.setTitle(getString(R.string.screen_interface));
		} else {
			mMainDisplay
					.setTitle("1st " + getString(R.string.screen_interface));
		}
		// Fill main interface list.
		CharSequence[] IfaceEntries = new CharSequence[main_display.length];
		CharSequence[] IfaceValue = new CharSequence[main_display.length];
		for (int i = 0; i < main_display.length; i++) {
			IfaceEntries[i] = getIfaceTitle(main_display[i]);
			IfaceValue[i] = Integer.toString(main_display[i]);
		}
		mMainDisplay.setEntries(IfaceEntries);
		mMainDisplay.setEntryValues(IfaceValue);
		mMainDisplay.setValue(Integer.toString(curIface));

		// Fill main display mode list.
		mMainModeList.setTitle(getIfaceTitle(curIface) + " "
				+ getString(R.string.screen_mode_title));
		SetModeList(mDisplayManagement.MAIN_DISPLAY, curIface);
		String mode = mDisplayManagement.getCurrentMode(
				mDisplayManagement.MAIN_DISPLAY, curIface);
		if (savedInstanceState != null) {
			String saved_mode_last = savedInstanceState.getString(
					"main_mode_last", null);
			String saved_mode_set = savedInstanceState.getString(
					"main_mode_set", null);
			if (DBG)
				Log.d(TAG, "get savedInstanceState mainmodelast="
						+ saved_mode_last + ",mainmodeset=" + saved_mode_set);
			if (saved_mode_last != null && saved_mode_set != null) {
				mMainModeList.setValue(saved_mode_last);
				mMainMode_last = saved_mode_last;
				mMainDisplay_set = mMainDisplay_last;
				mMainMode_set = saved_mode_set;
			}
		} else if (mode != null) {
			mMainModeList.setValue(mode);
			mMainMode_last = mode;
			mMainDisplay_set = mMainDisplay_last;
			mMainMode_set = mMainMode_last;
		}

		// Get Aux screen infomation
		mAuxDisplay = (ListPreference) findPreference(KEY_AUX_DISPLAY_INTERFACE);
		mAuxDisplay.setOnPreferenceChangeListener(this);
		mAuxModeList = (ListPreference) findPreference(KEY_AUX_DISPLAY_MODE);
		mAuxModeList.setOnPreferenceChangeListener(this);
		if (aux_display != null) {
			curIface = mDisplayManagement
					.getCurrentInterface(mDisplayManagement.AUX_DISPLAY);
			mAuxDisplay_last = curIface;
			mAuxDisplay.setTitle("2nd " + getString(R.string.screen_interface));
			// Fill aux interface list.
			IfaceEntries = new CharSequence[aux_display.length];
			IfaceValue = new CharSequence[aux_display.length];
			for (int i = 0; i < aux_display.length; i++) {
				IfaceEntries[i] = getIfaceTitle(aux_display[i]);
				IfaceValue[i] = Integer.toString(aux_display[i]);
			}
			mAuxDisplay.setEntries(IfaceEntries);
			mAuxDisplay.setEntryValues(IfaceValue);
			mAuxDisplay.setValue(Integer.toString(curIface));

			// Fill aux display mode list.
			mAuxModeList.setTitle(getIfaceTitle(curIface) + " "
					+ getString(R.string.screen_mode_title));
			SetModeList(mDisplayManagement.AUX_DISPLAY, curIface);
			mode = mDisplayManagement.getCurrentMode(
					mDisplayManagement.AUX_DISPLAY, curIface);
			if (savedInstanceState != null) {
				String saved_mode_last = savedInstanceState.getString(
						"aux_mode_last", null);
				String saved_mode_set = savedInstanceState.getString(
						"aux_mode_set", null);
				if (DBG)
					Log.d(TAG, "get savedInstanceState auxmodelast="
							+ saved_mode_last + ",auxmodeset=" + saved_mode_set);
				if (saved_mode_last != null && saved_mode_set != null) {
					mAuxModeList.setValue(saved_mode_last);
					mAuxMode_last = saved_mode_last;
					mAuxDisplay_set = mAuxDisplay_last;
					mAuxMode_set = saved_mode_set;
				}
			}
			if (mode != null) {
				mAuxModeList.setValue(mode);
				mAuxMode_last = mode;
				mAuxDisplay_set = mAuxDisplay_last;
				mAuxMode_set = mAuxMode_last;
			}
		} else {
			mAuxDisplay.setShouldDisableView(true);
			mAuxDisplay.setEnabled(false);
			getPreferenceScreen().removePreference(mAuxDisplay);
			mAuxModeList.setShouldDisableView(true);
			mAuxModeList.setEnabled(false);
			getPreferenceScreen().removePreference(mAuxModeList);
		}

		mHandler = new Handler();

		mRunnable = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (mDialog == null || mTime < 0)
					return;
				if (mTime > 0) {
					mTime--;
					if (isAdded()) {
						CharSequence text = getString(R.string.screen_control_ok_title)
								+ " (" + String.valueOf(mTime) + ")";
						mDialog.getButton(DialogInterface.BUTTON_POSITIVE)
								.setText(text);
					}
					mHandler.postDelayed(this, 1000);
				} else {
					// Restore display setting.
					RestoreDisplaySetting();
					removeDialog(DIALOG_ID_RECOVER);
					mDialog = null;
				}
			}
		};
		// $_rbox_$_modify_$_by szy,auto mode
		getPreferenceScreen().removePreference(mMainDisplay);
		IntentFilter hdmiplugFilter = new IntentFilter(
				"android.intent.action.HDMI_PLUGGED");
		getActivity().registerReceiver(mHdmiReceiver, hdmiplugFilter);
		// $_rbox_$_modify_$_end
	}

	// $_rbox_$_modify_$_by szy,auto mode
	BroadcastReceiver mHdmiReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if ("android.intent.action.HDMI_PLUGGED".equals(intent.getAction())) {
				boolean state = intent.getBooleanExtra("state", true);
				Log.d(TAG, "HDMI_PLUGGED state = " + state);
				if (state) {
					Resume();
				} else {
					SetModeList(mDisplayManagement.MAIN_DISPLAY,
							mDisplayManagement.DISPLAY_IFACE_TV);
					mDisplayManagement.setInterface(
							mDisplayManagement.MAIN_DISPLAY,
							mDisplayManagement.DISPLAY_IFACE_TV, true);
					Resume();
				}

				if (mMainModeList.getDialog() != null)
					mMainModeList.getDialog().dismiss();
			}
		}
	};

	public void Resume() {
		Log.d(TAG, "resume fill interface and mode");

		int curIface = 0;
		String mode = null;

		// Fill main interface list.
		int[] mainFace = mDisplayManagement
				.getIfaceList(mDisplayManagement.MAIN_DISPLAY);
		if (mainFace != null) {
			// get current main iface
			curIface = mDisplayManagement
					.getCurrentInterface(mDisplayManagement.MAIN_DISPLAY);
			mMainDisplay_last = curIface;

			String curInterface = getIfaceTitle(curIface);
			Log.d(TAG, "cur interface:" + curInterface);

			// Fill main display mode list.
			SetModeList(mDisplayManagement.MAIN_DISPLAY, curIface);

			mMainModeList.setLayoutResource(R.layout.custom_preference);// �����Զ��岼��

			mMainModeList.setTitle(getIfaceTitle(curIface) + " "
					+ getString(R.string.screen_mode_title));// ��ʾHDMI���ģʽ�ĵط�
			mode = mDisplayManagement.getCurrentMode(
					mDisplayManagement.MAIN_DISPLAY, curIface);
			Log.d(TAG, "cur mode = " + mode);
			if (mode != null) {
				mMainMode_last = mode;
				mMainDisplay_set = mMainDisplay_last;
				mMainMode_set = mMainMode_last;
				mMainModeList.setValue(mode);
			}
		}

	}

	// $_rbox_$_modify_$_end

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if (DBG)
			Log.d(TAG, "onStop()");
		mHandler.removeCallbacks(mRunnable);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		getActivity().unregisterReceiver(mHdmiReceiver);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		if (DBG)
			Log.d(TAG, "store onSaveInstanceState mainmodelast="
					+ mMainMode_last + ",mainmodeset=" + mMainMode_set
					+ ",auxmodelast=" + mAuxMode_last + ",auxmodeset="
					+ mAuxMode_set);
		super.onSaveInstanceState(outState);
		outState.putString("main_mode_last", mMainMode_last);
		outState.putString("main_mode_set", mMainMode_set);
		outState.putString("aux_mode_last", mAuxMode_last);
		outState.putString("aux_mode_set", mAuxMode_set);
	}

	@Override
	public void onDialogShowing() {
		// override in subclass to attach a dismiss listener, for instance
		if (mDialog != null) {
			mDialog.getButton(DialogInterface.BUTTON_NEGATIVE).requestFocus();
			CharSequence text = getString(R.string.screen_control_ok_title)
					+ " (" + String.valueOf(mTime) + ")";
			mDialog.getButton(DialogInterface.BUTTON_POSITIVE).setText(text);
			mHandler.postDelayed(mRunnable, 1000);
		}

	}

	private String getIfaceTitle(int iface) {
		String ifaceTitle = null;
		if (iface == mDisplayManagement.DISPLAY_IFACE_LCD)
			ifaceTitle = getString(R.string.screen_iface_lcd_title);
		if (iface == mDisplayManagement.DISPLAY_IFACE_HDMI)
			ifaceTitle = getString(R.string.screen_iface_hdmi_title);
		else if (iface == mDisplayManagement.DISPLAY_IFACE_VGA)
			ifaceTitle = getString(R.string.screen_iface_vga_title);
		else if (iface == mDisplayManagement.DISPLAY_IFACE_YPbPr)
			ifaceTitle = getString(R.string.screen_iface_ypbpr_title);
		else if (iface == mDisplayManagement.DISPLAY_IFACE_TV)
			ifaceTitle = getString(R.string.screen_iface_tv_title);

		return ifaceTitle;
	}

	private void SetModeList(int display, int iface) {

		if (DBG)
			Log.d(TAG, "SetModeList display " + display + " iface " + iface);

		String[] modelist = mDisplayManagement.getModeList(display, iface);
		CharSequence[] ModeEntries = new CharSequence[modelist.length];
		CharSequence[] ModeEntryValues = new CharSequence[modelist.length];
		for (int i = 0; i < modelist.length; i++) {
			ModeEntries[i] = modelist[i];
			System.out.println(ModeEntries[i]);
			if (iface == mDisplayManagement.DISPLAY_IFACE_TV) {
				String mode = modelist[i];
				if (mode.equals("720x576i-50")) {
					ModeEntries[i] = "CVBS: PAL";
				} else if (mode.equals("720x480i-60")) {
					ModeEntries[i] = "CVBS: NTSC";
				} else
					ModeEntries[i] = "YPbPr: " + modelist[i];
			}

			ModeEntryValues[i] = modelist[i];
		}
		if (display == mDisplayManagement.MAIN_DISPLAY) {
			mMainModeList.setEntries(ModeEntries);
			mMainModeList.setEntryValues(ModeEntryValues);
		} else {
			mAuxModeList.setEntries(ModeEntries);
			mAuxModeList.setEntryValues(ModeEntryValues);
		}
	}

	private void RestoreDisplaySetting() {
		if ((mMainDisplay_set != mMainDisplay_last)
				|| (mMainMode_last.equals(mMainMode_set) == false)) {
			if (mMainDisplay_set != mMainDisplay_last) {
				mDisplayManagement.setInterface(
						mDisplayManagement.MAIN_DISPLAY, mMainDisplay_set,
						false);
				mMainDisplay.setValue(Integer.toString(mMainDisplay_last));
				mMainModeList.setTitle(getIfaceTitle(mMainDisplay_last) + " "
						+ getString(R.string.screen_mode_title));
				// Fill display mode list.
				SetModeList(mDisplayManagement.MAIN_DISPLAY, mMainDisplay_last);
			}
			mMainModeList.setValue(mMainMode_last);
			mDisplayManagement.setMode(mDisplayManagement.MAIN_DISPLAY,
					mMainDisplay_last, mMainMode_last);
			mDisplayManagement.setInterface(mDisplayManagement.MAIN_DISPLAY,
					mMainDisplay_last, true);
			mMainDisplay_set = mMainDisplay_last;
			mMainMode_set = mMainMode_last;
		}
		if (mDisplayManagement.getDisplayNumber() > 1) {
			if ((mAuxDisplay_set != mAuxDisplay_last)
					|| (mAuxMode_last.equals(mAuxMode_set) == false)) {
				if (mAuxDisplay_set != mAuxDisplay_last) {
					mDisplayManagement.setInterface(
							mDisplayManagement.AUX_DISPLAY, mAuxDisplay_set,
							false);
					mAuxDisplay.setValue(Integer.toString(mAuxDisplay_last));
					mAuxModeList.setTitle(getIfaceTitle(mAuxDisplay_last) + " "
							+ getString(R.string.screen_mode_title));
					// Fill display mode list.
					SetModeList(mDisplayManagement.AUX_DISPLAY,
							mAuxDisplay_last);
				}
				mAuxModeList.setValue(mAuxMode_last);
				mDisplayManagement.setMode(mDisplayManagement.AUX_DISPLAY,
						mAuxDisplay_last, mAuxMode_last);
				mDisplayManagement.setInterface(mDisplayManagement.AUX_DISPLAY,
						mAuxDisplay_last, true);
				mAuxDisplay_set = mAuxDisplay_last;
				mAuxMode_set = mAuxMode_last;
			}
		}
	}

	int floatToIndex(float val) {
		String[] indices = getResources().getStringArray(
				R.array.entryvalues_font_size);
		float lastVal = Float.parseFloat(indices[0]);
		for (int i = 1; i < indices.length; i++) {
			float thisVal = Float.parseFloat(indices[i]);
			if (val < (lastVal + (thisVal - lastVal) * .5f)) {
				return i - 1;
			}
			lastVal = thisVal;
		}
		return indices.length - 1;
	}

	public void readFontSizePreference(ListPreference pref) {
		try {
			mCurConfig.updateFrom(ActivityManagerNative.getDefault()
					.getConfiguration());
		} catch (RemoteException e) {
			Log.w(TAG, "Unable to retrieve font size");
		}

		// mark the appropriate item in the preferences list
		int index = floatToIndex(mCurConfig.fontScale);
		pref.setValueIndex(index);

		// report the current size in the summary text
		final Resources res = getResources();
		String[] fontSizeNames = res.getStringArray(R.array.entries_font_size);
		pref.setSummary(String.format(
				res.getString(R.string.summary_font_size), fontSizeNames[index]));
	}

	@Override
	public void onResume() {
		super.onResume();

		/*
		 * RotationPolicy.registerRotationPolicyListener(getActivity(),
		 * mRotationPolicyListener);
		 */

		// updateState();
	}

	@Override
	public void onPause() {
		super.onPause();

		/*
		 * RotationPolicy.unregisterRotationPolicyListener(getActivity(),
		 * mRotationPolicyListener);
		 */
	}

	@Override
	public Dialog onCreateDialog(int dialogId) {
		if (dialogId == DLG_GLOBAL_CHANGE_WARNING) {
			/*
			 * return Utils.buildGlobalChangeWarningDialog(getActivity(),
			 * R.string.global_font_change_title, new Runnable() { public void
			 * run() { mFontSizePref.click(); } });
			 */
		}
		switch (dialogId) {
		case DIALOG_ID_RECOVER:
			mDialog = new AlertDialog.Builder(getActivity())
					.setTitle(R.string.screen_mode_switch_title)
					.setCancelable(false)
					.setPositiveButton(R.string.screen_control_ok_title,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// Keep display setting
									mTime = -1;
									mDisplayManagement.saveConfig();
									mMainModeList.setValue(mMainMode_set);
									mMainDisplay_last = mMainDisplay_set;
									mMainMode_last = mMainMode_set;
									mAuxModeList.setValue(mAuxMode_set);
									mAuxDisplay_last = mAuxDisplay_set;
									mAuxMode_last = mAuxMode_set;
								
									showhddme(intmode);
								}
							})
					.setNegativeButton(R.string.screen_control_cancel_title,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// Restore display setting.
									dialog.dismiss();
									mTime = -1;
									mDialog = null;
									RestoreDisplaySetting();
								}
							}).create();
			mDialog.setOnShowListener(new DialogInterface.OnShowListener() {

				@Override
				public void onShow(DialogInterface dialog) {
					// TODO Auto-generated method stub
					if (DBG)
						Log.d(TAG, "show dialog");
					// onDialogShowed();
				}
			});
			return mDialog;

		}

		return null;
	}

	private void showhddme(int intmode){
		if (intmode==1) {
			 m1920_1080p_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_2));
			 m1920_1080p_50_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m1920_1080i_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m1920_1080i_50_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m1280_720p_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m1280_720p_50_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m720_576i_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m720_576p_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m720_480p_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m720_480i_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
		}else if (intmode==2) {
			 m1920_1080p_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m1920_1080p_50_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_2));
			 m1920_1080i_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m1920_1080i_50_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m1280_720p_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m1280_720p_50_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m720_576i_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m720_576p_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m720_480p_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m720_480i_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
		}else if (intmode==3) {
			m1920_1080p_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m1920_1080p_50_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m1920_1080i_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_2));
			 m1920_1080i_50_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m1280_720p_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m1280_720p_50_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m720_576i_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m720_576p_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m720_480p_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m720_480i_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
		}else if (intmode==4) {
			m1920_1080p_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m1920_1080p_50_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m1920_1080i_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m1920_1080i_50_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_2));
			 m1280_720p_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m1280_720p_50_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m720_576i_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m720_576p_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m720_480p_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m720_480i_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
		}else if (intmode==5) {
			m1920_1080p_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m1920_1080p_50_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m1920_1080i_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m1920_1080i_50_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m1280_720p_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_2));
			 m1280_720p_50_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m720_576i_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m720_576p_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m720_480p_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m720_480i_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
		}else if (intmode==6) {
			m1920_1080p_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m1920_1080p_50_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m1920_1080i_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m1920_1080i_50_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m1280_720p_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m1280_720p_50_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_2));
			 m720_576i_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m720_576p_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m720_480p_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m720_480i_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
		}else if (intmode==7) {
			m1920_1080p_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m1920_1080p_50_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m1920_1080i_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m1920_1080i_50_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m1280_720p_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m1280_720p_50_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m720_576i_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_2));
			 m720_576p_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m720_480p_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m720_480i_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
		}else if (intmode==8) {
			 m1920_1080p_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m1920_1080p_50_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m1920_1080i_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m1920_1080i_50_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m1280_720p_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m1280_720p_50_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m720_576i_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m720_576p_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_2));
			 m720_480p_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m720_480i_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
		}else if (intmode==9) {
			 m1920_1080p_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m1920_1080p_50_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m1920_1080i_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m1920_1080i_50_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m1280_720p_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m1280_720p_50_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m720_576i_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m720_576p_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m720_480p_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_2));
			 m720_480i_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
		}else if (intmode==10) {
			 m1920_1080p_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m1920_1080p_50_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m1920_1080i_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m1920_1080i_50_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m1280_720p_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m1280_720p_50_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m720_576i_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m720_576p_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m720_480p_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_1));
			 m720_480i_60_Display.setIcon(getResources().getDrawable(R.drawable.hdmi_2));
		}
		
	}
	public void writeFontSizePreference(Object objValue) {
		try {
			mCurConfig.fontScale = Float.parseFloat(objValue.toString());
			ActivityManagerNative.getDefault().updatePersistentConfiguration(
					mCurConfig);
		} catch (RemoteException e) {
			Log.w(TAG, "Unable to save font size");
		}
	}
    private int intmode;
    Context context;
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		/*
		 * if (preference == mAccelerometer) {
		 * RotationPolicy.setRotationLockForAccessibility( getActivity(),
		 * !mAccelerometer.isChecked()); } else if (preference ==
		 * mNotificationPulse) { boolean value = mNotificationPulse.isChecked();
		 * Settings.System.putInt(getContentResolver(),
		 * Settings.System.NOTIFICATION_LIGHT_PULSE, value ? 1 : 0); return
		 * true; }
		 */

		final String key = preference.getKey();
		 String mode = "";
		if (key.equals(m1920_1080p_60)) {
			mode = "1920x1080p-60";
			intmode=1;
			SharedPreferencesUtils.setsum(getActivity(), "intmode", intmode);
			Log.d(TAG, key);
		} else if (key.equals(m1920_1080p_50)) {
			mode = "1920x1080p-50";
			intmode=3;
			SharedPreferencesUtils.setsum(getActivity(), "intmode", intmode);
			Log.d(TAG, key);
		} else if (key.equals(m1920_1080i_60)) {
			mode = "1920x1080i-60";
			intmode=2;
			SharedPreferencesUtils.setsum(getActivity(), "intmode", intmode);
			Log.d(TAG, key);
		} else if (key.equals(m1920_1080i_50)) {
			mode = "1920x1080i-50";
			intmode=4;
			SharedPreferencesUtils.setsum(getActivity(), "intmode", intmode);
			Log.d(TAG, key);
		} else if (key.equals(m1280_720p_60)) {
			mode = "1280x720p-60";
			intmode=5;
			SharedPreferencesUtils.setsum(getActivity(), "intmode", intmode);
			Log.d(TAG, key);
		} else if (key.equals(m1280_720p_50)) {
			mode = "1280x720p-50";
			intmode=6;
			SharedPreferencesUtils.setsum(getActivity(), "intmode", intmode);
			Log.d(TAG, key);
		} else if (key.equals(m720_576p_60)) {
			mode = "720x576p-50";
			intmode=8;
			SharedPreferencesUtils.setsum(getActivity(), "intmode", intmode);
			Log.d(TAG, key);
		} else if (key.equals(m720_576i_60)) {
			mode = "720x576i-50";
			intmode=7;
			SharedPreferencesUtils.setsum(getActivity(), "intmode", intmode);
			Log.d(TAG, key);
		} else if (key.equals(m720_480p_60)) {
			mode = "720x480p-60";
			intmode=9;
			SharedPreferencesUtils.setsum(getActivity(), "intmode", intmode);
			Log.d(TAG, key);
		} else if (key.equals(m720_480i_60)) {
			mode = "720x480i-60";
			intmode=10;
			SharedPreferencesUtils.setsum(getActivity(), "intmode", intmode);
			Log.d(TAG, key);
		}
		
		if(!mode.equals("")){
			mMainModeList.setValue(mode);
			mMainMode_set = mode;
			mMainDisplay_last = mDisplayManagement
					.getCurrentInterface(mDisplayManagement.MAIN_DISPLAY);
			if ((mMainDisplay_set != mMainDisplay_last)
					|| (mMainMode_last.equals(mMainMode_set) == false)) {
				if (mMainDisplay_set != mMainDisplay_last) {
					mDisplayManagement.setInterface(
							mDisplayManagement.MAIN_DISPLAY, mMainDisplay_last,
							false);
					mTime = 30;
				} else
					mTime = 15;
				mDisplayManagement.setMode(mDisplayManagement.MAIN_DISPLAY,
						mMainDisplay_set, mMainMode_set);
				mDisplayManagement
						.setInterface(mDisplayManagement.MAIN_DISPLAY,
								mMainDisplay_set, true);
				showDialog(DIALOG_ID_RECOVER);
			}
		}
//		mHandler2.sendEmptyMessage(0);
		/*
		 * if (!mode.equals("")) { mAuxModeList.setValue(mode); mAuxMode_set =
		 * mode; mAuxDisplay_last = mDisplayManagement
		 * .getCurrentInterface(mDisplayManagement.AUX_DISPLAY); if
		 * ((mAuxDisplay_set != mAuxDisplay_last) ||
		 * (mAuxMode_last.equals(mAuxMode_set) == false)) { if (mAuxDisplay_set
		 * != mAuxDisplay_last) { mDisplayManagement.setInterface(
		 * mDisplayManagement.AUX_DISPLAY, mAuxDisplay_last, false); mTime = 30;
		 * } else mTime = 15;
		 * mDisplayManagement.setMode(mDisplayManagement.AUX_DISPLAY,
		 * mAuxDisplay_set, mAuxMode_set);
		 * mDisplayManagement.setInterface(mDisplayManagement.AUX_DISPLAY,
		 * mAuxDisplay_set, true); showDialog(DIALOG_ID_RECOVER);
		 * 
		 * } }
		 */

		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object objValue) {
		final String key = preference.getKey();
		/*
		 * if (KEY_SCREEN_TIMEOUT.equals(key)) { int value =
		 * Integer.parseInt((String) objValue); try {
		 * Settings.System.putInt(getContentResolver(), SCREEN_OFF_TIMEOUT,
		 * value); updateTimeoutPreferenceDescription(value); } catch
		 * (NumberFormatException e) { Log.e(TAG,
		 * "could not persist screen timeout setting", e); } } if
		 * (KEY_FONT_SIZE.equals(key)) { writeFontSizePreference(objValue); }
		 */

		// $_rbox_$_modify_$_hhq: move screensetting
		// $_rbox_$_modify_$_begin
		if (key.equals(KEY_MAIN_DISPLAY_INTERFACE)) {
			mMainDisplay.setValue((String) objValue);
			int iface = Integer.parseInt((String) objValue);
			mMainDisplay_set = iface;
			mMainModeList.setTitle(getIfaceTitle(iface) + " "
					+ getString(R.string.screen_mode_title));
			SetModeList(mDisplayManagement.MAIN_DISPLAY, iface);
			String mode = mDisplayManagement.getCurrentMode(
					mDisplayManagement.MAIN_DISPLAY, iface);
			if (mode != null) {
				mMainModeList.setValue(mode);
			}
		}
//		if (key.equals(KEY_MAIN_DISPLAY_MODE)) {
//			String mode = (String) objValue;
//			mMainModeList.setValue(mode);
//			mMainMode_set = mode;
//			mMainDisplay_last = mDisplayManagement
//					.getCurrentInterface(mDisplayManagement.MAIN_DISPLAY);
//			if ((mMainDisplay_set != mMainDisplay_last)
//					|| (mMainMode_last.equals(mMainMode_set) == false)) {
//				if (mMainDisplay_set != mMainDisplay_last) {
//					mDisplayManagement.setInterface(
//							mDisplayManagement.MAIN_DISPLAY, mMainDisplay_last,
//							false);
//					mTime = 30;
//				} else
//					mTime = 15;
//				mDisplayManagement.setMode(mDisplayManagement.MAIN_DISPLAY,
//						mMainDisplay_set, mMainMode_set);
//				mDisplayManagement
//						.setInterface(mDisplayManagement.MAIN_DISPLAY,
//								mMainDisplay_set, true);
//				showDialog(DIALOG_ID_RECOVER);
//			}
//		}

		if (key.equals(KEY_AUX_DISPLAY_INTERFACE)) {
			mAuxDisplay.setValue((String) objValue);
			int iface = Integer.parseInt((String) objValue);
			mAuxDisplay_set = iface;
			mAuxModeList.setTitle(getIfaceTitle(iface) + " "
					+ getString(R.string.screen_mode_title));
			SetModeList(mDisplayManagement.AUX_DISPLAY, iface);
			String mode = mDisplayManagement.getCurrentMode(
					mDisplayManagement.AUX_DISPLAY, iface);
			if (mode != null) {
				mAuxModeList.setValue(mode);
			}
		}
		if (key.equals(KEY_AUX_DISPLAY_MODE)) {
			String mode = (String) objValue;
			mAuxModeList.setValue(mode);
			mAuxMode_set = mode;
			mAuxDisplay_last = mDisplayManagement
					.getCurrentInterface(mDisplayManagement.AUX_DISPLAY);
			if ((mAuxDisplay_set != mAuxDisplay_last)
					|| (mAuxMode_last.equals(mAuxMode_set) == false)) {
				if (mAuxDisplay_set != mAuxDisplay_last) {
					mDisplayManagement.setInterface(
							mDisplayManagement.AUX_DISPLAY, mAuxDisplay_last,
							false);
					mTime = 30;
				} else
					mTime = 15;
				mDisplayManagement.setMode(mDisplayManagement.AUX_DISPLAY,
						mAuxDisplay_set, mAuxMode_set);
				mDisplayManagement.setInterface(mDisplayManagement.AUX_DISPLAY,
						mAuxDisplay_set, true);
				showDialog(DIALOG_ID_RECOVER);

			}
		}

		// $_rbox_$_modify_$_end
		return true;
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		/*
		 * if (preference == mFontSizePref) { if
		 * (Utils.hasMultipleUsers(getActivity())) {
		 * showDialog(DLG_GLOBAL_CHANGE_WARNING); return true; } else {
		 * mFontSizePref.click(); } }
		 */
		return false;
	}

	Handler mHandler2 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);

			switch (msg.what) {
			case 0:

				
				mAuxModeList.setValue(setMode);
				mAuxMode_set = setMode;
//				mAuxDisplay_last = mDisplayManagement
//						.getCurrentInterface(mDisplayManagement.AUX_DISPLAY);
				mAuxDisplay_last = 4;
				if ((mAuxDisplay_set != mAuxDisplay_last)
						|| (mAuxMode_last.equals(mAuxMode_set) == false)) {
					if (mAuxDisplay_set != mAuxDisplay_last) {
						mDisplayManagement.setInterface(
								mDisplayManagement.AUX_DISPLAY, mAuxDisplay_last,
								false);
						mTime = 30;
					} else
						mTime = 15;
					mDisplayManagement.setMode(mDisplayManagement.AUX_DISPLAY,
							mAuxDisplay_set, mAuxMode_set);
					mDisplayManagement.setInterface(mDisplayManagement.AUX_DISPLAY,
							mAuxDisplay_set, true);
					showDialog(DIALOG_ID_RECOVER);

				}
				break;
			case 1:

				break;
			default:
				break;

			}
		}
	};
}
