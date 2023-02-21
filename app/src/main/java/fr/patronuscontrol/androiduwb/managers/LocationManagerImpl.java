package fr.patronuscontrol.androiduwb.managers;

import android.content.Context;
import android.location.LocationManager;

public class LocationManagerImpl {
    private static final String TAG = LocationManagerImpl.class.getSimpleName();

    private final LocationManager locationManager;

    private static LocationManagerImpl mInstance = null;

    private LocationManagerImpl(final Context context) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public static synchronized LocationManagerImpl getInstance(final Context context) {
        if (mInstance == null) {
            mInstance = new LocationManagerImpl(context);
        }

        return mInstance;
    }

    public boolean isSupported() {
        return locationManager != null;
    }

    public boolean isEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
}
