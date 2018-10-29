package com.app.noknok.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.noknok.R;

import java.util.List;

public class FriendsAdapter extends BaseAdapter {

    // ArrayList<String> mColorArrayList = new ArrayList<>();
    Context context;
    int mWidth, mHeight;
    private List<Row> rows;

    public FriendsAdapter(Context context) {
        this.context = context;
        getMeasure();
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }

    @Override
    public int getCount() {
        return rows.size();
    }

    @Override
    public Row getItem(int position) {
        return rows.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position) instanceof Section) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;


        if (getItemViewType(position) == 0) { // Item
            MyHolder myHolder = new MyHolder();
            if (view == null) {

                LayoutInflater inflater = (LayoutInflater) parent.getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = (LinearLayout) inflater.inflate(R.layout.adapter_friends_item,
                        parent, false);
                myHolder.imageView = (ImageView) view.findViewById(R.id.img_friends_item);
                myHolder.textView = (TextView) view.findViewById(R.id.txt_friends_item);
                myHolder.lMain = (LinearLayout) view.findViewById(R.id.ll_main);

                view.setTag(myHolder);
            } else {
                myHolder = (MyHolder) convertView.getTag();
            }
            Item item = (Item) getItem(position);

            LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(mHeight / 15, mHeight / 15);
            imgParams.setMargins(mWidth / 8, 0, mWidth / 15, 0);
            myHolder.imageView.setBackgroundResource(R.drawable.circularcornerswhite);
//            myHolder.imageView.setBackgroundColor(Color.parseColor(item.mColor));

            GradientDrawable roundDrawable = (GradientDrawable)   myHolder.imageView.getBackground();
            roundDrawable.setCornerRadius(5);
            roundDrawable.setColor(Color.parseColor(item.mColor));

            myHolder.imageView.setLayoutParams(imgParams);

            myHolder.textView.setText(item.text);

            float txtSizeSelection = mWidth / 20;

            AbsListView.LayoutParams convertViewParams = new AbsListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                    , mHeight / 10);
            // convertViewParams.setMargins(mWidth/8, 0, 0, 0);
            view.setLayoutParams(convertViewParams);

            LinearLayout.LayoutParams linearparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.MATCH_PARENT);
            linearparam.setMargins(mWidth / 80, 0, 0, 0);

            myHolder.lMain.setLayoutParams(linearparam);
            myHolder.textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,txtSizeSelection);

        } else { // Section
            MyHolder myHolder = new MyHolder();
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) parent.getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = (LinearLayout) inflater.inflate(R.layout.adapter_friends_section,
                        parent, false);
                myHolder.textView = (TextView) view.findViewById(R.id.txt_friends_section_item);
                myHolder.lMain = (LinearLayout) view.findViewById(R.id.ll_main);
                view.setTag(myHolder);
            } else {
                myHolder = (MyHolder) convertView.getTag();
            }

            //LinearLayout.LayoutParams params= (LinearLayout.LayoutParams) view.getLayoutParams();
            float txtSizeSelection = mWidth / 28;

            AbsListView.LayoutParams convertViewParams = new AbsListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                    , mHeight / 20);
            view.setLayoutParams(convertViewParams);

            LinearLayout.LayoutParams linearparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.MATCH_PARENT);
            linearparam.setMargins(mWidth / 28, 0, 0, 0);

            myHolder.lMain.setLayoutParams(linearparam);
            Section section = (Section) getItem(position);
            myHolder.textView.setText(section.text);
            myHolder.textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,txtSizeSelection);
            myHolder.textView.setTextColor(Color.GRAY);
        }

        return view;
    }

    void getMeasure() {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        mHeight = displayMetrics.heightPixels;
        mWidth = displayMetrics.widthPixels;
    }

    public static abstract class Row {
    }

    public static final class Section extends Row {
        public final String text;

        public Section(String text) {
            this.text = text;
        }
    }

    public static final class Item extends Row {
        public final String text;
        public final String mColor;

        public Item(String text, String mColor) {
            this.text = text;
            this.mColor = mColor;
        }
    }

    public class MyHolder {
        TextView textView;
        ImageView imageView;
        LinearLayout lMain;
    }
}