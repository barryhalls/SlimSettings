
package com.ar.slimsettings.service;

import java.net.URISyntaxException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.widget.Toast;

import com.android.internal.statusbar.IStatusBarService;
import com.ar.slimsettings.util.WeatherPrefs;
import com.ar.slimsettings.R;

public class WeatherReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(WeatherService.INTENT_REQUEST_WEATHER)) {
			boolean updateweather = true;

			// SystemUI sends the broadcast to update weather upon booting up,
            // make sure we want to refresh it
            if (updateweather
                    && Settings.System.getInt(context.getContentResolver(),
                            Settings.System.USE_WEATHER, 0) != 0) {
                Toast.makeText(context, context.getText(R.string.weather_refreshing),
                        Toast.LENGTH_SHORT).show();
            Intent getWeatherNow = new Intent(context, WeatherService.class);
            getWeatherNow.setAction(action);
            context.startService(getWeatherNow);
			}
        }
    }

    private void collapseStatusBar() {
        try {
            IStatusBarService sb = IStatusBarService.Stub.asInterface(
                    ServiceManager.getService(Context.STATUS_BAR_SERVICE));
            sb.collapse();
        } catch (RemoteException e) {
        }
    }

}
