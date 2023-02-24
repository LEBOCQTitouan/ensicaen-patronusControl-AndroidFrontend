package fr.patronuscontrol.androiduwb.uwb;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;

import androidx.core.uwb.RangingResult;
import androidx.core.uwb.UwbAddress;
import androidx.core.uwb.UwbControleeSessionScope;
import androidx.core.uwb.UwbControllerSessionScope;
import androidx.core.uwb.rxjava3.UwbManagerRx;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import fr.patronuscontrol.androiduwb.MainActivity;
import fr.patronuscontrol.androiduwb.managers.UwbManagerImpl;
import fr.patronuscontrol.androiduwb.utils.Utils;
import io.reactivex.rxjava3.core.Single;

public class UwbJetPack {
    private final String TAG = UwbJetPack.class.getName();

    private final Context mContext;

    private final UwbManagerImpl mUwbManagerImpl;

    public static final int UWB_CHANNEL = 9;
    public static final int UWB_PREAMBLE_INDEX = 10;

    private UwbDeviceConfigData mUwbDeviceConfigData;
    private UwbPhoneConfigData mUwbPhoneConfigData;
    private Map<String, BluetoothDevice> mBLEDeviceMap;

    private Single<UwbControllerSessionScope> controllerSessionScopeSingle = null;
    private UwbControllerSessionScope controllerSessionScope = null;
    private Single<UwbControleeSessionScope> controleeSessionScopeSingle = null;
    private UwbControleeSessionScope controleeSessionScope = null;

    public UwbJetPack(Context context, UwbDeviceConfigData uwbDeviceConfigData) {
        mContext = context;

        mUwbManagerImpl = UwbManagerImpl.getInstance(context);
        mUwbDeviceConfigData = uwbDeviceConfigData;
        mUwbPhoneConfigData = new UwbPhoneConfigData();

        int sessionId = new Random().nextInt();
        Log.d(TAG, "UWB SessionID : " + sessionId);
        byte uwbDeviceRangingRole = mUwbManagerImpl.selectUwbDeviceRangingRole(uwbDeviceConfigData.getSupportedDeviceRangingRoles());
        Log.d(TAG, "UWB device supported ranging roles : " + uwbDeviceConfigData.getSupportedDeviceRangingRoles()
        + ", selected uwb ranging roles: " + uwbDeviceRangingRole);
        byte uwbProfileID = mUwbManagerImpl.selectUwbProfileId(uwbDeviceConfigData.getSupportedUwbProfileIds());
        Log.d(TAG, "UWB device supported uwb profiles ID : " + uwbDeviceConfigData.getSupportedUwbProfileIds()
                + ", selected uwb profile ID : " + uwbProfileID);
        UwbAddress localAddress = getLocalAddress(uwbDeviceRangingRole);

        mUwbPhoneConfigData = new UwbPhoneConfigData();
        mUwbPhoneConfigData.setSpecVerMajor((short) 0x0100);
        mUwbPhoneConfigData.setSpecVerMinor((short) 0x0000);
        mUwbPhoneConfigData.setSessionId(sessionId);
        mUwbPhoneConfigData.setPreambleId((byte) UWB_PREAMBLE_INDEX);
        mUwbPhoneConfigData.setChannel((byte) UWB_CHANNEL);
        mUwbPhoneConfigData.setProfileId(uwbProfileID);
        mUwbPhoneConfigData.setDeviceRangingRole(uwbDeviceRangingRole);
        mUwbPhoneConfigData.setPhoneMacAddress(Utils.revert(localAddress.getAddress()));

        mBLEDeviceMap = null;
    }

    public void setBLEDeviceList(List<BluetoothDevice> bleDeviceList, byte[] deviceMacAddress){
        deviceMacAddress = Utils.revert(deviceMacAddress);
        if (mBLEDeviceMap == null && bleDeviceList.size() == 1) {
            mBLEDeviceMap = new HashMap<>();
            mBLEDeviceMap.put(Utils.byteArrayToHexString(deviceMacAddress), bleDeviceList.get(0));
        } else if (mBLEDeviceMap != null) {
            mBLEDeviceMap.put(Utils.byteArrayToHexString(deviceMacAddress), bleDeviceList.get(bleDeviceList.size()-1));
        }
    }

    /**
     * Start UWB ranging
     */
    public void startUwbPhone() {
        mUwbManagerImpl.startRanging(mUwbPhoneConfigData,
                mUwbDeviceConfigData,
                controleeSessionScope,
                controllerSessionScope,
                new UwbRangingListener() {
                    @Override
                    public void onRangingStarted() {
                        // Do nothing
                        Log.d(TAG, "UWB Started");
                    }

                    @Override
                    public void onRangingResult(RangingResult rangingResult) {
                        RangingResult.RangingResultPosition rangingResultPosition =
                                (RangingResult.RangingResultPosition) rangingResult;
                        if (rangingResultPosition.getPosition().getDistance() != null) {
                            int distance = (int) (Objects.requireNonNull(rangingResultPosition
                                            .getPosition()
                                            .getDistance()).getValue() * 100);
                            int angleAzimuth = (int) Objects.requireNonNull(rangingResultPosition
                                                .getPosition()
                                                .getAzimuth()).getValue();
                            int angleElevation = (int) Objects.requireNonNull(rangingResultPosition
                                    .getPosition()
                                    .getElevation()).getValue();

                            //TODO display
//                                    "Distance : " + distance + " cm\n" +
//                                    "Angle Az : " + angleAzimuth + "°\n" +
//                                    "Angle El : " + angleElevation + "°\n"

                            int angle = 20;

                            String macAddress = Utils.byteArrayToHexString(
                                    rangingResult.getDevice().getAddress().getAddress());

                            if (Math.abs(angleAzimuth) < angle) {
                                ((MainActivity) mContext).lockVibration(
                                        Objects.requireNonNull(mBLEDeviceMap.get(macAddress)).getAddress());
                            } else {
                                ((MainActivity) mContext).unlockVibration();
                            }
                        } else {
                            Log.d(TAG, "Position is null");
                        }
                    }

                    @Override
                    public void onRangingError(Throwable error) {
                        // TODO : display error
                    }

                    @Override
                    public void onRangingComplete() {
                        // TODO : Do nothing
                    }
                });
    }

    public void stopUWBPhone() {
        mUwbManagerImpl.stopRanging();
    }

    /**
     * Get local address
     * @param uwbDeviceRangingRole uwb ranging role
     * @return local uwb address
     */
    private UwbAddress getLocalAddress(byte uwbDeviceRangingRole) {
        UwbAddress localAddress = null;
        if (uwbDeviceRangingRole == Protocol.RANGING_ROLE_CONTROLLER) {
            Log.d(TAG, "Android phone will act as a Controlee");
            controleeSessionScopeSingle = UwbManagerRx.controleeSessionScopeSingle(mUwbManagerImpl.getUwbManager());
            controleeSessionScope = controleeSessionScopeSingle.blockingGet();
            localAddress = controleeSessionScope.getLocalAddress();
        } else if (uwbDeviceRangingRole == Protocol.RANGING_ROLE_CONTROLEE) {
            Log.d(TAG, "Android phone will act as a Controller");
            controllerSessionScopeSingle = UwbManagerRx.controllerSessionScopeSingle(mUwbManagerImpl.getUwbManager());
            controllerSessionScope = controllerSessionScopeSingle.blockingGet();
            localAddress = controllerSessionScope.getLocalAddress();
        }
        return localAddress;
    }

    /**
     * Get UWB phone config data
     * @return UWB phone config data
     */
    public UwbPhoneConfigData getmUwbPhoneConfigData() {
        return mUwbPhoneConfigData;
    }
}
