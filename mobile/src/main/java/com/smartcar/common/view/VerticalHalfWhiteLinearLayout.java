package com.smartcar.common.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by Mathew on 12/31/2014.
 */
public class VerticalHalfWhiteLinearLayout extends LinearLayout {
    private Paint mPaint;

    public VerticalHalfWhiteLinearLayout(Context context) {
        super(context);
        init();
    }

    public VerticalHalfWhiteLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VerticalHalfWhiteLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public VerticalHalfWhiteLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int height = getHeight();
        int width = getWidth();

        canvas.drawRect(0, height / 2, width, height, mPaint);
    }
}
