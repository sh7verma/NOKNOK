package com.app.noknok.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.noknok.R;
import com.app.noknok.activities.MessageSearchActivity;
import com.app.noknok.activities.MessageTimelineActivity;
import com.app.noknok.definitions.Config;
import com.app.noknok.utils.RandomColor;
import com.app.noknok.utils.RandomIllustrator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static com.app.noknok.fragments.OldMessagesFragment.chatDialogueList;

/**
 * Created by dev on 30/8/17.
 */

public class MessageSearchAdapter extends RecyclerView.Adapter<MessageSearchAdapter.ViewHolder> {

    Context mContext;


    RandomIllustrator randomIllustrator = new RandomIllustrator();
    ArrayList<HashMap<String, String>> mChatDialogueList;
    float mWidth, mHeight;
    MessageSearchActivity.MessageSearchClickInterface mMessageSearchClickInterface;
    public MessageSearchAdapter(Context context, ArrayList<HashMap<String, String>> chatDialogueList, MessageSearchActivity.MessageSearchClickInterface messageSearchClickInterface) {
        mContext = context;
        mChatDialogueList = chatDialogueList;
        mMessageSearchClickInterface = messageSearchClickInterface;
        getMeasure();

    }

//    public void updateList(ArrayList<HashMap<String, String>> list) {
//        if (mChatDialogueList.size() > 0)
//            mChatDialogueList.clear();
//        mChatDialogueList = list;
//        notifyDataSetChanged();
//    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.adapter_message_search, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.ivIllustration.setImageResource(randomIllustrator.getDrawable(Integer.parseInt(mChatDialogueList.get(position).get(Config.MESSAGE_ICON))));
        RandomColor randomColor = new RandomColor();
        GradientDrawable drawable = new GradientDrawable();
        int colors[] = {Color.parseColor(randomColor.getDrawable(Integer.parseInt(mChatDialogueList.get(position).get(Config.MESSAGE_COLOR))).getGradient1()),
                Color.parseColor(randomColor.getDrawable(Integer.parseInt(mChatDialogueList.get(position).get(Config.MESSAGE_COLOR))).getGradient2())};
        drawable.setColors(colors);
        drawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        drawable.setCornerRadius(10);
        holder.llMainContainer.setBackgroundDrawable(drawable);

        holder.tvName.setText(mChatDialogueList.get(position).get(Config.FULL_NAME));

        String convertedTime = "";
        try {
            convertedTime = timeConvert(Long.parseLong(mChatDialogueList.get(position).get(Config.LAST_MESSAGE_TIME)));
        } catch (ParseException e) {
            convertedTime = mChatDialogueList.get(position).get(Config.LAST_MESSAGE_TIME);
        }
        holder.tvTime.setText(convertedTime);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMessageSearchClickInterface.onClick(position);
//                Toast.makeText(mContext, "clicked position: " + position, Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return mChatDialogueList.size();
    }

    void getMeasure() {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        mHeight = displayMetrics.heightPixels;
        mWidth = displayMetrics.widthPixels;
    }

    String timeConvert(long t) throws ParseException {
        SimpleDateFormat dayTime = new SimpleDateFormat("dd MMM" + ", " + "hh:mm a", Locale.US);
        String str = dayTime.format(new Date(t));
        return str;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout llMainContainer, llSearchIconContainer, llIconTextMainContainer, llIconTextContainer;
        TextView tvName, tvTime;
        ImageView ivPlayButton, ivIllustration;

        public ViewHolder(View v) {

            super(v);

            llMainContainer = (LinearLayout) v.findViewById(R.id.ll_adapter_message_search_main_container);
            llSearchIconContainer = (LinearLayout) v.findViewById(R.id.ll_adapter_message_search_icon_container);
            llIconTextMainContainer = (LinearLayout) v.findViewById(R.id.ll_adapter_message_search_icon_text_main_container);
            llIconTextContainer = (LinearLayout) v.findViewById(R.id.ll_adapter_message_search_icon_text_container);
            tvName = (TextView) v.findViewById(R.id.tv_adapter_message_search_name);
            tvTime = (TextView) v.findViewById(R.id.tv_adapter_message_search_time);
            ivPlayButton = (ImageView) v.findViewById(R.id.iv_adapter_message_search_play);
            ivIllustration = (ImageView) v.findViewById(R.id.iv_adapter_message_search_illustration);


            LinearLayout.LayoutParams mainParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (mHeight / 6));
            int mainMargin = (int) (mWidth * 0.03);
            mainParams.setMargins(mainMargin, mainMargin, mainMargin, mainMargin);
            llMainContainer.setLayoutParams(mainParams);

            int searchIconContainerPadding = (int) (mWidth * 0.06);
            llSearchIconContainer.setPadding(searchIconContainerPadding, searchIconContainerPadding,
                    searchIconContainerPadding, searchIconContainerPadding);

            LinearLayout.LayoutParams iconTextMainParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            int iconTextMainMargin = (int) (mWidth * 0.02);
            iconTextMainParams.setMargins(iconTextMainMargin, iconTextMainMargin, iconTextMainMargin, iconTextMainMargin);
            llIconTextMainContainer.setLayoutParams(iconTextMainParams);


            LinearLayout.LayoutParams iconTextParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            int iconTextMargin = (int) (mWidth * 0.03);
            iconTextParams.setMargins(0, iconTextMargin, 0, iconTextMargin);
            llIconTextContainer.setLayoutParams(iconTextParams);

            tvName.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (mWidth * 0.048));
            tvTime.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (mWidth * 0.035));

            LinearLayout.LayoutParams playIconParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int playIconMargin = (int) (mWidth * 0.02);
            playIconParams.setMargins(playIconMargin, playIconMargin, playIconMargin, playIconMargin);
            ivPlayButton.setLayoutParams(playIconParams);
        }
    }
}
