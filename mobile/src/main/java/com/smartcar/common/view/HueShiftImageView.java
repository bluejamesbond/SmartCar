package com.smartcar.common.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Mathew on 1/1/2015.
 */
public class HueShiftImageView extends ImageView {

    public HueShiftImageView(Context context) {
        super(context);
    }

    public HueShiftImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HueShiftImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HueShiftImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private static float cleanValue(float p_val, float p_limit) {
        return Math.min(p_limit, Math.max(-p_limit, p_val));
    }

    public void shiftHue(float value) {

        value = value > 180 ? -(360f - value) : value;
        ColorMatrix cm = new ColorMatrix();
        value = cleanValue(value, 180f) / 180f * (float) Math.PI;

        if (value == 0) {
            return;
        }

        float cosVal = (float) Math.cos(value);
        float sinVal = (float) Math.sin(value);
        float lumR = 0.213f;
        float lumG = 0.715f;
        float lumB = 0.072f;
        float[] mat = new float[]
                {
                        lumR + cosVal * (1 - lumR) + sinVal * (-lumR), lumG + cosVal * (-lumG) + sinVal * (-lumG), lumB + cosVal * (-lumB) + sinVal * (1 - lumB), 0, 0,
                        lumR + cosVal * (-lumR) + sinVal * (0.143f), lumG + cosVal * (1 - lumG) + sinVal * (0.140f), lumB + cosVal * (-lumB) + sinVal * (-0.283f), 0, 0,
                        lumR + cosVal * (-lumR) + sinVal * (-(1 - lumR)), lumG + cosVal * (-lumG) + sinVal * (lumG), lumB + cosVal * (1 - lumB) + sinVal * (lumB), 0, 0,
                        0f, 0f, 0f, 1f, 0f,
                        0f, 0f, 0f, 0f, 1f};

        cm.postConcat(new ColorMatrix(mat));
        setColorFilter(new ColorMatrixColorFilter(cm));
        postInvalidate();
    }
}
