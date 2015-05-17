package com.smartcar.common.bluetooth;

import android.bluetooth.BluetoothDevice;

public interface IBluetoothDiscoverHandler {
    public void onDiscoveredDevice(BluetoothDevice device);
}
