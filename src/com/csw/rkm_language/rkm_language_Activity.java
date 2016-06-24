package com.csw.rkm_language;

import java.util.List;

import com.csw.tianlai_ui.R;



import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceActivity.Header;
import android.view.Menu;
import android.view.MenuItem;

public class rkm_language_Activity extends PreferenceActivity {

	
	@Override
	   public void onBuildHeaders(List<Header> headers) {
	       loadHeadersFromResource(R.xml.settings_headers, headers);

	   }
	
	   

	   @Override
	   protected boolean isValidFragment(String fragmentName) {
	       return true;
	   }
	   
}
