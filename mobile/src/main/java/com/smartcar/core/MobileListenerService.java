package com.smartcar.core;

import android.content.Intent;

import com.smartcar.activity.StartActivity;
import com.smartcar.common.ListenerService;

public class MobileListenerService extends ListenerService {

    @Override
    protected void handleMessage(MessageId id, String msg) {

        switch (id) {
            case OPEN_HOME_ACTIVITY: {
                Intent intent = new Intent(this, StartActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            }
        }
    }
}
