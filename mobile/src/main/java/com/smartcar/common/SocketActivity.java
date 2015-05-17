package com.smartcar.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.smartcar.core.MessageId;

public abstract class SocketActivity extends ReferencedActivity {

    public static SocketActivity getActive(Context context) {
        return (SocketActivity) ReferencedActivity.getActive(context);
    }

    public boolean isCarConnected() {
        return Wearable.NodeApi.getConnectedNodes(mGoogleClient).await().getNodes().size() > 0;
    }

    // Send a message when the data layer connection is successful.
    @Override
    public void onConnected(Bundle connectionHint) {
        onConnectionChange(true);
    }

    @Override
    public void onConnectionSuspended(int i) {
        onConnectionChange(false);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        onConnectionChange(false);
    }

    protected void onConnectionChange(boolean connected) {
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleClient.connect();
    }

    // Disconnect from the data layer when the Activity stops
    @Override
    protected void onStop() {
        if (null != mGoogleClient && mGoogleClient.isConnected()) {
            mGoogleClient.disconnect();
        }
        super.onStop();
    }

    public void sendMessage(MessageId messageId) {
        new SendMessage("/message_path", messageId, "").start();
    }

    public void sendMessage(MessageId messageId, String message) {
        new SendMessage("/message_path", messageId, message).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Build a new GoogleApiClient
        mGoogleClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

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

    private class SendMessage extends Thread {

        String path;
        String message;

        // Constructor to send a message to the data layer
        SendMessage(String p, MessageId msgId, String msg) {
            path = p;
            message = (msgId + "|" + msg);
        }

        public void run() {
            NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleClient).await();
            for (Node node : nodes.getNodes()) {
                MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(mGoogleClient, node.getId(), path, message.getBytes()).await();
                if (result.getStatus().isSuccess()) {
                    Log.w("SendMessage", "Message: {" + message + "} sent to: " + node.getDisplayName());
                } else {
                    // Log an error
                    Log.w("SendMessage", "ERROR: failed to send Message");
                }
            }
        }
    }
}
