package com.smartcar.common.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.smartcar.core.MessageId;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Bluetooth implements Runnable {

    private BluetoothAdapter bluetoothAdapter;
    private Lock lock;
    private Thread listenerThread;
    private Thread discoverThread;
    private Set<IBluetoothDiscoverHandler> discoverHandlers;
    private Set<IBluetoothMessageHandler> messageHandlers;
    private Set<IBluetoothPairHandler> pairHandlers;
    private BluetoothSocket activeSocket;

    public Bluetooth(Context context) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        discoverHandlers = new HashSet<>();
        messageHandlers = new HashSet<>();
        pairHandlers = new HashSet<>();

        context.registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (discoverHandlers != null) {
                        for (IBluetoothDiscoverHandler handler : discoverHandlers) {
                            handler.onDiscoveredDevice(device);
                        }
                    }
                } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (pairHandlers != null) {
                        for (IBluetoothPairHandler handler : pairHandlers) {
                            handler.onPairedDevice(device);
                        }
                    }
                }
            }
        }, new IntentFilter(BluetoothDevice.ACTION_FOUND));
    }

    public boolean isEnabled() {
        return bluetoothAdapter.isEnabled();
    }

    public void startDiscovering() {
        if (discoverThread == null) {
            discoverThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Timer timer = new Timer();
                    timer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            bluetoothAdapter.cancelDiscovery();
                            bluetoothAdapter.startDiscovery();
                        }
                    }, 0, 40000);
                }
            });

            discoverThread.start();
        }

    }

    public void cancelDiscovery() {
        if (discoverThread != null) {
            discoverThread.interrupt();
            discoverThread = null;
        }

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
    }

    public void addMessageHandler(IBluetoothMessageHandler handler) {
        messageHandlers.add(handler);
    }

    public void addDiscoverHandler(IBluetoothDiscoverHandler handler) {
        discoverHandlers.add(handler);
    }

    public void addPairHandler(IBluetoothPairHandler handler) {
        pairHandlers.add(handler);
    }

    public void removeMessageHandler(IBluetoothMessageHandler handler) {
        messageHandlers.remove(handler);
    }

    public void removeDiscoverHandler(IBluetoothDiscoverHandler handler) {
        discoverHandlers.remove(handler);
    }

    public void removePairHandler(IBluetoothPairHandler handler) {
        pairHandlers.remove(handler);
    }

    public void connect(BluetoothDevice device) {
        try {
            activeSocket = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            activeSocket.connect();

            if (listenerThread != null) {
                listenerThread.interrupt();
                lock = new ReentrantLock();
            }

            if (lock == null) {
                lock = new ReentrantLock();
            }

            listenerThread = new Thread(this);
            listenerThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BluetoothDevice getDeviceFromAddress(String msg) {
        return BluetoothAdapter.getDefaultAdapter().getRemoteDevice(msg);
    }

    public void sendMessage(final MessageId id, final String msg) {

        if (activeSocket == null) {
            return;
        }

        if (lock == null) {
            lock = new ReentrantLock();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    lock.lock();
                    OutputStream os = activeSocket.getOutputStream();
                    os.write((id.toString() + "|" + msg).getBytes());
                    os.flush();
                    os.close();  // FIXME close or not?
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
        });
    }

    public void disconnect() {
        if (listenerThread != null) {
            listenerThread.interrupt();
            listenerThread = null;
        }

        cancelDiscovery();
    }

    @Override
    public void run() {
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        try {

            InputStream instream = activeSocket.getInputStream();
            int bytesRead;
            String message;

            for (; ; ) {
                try {
                    lock.lock();
                    message = "";
                    bytesRead = instream.read(buffer);
                    if (bytesRead != -1) {
                        while ((bytesRead == bufferSize) && (buffer[bufferSize - 1] != 0)) {
                            message = message + new String(buffer, 0, bytesRead);
                            bytesRead = instream.read(buffer);
                        }

                        message = message + new String(buffer, 0, bytesRead - 1);

                        Log.e(getClass().getName(), "Incomplete: Parse message id in the future!");

                        for (IBluetoothMessageHandler messageHandler : messageHandlers)
                            messageHandler.onMessage(message);
                    }

                    activeSocket.getInputStream();

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

}
