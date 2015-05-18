package com.smartcar.common.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.smartcar.common.ListenerService;
import com.smartcar.core.MessageId;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * Created by Mathew on 5/17/2015.
 */
public class Bluetooth {

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothMessageListener messageListener;
    private Thread discoverThread;
    private Runnable dataTransferThread;
    private Set<IBluetoothDiscoverHandler> discoverHandlers;
    private Set<IBluetoothMessageHandler> messageHandlers;
    private Set<IBluetoothPairHandler> pairHandlers;

    public Bluetooth(Context context) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        discoverHandlers = new HashSet<IBluetoothDiscoverHandler>();
        messageHandlers = new HashSet<IBluetoothMessageHandler>();
        pairHandlers = new HashSet<IBluetoothPairHandler>();

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

    public IBluetoothMessageHandler getMessageHandler() {
        return messageListener.getMessageHandler();
    }

    public void setMessageHandler(IBluetoothMessageHandler messageHandler) {
        this.messageListener.setMessageHandler(messageHandler);
    }

    public boolean isEnabled() {
        return bluetoothAdapter.isEnabled();
    }

    public void startDiscovering() {
        if(discoverThread == null) {
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

    public void cancelDiscovering() {
        if(discoverThread != null){
            discoverThread.interrupt();
            discoverThread = null;
        }

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
    }

    public void startListening(BluetoothDevice device) {
        // TODO Clean up
        BluetoothSocket socket;
        final InputStream in;
        final OutputStream out;
        if ((socket = connect(device)) != null) {
            try {
                in = socket.getInputStream();
                out = socket.getOutputStream();
                dataTransferThread = new Runnable() {
                    @Override
                    public void run() {
                        byte[] buffer = new byte[256];

                        while (true) {
                            try {
                                if (in.read() > 0) {
                                    in.read(buffer);
                                    String data = buffer.toString();
                                    // broadcast this data as received message with ID RECEIVED_DATA
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    public void sendMessage(MessageId id, String msg) {
                        try {
                            out.write((id + "|" + msg).getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    public void sendMessage(MessageId id) {
                        try {
                            out.write(id.toString().getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };
                new Thread(dataTransferThread).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    public BluetoothSocket connect(BluetoothDevice device) {
        BluetoothSocket socket = null;
        try {
            socket = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            socket.connect();
            messageListener = new BluetoothMessageListener(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return socket;
    }

    public BluetoothDevice getDeviceFromAddress(String msg) {
        return BluetoothAdapter.getDefaultAdapter().getRemoteDevice(msg);
    }

    public void sendMessage(MessageId id, String msg) {

    }

}
