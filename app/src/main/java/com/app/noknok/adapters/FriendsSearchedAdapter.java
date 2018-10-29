package com.app.noknok.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.noknok.R;
import com.app.noknok.models.FriendsModel;

import java.util.ArrayList;

/**
 * Created by dev on 26/6/17.
 */

public class FriendsSearchedAdapter extends BaseAdapter {

    ArrayList<FriendsModel> mFriendsArrayList = new ArrayList<>();
    Context context;
    int mWidth, mHeight;

    public FriendsSearchedAdapter(Context context, ArrayList<FriendsModel> mFriendsArrayList) {
        this.context = context;
        this.mFriendsArrayList = mFriendsArrayList;
        getMeasure();
    }

    @Override
    public int getCount() {
        return mFriendsArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = (LinearLayout) inflater.inflate(R.layout.search_item, parent, false);

        AbsListView.LayoutParams convertViewParams = new AbsListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                , mHeight / 10);
        convertView.setLayoutParams(convertViewParams);
        float txtSizeSelection = mWidth / 20;

        TextView textView = (TextView) convertView.findViewById(R.id.txt_search_item);
        textView.setText(mFriendsArrayList.get(position).getName_in_contact());
        textView.setTextColor(Color.GRAY);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtSizeSelection);
        return convertView;
    }

    void getMeasure() {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        mHeight = displayMetrics.heightPixels;
        mWidth = displayMetrics.widthPixels;
    }

}