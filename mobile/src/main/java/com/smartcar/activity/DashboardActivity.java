package com.smartcar.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.widget.EditText;
import android.widget.TextView;

import com.smartcar.R;
import com.smartcar.core.MobileActivity;
import com.smartcar.core.MonitorLog;
import com.smartcar.common.Global;
import com.smartcar.common.Utils;
import com.smartcar.common.view.HoloCircularProgressBar;
import com.smartcar.core.MessageId;

import java.util.Iterator;

public class DashboardActivity extends MobileActivity {

    private Object lastGoalInput;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        final int alerts = MonitorLog.getRecent(this).getAlerts();

        updateAverage();
        updateGoal();

        _runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setProgress(alerts, getGoal());
            }
        });

        final EditText editText = ((EditText)findViewById(R.id.goal_value));

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Utils.clearTimout(lastGoalInput);
                lastGoalInput = Utils.setTimeout(new Runnable() {
                    @Override
                    public void run() {
                        int alerts = MonitorLog.getRecent(DashboardActivity.this).getAlerts();
                        int goal = Math.abs(Integer.parseInt(editText.getText().toString()));
                        setProgress(alerts, goal);
                        Utils.getStore(DashboardActivity.this, Global.GOAL_COUNT_STORE_KEY, goal);
                    }
                }, 1000);
            }
        });
    }

    @Override
    protected void onThemeChange(final int appColor, final float hue) {
        super.onThemeChange(appColor, hue);
        _runOnUiThread(new Runnable() {
            @Override
            public void run() {

                ((TextView) findViewById(R.id.wear_button__icon)).setTextColor(appColor);
                ((TextView) findViewById(R.id.avg_text)).setTextColor(appColor);
                ((TextView) findViewById(R.id.progress_label)).setTextColor(appColor);
                ((HoloCircularProgressBar) findViewById(R.id.progress)).setProgressColor(appColor);

                findViewById(R.id.date_button).setBackground(createBigButtonStateList(appColor));
                findViewById(R.id.recalibrate_ok_button).setBackground(createBigButtonStateList(appColor));
                findViewById(R.id.reset_ok_button).setBackground(createBigButtonStateList(appColor));
                findViewById(R.id.goal_item).setBackground(createBigButtonStateList(appColor));
            }
        });
    }

    @Override
    protected int getContentViewId() {
        return R.layout.dashboard_activity;
    }

    @Override
    protected void onMessageReceived(MessageId id, String message) {
        switch (id) {
            case MONITOR_ALERT:
                setProgress(MonitorLog.getRecent(this).getAlerts(), getGoal());
                break;
        }
    }

    public void updateGoal() {
        ((TextView) findViewById(R.id.goal_value)).setText(Integer.toString(getGoal()));
    }

    public int getGoal() {
        return Utils.getStore(this, Global.GOAL_COUNT_STORE_KEY, Global.DEFAULT_GOAL_COUNT);
    }

    public int updateAverage() {
        new AsyncTask<Void, Void, Integer>() {

            @Override
            protected Integer doInBackground(Void... params) {
                Integer total = 0;
                Integer count = 0;
                Iterator<MonitorLog> habitLogList = MonitorLog.findAll(MonitorLog.class);

                while (habitLogList.hasNext()) {
                    total += habitLogList.next().getAlerts();
                    count++;
                }

                try {
                    return total / count;
                } catch (ArithmeticException e) {
                    return 0;
                }
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                setAverage(integer);
            }
        };

        return 0;
    }

    public void setProgress(int alerts, int goal) {
        String alertStr = Integer.toString(alerts);
        TextView progressText = (TextView) findViewById(R.id.progress_value);
        progressText.setTextSize(TypedValue.COMPLEX_UNIT_SP, new int []{ 90, 90, 85, 60, 55, 45 }[alertStr.length()]);
        progressText.setText(Integer.toString(alerts));
        ((HoloCircularProgressBar) findViewById(R.id.progress)).setProgress((float) alerts / (float) goal);
    }

    public void setAverage(int avg) {
        ((TextView) findViewById(R.id.goal_value)).setText(Integer.toString(avg));
    }
}
