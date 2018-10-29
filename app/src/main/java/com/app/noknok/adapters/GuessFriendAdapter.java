package com.app.noknok.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.noknok.R;
import com.app.noknok.activities.GuessActivity;
import com.app.noknok.interfaces.GuessInterface;
import com.app.noknok.models.FriendsModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.app.noknok.activities.GuessActivity.mFriendsSelectedCounter;


/**
 * Created by dev on 23/8/17.
 */

public class GuessFriendAdapter extends BaseAdapter {

    public static GuessInterface mGuessInterface;
    Context context;
    int mWidth, mHeight;
    GuessActivity.GuessDoneInterface mGuessDoneInterface;
    //  int mSelectedCounter = 0;
    Button btDone;
    ArrayList<FriendsModel> mCheckedfriendsModelList = new ArrayList<>();
    SharedPreferences sp;
    HashMap<String, Boolean> mCheckMap;
    private List<Row> rows;

    public GuessFriendAdapter(Context context, List<Row> rows, GuessActivity.GuessDoneInterface guessDoneInterface, Button btDone,
                              ArrayList<FriendsModel> mCheckedfriendsModelList, HashMap<String, Boolean> checkMap) {
        this.context = context;
        this.rows = rows;
        this.btDone = btDone;
        this.mCheckedfriendsModelList = mCheckedfriendsModelList;
        mCheckMap = checkMap;
        mGuessDoneInterface = guessDoneInterface;
        //    mSelectedCounter = selectedCounter;
        getMeasure();

    }

    public static void setGuessInterface(GuessInterface guessInterface) {
        mGuessInterface = guessInterface;
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
    public View getView(final int position, final View convertView, ViewGroup parent) {

        View view = convertView;

        if (getItemViewType(position) == 0) { // Item
            MyHolder myHolder = new MyHolder();

            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) parent.getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = (LinearLayout) inflater.inflate(R.layout.adapter_guess_friend,
                        parent, false);
                myHolder.checkBox = (CheckBox) view.findViewById(R.id.check_guess_item);
                myHolder.textView = (TextView) view.findViewById(R.id.txt_guess_item);
                myHolder.llItemContainer = (LinearLayout) view.findViewById(R.id.ll_guess_main);
                myHolder.llMainContainer = (LinearLayout) view.findViewById(R.id.ll_adapter_guess_friend_container);
                view.setTag(myHolder);
            } else {
                myHolder = (MyHolder) convertView.getTag();
            }


            final Item item = (Item) getItem(position);

            LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(mHeight / 15, mHeight / 15);
            imgParams.setMargins(mWidth / 8, 0, mWidth / 15, 0);
            myHolder.checkBox.setLayoutParams(imgParams);
            myHolder.textView.setText(item.text);
            float txtSizeSelection = mWidth / 20;

            AbsListView.LayoutParams convertViewParams = new AbsListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                    , mHeight / 10);
            view.setLayoutParams(convertViewParams);

            LinearLayout.LayoutParams linearparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.MATCH_PARENT);
            linearparam.setMargins(mWidth / 80, 0, 0, 0);

            myHolder.llItemContainer.setLayoutParams(linearparam);
            myHolder.textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtSizeSelection);
////
////            if (mCheckedfriendsModelList != null && mCheckedfriendsModelList.size() > 0) {
////
////                for (int i = 0; i < mCheckedfriendsModelList.size(); i++) {
////                    if (mCheckedfriendsModelList.get(i).getUser_id().equalsIgnoreCase(((Item) getItem(position)).mFriendsModel.getUser_id())) {
////                        myHolder.checkBox.setChecked(true);
////                    }
////                }
////            }
//
//
//            for(int i=0;i<mCheckedfriendsModelList.size(); i++){
//                mCheckMap.put(mCheckedfriendsModelList.get(i).getUser_id(),true);
//            }

            myHolder.checkBox.setChecked(mCheckMap.get(item.mFriendsModel.getUser_id()));


            final MyHolder finalMyHolder = myHolder;

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (finalMyHolder.checkBox.isChecked()) {
                        finalMyHolder.checkBox.setChecked(false);
                        //mSelectedCounter--;
                        //  mCheckedfriendsModelList.remove(item.mFriendsModel);
                        // mFriendsSelectedCounter--;

                        //  mCheckMap.put(item.mFriendsModel.getUser_id(),false);

                        mGuessInterface.unCheckedItem(item.mFriendsModel); //interface for guessactivity

                    } else {
                        if (mFriendsSelectedCounter < 3) {
                            finalMyHolder.checkBox.setChecked(true);
                            //   mSelectedCounter ++;
                            //  mCheckedfriendsModelList.add(item.mFriendsModel);
                            //    mFriendsSelectedCounter++;

                            //       mCheckMap.put(item.mFriendsModel.getUser_id(),true);

                            mGuessInterface.checkedItem(item.mFriendsModel);  //interface for guessactivity
                        } else {
                            Snackbar.make(finalMyHolder.llMainContainer, "You can select maximum 3 contacts",
                                    Snackbar.LENGTH_SHORT).show();
                        }
                    }

                }
            });

            btDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCheckedfriendsModelList.size() > 0) {
                        mGuessDoneInterface.getCheckedFriends(mCheckedfriendsModelList);
                        for (int i = 0; i < mCheckedfriendsModelList.size(); i++)
                            Log.d("mCheckelList", mCheckedfriendsModelList.get(i).getName_in_contact());
                    }else{
                        Snackbar.make(finalMyHolder.llMainContainer, "Please select atleast 1 contact",
                                Snackbar.LENGTH_SHORT).show();
                    }

                }
            });


        } else { // Section
            MyHolder myHolder = new MyHolder();
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) parent.getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = (LinearLayout) inflater.inflate(R.layout.adapter_friends_section,
                        parent, false);
                myHolder.textView = (TextView) view.findViewById(R.id.txt_friends_section_item);
                myHolder.llItemContainer = (LinearLayout) view.findViewById(R.id.ll_main);
                view.setTag(myHolder);
            } else {
                myHolder = (MyHolder) convertView.getTag();
            }

            float txtSizeSelection = mWidth / 28;
            AbsListView.LayoutParams convertViewParams = new AbsListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                    , mHeight / 20);
            view.setLayoutParams(convertViewParams);

            LinearLayout.LayoutParams linearparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.MATCH_PARENT);
            linearparam.setMargins(mWidth / 28, 0, 0, 0);
            myHolder.llItemContainer.setLayoutParams(linearparam);
            Section section = (Section) getItem(position);
            myHolder.textView.setText(section.text);
            myHolder.textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtSizeSelection);
            myHolder.textView.setTextColor(Color.GRAY);
        }
        return view;
    }


    void getMeasure() {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        mHeight = displayMetrics.heightPixels;
        mWidth = displayMetrics.widthPixels;
    }


    public static class Item extends Row {
        final String text;
        final FriendsModel mFriendsModel;

        public Item(String text, FriendsModel friendsModel) {
            this.text = text;
            mFriendsModel = friendsModel;
        }
    }

    public static abstract class Row {

    }

    public static class Section extends Row {
        public final String text;

        public Section(String text) {
            this.text = text;
        }
    }

    class MyHolder {
        TextView textView;
        CheckBox checkBox;
        LinearLayout llItemContainer, llMainContainer;
    }
}