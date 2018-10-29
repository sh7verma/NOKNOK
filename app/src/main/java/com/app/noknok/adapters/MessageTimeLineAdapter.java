package com.app.noknok.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.noknok.R;
import com.app.noknok.definitions.Config;
import com.app.noknok.interfaces.MessageTimelineInterface;
import com.app.noknok.models.MessageRealm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.app.noknok.activities.MessageTimelineActivity.mAudioIndex;

/**
 * Created by dev on 14/7/17.
 */

public class MessageTimeLineAdapter extends RecyclerView.Adapter<MessageTimeLineAdapter.ViewHolder> {

    private static final String TAG = "TIMELINEADAPTERTAG";
    Context context;
    ArrayList<MessageRealm> mMessageList;
    String mUserId;
    String mSenderName;
    MessageTimelineInterface messageTimelineInterface;


      int oldPosition ;
    float mWidth, mHeight;

    public MessageTimeLineAdapter(Context context, ArrayList<MessageRealm> mMessageList, String mSenderName, MessageTimelineInterface messageTimelineInterface) {
        this.mMessageList = mMessageList;
        this.mSenderName = mSenderName;
        this.context = context;
        this.messageTimelineInterface = messageTimelineInterface;
        getMeasure();

        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        mUserId = sharedPreferences.getString(Config.USER_ID, "");
    }

    @Override
    public MessageTimeLineAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.message_timeline_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final MessageTimeLineAdapter.ViewHolder holder, final int position) {

        if (position == 0) {
            holder.ivMessageTimelineThread2.setVisibility(View.INVISIBLE);
        } else {
            holder.ivMessageTimelineThread2.setVisibility(View.VISIBLE);
        }




        String userId = mMessageList.get(position).getSender_id();

        String messageTime = null;
        try {
            messageTime = timeConvert(Long.parseLong(mMessageList.get(position).getMessage_time()));
        } catch (ParseException e) {
            messageTime = mMessageList.get(position).getMessage_time();
        }

        if (userId.equals(mUserId)) {
            holder.tvMessageTimelineRightName.setText(R.string.you);
            holder.tvMessageTimelineRightTime.setText(messageTime);

            holder.tvMessageTimelineRightTime.setVisibility(View.VISIBLE);
            holder.tvMessageTimelineRightName.setVisibility(View.VISIBLE);

            holder.tvMessageTimelineLeftName.setVisibility(View.INVISIBLE);
            holder.tvMessageTimelineLeftTime.setVisibility(View.INVISIBLE);

            holder.llMessageThreadCircularDot.setBackgroundResource(R.drawable.dot_blue);
            if (mMessageList.get(position).isMessage_read()) {
                //  holder.llMessageThreadCircularDot.setBackgroundResource(R.drawable.dot_blue);
//                holder.llMessageThreadCircularDot.setBackgroundResource(R.drawable.dot_blue);

                if (mAudioIndex == position) {
                    holder.llMessageThreadCircularDot.setBackgroundResource(R.drawable.dot_pink);
                } else {
                    holder.llMessageThreadCircularDot.setBackgroundResource(R.drawable.dot_blue);
                }
            } else {
                holder.llMessageThreadCircularDot.setBackgroundResource(R.drawable.dot_green);
            }
        } else {
            holder.tvMessageTimelineLeftName.setText(mSenderName);
            holder.tvMessageTimelineLeftTime.setText(messageTime);
            holder.tvMessageTimelineLeftTime.setVisibility(View.VISIBLE);
            holder.tvMessageTimelineLeftName.setVisibility(View.VISIBLE);
            holder.tvMessageTimelineRightName.setVisibility(View.INVISIBLE);
            holder.tvMessageTimelineRightTime.setVisibility(View.INVISIBLE);

            if (mMessageList.get(position).isMessage_read()) {
//                holder.llMessageThreadCircularDot.setBackgroundResource(R.drawable.dot_blue);
                if (mAudioIndex == position) {
                    holder.llMessageThreadCircularDot.setBackgroundResource(R.drawable.dot_pink);
                } else {
                    holder.llMessageThreadCircularDot.setBackgroundResource(R.drawable.dot_blue);
                }
            } else {
                holder.llMessageThreadCircularDot.setBackgroundResource(R.drawable.dot_green);
            }
        }
//        if(position == (mMessageList.size()-1)) {
//            holder.llMessageThreadCircularDot.setBackgroundResource(R.drawable.dot_pink);
//        }
//        boolean isPlaying = false;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                messageTimelineInterface.itemClicked(position, holder.llMessageThreadCircularDot);

                if (mAudioIndex == position) {
                    holder.llMessageThreadCircularDot.setBackgroundResource(R.drawable.dot_pink);
                } else {
                    holder.llMessageThreadCircularDot.setBackgroundResource(R.drawable.dot_blue);
                }
                notifyDataSetChanged();

            }
        });
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    void getMeasure() {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        mHeight = displayMetrics.heightPixels;
        mWidth = displayMetrics.widthPixels;
    }

    String timeConvert(long t) throws ParseException {
        SimpleDateFormat dayTime = new SimpleDateFormat("dd MMM" + ", " + "hh:mm a", Locale.US);
        String str = dayTime.format(new Date(t));
        String s = "";
        for (int i = 0; i < str.length(); i++) {
            s += Character.toUpperCase(str.charAt(i));
        }
        return s;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivMessageTimelineThread1, ivMessageTimelineThread2;
        LinearLayout llMessageThreadCircularDot, llMessageThreadLineContainer;
        RelativeLayout rlMessageThreadContainer;
        TextView tvMessageTimelineRightName, tvMessageTimelineLeftName, tvMessageTimelineLeftTime, tvMessageTimelineRightTime;


        public ViewHolder(View v) {

            super(v);


            rlMessageThreadContainer = (RelativeLayout) v.findViewById(R.id.rl_message_thread_container);
            llMessageThreadCircularDot = (LinearLayout) v.findViewById(R.id.ll_message_thread_circular_dot);
            ivMessageTimelineThread1 = (ImageView) v.findViewById(R.id.iv_message_thread_line1);
            ivMessageTimelineThread2 = (ImageView) v.findViewById(R.id.iv_message_thread_line2);
            llMessageThreadLineContainer = (LinearLayout) v.findViewById(R.id.ll_message_thread_line_container);

            tvMessageTimelineRightName = (TextView) v.findViewById(R.id.tv_message_timeline_right_name);
            tvMessageTimelineRightTime = (TextView) v.findViewById(R.id.tv_message_timeline_right_time);

            tvMessageTimelineLeftName = (TextView) v.findViewById(R.id.tv_message_timeline_left_name);
            tvMessageTimelineLeftTime = (TextView) v.findViewById(R.id.tv_message_timeline_left_time);

            LinearLayout.LayoutParams rlmainParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) mHeight / 8);
            rlMessageThreadContainer.setLayoutParams(rlmainParams);

            tvMessageTimelineLeftName.setTextSize(TypedValue.COMPLEX_UNIT_PX, mWidth / 22);
            tvMessageTimelineRightName.setTextSize(TypedValue.COMPLEX_UNIT_PX, mWidth / 22);

            tvMessageTimelineLeftTime.setTextSize(TypedValue.COMPLEX_UNIT_PX, mWidth / 30);
            tvMessageTimelineRightTime.setTextSize(TypedValue.COMPLEX_UNIT_PX, mWidth / 30);

            LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams((int) mWidth / 140, ViewGroup.LayoutParams.MATCH_PARENT);
            lineParams.gravity = Gravity.CENTER;
            llMessageThreadLineContainer.setLayoutParams(lineParams);

            RelativeLayout.LayoutParams circularDotparams = new RelativeLayout.LayoutParams((int) mWidth / 25, (int) mWidth / 25);
            circularDotparams.addRule(RelativeLayout.CENTER_IN_PARENT);
            circularDotparams.setMargins((int) mWidth / 40, 0, (int) mWidth / 40, 0);
            llMessageThreadCircularDot.setLayoutParams(circularDotparams);


        }
    }
}
