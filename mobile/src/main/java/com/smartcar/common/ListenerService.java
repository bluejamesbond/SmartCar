package com.smartcar.common;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.smartcar.common.bluetooth.IBluetoothDiscoverHandler;
import com.smartcar.common.bluetooth.IBluetoothMessageHandler;
import com.smartcar.common.bluetooth.IBluetoothPairHandler;
import com.smartcar.core.MessageId;

public abstract class ListenerService extends Service implements IBluetoothMessageHandler, IBluetoothDiscoverHandler, IBluetoothPairHandler, IMessageReceiverHandler {

    public ListenerService() {
        super();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        MessageReceiver messageReceiver = new MessageReceiver(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onMessage(String message) {
        // TODO Optimize
        int separator = message.indexOf('|');
        final MessageId mid = MessageId.valueOf(message.substring(0, separator));
        final String msg = message.substring(separator + 1);

        // send to all the open applications
        Intent messageIntent = new Intent();
        messageIntent.setAction(Intent.ACTION_SEND);
        messageIntent.putExtra("id", mid.name());
        messageIntent.putExtra("message", msg);
        LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);

        // send to the service
        onMessageReceived(mid, msg);
    }

    protected void sendMessage(MessageId id) {
        getActivity().sendMessage(id);
    }

    protected SocketActivity getActivity() {
        return SocketActivity.getActive(this);
    }

    protected void sendMessage(MessageId id, String msg) {
        getActivity().sendMessage(id, msg);
    }

    public void startActivity(Class actvity) {
        Intent intent = new Intent(this, actvity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onDiscoveredDevice(BluetoothDevice device) {
        onMessage(MessageId.DISCOVERED_BLUETOOTH_DEVICE.toString() + "|" + device.getAddress());
    }

    @Override
    public void onPairedDevice(BluetoothDevice device) {
        onMessage(MessageId.PAIRED_BLUETOOTH_DEVICE.toString() + "|" + device.getAddress());
    }
}
