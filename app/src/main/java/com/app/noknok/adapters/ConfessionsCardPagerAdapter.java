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
import com.app.noknok.activities.ConfessionsChatActivity;
import com.app.noknok.definitions.Config;
import com.app.noknok.dialogs.ConfessionDeleteDialog;
import com.app.noknok.interfaces.CardAdapter;
import com.app.noknok.models.Message;
import com.app.noknok.models.MessageRealm;
import com.app.noknok.utils.RandomColor;
import com.app.noknok.utils.RandomIllustrator;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by dev on 27/7/17.
 */
public class ConfessionsCardPagerAdapter extends PagerAdapter implements CardAdapter {

    ArrayList<Message> confessionsList;
    Context mContext;
    int mWidth, mHeight;
    String confessionType;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference messageReference = firebaseDatabase.getReference("Messages").getRef();
    DatabaseReference confessionsReference = firebaseDatabase.getReference("Confessions").getRef();
    Realm mRealm;
    SharedPreferences sp;
    RandomIllustrator randomIllustrator;
    boolean circleEnded = false;
    int counter = 0;
    int totalCounter = 0;
    private List<CardView> mViews;

    private float mBaseElevation;

    public ConfessionsCardPagerAdapter(Context context, ArrayList<Message> confessionsList, String confessionType) {
        this.confessionType = confessionType;
        mViews = new ArrayList<>();
        randomIllustrator = new RandomIllustrator();
        mContext = context;
        sp = mContext.getSharedPreferences(mContext.getPackageName(), Context.MODE_PRIVATE);
        this.confessionsList = confessionsList;
        for (int i = 0; i < confessionsList.size(); i++) {
            mViews.add(null);
        }
        getScreenSize();

        mRealm = Realm.getDefaultInstance();
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
        return confessionsList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        final View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.message_item, container, false);
        container.addView(view);
        bind(confessionsList, view, position);

        CardView cardView = (CardView) view.findViewById(R.id.cardView);

        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }

        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ConfessionsChatActivity.class);

                String confessionsListString = new Gson().toJson(confessionsList);
                intent.putExtra(Config.CONFESSIONS_LIST, confessionsListString);
                intent.putExtra(Config.CURRENT_POSITION, position);
                intent.putExtra(Config.CONFESSION_TYPE, confessionType);
                intent.putExtra(Config.FULL_NAME, ((TextView) view.findViewById(R.id.tv_message_item_name)).getText().toString());
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.enter_from_bottom, R.anim.exit_to_bottom);
            }
        });

        return view;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        //  mViews.set(position, null);
    }

    private void bind(final ArrayList<Message> confessionsList, View view, final int position) {
        TextView tvName = (TextView) view.findViewById(R.id.tv_message_item_name);
        TextView tvTime = (TextView) view.findViewById(R.id.tv_message_item_time);
        TextView tvConfessionAs = (TextView) view.findViewById(R.id.tv_message_item_As);
        ImageView ivPlayPause = (ImageView) view.findViewById(R.id.iv_message_item_play_button);
        LinearLayout llContainer = (LinearLayout) view.findViewById(R.id.ll_message_item_container);

        ImageView ivDelete = (ImageView) view.findViewById(R.id.iv_message_item_delete);
        ImageView ivIllustration = (ImageView) view.findViewById(R.id.iv_message_item_illustration);

        RandomColor randomColor = new RandomColor();

        GradientDrawable drawable = new GradientDrawable();

        int colors[] = {Color.parseColor(randomColor.getDrawable(confessionsList.get(position).getMessage_color()).getGradient1()), Color.parseColor(randomColor.getDrawable(confessionsList.get(position).getMessage_color()).getGradient2())};

        drawable.setColors(colors);
        drawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        drawable.setCornerRadius(10);

        llContainer.setBackgroundDrawable(drawable);

        Log.d("TAGG", "bind: RENDOMILL" + confessionsList.get(position).getMessage_icon());
        ivIllustration.setImageResource(randomIllustrator.getDrawable(confessionsList.get(position).getMessage_icon()));
        // }
        if (confessionType.equals(Config.MY_CONFESSIONS)) {
            ivDelete.setVisibility(View.VISIBLE);
            tvConfessionAs.setText("Sent As");
        } else {
            ivDelete.setVisibility(View.INVISIBLE);
        }

        LinearLayout.LayoutParams playPauseParams = new LinearLayout.LayoutParams((int) (mWidth * 0.12), (int) (mWidth * 0.12));
        ivPlayPause.setLayoutParams(playPauseParams);

        tvName.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (mWidth * 0.04));
        tvTime.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (mWidth * 0.03));
        tvTime.setPadding(0,mWidth/32,0,0);
        tvConfessionAs.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (mWidth * 0.03));

        String id = sp.getString(Config.USER_ID, "");

        if (!confessionsList.get(position).getSender_id().equals(id)) {
            if (confessionsList.get(position).isUser_type()) {
                Log.d("NAMEINCONFESS", "true: " + confessionsList.get(position).getMessage_theme().get(id));
                tvName.setText(fixRandomName(confessionsList.get(position).getMessage_theme().get(id)));
            } else {
                Log.d("NAMEINCONFESS", "false: " + confessionsList.get(position).getMessage_theme().get(id));
                tvName.setText(confessionsList.get(position).getMessage_theme().get(id));
            }
        } else {
            if (confessionsList.get(position).isUser_type()) {
                tvName.setText("ANONYMOUS");
            } else {
                tvName.setText(confessionsList.get(position).getSender_appname());
            }
        }

        try {
            tvTime.setText(timeConvert(Long.parseLong(confessionsList.get(position).getMessage_time())));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteConfession(position);
            }
        });

//        RealmResults<FriendsModelRealm> friendsModelRealms = mRealm.where(FriendsModelRealm.class).findAll();
//        friendsModelRealms.removeChangeListener(new RealmChangeListener<RealmResults<FriendsModelRealm>>() {
//                                                    @Override
//                                                    public void onChange(RealmResults<FriendsModelRealm> friendsModelRealms) {
//                                                        notifyDataSetChanged();
//                                                    }
//                                                }
//        );
//
//
        RealmResults<MessageRealm> realmResults = mRealm.where(MessageRealm.class)
                .equalTo("message_type", true).equalTo("sender_id", sp.getString(Config.USER_ID, "")).findAll();
//
//        realmResults.addChangeListener(new RealmChangeListener<RealmResults<MessageRealm>>() {
//            @Override
//            public void onChange(RealmResults<MessageRealm> messageRealms) {
//                notifyDataSetChanged();
//            }
//        });
//
        realmResults.removeChangeListener(new RealmChangeListener<RealmResults<MessageRealm>>() {
            @Override
            public void onChange(RealmResults<MessageRealm> messageRealms) {
                notifyDataSetChanged();
            }
        });


    }


    void deleteConfession(int position) {

        final String messageId = confessionsList.get(position).getMessage_id();
//        mRealm.beginTransaction();
//        RealmResults<MessageRealm> messageRealm = mRealm.where(MessageRealm.class).equalTo("message_id", messageId).findAll();
//        messageRealm.deleteFirstFromRealm();
//        mRealm.commitTransaction();
        new ConfessionDeleteDialog(mContext, messageId, confessionsReference, messageReference, false, this).showDialog();
//        confessionsReference.child(messageId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                messageReference.child(messageId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//
//                        mRealm.beginTransaction();
//                        RealmResults<MessageRealm> messageRealm = mRealm.where(MessageRealm.class).equalTo("message_id", messageId).findAll();
//                        messageRealm.deleteFirstFromRealm();
//                        mRealm.commitTransaction();
//                        notifyDataSetChanged();
//                    }
//                });
//            }
//        });
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

    String fixRandomName(String s) {
        String res = "";

        if (s != null && !s.equals("")) {
            String[] a = s.split(",");
            String str = a[1] + " " + a[0];
            for (int i = 0; i < str.length(); i++) {
                res += Character.toUpperCase(str.charAt(i));
            }
        }
        return res;
    }


    void getScreenSize() {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        mHeight = displayMetrics.heightPixels;
        mWidth = displayMetrics.widthPixels;
    }
}