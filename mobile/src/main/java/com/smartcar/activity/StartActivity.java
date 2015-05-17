package com.smartcar.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.smartcar.R;
import com.smartcar.common.Global;
import com.smartcar.common.Utils;
import com.smartcar.common.view.HueShiftImageView;
import com.smartcar.core.MessageId;
import com.smartcar.core.SmartCarActivity;

public class StartActivity extends SmartCarActivity {

    Object res;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        if (Utils.getStore(this, Global.GOAL_COUNT_STORE_KEY, -1) < 0) {
            Utils.putStore(this, Global.GOAL_COUNT_STORE_KEY, Global.DEFAULT_GOAL_COUNT);
        }

        _runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.calibrate_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (Global.DEBUG) {
                            Utils.putStore(StartActivity.this, Global.CALIBRATED_FLAG_STORE_KEY, false);
                        }

                        if (Utils.getStore(StartActivity.this, Global.CALIBRATED_FLAG_STORE_KEY, false)) {
                            startActivity(DashboardActivity.class);
                        } else {
                            startActivity(DiscoverActivity.class);
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onThemeChange(final int appColor, final float hue) {
        super.onThemeChange(appColor, hue);

        _runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TextView) findViewById(R.id.tagline)).setTextColor(appColor);
                ((HueShiftImageView) findViewById(R.id.logo)).shiftHue(hue);
                findViewById(R.id.calibrate_button).setBackground(createBigButtonStateList(appColor));
            }
        });
    }

    @Override
    protected int getContentViewId() {
        return R.layout.start_activity;
    }

    protected void updateConnectionStatus(boolean status) {
        ((TextView) findViewById(R.id.connection_status)).setText(getResources().getText(status ? R.string.wear_connected : R.string.wear_disconnected));
    }

    public void setNextPositionEnabled(final Activity activity, final boolean enable) {
        //    activity.findViewById(R.id.calibrate_next_button).setEnabled(enable)
    }
}
