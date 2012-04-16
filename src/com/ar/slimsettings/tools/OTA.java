
package com.ar.slimsettings.tools;

import android.os.Bundle;

import android.app.Activity;
import android.content.Intent;
import android.content.ComponentName;

public class OTA extends Activity
 {

   @Override
    public void onCreate(Bundle savedInstanceState) 
	{
	       Intent intentDeviceTest = new Intent("android.intent.action.MAIN");                
		intentDeviceTest.setComponent(new  ComponentName("com.acquariusoft.UpdateMe","com.acquariusoft.UpdateMe.UpdateMeActivity"));
		startActivity(intentDeviceTest);
    	}

  
}
