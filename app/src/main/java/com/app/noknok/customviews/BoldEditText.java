package com.app.noknok.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by dev on 19/6/17.
 */

public class BoldEditText extends EditText {

    public BoldEditText(Context context) {
        super(context);
        init();
    }

    public BoldEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BoldEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface typeface = Typeface.createFromAsset(getContext()
                    .getAssets(), "fonts/Ubuntu-Bold.ttf");
            setTypeface(typeface);
        }

    }
}
