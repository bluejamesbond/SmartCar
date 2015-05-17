package com.smartcar.common.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by Mathew on 5/17/2015.
 */
public class Bluetooth {

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothMessageListener messageListener;
    private IBluetoothDiscoverHandler discoverHandler;
    private IBluetoothPairHandler pairHandler;

    public Bluetooth(Context context) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        context.registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (discoverHandler != null) {
                        discoverHandler.onDiscoveredDevice(device);
                    }
                } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (pairHandler != null) {
                        pairHandler.onPairedDevice(device);
                    }
                }
            }
        }, new IntentFilter(BluetoothDevice.ACTION_FOUND));
    }

    public boolean isEnabled() {
        return bluetoothAdapter.isEnabled();
    }

    public void startDiscovering(final IBluetoothDiscoverHandler discoverHandler) {
        bluetoothAdapter.startDiscovery();

    }

    public void cancelDiscovering() {
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
    }

    public void startListening() {
        messageListener = new BluetoothMessageListener(bluetoothAdapter);
    }
}
