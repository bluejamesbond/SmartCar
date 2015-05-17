package com.smartcar.common.view.text.font;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by Mathew on 12/27/2014.
 */
public class ProximaNovaBlackTextView extends ITypefaceTextView {

    public ProximaNovaBlackTextView(Context context) {
        super(context);
    }

    public ProximaNovaBlackTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProximaNovaBlackTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public String getTypefacePath() {
        return "fonts/proxima-nova-black.otf";
    }
}
