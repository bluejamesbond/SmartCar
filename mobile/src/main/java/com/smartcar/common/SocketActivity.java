package com.smartcar.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.smartcar.common.bluetooth.Bluetooth;
import com.smartcar.core.MessageId;

public abstract class SocketActivity extends ReferencedActivity {

    public static SocketActivity getActive(Context context) {
        return (SocketActivity) ReferencedActivity.getActive(context);
    }

    public boolean isConnected() {
        // TODO update this as necessary
        return Bluetooth.isEnabled();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void sendMessage(MessageId messageId) {
        Bluetooth.sendMessage(messageId + "|");
    }

    public void sendMessage(MessageId messageId, String message) {
        Bluetooth.sendMessage(messageId + "|" + message);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        MessageReceiver messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);

        setContentView(getContentViewId());
    }

    protected abstract int getContentViewId();

    @SuppressWarnings("unused")
    protected void onMessageReceived(MessageId id, String message) {
    }


    private class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            MessageId id = MessageId.valueOf(intent.getStringExtra("id"));
            String message = intent.getStringExtra("message");
            onMessageReceived(id, message);
            Toast.makeText(context, id + (message.length() == 0 ? "" : ": " + message), Toast.LENGTH_SHORT).show();
        }
    }
}
