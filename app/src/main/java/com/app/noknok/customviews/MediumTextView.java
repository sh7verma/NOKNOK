package com.app.noknok.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by dev on 19/6/17.
 */

public class MediumTextView extends TextView {

    public MediumTextView(Context context) {
        super(context);
        init();
    }

    public MediumTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MediumTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface typeface = Typeface.createFromAsset(getContext()
                    .getAssets(), "fonts/Ubuntu-Medium.ttf");
            setTypeface(typeface);
        }

    }
}
