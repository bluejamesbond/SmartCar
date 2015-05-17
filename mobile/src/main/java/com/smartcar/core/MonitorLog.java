package com.smartcar.core;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.smartcar.common.Global;
import com.smartcar.common.Utils;
import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MonitorLog extends SugarRecord {

    private int alerts;
    private int goal;
    private Date date;

    public MonitorLog() {
        super();
    }

    public MonitorLog(int alerts, int goal) {
        super();
        this.alerts = alerts;
        this.goal = goal;
        this.date = new Date();
    }

    public static MonitorLog getRecent(Context context) {
        List<MonitorLog> entries = new ArrayList<>(0);

        try {
            entries = MonitorLog.find(MonitorLog.class, null, null, null, "date DESC", "1");
        } catch (SQLiteException e) {
            // ignore
        }

        if (entries.size() == 0 || !Utils.isSameDay(entries.get(0).date, new Date())) {
            MonitorLog monitorLog = new MonitorLog(1, Utils.getStore(context, Global.GOAL_COUNT_STORE_KEY, -1));
            monitorLog.save();
            return monitorLog;
        } else {
            return entries.get(0);
        }
    }

    public Date getDate() {
        return date;
    }

    public void incrementAlerts() {
        Log.d("MonitorLog", "Incremented to " + (alerts + 1));
        alerts++;
    }

    public int getAlerts() {
        return alerts;
    }

    public void setAlerts(int alerts) {
        this.alerts = alerts;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }
}
