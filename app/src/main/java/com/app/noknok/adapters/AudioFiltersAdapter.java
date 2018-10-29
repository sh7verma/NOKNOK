package com.app.noknok.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.app.noknok.R;
import com.app.noknok.interfaces.FilterListInterface;

import java.util.ArrayList;

/**
 * Created by dev on 30/6/17.
 */

public class AudioFiltersAdapter extends RecyclerView.Adapter<AudioFiltersAdapter.MyViewHolder> {

    int[] filterIconList = new int[]{R.drawable.filter_1, R.drawable.filter_2, R.drawable.filter_3,
            R.drawable.filter_4, R.drawable.filter_5, R.drawable.filter_6, R.drawable.filter_7};

    ArrayList<Boolean> visibilityList = new ArrayList<>();
    FilterListInterface filterListInterface;
    Context mContext;
    int mHeight, mWidth;

    public AudioFiltersAdapter(Context context, FilterListInterface filterListInterface) {
        this.filterListInterface = filterListInterface;
        mContext = context;
        getScreenMeasure();
    }

    private void getScreenMeasure() {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        mHeight = displayMetrics.heightPixels;
        mWidth = displayMetrics.widthPixels;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.filter_layout_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.filterIcon.setImageResource(filterIconList[position]);
        if (visibilityList.get(position)) {
            holder.selectIcon.setVisibility(View.VISIBLE);
        } else {
            holder.selectIcon.setVisibility(View.INVISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < filterIconList.length; i++) {

                    if (i != position) {
                        visibilityList.set(i, false);
                    } else {
                        visibilityList.set(position, true);
                    }
                }

                notifyDataSetChanged();

                filterListInterface.onFilterClicked(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return filterIconList.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView filterIcon;
        public LinearLayout selectIcon, llFilterIconContainer;

        public MyViewHolder(View itemView) {
            super(itemView);
            filterIcon = (ImageView) itemView.findViewById(R.id.iv_filter_icon);
            selectIcon = (LinearLayout) itemView.findViewById(R.id.ll_filter_select_icon);
            llFilterIconContainer = (LinearLayout) itemView.findViewById(R.id.ll_filter_icon_container);

            LinearLayout.LayoutParams iconContainerParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int marginForContainer = (int) (mWidth * 0.04);

            iconContainerParams.setMargins(marginForContainer - 3, marginForContainer, marginForContainer - 3, marginForContainer);
            llFilterIconContainer.setLayoutParams(iconContainerParams);


            LinearLayout.LayoutParams filterIconParams = new LinearLayout.LayoutParams((int) (mWidth * 0.11), (int) (mWidth * 0.11));
            filterIcon.setLayoutParams(filterIconParams);

            LinearLayout.LayoutParams selectIconParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (mWidth * 0.025));
            selectIconParams.setMargins(0, 0, 0, (int) (mWidth * 0.015));
            selectIcon.setLayoutParams(selectIconParams);

            for (int i = 0; i < filterIconList.length; i++)
                if (i == 0) {
                    visibilityList.add(true);
                } else {
                    visibilityList.add(false);
                }
        }
    }


}
