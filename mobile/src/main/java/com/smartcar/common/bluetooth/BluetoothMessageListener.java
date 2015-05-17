package com.smartcar.common.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

public class BluetoothMessageListener implements Runnable {

    private BluetoothSocket socket;
    private IBluetoothMessageHandler messageHandler;

    public BluetoothMessageListener(BluetoothSocket socket) {
        this.socket = socket;
    }

    public BluetoothMessageListener(BluetoothSocket socket,
                                    IBluetoothMessageHandler handler) {
        this(socket);
        this.messageHandler = handler;
    }

    public IBluetoothMessageHandler getMessageHandler() {
        return messageHandler;
    }

    public void setMessageHandler(IBluetoothMessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public void run() {
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        try {
            InputStream instream = socket.getInputStream();
            int bytesRead = -1;
            String message = "";
            while (true) {
                message = "";
                bytesRead = instream.read(buffer);
                if (bytesRead != -1) {
                    while ((bytesRead == bufferSize) && (buffer[bufferSize - 1] != 0)) {
                        message = message + new String(buffer, 0, bytesRead);
                        bytesRead = instream.read(buffer);
                    }

                    message = message + new String(buffer, 0, bytesRead - 1);

                    Log.e(getClass().getName(), "Incomplete: Parse message id in the future!");

                    if (messageHandler != null) {
                        messageHandler.onMessage(0, message);
                    }

                    socket.getInputStream();
                }
            }
        } catch (IOException e) {
            Log.d("BLUETOOTH_COMMS", e.getMessage());
        }
    }
}
