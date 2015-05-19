package com.smartcar.core;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.smartcar.R;

public abstract class SmartCarActivity extends UIXActivity {

    @SuppressWarnings("unused")
    protected void startActivity(Class act) {
        startActivity(new Intent(this, act));
        finish();
    }

    @Override
    protected void onThemeChange(int appColor, float hue) {
        // none for now
    }

    @Override
    public View getBackgroundView() {
        return findViewById(R.id.bg);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }
}
