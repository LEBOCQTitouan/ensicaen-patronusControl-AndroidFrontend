package fr.patronuscontrol.androiduwb.managers;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.uwb.RangingParameters;
import androidx.core.uwb.RangingResult;
import androidx.core.uwb.UwbAddress;
import androidx.core.uwb.UwbComplexChannel;
import androidx.core.uwb.UwbControleeSessionScope;
import androidx.core.uwb.UwbControllerSessionScope;
import androidx.core.uwb.UwbDevice;
import androidx.core.uwb.UwbManager;
import androidx.core.uwb.rxjava3.UwbClientSessionScopeRx;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import fr.patronuscontrol.androiduwb.utils.Utils;
import fr.patronuscontrol.androiduwb.uwb.Protocol;
import fr.patronuscontrol.androiduwb.uwb.UwbDeviceConfigData;
import fr.patronuscontrol.androiduwb.uwb.UwbPhoneConfigData;
import fr.patronuscontrol.androiduwb.uwb.UwbRangingListener;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subscribers.DisposableSubscriber;

public class UwbManagerImpl {
    private UwbManager uwbManager = null;
    private Disposable disposable = null;

    private static UwbManagerImpl mInstance = null;

    /**
     * Private constructor
     * @param context context
     */
    private UwbManagerImpl(final Context context) {
        PackageManager packageManager = context.getPackageManager();
        if (packageManager.hasSystemFeature("android.hardware.uwb")) {
            uwbManager = UwbManager.createInstance(context);
        }
    }

    /**
     * Get the instance of the UwbManagerImpl
     * @param context context
     * @return the instance of the UwbManagerImpl
     */
    public static synchronized UwbManagerImpl getInstance(final Context context) {
        if (mInstance == null) {
            mInstance = new UwbManagerImpl(context);
        }
        return mInstance;
    }

    /**
     * Check if the UWB is supported
     * @return true if the UWB is supported
     */
    public boolean isSupported() {
        return uwbManager != null;
    }

    /**
     * Check if the UWB is enabled
     * @return only true
     */
    public boolean isEnabled() {
        return true;
    }

    /**
     * Start ranging
     * @param uwbPhoneConfigData uwb phone configuration
     * @param uwbDeviceConfigData uwb device configuration
     * @param uwbControleeSessionScope uwb controlee session scope
     * @param uwbControllerSessionScope uwb controller session scope
     * @param uwbRangingListener uwb ranging listener
     */
    public void startRanging(UwbPhoneConfigData uwbPhoneConfigData,
                             UwbDeviceConfigData uwbDeviceConfigData,
                             UwbControleeSessionScope uwbControleeSessionScope,
                             UwbControllerSessionScope uwbControllerSessionScope,
                             UwbRangingListener uwbRangingListener) {
        Thread t = new Thread( () -> {
            byte uwbRangingRole = uwbPhoneConfigData.getDeviceRangingRole();
            byte uwbProfileId = uwbPhoneConfigData.getProfileId();
            int sessionId = uwbPhoneConfigData.getSessionId();
            UwbComplexChannel uwbComplexChannel = new UwbComplexChannel(
                    uwbPhoneConfigData.getChannel(),
                    uwbPhoneConfigData.getPreambleId()
            );

            UwbAddress shieldUwbAddress = new UwbAddress(Utils.revert(uwbDeviceConfigData.getDeviceMacAddress()));
            UwbDevice shieldUwnDevice = new UwbDevice(shieldUwbAddress);

            List<UwbDevice> listUwbDevices = new ArrayList<>();
            listUwbDevices.add(shieldUwnDevice);

            RangingParameters rangingParameters = new RangingParameters(
                    uwbProfileId,
                    sessionId,
                    null,
                    uwbComplexChannel,
                    listUwbDevices,
                    RangingParameters.RANGING_UPDATE_RATE_AUTOMATIC
            );

            Flowable<RangingResult> rangingResultFlowable = null;
            if (uwbRangingRole == Protocol.RANGING_ROLE_CONTROLLER) {
                rangingResultFlowable = UwbClientSessionScopeRx
                        .rangingResultsFlowable(uwbControleeSessionScope, rangingParameters);
            } else if (uwbRangingRole == Protocol.RANGING_ROLE_CONTROLEE) {
                rangingResultFlowable = UwbClientSessionScopeRx
                        .rangingResultsFlowable(uwbControllerSessionScope, rangingParameters);
            }

            assert rangingResultFlowable != null;
            disposable = rangingResultFlowable
                    .subscribeWith(new DisposableSubscriber<RangingResult>() {
                        @Override
                        protected void onStart() {
                            request(1);
                        }

                        @Override
                        public void onNext(RangingResult rangingResult) {
                            uwbRangingListener.onRangingResult(rangingResult);
                            request(1);
                        }

                        @Override
                        public void onError(Throwable t) {
                            uwbRangingListener.onRangingError(t);
                        }

                        @Override
                        public void onComplete() {
                            uwbRangingListener.onRangingComplete();
                        }
                    });
            uwbRangingListener.onRangingStarted();
        });

        t.start();
    }

    /**
     * Get UwbManager
     * @return UwbManager
     */
    public UwbManager getUwbManager() {
        return uwbManager;
    }

    /**
     * Stop ranging
     */
    public void stopRanging() {
        if (disposable != null) {
            disposable.dispose();
        }
    }

    /**
     * Select the first supported profile id
     * @param supportedUwbProfileIds
     * @return
     */
    public byte selectUwbProfileId(int supportedUwbProfileIds) {
        if (BigInteger.valueOf(supportedUwbProfileIds).testBit(RangingParameters.UWB_CONFIG_ID_1)) {
            return (byte) RangingParameters.UWB_CONFIG_ID_1;
        } else if (BigInteger.valueOf(supportedUwbProfileIds).testBit(RangingParameters.UWB_CONFIG_ID_2)) {
            return (byte) RangingParameters.UWB_CONFIG_ID_2;
        } else if (BigInteger.valueOf(supportedUwbProfileIds).testBit(RangingParameters.UWB_CONFIG_ID_3)) {
            return (byte) RangingParameters.UWB_CONFIG_ID_3;
        }

        return 0;
    }

    /**
     * Select the first supported ranging role
     * @param supportedUwbRangingRoles
     * @return
     */
    public byte selectUwbDeviceRangingRole(int supportedUwbRangingRoles) {
        if (BigInteger.valueOf(supportedUwbRangingRoles).testBit(0)) {
            return 1;
        } else if (BigInteger.valueOf(supportedUwbRangingRoles).testBit(1)) {
            return 2;
        }

        return 0;
    }
}
