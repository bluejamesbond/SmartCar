package com.smartcar.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.smartcar.R;
import com.smartcar.common.Global;
import com.smartcar.common.Utils;
import com.smartcar.common.view.HoloCircularProgressBar;
import com.smartcar.common.view.HueShiftImageView;
import com.smartcar.core.MessageId;
import com.smartcar.core.MobileActivity;

public class DiscoverActivity extends MobileActivity {

    private static final int maxSteps = 40;
    private static final int incrementSteps = 10;
    private int storedSteps;
    private Object timeout;

    @Override
    protected void onResume() {
        super.onResume();

        reset();
    }

    public void reset() {

        storedSteps = 0;

        _runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // change button text on done
                ((TextView) findViewById(R.id.calibrate_next_button_left)).setText("next");
                ((TextView) findViewById(R.id.calibrate_next_button_right)).setText("position");
                ((TextView) findViewById(R.id.progress_value)).setText(Integer.toString(0));
            }
        });

    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        reset();

        _runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.calibrate_next_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (storedSteps >= maxSteps) {
                            startActivity(DashboardActivity.class);
                            sendMessage(MessageId.OPEN_HOME_ACTIVITY);
                        } else {
                            sendMessage(MessageId.NEXT_CALIBRATION_POSITION);
                            setNextPositionEnabled(false);

                            timeout = Utils.setTimeout(new Runnable() {
                                @Override
                                public void run() {
                                    showToast("Are you sure your watch is connected?");
                                    setNextPositionEnabled(true);
                                }
                            }, 5000);

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
                ((HueShiftImageView) findViewById(R.id.logo)).shiftHue(hue);
                findViewById(R.id.calibrate_next_button).setBackground(createBigButtonStateList(appColor));
                ((TextView) findViewById(R.id.progress_label)).setTextColor(appColor);
                ((HoloCircularProgressBar) findViewById(R.id.progress)).setProgressColor(appColor);
            }
        });
    }

    @Override
    protected int getContentViewId() {
        return R.layout.calibrate_activity;
    }

    @Override
    protected void onMessageReceived(MessageId id, String message) {
        switch (id) {

            case FINISHED_CALIBRATION_SERVICE:
            case STORED_CALIBRATION_POSITION: {

                Utils.clearTimout(timeout);

                storedSteps += incrementSteps;

                // update progress
                ((HoloCircularProgressBar) findViewById(R.id.progress)).setProgress((float) storedSteps / (float) maxSteps);
                ((TextView) findViewById(R.id.progress_value)).setText(Integer.toString(storedSteps));

                if (storedSteps >= maxSteps) {

                    // store that a calibration is complete
                    Utils.putStore(DiscoverActivity.this, Global.CALIBRATED_FLAG_STORE_KEY, true);

                    // change button text on done
                    ((TextView) findViewById(R.id.calibrate_next_button_left)).setText("view");
                    ((TextView) findViewById(R.id.calibrate_next_button_right)).setText("dashboard");
                }

                // enable the button
                setNextPositionEnabled(true);

                break;
            }
        }
    }

    public void setNextPositionEnabled(final boolean enable) {
        View calibrateButton = findViewById(R.id.calibrate_next_button);
        calibrateButton.setEnabled(enable);

        findViewById(R.id.calibrate_next_button_right).setAlpha(enable ? 1.0f : 0.7f);
    }
}
