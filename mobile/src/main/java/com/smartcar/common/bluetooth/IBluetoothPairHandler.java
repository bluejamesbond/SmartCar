package com.smartcar.common.bluetooth;

import android.bluetooth.BluetoothDevice;

public interface IBluetoothPairHandler {
    public void onPairedDevice(BluetoothDevice device);
}
