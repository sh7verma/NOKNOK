package com.app.noknok.utils;

import com.app.noknok.R;

import java.util.ArrayList;

/**
 * Created by dev on 4/8/17.
 */

public class RandomIllustrator {

    ArrayList<Integer> drawableArrayList = new ArrayList<>();

    public RandomIllustrator() {
        drawableArrayList.add(R.drawable.illustration_2);
        drawableArrayList.add(R.drawable.illustration_3);
        drawableArrayList.add(R.drawable.illustration_4);
        drawableArrayList.add(R.drawable.illustration_5);
        drawableArrayList.add(R.drawable.illustration_6);
        drawableArrayList.add(R.drawable.illustration_7);
        drawableArrayList.add(R.drawable.illustration_8);
        drawableArrayList.add(R.drawable.illustration_9);
    }

    public int getDrawable(int pos) {

        return drawableArrayList.get(pos);
    }

    public int size() {
        return drawableArrayList.size();
    }

}
