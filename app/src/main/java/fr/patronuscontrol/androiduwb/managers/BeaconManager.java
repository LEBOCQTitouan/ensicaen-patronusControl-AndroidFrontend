package fr.patronuscontrol.androiduwb.managers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.webkit.WebView;

import fr.patronuscontrol.androiduwb.MainActivity;
import fr.patronuscontrol.androiduwb.R;

public class BeaconManager {
    private static final String TAG = "BeaconManager";
    @SuppressLint("StaticFieldLeak")
    private static BeaconManager instance = null;
    private final SensorCompassManager mSensorCompassManager;
    private final WebView mWebView;

    private BeaconManager(Context context) {
        mWebView = ((MainActivity) context).findViewById(R.id.webview);
        mSensorCompassManager = ((MainActivity) context).getSensorCompassManager();
    }

    public static synchronized BeaconManager getInstance(Context context) {
        if (instance == null) {
            instance = new BeaconManager(context);
        }
        return instance;
    }

    /**
     * Update the location of the phone relative to the beacon
     *
     * @param beaconAngle angle from the beacon
     * @param beaconDistance distance from the beacon
     */
    public void updatePhonePosition(int beaconAngle, int beaconDistance) {
        int[] coordinates = getPhoneXYAngCoordinates(mSensorCompassManager.getNorthAngle(), beaconAngle, beaconDistance);
        updatePhonePosition(coordinates[0], coordinates[1], coordinates[2]);
    }

    /**
     * Get the coordinates of the phone relative to the beacon
     *
     * @param northAngle  angle of the north
     * @param beaconAngle angle of the beacon
     * @return coordinates (X,Y) of the phone
     */
    private int[] getPhoneXYAngCoordinates(int northAngle, int beaconAngle, int beaconDistance) {
        int[] coordinates = new int[3];

        int strategy = 0;
        int alpha = northAngle - beaconAngle;
        if (alpha < 0) {
            strategy = 2;
            alpha = alpha % 180;
        }
        if (alpha > 90) {
            strategy += 1;
            alpha = 180 - alpha;
        }

        double x = Math.sin(Math.toRadians(alpha)) * beaconDistance;
        double y = Math.cos(Math.toRadians(alpha)) * beaconDistance;

        switch (strategy) {
            case 0:
                coordinates[0] = -(int) x;
                coordinates[1] = -(int) y;
                break;
            case 1:
                coordinates[0] = -(int) x;
                coordinates[1] = (int) y;
                break;
            case 2:
                coordinates[0] = (int) x;
                coordinates[1] = -(int) y;
                break;
            case 3:
                coordinates[0] = (int) x;
                coordinates[1] = (int) y;
                break;
        }
        coordinates[2] = northAngle;

        return coordinates;
    }

    /**
     * Update the location of the phone relative to the beacon
     *
     * @param x   x coordinate
     * @param y   y coordinate
     * @param ang angle
     */
    private void updatePhonePosition(int x, int y, int ang) {
        Log.d(TAG, "findByCoordinates: x=" + x + " y=" + y + " ang=" + ang);
        mWebView.loadUrl("javascript:findByCoordinates(x,y,ang)");
    }

    public void onResume() {
        if (mSensorCompassManager != null) {
            mSensorCompassManager.onResume();
        }
    }

    public void onPause() {
        if (mSensorCompassManager != null) {
            mSensorCompassManager.onPause();
        }
    }
}
