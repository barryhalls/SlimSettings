
package com.ar.slimsettings.fragments;

import android.app.Fragment;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.content.Intent;
import android.content.ComponentName;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.preference.PreferenceManager;

import com.ar.slimsettings.R;
import com.ar.slimsettings.SettingsPreferenceFragment;

public class OTA extends Fragment{

   @Override
    public void onCreate(Bundle savedInstanceState) 
	{
	       	super.onCreate(savedInstanceState);
	        Intent intentDeviceTest = new Intent("android.intent.action.MAIN");                
		intentDeviceTest.setComponent(new  ComponentName("com.acquariusoft.UpdateMe","com.acquariusoft.UpdateMe.UpdateMeActivity"));
		startActivity(intentDeviceTest);
    	}

  
}
