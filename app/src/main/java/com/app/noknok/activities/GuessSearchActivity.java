package com.app.noknok.activities;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.noknok.R;
import com.app.noknok.definitions.Config;
import com.app.noknok.interfaces.GuessSearchInterface;
import com.app.noknok.models.FriendsModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dev on 23/8/17.
 */

public class GuessSearchActivity extends BaseActivity implements View.OnClickListener {
    public static GuessSearchInterface guessSearchInterface;
    public static GuessSearchActivity mGuessSearchActivity;
    EditText etSearchBar;
    LinearLayout llCancel;
    TextView tvSearchNoResultFound;
    ImageView ivSearchCancel,imgSearchNoResult;
    ListView lvFriendsearched;
      ArrayList<FriendsModel> mRemainingFriendsList = new ArrayList<>();
    RelativeLayout rlNoResultFound;
    LinearLayout llMainContainer;

    ArrayList<FriendsModel> mSelectedFriendsList = new ArrayList<>();
    int mSelectedCounter = 0;
    TextView txtSearchCancel;


    HashMap<String, Boolean> mCheckMap = new HashMap<>();

    public static void setGuessSearchInterface(GuessSearchInterface selec) {
        guessSearchInterface = selec;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list_search);

        mGuessSearchActivity = this;

        initUI();
        initListners();

        mSelectedCounter = getIntent().getIntExtra(Config.SELECTED_COUNTER, 0);

      //  Toast.makeText(GuessSearchActivity.this, "selected counter: " + mSelectedCounter, Toast.LENGTH_SHORT).show();

        String friendsListString = getIntent().getStringExtra(Config.FRIENDSDETAILS);
        mCheckMap = (HashMap<String, Boolean>) getIntent().getSerializableExtra("CHECKMAP");
        JsonConversion(friendsListString);

//        for(int i = 0; i < mRemainingFriendsList.size(); i++){
//            mRemainingFriendsList.get(i).getName_in_contact().replace(mRemainingFriendsList.get(i).getName_in_contact()
//                    ,mRemainingFriendsList.get(i).getName_in_contact().toUpperCase());
//        }

//        lvFriendsearched.setAdapter(new GuessSearchAdapter(GuessSearchActivity.this, mRemainingFriendsList));
//        rlNoResultFound.setVisibility(View.GONE);
        rlNoResultFound.setVisibility(View.VISIBLE);
        imgSearchNoResult.setImageResource(R.drawable.ill_search_friend);
        imgSearchNoResult.setVisibility(View.VISIBLE);
        tvSearchNoResultFound.setText("Search Friends");

        etSearchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
                ivSearchCancel.setVisibility(View.VISIBLE);
                ArrayList<FriendsModel> listClone = new ArrayList<>();
                listClone.clear();
                for (int i = 0; i < mRemainingFriendsList.size(); i++) {
                    if (mRemainingFriendsList.get(i).name_in_contact.contains(s.toString())) {
                        listClone.add(mRemainingFriendsList.get(i));
                        Log.d("rsutt", mRemainingFriendsList.get(i).getName_in_contact().toString());
                    }
                }
                if (listClone.size() == 0) {
                    rlNoResultFound.setVisibility(View.VISIBLE);
                    imgSearchNoResult.setImageResource(R.drawable.ill_no_result);
                    imgSearchNoResult.setVisibility(View.VISIBLE);
                    tvSearchNoResultFound.setText("No Result Found");
                } else {
                    lvFriendsearched.setAdapter(new GuessSearchAdapter(GuessSearchActivity.this, listClone));
                    rlNoResultFound.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                ivSearchCancel.setVisibility(View.VISIBLE);
                if (etSearchBar.getText().length() == 0) {
                    ivSearchCancel.setVisibility(View.INVISIBLE);
                    rlNoResultFound.setVisibility(View.VISIBLE);
                    imgSearchNoResult.setImageResource(R.drawable.ill_search_friend);
                    imgSearchNoResult.setVisibility(View.VISIBLE);
                    tvSearchNoResultFound.setText("Search Friends");


                }
            }
        });
    }

    private void initListners() {

        ivSearchCancel.setOnClickListener(this);
        llCancel.setOnClickListener(this);
    }

    void initUI() {

        llMainContainer = (LinearLayout) findViewById(R.id.ll_main_search_friends_list_container);
        lvFriendsearched = (ListView) findViewById(R.id.friends_searched_list);
        etSearchBar = (EditText) findViewById(R.id.edit_frinds_search);
        etSearchBar.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        rlNoResultFound = (RelativeLayout) findViewById(R.id.rl_search_no_result);
        ivSearchCancel = (ImageView) findViewById(R.id.img_cancel_search);
        llCancel = (LinearLayout) findViewById(R.id.ll_search_activity_cancel);
        txtSearchCancel = (TextView) findViewById(R.id.txt_search_activity_cancel);


        imgSearchNoResult= (ImageView) findViewById(R.id.img_search_no_result);
        tvSearchNoResultFound= (TextView) findViewById(R.id.tv_search_no_result_found);
    }

    public void JsonConversion(String friendsListString) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<FriendsModel>>() {
        }.getType();
        mRemainingFriendsList = gson.fromJson(friendsListString, type);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.img_cancel_search:
                etSearchBar.setText("");
                ivSearchCancel.setVisibility(View.GONE);
                break;

            case R.id.ll_search_activity_cancel:

                guessSearchInterface.selectedFriends(mSelectedFriendsList, mSelectedCounter, mCheckMap);
                finish();

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }


    class GuessSearchAdapter extends BaseAdapter {

        Context context;
        ArrayList<FriendsModel> mFriendsArrayList;
        int mWidth, mHeight;

        GuessSearchAdapter(Context mContext, ArrayList<FriendsModel> mFriendsArrayList) {
            context = mContext;
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView =  inflater.inflate(R.layout.guess_search_item, parent, false);

            AbsListView.LayoutParams convertViewParams = new AbsListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                    , mHeight / 10);
            convertView.setLayoutParams(convertViewParams);
            float txtSizeSelection = mWidth / 20;
            TextView textView = (TextView) convertView.findViewById(R.id.txt_guess_search_item);
            final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.check_guess_search_item);
            textView.setText(mFriendsArrayList.get(position).getName_in_contact());
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtSizeSelection);

            LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(mHeight / 15, mHeight / 15);
            imgParams.setMargins(mWidth / 15, mWidth / 15, mWidth / 15, mWidth / 15);
            checkBox.setLayoutParams(imgParams);

            checkBox.setChecked(mCheckMap.get(mFriendsArrayList.get(position).getUser_id()));

         //   if (mSelectedCounter < 3) {
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Toast.makeText(context, String.valueOf(mSelectedCounter), Toast.LENGTH_SHORT).show();
                        if (checkBox.isChecked()) {
                            checkBox.setChecked(false);
                            mSelectedFriendsList.remove(mFriendsArrayList.get(position));
                            mSelectedCounter--;

                            if (mCheckMap != null) {
                                mCheckMap.put(mFriendsArrayList.get(position).getUser_id(), false);
                            }

                          //  Toast.makeText(context, mSelectedCounter + "", Toast.LENGTH_SHORT).show();
                        } else {
                            if (mSelectedCounter < 3) {
                                checkBox.setChecked(true);
                                mSelectedFriendsList.add(mFriendsArrayList.get(position));
                                mSelectedCounter++;

                                if (mCheckMap != null) {
                                    mCheckMap.put(mFriendsArrayList.get(position).getUser_id(), true);
                                }

                             //   Toast.makeText(context, mSelectedCounter + "", Toast.LENGTH_SHORT).show();
                            } else {
                            Snackbar.make(llMainContainer, "You can select maximum 3 contacts",
                                    Snackbar.LENGTH_SHORT).show();
                        }
                        }

                        if (mSelectedFriendsList.size() > 0) {
                            txtSearchCancel.setText("DONE");
                        } else {
                            txtSearchCancel.setText("CANCEL");
                        }
                    }
                });
//            } else {
//                Toast.makeText(context, "You can select maximum 3 contacts", Toast.LENGTH_SHORT).show();
//            }

            return convertView;
        }

        void getMeasure() {
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            mHeight = displayMetrics.heightPixels;
            mWidth = displayMetrics.widthPixels;
        }

    }


}
