package com.app.noknok.interfaces;

import android.widget.LinearLayout;

import com.app.noknok.adapters.MessageTimeLineAdapter;

/**
 * Created by dev on 19/7/17.
 */

public interface MessageTimelineInterface {

    void itemClicked(int position, LinearLayout holder);
}
