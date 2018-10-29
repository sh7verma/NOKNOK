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
import com.app.noknok.models.MutualFriendsModel;

import java.util.ArrayList;

import static android.R.attr.width;
import static com.app.noknok.R.id.imageView;
import static com.app.noknok.activities.CountryCodeActivity.h;

/**
 * Created by dev on 27/6/17.
 */

public class DirectMessageAdapter extends BaseAdapter {
    Context context;
    ArrayList<MutualFriendsModel> mNameArraylist = new ArrayList<>();
    ArrayList<String> mColorArrayList=new ArrayList<>();
    int mWidth, mHeight;
    public DirectMessageAdapter(Context context, ArrayList<MutualFriendsModel> mNameArraylist, ArrayList<String> mColorArrayList) {
        this.context = context;
        this.mNameArraylist = mNameArraylist;
        this.mColorArrayList=mColorArrayList;
        getMeasure();
    }

    @Override
    public int getCount() {
        return mNameArraylist.size();
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

        MyHolder holder = null;
        float txtSizeSelection = mWidth / 20;

        int height=0,width = 0;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView =   inflater.inflate(R.layout.direct_message_item, parent, false);

            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            height = displayMetrics.heightPixels;
            width = displayMetrics.widthPixels;


            AbsListView.LayoutParams convertViewParams = new AbsListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                    , height / 10);
            convertView.setLayoutParams(convertViewParams);

            holder=new MyHolder();
            holder.imageView= (ImageView) convertView.findViewById(R.id.image_direct_message_smile);
            holder.tvDirectMessageItem = (TextView) convertView.findViewById(R.id.txt_direct_message_item);
            LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(width/10, height/15);
            imgParams.setMargins(0,0,width/15,0);
            holder.imageView.setLayoutParams(imgParams);

            convertView.setTag(holder);
        }
        else {
            holder= (MyHolder) convertView.getTag();
        }

//        holder.imageView.setBackgroundColor(Color.parseColor(mColorArrayList.get(position)));

        GradientDrawable roundDrawable = (GradientDrawable)   holder.imageView.getBackground();
        roundDrawable.setCornerRadius(5);
        roundDrawable.setColor(Color.parseColor(mColorArrayList.get(position)));

//      tvDirectMessageItem.setGravity(View.TEXT_ALIGNMENT_CENTER);
        holder.tvDirectMessageItem.setText(mNameArraylist.get(position).getName_in_contact());
        holder.tvDirectMessageItem.setTextSize(TypedValue.COMPLEX_UNIT_PX,txtSizeSelection);


        return convertView;
    }

    public  class MyHolder{


        ImageView imageView;
        TextView tvDirectMessageItem ;
    }
    void getMeasure() {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        mHeight = displayMetrics.heightPixels;
        mWidth = displayMetrics.widthPixels;
    }

}