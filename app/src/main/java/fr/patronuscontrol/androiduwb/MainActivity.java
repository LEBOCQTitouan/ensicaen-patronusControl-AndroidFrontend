package fr.patronuscontrol.androiduwb;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import fr.patronuscontrol.androiduwb.bluetooth.BleRanging;
import fr.patronuscontrol.androiduwb.managers.UwbManagerImpl;

public class MainActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> mActivityResultLauncher;
    private UwbManagerImpl uwbManagerImpl;
    private BleRanging bleRanging;
    private Vibrator mVibrator;
    private boolean isVibrating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.webview);

        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.loadUrl("http://192.168.1.11");

        WebSettings webSettings = myWebView.getSettings();

        myWebView.loadUrl("javascript:deviceUpdate(\"1\")");

        startUWBBLE();

        mVibrator = ((VibratorManager) getSystemService(Context.VIBRATOR_MANAGER_SERVICE))
                .getDefaultVibrator();

        mActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        // TODO : do something with the data
                    }
                }
        );

        if (!checkPermission()) {
            requestPermissions();
        }
    }

    /**
     * Make the phone vibrate with a lock pattern
     */
    public void lockVibration() {
        if (!isVibrating) {
            VibrationEffect v = VibrationEffect.startComposition()
                    .addPrimitive(VibrationEffect.Composition.PRIMITIVE_SLOW_RISE, 0.5f)
                    .addPrimitive(VibrationEffect.Composition.PRIMITIVE_QUICK_FALL, 0.5f)
                    .addPrimitive(VibrationEffect.Composition.PRIMITIVE_TICK, 1.0f)
                    .compose();
            mVibrator.vibrate(v);
            isVibrating = true;
        }
    }

    /**
     * Make the phone vibrate with an unlock pattern
     */
    public void unlockVibration() {
        if (isVibrating) {
            VibrationEffect v = VibrationEffect.startComposition()
                    .addPrimitive(VibrationEffect.Composition.PRIMITIVE_QUICK_RISE, 0.5f)
                    .addPrimitive(VibrationEffect.Composition.PRIMITIVE_THUD, 0.5f)
                    .compose();
            mVibrator.vibrate(v);
            isVibrating = false;
        }
    }

    public void startUWBBLE() {
        uwbManagerImpl = UwbManagerImpl.getInstance(this);
        bleRanging = new BleRanging(this);

        final boolean[] isReady = {false};
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isReady[0] = bleRanging.initializeBLECounterpart();
                if (isReady[0]) {
                    handler.removeCallbacks(this);
                } else {
                    handler.postDelayed(this, 4000);
                }
            }
        }, 0);
    }

    public ActivityResultLauncher<Intent> getmActivityResultLauncher() {
        return mActivityResultLauncher;
    }

    public boolean checkPermission() {
        boolean result;

        result = (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                &&(ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(this, Manifest.permission.UWB_RANGING) == PackageManager.PERMISSION_GRANTED);

        return result;
    }

    public void requestPermissions() {
        final int requestPermission = 5;
        String[] permissionArray = {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.UWB_RANGING
        };

        ActivityCompat.requestPermissions(this, permissionArray, requestPermission);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        System.exit(0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
