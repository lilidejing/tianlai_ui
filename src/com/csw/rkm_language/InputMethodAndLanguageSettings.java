/*
 * Copyright (C) 2008 The Android Open Source Project
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

package com.csw.rkm_language;




import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.hardware.input.InputManager;
import android.hardware.input.KeyboardLayout;
import android.os.Bundle;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.System;
import android.text.TextUtils;
import android.view.InputDevice;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import com.csw.tianlai_ui.R;

public class InputMethodAndLanguageSettings extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener, InputManager.InputDeviceListener{

    private static final String KEY_PHONE_LANGUAGE = "phone_language";
//    private static final String KEY_CURRENT_INPUT_METHOD = "current_input_method";
    private static final String KEY_INPUT_METHOD_SELECTOR = "input_method_selector";
    private static final String KEY_USER_DICTIONARY_SETTINGS = "key_user_dictionary_settings";
    // false: on ICS or later
    private static final boolean SHOW_INPUT_METHOD_SWITCHER_SETTINGS = false;

    private static final String[] sSystemSettingNames = {
        System.TEXT_AUTO_REPLACE, System.TEXT_AUTO_CAPS, System.TEXT_AUTO_PUNCTUATE,
    };

    private static final String[] sHardKeyboardKeys = {
        "auto_replace", "auto_caps", "auto_punctuate",
    };

    
    private ListPreference mShowInputMethodSelectorPref;
//    private PreferenceCategory mKeyboardSettingsCategory;
    private PreferenceCategory mHardKeyboardCategory;
    private PreferenceCategory mGameControllerCategory;
    private Preference mLanguagePref;
   /* private final ArrayList<InputMethodPreference> mInputMethodPreferenceList =
            new ArrayList<InputMethodPreference>();
    private final ArrayList<PreferenceScreen> mHardKeyboardPreferenceList =
            new ArrayList<PreferenceScreen>();*/
    private InputManager mIm;
    private InputMethodManager mImm;
    private boolean mIsOnlyImeSettings;
    private Handler mHandler;
    private SettingsObserver mSettingsObserver;
    private Intent mIntentWaitingForResult;
   

   
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.language_settings);

       

        if (getActivity().getAssets().getLocales().length == 1) {
            // No "Select language" pref if there's only one system locale available.
            getPreferenceScreen().removePreference(findPreference(KEY_PHONE_LANGUAGE));
        } else {
            mLanguagePref = findPreference(KEY_PHONE_LANGUAGE);
        }
        if (SHOW_INPUT_METHOD_SWITCHER_SETTINGS) {
            mShowInputMethodSelectorPref = (ListPreference)findPreference(
                    KEY_INPUT_METHOD_SELECTOR);
            mShowInputMethodSelectorPref.setOnPreferenceChangeListener(this);
            // TODO: Update current input method name on summary
//            updateInputMethodSelectorSummary(loadInputMethodSelectorVisibility());
        }

       

        // Get references to dynamically constructed categories.
        mHardKeyboardCategory = (PreferenceCategory)findPreference("hard_keyboard");
//        mKeyboardSettingsCategory = (PreferenceCategory)findPreference(
//                "keyboard_settings_category");
        mGameControllerCategory = (PreferenceCategory)findPreference(
                "game_controller_settings_category");

        // Filter out irrelevant features if invoked from IME settings button.
        mIsOnlyImeSettings = Settings.ACTION_INPUT_METHOD_SETTINGS.equals(
                getActivity().getIntent().getAction());
        getActivity().getIntent().setAction(null);
        if (mIsOnlyImeSettings) {
            getPreferenceScreen().removeAll();
            getPreferenceScreen().addPreference(mHardKeyboardCategory);
            if (SHOW_INPUT_METHOD_SWITCHER_SETTINGS) {
                getPreferenceScreen().addPreference(mShowInputMethodSelectorPref);
            }
//            getPreferenceScreen().addPreference(mKeyboardSettingsCategory);
        }

        // Build IME preference category.
        mImm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
      

//        mKeyboardSettingsCategory.removeAll();
//        if (!mIsOnlyImeSettings) {
//            final PreferenceScreen currentIme = new PreferenceScreen(getActivity(), null);
//            currentIme.setKey(KEY_CURRENT_INPUT_METHOD);
//            currentIme.setTitle(getResources().getString(R.string.current_input_method));
//            mKeyboardSettingsCategory.addPreference(currentIme);
//        }

        // Build hard keyboard and game controller preference categories.
        mIm = (InputManager)getActivity().getSystemService(Context.INPUT_SERVICE);
       

        // Spell Checker
        /*final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClass(getActivity(), SpellCheckersSettingsActivity.class);
        final SpellCheckersPreference scp = ((SpellCheckersPreference)findPreference(
                "spellcheckers_settings"));
        if (scp != null) {
            scp.setFragmentIntent(this, intent);
        }*/

        mHandler = new Handler();
        mSettingsObserver = new SettingsObserver(mHandler, getActivity());
    }

  

   
    @Override
    public void onResume() {
        super.onResume();

        mSettingsObserver.resume();
        mIm.registerInputDeviceListener(this, null);

        if (!mIsOnlyImeSettings) {
            if (mLanguagePref != null) {
                Configuration conf = getResources().getConfiguration();
                String language = conf.locale.getLanguage();
                String localeString;
                // TODO: This is not an accurate way to display the locale, as it is
                // just working around the fact that we support limited dialects
                // and want to pretend that the language is valid for all locales.
                // We need a way to support languages that aren't tied to a particular
                // locale instead of hiding the locale qualifier.
                if (language.equals("zz")) {
                    String country = conf.locale.getCountry();
                    if (country.equals("ZZ")) {
                        localeString = "[Developer] Accented English (zz_ZZ)";
                    } else if (country.equals("ZY")) {
                        localeString = "[Developer] Fake Bi-Directional (zz_ZY)";
                    } else {
                        localeString = "";
                    }
                } else if (hasOnlyOneLanguageInstance(language,
                        Resources.getSystem().getAssets().getLocales())) {
                    localeString = conf.locale.getDisplayLanguage(conf.locale);
                } else {
                    localeString = conf.locale.getDisplayName(conf.locale);
                }
                if (localeString.length() > 1) {
                    localeString = Character.toUpperCase(localeString.charAt(0))
                            + localeString.substring(1);
                    mLanguagePref.setSummary(localeString);
                }
            }

//            updateUserDictionaryPreference(findPreference(KEY_USER_DICTIONARY_SETTINGS));
            if (SHOW_INPUT_METHOD_SWITCHER_SETTINGS) {
                mShowInputMethodSelectorPref.setOnPreferenceChangeListener(this);
            }
        }

       

       

   
        
    }

    @Override
    public void onPause() {
        super.onPause();

        mIm.unregisterInputDeviceListener(this);
        mSettingsObserver.pause();

        if (SHOW_INPUT_METHOD_SWITCHER_SETTINGS) {
            mShowInputMethodSelectorPref.setOnPreferenceChangeListener(null);
        }
        // TODO: Consolidate the logic to InputMethodSettingsWrapper
        /*InputMethodAndSubtypeUtil.saveInputMethodSubtypeList(
                this, getContentResolver(), mInputMethodSettingValues.getInputMethodList(),
                !mHardKeyboardPreferenceList.isEmpty());*/
    }

    @Override
    public void onInputDeviceAdded(int deviceId) {
       
    }

    @Override
    public void onInputDeviceChanged(int deviceId) {
      
    }

    @Override
    public void onInputDeviceRemoved(int deviceId) {
        
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        // Input Method stuff
        if (Utils.isMonkeyRunning()) {
            return false;
        }
        if (preference instanceof PreferenceScreen) {
            if (preference.getFragment() != null) {
                // Fragment will be handled correctly by the super class.
            } 
        } 
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    private boolean hasOnlyOneLanguageInstance(String languageCode, String[] locales) {
        int count = 0;
        for (String localeCode : locales) {
            if (localeCode.length() > 2
                    && localeCode.startsWith(languageCode)) {
                count++;
                if (count > 1) {
                    return false;
                }
            }
        }
        return count == 1;
    }

   

   
    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
       return false;
    }

   

    private void updateCurrentImeName() {
        final Context context = getActivity();
        if (context == null || mImm == null) return;
        /*final Preference curPref = getPreferenceScreen().findPreference(KEY_CURRENT_INPUT_METHOD);
        if (curPref != null) {
            final CharSequence curIme =
                    mInputMethodSettingValues.getCurrentInputMethodName(context);
            if (!TextUtils.isEmpty(curIme)) {
                synchronized(this) {
                    curPref.setSummary(curIme);
                }
            }
        }*/
    }

   

  

   

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*if (mIntentWaitingForResult != null) {
            String inputDeviceDescriptor = mIntentWaitingForResult.getStringExtra(
                    KeyboardLayoutPickerFragment.EXTRA_INPUT_DEVICE_DESCRIPTOR);
            mIntentWaitingForResult = null;
            showKeyboardLayoutDialog(inputDeviceDescriptor);
        }*/
    }

    private void updateGameControllers() {
        if (haveInputDeviceWithVibrator()) {
            getPreferenceScreen().addPreference(mGameControllerCategory);

            CheckBoxPreference chkPref = (CheckBoxPreference)
                    mGameControllerCategory.findPreference("vibrate_input_devices");
            chkPref.setChecked(System.getInt(getContentResolver(),
                    Settings.System.VIBRATE_INPUT_DEVICES, 1) > 0);
        } else {
            getPreferenceScreen().removePreference(mGameControllerCategory);
        }
    }

    private boolean haveInputDeviceWithVibrator() {
        final int[] devices = InputDevice.getDeviceIds();
        for (int i = 0; i < devices.length; i++) {
            InputDevice device = InputDevice.getDevice(devices[i]);
            if (device != null && !device.isVirtual() && device.getVibrator().hasVibrator()) {
                return true;
            }
        }
        return false;
    }

    private class SettingsObserver extends ContentObserver {
        private Context mContext;

        public SettingsObserver(Handler handler, Context context) {
            super(handler);
            mContext = context;
        }

        @Override public void onChange(boolean selfChange) {
            updateCurrentImeName();
        }

        public void resume() {
            final ContentResolver cr = mContext.getContentResolver();
            cr.registerContentObserver(
                    Settings.Secure.getUriFor(Settings.Secure.DEFAULT_INPUT_METHOD), false, this);
            cr.registerContentObserver(Settings.Secure.getUriFor(
                    Settings.Secure.SELECTED_INPUT_METHOD_SUBTYPE), false, this);
        }

        public void pause() {
            mContext.getContentResolver().unregisterContentObserver(this);
        }
    }
}
