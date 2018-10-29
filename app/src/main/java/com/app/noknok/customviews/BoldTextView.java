package com.app.noknok.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by dev on 19/6/17.
 */

public class BoldTextView extends TextView {


    public BoldTextView(Context context) {
        super(context);
        init();
    }

    public BoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BoldTextView(Context context, AttributeSet attrs, int defStyleAttr) {
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
