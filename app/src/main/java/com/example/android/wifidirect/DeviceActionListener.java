package com.example.android.wifidirect;

import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;

/**
 * Created by yzhan14 on 11/9/2017.
 */
public interface DeviceActionListener {

    void showDetails(WifiP2pDevice device);

    void cancelDisconnect();

    void connect(WifiP2pConfig config);

    void disconnect();

    void onNewSensorData(String values);

    void startTrial();

    void onTrialStopped();
}
