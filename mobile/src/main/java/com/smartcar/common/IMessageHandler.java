package com.smartcar.common;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

public interface IMessageHandler {
    public void onMessage(int id, String message);
}
