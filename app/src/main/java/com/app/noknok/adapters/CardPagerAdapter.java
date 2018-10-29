package com.app.noknok.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.noknok.R;
import com.app.noknok.activities.MessageTimelineActivity;
import com.app.noknok.definitions.Config;
import com.app.noknok.interfaces.CardAdapter;
import com.app.noknok.services.GetContacts;
import com.app.noknok.utils.RandomColor;
import com.app.noknok.utils.RandomIllustrator;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class CardPagerAdapter extends PagerAdapter implements CardAdapter {

    ArrayList<HashMap<String, String>> chatDialogueList;
    Context mContext;
    int mWidth, mHeight;
    boolean isThisForNewMessages;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference messageReference = firebaseDatabase.getReference("Messages").getRef();
    SharedPreferences sp;
    RandomIllustrator randomIllustrator = new RandomIllustrator();
    private List<CardView> mViews;
    private float mBaseElevation;

    public CardPagerAdapter(Context context, ArrayList<HashMap<String, String>> chatDialogueList, boolean isThisForNewMessages) {

        mViews = new ArrayList<>();
        mContext = context;

        sp = mContext.getSharedPreferences(mContext.getPackageName(), Context.MODE_PRIVATE);
        this.chatDialogueList = chatDialogueList;
        this.isThisForNewMessages = isThisForNewMessages;
        for (int i = 0; i < chatDialogueList.size(); i++) {
            mViews.add(null);
        }
        getScreenSize();
    }

    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mViews.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return chatDialogueList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.message_item, container, false);
        container.addView(view);
        bind(chatDialogueList.get(position), view);
        CardView cardView = (CardView) view.findViewById(R.id.cardView);
        LinearLayout llContainer = (LinearLayout) view.findViewById(R.id.ll_message_item_container);
        ImageView ivIllustration = (ImageView) view.findViewById(R.id.iv_message_item_illustration);
        ivIllustration.setImageResource(randomIllustrator.getDrawable(Integer.parseInt(chatDialogueList.get(position).get(Config.MESSAGE_ICON))));

        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }
        Log.d("mmmmmmm", chatDialogueList.get(position).toString());

        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);

        RandomColor randomColor = new RandomColor();
        GradientDrawable drawable = new GradientDrawable();
        int colors[] = {Color.parseColor(randomColor.getDrawable(Integer.parseInt(chatDialogueList.get(position).get(Config.MESSAGE_COLOR))).getGradient1()), Color.parseColor(randomColor.getDrawable(Integer.parseInt(chatDialogueList.get(position).get(Config.MESSAGE_COLOR))).getGradient2())};
        drawable.setColors(colors);
        drawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        drawable.setCornerRadius(10);
        llContainer.setBackgroundDrawable(drawable);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(mContext, MessageTimelineActivity.class);
                intent.putExtra(Config.CHAT_DIALOGUE_ID, chatDialogueList.get(position).get(Config.CHAT_DIALOGUE_ID));
                intent.putExtra(Config.FULL_NAME, chatDialogueList.get(position).get(Config.FULL_NAME));
                intent.putExtra(Config.MESSAGE_TYPE, chatDialogueList.get(position).get(Config.MESSAGE_TYPE));
                intent.putExtra(Config.RECEIVER_ID, chatDialogueList.get(position).get(Config.RECEIVER_ID));
                intent.putExtra(Config.LAST_GUESS_TIME, chatDialogueList.get(position).get(Config.LAST_GUESS_TIME));
                intent.putExtra(Config.MESSAGE_COLOR, chatDialogueList.get(position).get(Config.MESSAGE_COLOR));
                intent.putExtra(Config.MESSAGE_ICON, chatDialogueList.get(position).get(Config.MESSAGE_ICON));
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.enter_from_bottom, R.anim.exit_to_bottom);
            }
        });
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    private void bind(HashMap<String, String> chat, View view) {
        TextView tvName = (TextView) view.findViewById(R.id.tv_message_item_name);
        TextView tvTime = (TextView) view.findViewById(R.id.tv_message_item_time);
        ImageView ivPlayPause = (ImageView) view.findViewById(R.id.iv_message_item_play_button);
        ImageView ivDelete = (ImageView) view.findViewById(R.id.iv_message_item_delete);
        ivDelete.setVisibility(View.INVISIBLE);


        LinearLayout.LayoutParams playPauseParams = new LinearLayout.LayoutParams((int) (mWidth * 0.12), (int) (mWidth * 0.12));
        ivPlayPause.setLayoutParams(playPauseParams);
        tvName.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (mWidth * 0.04));
        tvTime.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (mWidth * 0.03));

        String s = chat.get(Config.FULL_NAME);
        if (GetContacts.mContactMap.containsKey(s)) {
            tvName.setText(GetContacts.mContactMap.get(s));
        } else {
            tvName.setText(chat.get(Config.FULL_NAME));
        }
        try {
            tvTime.setText(timeConvert(Long.parseLong(chat.get(Config.LAST_MESSAGE_TIME))));
        } catch (ParseException e) {
            tvTime.setText(chat.get(Config.LAST_MESSAGE_TIME));
        }

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


    void getScreenSize() {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        mHeight = displayMetrics.heightPixels;
        mWidth = displayMetrics.widthPixels;
    }


}