package com.smartcar.service;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.widget.Toast;

import com.smartcar.activity.StartActivity;
import com.smartcar.common.BluetoothService;
import com.smartcar.common.Global;
import com.smartcar.common.Utils;
import com.smartcar.common.bluetooth.Bluetooth;
import com.smartcar.core.MessageId;

public class SmartCarBackgroundService extends BluetoothService {

    private Bluetooth bluetooth;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int res = super.onStartCommand(intent, flags, startId);

        bluetooth = new Bluetooth(this);

        showToast( "Service loading...");

        bluetooth.addMessageHandler(this);
        bluetooth.addDiscoverHandler(this);
        bluetooth.addPairHandler(this);

        String lastConnectedAddr = Utils.getStore(this, Global.BLUETOOTH_REMOVE_ADDRESS_KEY, "");

        assert lastConnectedAddr != null;

        if (lastConnectedAddr.length() != 0) {
            try {
                BluetoothDevice device = bluetooth.getDeviceFromAddress(lastConnectedAddr);
                bluetooth.connect(device);
            } catch (Exception e) {
                showToast(e.getMessage());
            }
        }

        bluetooth.startDiscovering();

        showToast( "Service started");

        sendMessage(MessageId.BACKGROUND_SERVICE_STARTED);

        return res;
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        bluetooth.removeMessageHandler(this);
        bluetooth.removeDiscoverHandler(this);
        bluetooth.removePairHandler(this);

        showToast("Service stopped");

        super.onDestroy();
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
                bluetooth.cancelDiscovering();
                bluetooth.startDiscovering();
                sendMessage(MessageId.STARTED_DISCOVERY);
                break;
            }
            case GET_BLUETOOTH_STATUS: {
                sendMessage(MessageId.BLUETOOTH_STATUS, Boolean.toString(bluetooth.isEnabled()));
                break;
            }
        }
    }
}
