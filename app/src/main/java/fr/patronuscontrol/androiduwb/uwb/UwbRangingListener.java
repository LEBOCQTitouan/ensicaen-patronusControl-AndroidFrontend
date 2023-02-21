package fr.patronuscontrol.androiduwb.uwb;

import androidx.core.uwb.RangingResult;

public interface UwbRangingListener {
    void onRangingStarted();

    void onRangingResult(RangingResult rangingResult);

    void onRangingError(Throwable error);

    void onRangingComplete();
}
