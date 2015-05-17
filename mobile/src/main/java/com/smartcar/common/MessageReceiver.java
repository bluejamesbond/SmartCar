package com.smartcar.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.smartcar.core.MessageId;

/**
 * Created by Mathew on 5/17/2015.
 */


public class MessageReceiver extends BroadcastReceiver {
    IMessageReceiverHandler messageReceiverHandler;
    public MessageReceiver(IMessageReceiverHandler messageReceiverHandler){
        this.messageReceiverHandler = messageReceiverHandler;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        MessageId id = MessageId.valueOf(intent.getStringExtra("id"));
        String message = intent.getStringExtra("message");
        messageReceiverHandler.onMessageReceived(id, message);
        Toast.makeText(context, id + (message.length() == 0 ? "" : ": " + message), Toast.LENGTH_SHORT).show();
    }
}