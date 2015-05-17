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

import java.io.IOException;
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
    //private IBluetoothDiscoverHandler discoverHandler;
    //private IBluetoothPairHandler pairHandler;
    private Thread discoverThread;
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
                    if (discoverHandler != null) {
                        discoverHandler.onDiscoveredDevice(device);
                    }
                } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (pairHandler != null) {
                        pairHandler.onPairedDevice(device);
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
        try {
            BluetoothSocket socket;
            socket = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            socket.connect();
            messageListener = new BluetoothMessageListener(socket);
        } catch (IOException e) {
            e.printStackTrace();
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

    public void removeDiscoverhandler(IBluetoothDiscoverHandler handler) {
        discoverHandlers.remove(handler);
    }

    public void removePairHandler(IBluetoothPairHandler handler) {
        pairHandlers.remove(handler);
    }
}
