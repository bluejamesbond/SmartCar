package com.smartcar.service;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;

import com.smartcar.activity.StartActivity;
import com.smartcar.common.ListenerService;
import com.smartcar.common.Global;
import com.smartcar.common.Utils;
import com.smartcar.common.bluetooth.Bluetooth;
import com.smartcar.core.MessageId;

public class SmartCarListenerService extends ListenerService {

    private Bluetooth bluetooth;
    private BluetoothDevice device;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        bluetooth.addMessageHandler(this);
        bluetooth.addDiscoverHandler(this);
        bluetooth.addPairHandler(this);

        bluetooth.startDiscovering();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        bluetooth.removeMessageHandler(this);
        bluetooth.removeDiscoverHandler(this);
        bluetooth.removePairHandler(this);
    }

    @Override
    public void onMessageReceived(MessageId id, String msg) {
        switch (id) {
            case OPEN_HOME_ACTIVITY: {
                Intent intent = new Intent(this, StartActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            }
            case START_DISCOVERY: {
                bluetooth.startDiscovering();
                sendMessage(MessageId.STARTED_DISCOVERY);
                break;
            }
            case DISCOVERED_BLUETOOTH_DEVICE: {
                BluetoothDevice device = bluetooth.getDeviceFromAddress(msg);
                String remoteAddr = Utils.getStore(this, Global.BLUETOOTH_REMOVE_ADDRESS_KEY, "");

                if (remoteAddr.length() != 0 && device == null) {
                    bluetooth.connect(device);
                    bluetooth.listen();
                    bluetooth.sendMessage(MessageId.OPEN_DOOR, "strsfsfs");
                    bluetooth.sendMessage(MessageId.OPEN_DOOR);
                }
            }
        }

    }
}
