package com.csw.rkm_hdmi_display;

import java.util.List;


import com.csw.tianlai_ui.R;



import android.app.Activity;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.DisplayOutputManager;
import android.os.Handler;
import android.os.RemoteException;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceActivity.Header;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

public class rkm_hdmi_Activity extends Activity {

	 private static final String TAG = "MainActivity";
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.display_main);
		
		android.app.FragmentManager fragmentManager = getFragmentManager();  
        android.app.FragmentTransaction fragmentTransaction =   
            fragmentManager.beginTransaction();  
        DisplaySettings dreamsettings=new DisplaySettings();

        fragmentTransaction.replace(android.R.id.content, dreamsettings);          
        fragmentTransaction.addToBackStack(null);   
        fragmentTransaction.commit();  
		
// 		this.finish();
	}

	/*@Override
	   public void onBuildHeaders(List<Header> headers) {
	       loadHeadersFromResource(R.xml.settings_headers, headers);

	   }*/
	
	
/*
	   @Override
	   protected boolean isValidFragment(String fragmentName) {
	       return true;
	   }
	*/
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
		// TODO Auto-generated method stub
		
		
		if (keyCode == KeyEvent.KEYCODE_BACK) { // ¼à¿Ø/À¹½Ø/ÆÁ±Î·µ»Ø¼ü

			rkm_hdmi_Activity.this.finish();
		}
		return super.onKeyDown(keyCode, keyEvent);
	}
}
