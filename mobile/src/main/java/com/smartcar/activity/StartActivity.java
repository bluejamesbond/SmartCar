package com.smartcar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.smartcar.R;
import com.smartcar.common.Global;
import com.smartcar.common.Utils;
import com.smartcar.common.view.HueShiftImageView;
import com.smartcar.core.MessageId;
import com.smartcar.core.SmartCarActivity;
import com.smartcar.service.SmartCarBackgroundService;

public class StartActivity extends SmartCarActivity {

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        StartActivity.this.findViewById(R.id.discover_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Global.DEBUG) {
                    Utils.putStore(StartActivity.this, Global.DISCOVERED_FLAG_STORE_KEY, false);
                }
            }
        });

        startService(new Intent(this, SmartCarBackgroundService.class));
    }

    @Override
    protected void onThemeChange(final int appColor, final float hue) {
        super.onThemeChange(appColor, hue);

        _runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TextView) StartActivity.this.findViewById(R.id.tagline)).setTextColor(appColor);
                ((HueShiftImageView) StartActivity.this.findViewById(R.id.logo)).shiftHue(hue);
                StartActivity.this.findViewById(R.id.discover_button).setBackground(StartActivity.this.createBigButtonStateList(appColor));
            }
        });
    }

    @Override
    protected int getContentViewId() {
        return R.layout.start_activity;
    }

    protected void updateConnectionStatus(String text) {
        ((TextView) findViewById(R.id.connection_status)).setText(text);
    }

    public void setNextPositionEnabled(final boolean enable) {
        findViewById(R.id.discover_button).setEnabled(enable);
    }

    @Override
    public void onMessageReceived(MessageId id, final String msg) {
        switch (id) {
            case BLUETOOTH_STATUS: {
                _runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setNextPositionEnabled(Boolean.parseBoolean(msg));
                        updateConnectionStatus(getResources().getString(R.string.ble_connected));
                    }
                });
                break;
            }
            case BACKGROUND_SERVICE_STARTED: {
                _runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // disable the next button
                        StartActivity.this.setNextPositionEnabled(false);

                        // set text
                        StartActivity.this.updateConnectionStatus(getResources().getString(R.string.ble_disconnected));

                        // get all the bluetooth devices
                        StartActivity.this.sendMessage(MessageId.GET_BLUETOOTH_STATUS);

                        showToast("Started StartActivity!");
                    }
                });
            }
        }
    }
}
