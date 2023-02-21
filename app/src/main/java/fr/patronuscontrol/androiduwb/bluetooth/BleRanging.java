package fr.patronuscontrol.androiduwb.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import fr.patronuscontrol.androiduwb.MainActivity;
import fr.patronuscontrol.androiduwb.managers.BluetoothManagerImpl;
import fr.patronuscontrol.androiduwb.managers.LocationManagerImpl;
import fr.patronuscontrol.androiduwb.utils.Utils;
import fr.patronuscontrol.androiduwb.uwb.Protocol;
import fr.patronuscontrol.androiduwb.uwb.UwbDeviceConfigData;
import fr.patronuscontrol.androiduwb.uwb.UwbJetPack;
import fr.patronuscontrol.androiduwb.uwb.UwbPhoneConfigData;

public class BleRanging implements BluetoothManagerImpl.BluetoothConnectionListener
        , BluetoothManagerImpl.BluetoothDataReceivedListener {
    private static final String TAG = BleRanging.class.getSimpleName();

    private final Context mContext;
    private final MainActivity mMainActivity;

    private final BluetoothManagerImpl mBluetoothManagerImpl;
    private final LocationManagerImpl mLocationManagerImpl;

    private UwbJetPack mUwbConfigJetPack = null;

    private final List<BluetoothDevice> deviceList;

    public BleRanging(MainActivity mainActivity) {
        mContext = mainActivity;
        mBluetoothManagerImpl = BluetoothManagerImpl.getInstance(mContext, this);
        mLocationManagerImpl = LocationManagerImpl.getInstance(mContext);
        mMainActivity = mainActivity;

        deviceList = new ArrayList<>();
    }

    /**
     * Ask the user to enable the Bluetooth
     */
    public void enableBluetooth() {
        if (!mBluetoothManagerImpl.isSupported()) {
            // Device doesn't support Bluetooth
            Toast.makeText(mContext, "error: Bluetooth not supported by the phone", Toast.LENGTH_LONG).show();
            return;
        }
        if (!mBluetoothManagerImpl.isEnabled()) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // Permission was not granted by user
                Toast.makeText(mContext, "error: Permission to enable Bluetooth not granted\n" +
                        "Enable Bluetooth manually or restart the application", Toast.LENGTH_LONG).show();
            }
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mMainActivity.getmActivityResultLauncher().launch(enableBluetoothIntent);
        }
    }

    public boolean initializeBLECounterpart() {
        if (!mBluetoothManagerImpl.isSupported()
                || !mLocationManagerImpl.isSupported()) {
            Toast.makeText(mContext, "Missing BLE", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!mBluetoothManagerImpl.isEnabled()) {
            Log.d(TAG, "Bluetooth not enabled");
            enableBluetooth();
            return false;
        }
        if (!mLocationManagerImpl.isEnabled()) {
            Intent enableLocationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            mMainActivity.getmActivityResultLauncher().launch(enableLocationIntent);
            return false;
        }

        //TODO multiple session : also check if already connected
        Log.d(TAG, "Start Bluetooth LE Device scanning");
        startBLEScanning();

        return true;
    }

    public void startBLEScanning() {
        mBluetoothManagerImpl.startLeDeviceScan(device -> {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                // Ignore devices that do not define name or address
                if ((device.getName() == null)
                        || (device.getAddress() == null)
                        || (deviceList.contains(device))) {
                    return;
                }

                Log.d(TAG, "Let's proceed to connect to: " + device.getName());

                // Stop scanning and further proceed to connect to the device
                mBluetoothManagerImpl.stopLeDeviceScan();

                pairDevice(device);

                mBluetoothManagerImpl.connect(device.getAddress());
                deviceList.add(device);
            } else {
                Log.e(TAG, "Missing required permission to read Bluetooth device name!", new Exception());
            }
        });
    }

    /**
     * Method which ask for a BLE pairing between the application and the device in parameter.
     *
     * @param device The device which need to be paired.
     */
    @SuppressWarnings("CommentedOutCode")
    private void pairDevice(BluetoothDevice device) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothManagerImpl.getPairedDevices();

        if (pairedDevices.contains(device)) {
            Log.d(TAG, "Device already paired");
            return;
        }

        Log.d(TAG, "Start Pairing... with: " + device.getName());
//        /*
//        try {
//            Method m = device.getClass()
//                    .getMethod("removeBond", (Class[]) null);
//            m.invoke(device, (Object[]) null);
//        } catch (Exception e) {
//            Log.e(TAG, "Pairing Device removeBond Error", e);
//        }
//        */

        try {
            Method m = device.getClass()
                    .getMethod("createBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
        } catch (Exception e) {
            Log.e(TAG, "Pairing Device createBond Error", e);
        }
    }

    public void processBLEConfigurationData(byte[] data) {
        byte messageId = data[0];
        Log.d(TAG, "processBLEConfigurationData: " + Arrays.toString(data));

        if (messageId == Protocol.MessageId.uwbDeviceConfigurationData.getValue()) {
            byte[] trimmedData = Utils.trimLeadingBytes(data, 1);
            configureAndStartUwbRangingSession(trimmedData);
        } else if (messageId == Protocol.MessageId.uwbDidStart.getValue()) {
            // todo uwbRangingSessionStarted();
            startBLEScanning();
            mUwbConfigJetPack.startUwbPhone();
        } else if (messageId == Protocol.MessageId.uwbDidStop.getValue()) {
            // todo uwbRangingSessionStopped();
        } else {
            throw new IllegalArgumentException("Unexpected value");
        }
    }

    public void transmitUwbPhoneConfigData(UwbPhoneConfigData uwbPhoneConfigData) {
        mBluetoothManagerImpl.transmit(Utils.concat(
                new byte[]{Protocol.MessageId.uwbPhoneConfigurationData.getValue()},
                uwbPhoneConfigData.toByteArray()));
    }

    public void configureAndStartUwbRangingSession(byte[] data) {
        Log.d(TAG, "UWB Configure UwbDeviceConfigData: " + Utils.byteArrayToHexString(data));

        final UwbDeviceConfigData uwbDeviceConfigData = UwbDeviceConfigData.fromByteArray(data);

        new Thread(() -> {
            try {
                mUwbConfigJetPack = new UwbJetPack(mContext, uwbDeviceConfigData);

                mBluetoothManagerImpl.transmit(Utils.concat(
                        new byte[]{Protocol.MessageId.uwbPhoneConfigurationData.getValue()},
                        mUwbConfigJetPack.getmUwbPhoneConfigData().toByteArray()));
            } catch (Exception ignored) {
                Log.d(TAG, "No JETPACK UWB found.");
                Toast.makeText(mContext, "No JETPACK UWB found.", Toast.LENGTH_LONG).show();
            }
        }).start();
    }

    @Override
    public void onConnect(String remoteDeviceName) {
        Toast.makeText(mContext, "Bluetooth connected!", Toast.LENGTH_LONG).show();
        mBluetoothManagerImpl.transmit(new byte[]{Protocol.MessageId.initialize.getValue()});
    }

    @Override
    public void onDisconnect() {
        Log.d(TAG, "Device " + mBluetoothManagerImpl.getRemoteDevice().getAddress() + " disconnected !");
        deviceList.remove(mBluetoothManagerImpl.getRemoteDevice());
        if (!mBluetoothManagerImpl.isDiscovering()) {
            startBLEScanning();
        }
        //todo close UWB ranging
    }

    @Override
    public void onDataReceived(byte[] data) {
        processBLEConfigurationData(data);
    }

    public List<BluetoothDevice> getDeviceList() {
        return deviceList;
    }
}
