package com.smartcar.core;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public abstract class MobileActivity extends SmartCarActivity {

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
