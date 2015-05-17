package com.smartcar.common;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.smartcar.core.MessageId;

public abstract class SocketActivity extends ReferencedActivity implements IMessageReceiverHandler {

    public static SocketActivity getActive(Context context) {
        return (SocketActivity) ReferencedActivity.getActive(context);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void sendMessage(MessageId messageId) {
        sendMessage(messageId, "");
    }

    public void sendMessage(MessageId mid, String msg) {
        // send to all the open applications
        Intent messageIntent = new Intent();
        messageIntent.setAction(Intent.ACTION_SEND);
        messageIntent.putExtra("id", mid.name());
        messageIntent.putExtra("message", msg);
        LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        MessageReceiver messageReceiver = new MessageReceiver(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);

        setContentView(getContentViewId());
    }

    protected abstract int getContentViewId();
}
