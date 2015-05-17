package com.smartcar.core;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.smartcar.common.SocketActivity;
import com.smartcar.common.Utils;

import java.util.ArrayList;
import java.util.List;

public abstract class UIXActivity extends SocketActivity {

    protected final static boolean GRADIENT_BACKGROUND_ENABLED = true;

    protected static DisplayMetrics mMetrics = null;
    protected static float mHue = 0;

    private View mRoot;
    private volatile List<Runnable> mPreInflation;
    private volatile boolean mInflated = false;

    protected abstract void onThemeChange(final int appColor, final float hue);

    public final void onDestroy() {
        super.onDestroy();
        mRoot = null;

        if (mPreInflation != null) {
            mPreInflation.removeAll(mPreInflation);
            mPreInflation = null;
        }
    }

    protected View getRootView() {
        return findViewById(android.R.id.content);
    }

    public final void setTheme(float theme) {
        mHue = theme;


        int bgStartColor = Utils.shiftHue(getResources().getColor(com.smartcar.R.color.app__background_startcolor), theme);
        int bgCenterColor = Utils.shiftHue(getResources().getColor(com.smartcar.R.color.app__background_centercolor), theme);
        int bgEndColor = Utils.shiftHue(getResources().getColor(com.smartcar.R.color.app__background_endcolor), theme);

        final Drawable bgDrawable;

        if (GRADIENT_BACKGROUND_ENABLED) {
            float bgCenterX = getResources().getFraction(com.smartcar.R.fraction.app__background_centerx, 1, 1);
            float bgCenterY = getResources().getFraction(com.smartcar.R.fraction.app__background_centery, 1, 1);
            float bgGradientRadius = getResources().getFraction(com.smartcar.R.fraction.app__background_radius, 1, 1) * mMetrics.widthPixels;

            GradientDrawable bgGradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{bgStartColor, bgCenterColor, bgEndColor});
            bgGradientDrawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
            bgGradientDrawable.setGradientRadius(bgGradientRadius);
            bgGradientDrawable.setGradientCenter(bgCenterX, bgCenterY);
            bgDrawable = bgGradientDrawable;
        } else {
            bgDrawable = new ColorDrawable(getResources().getColor(R.color.dark_grey));
        }

        _runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getBackgroundView().setBackground(bgDrawable);
            }
        });

        onThemeChange(Utils.shiftHue(getRootView().getResources().getColor(R.color.universal__appcolor), theme), theme);
    }

    public abstract View getBackgroundView();

    protected StateListDrawable createStateList(final int appColor, final int pressed, final int def, final int presid, final int defid) {

        StateListDrawable stateListDrawable;
        LayerDrawable layerDrawable;
        GradientDrawable gradientDrawable;

        // ---
        stateListDrawable = new StateListDrawable();

        layerDrawable = (LayerDrawable) getResources().getDrawable(pressed);
        gradientDrawable = (GradientDrawable) layerDrawable.findDrawableByLayerId(presid);
        gradientDrawable.setColor(appColor);
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, layerDrawable);
        stateListDrawable.addState(new int[]{-android.R.attr.state_enabled}, layerDrawable);

        layerDrawable = (LayerDrawable) getResources().getDrawable(def);
        gradientDrawable = (GradientDrawable) layerDrawable.findDrawableByLayerId(defid);
        gradientDrawable.setColor(appColor);
        stateListDrawable.addState(new int[]{}, layerDrawable);

        return stateListDrawable;
    }

    protected StateListDrawable createBigButtonStateList(final int appColor) {
        return createStateList(appColor, R.drawable.big_button__background_pressed, R.drawable.big_button__background_default, R.id.big_button__background_pressed_backgrounditem,
                R.id.big_button__background_default_backgrounditem);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        if (mMetrics == null) {
            mMetrics = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(mMetrics);
        }

        mPreInflation = new ArrayList<>();
        mRoot = getRootView();
        mInflated = true; // NOTE remember to fix this as necessary

        setTheme(Theme.RANDOM);
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void _runOnUiThread(Runnable action) {
        if (mInflated) {
            runOnUiThread(action);
        } else {
            synchronized (this) {
                if (mPreInflation == null) {
                    _runOnUiThread(action);
                } else {
                    mPreInflation.add(action);
                }
            }
        }
    }
}
