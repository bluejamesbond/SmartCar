package com.smartcar.common;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

public class ReferencedActivity extends Activity {
    protected ReferencedApplication mApp;

    public static ReferencedActivity getActive(Context context) {
        return (ReferencedActivity) ((ReferencedApplication) context.getApplicationContext()).getCurrentActivity();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApp = (ReferencedApplication) this.getApplicationContext();
    }

    protected void onResume() {
        super.onResume();
        mApp.setCurrentActivity(this);
    }

    protected void onPause() {
        clearReferences();
        super.onPause();
    }

    protected void onDestroy() {
        clearReferences();
        super.onDestroy();
    }

    private void clearReferences() {
        Activity currActivity = mApp.getCurrentActivity();
        if (currActivity != null && currActivity.equals(this))
            mApp.setCurrentActivity(null);
    }
}