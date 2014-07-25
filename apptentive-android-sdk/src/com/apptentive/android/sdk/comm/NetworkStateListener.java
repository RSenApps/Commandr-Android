package com.apptentive.android.sdk.comm;

import android.net.NetworkInfo;

/**
 * @author Sky Kelsey.
 */
public interface NetworkStateListener {
    public void stateChanged(NetworkInfo networkInfo);
}
