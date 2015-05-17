package com.smartcar.common;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.smartcar.core.MessageId;

public abstract class ListenerService extends Service implements IMessageHandler {

    private Handler mHandler;

    public ListenerService() {
        super();
        mHandler = new Handler();
    }

    public final void runOnUiThread(Runnable action) {
        mHandler.post(action);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onMessage(int id, String message) {
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
            handleMessage(mid, msg);
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

    protected abstract void handleMessage(MessageId id, String msg);
}
