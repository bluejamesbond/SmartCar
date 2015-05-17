package com.smartcar.common.view.text.font;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by Mathew on 12/27/2014.
 */
public abstract class ITypefaceEditText extends EditText {
    public ITypefaceEditText(Context context) {
        super(context);
        if (!this.isInEditMode()) {
            setTypeface(Typeface.createFromAsset(context.getAssets(), getTypefacePath()));
        }
    }

    public ITypefaceEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!this.isInEditMode()) {
            setTypeface(Typeface.createFromAsset(context.getAssets(), getTypefacePath()));
        }
    }

    public ITypefaceEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (!this.isInEditMode()) {
            setTypeface(Typeface.createFromAsset(context.getAssets(), getTypefacePath()));
        }
    }

    public abstract String getTypefacePath();
}
