package com.app.noknok.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.noknok.R;
import com.app.noknok.adapters.MessageSearchAdapter;
import com.app.noknok.definitions.Config;
import com.app.noknok.fragments.OldMessagesFragment;
import com.app.noknok.interfaces.MessageSearchInterface;
import com.app.noknok.models.FriendsModel;

import java.util.ArrayList;
import java.util.HashMap;

import static com.app.noknok.fragments.OldMessagesFragment.chatDialogueList;

/**
 * Created by dev on 29/8/17.
 */

public class MessageSearchActivity extends BaseActivity implements View.OnClickListener {


    EditText etSearchBar;
    LinearLayout llCancel;
    TextView tvSearchNoResultFound;
    ImageView ivSearchCancel, imgSearchNoResult;
    ArrayList<FriendsModel> mFriendsArrayList = new ArrayList<>();
    RelativeLayout rlNoResultFound;


    ArrayList<HashMap<String, String>> mChatDialogueList = new ArrayList<>();
   // ArrayList<HashMap<String, String>> mTotalDialogueList = OldMessagesFragment.chatDialogueList;
   // ArrayList<HashMap<String, String>> searchResultList = new ArrayList<>();
    RecyclerView mRecyclerView;

    MessageSearchAdapter messageSearchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_search);

        mChatDialogueList.addAll(OldMessagesFragment.chatDialogueList);
//      mTotalDialogueList = OldMessagesFragment.chatDialogueList;
        initUI();
        initListners();
        //   JsonConversion();

//        lvFriendsearched.setAdapter(new FriendsSearchedAdapter(FriendsSearchActivity.this, mFriendsArrayList));
//        rlNoResultFound.setVisibility(View.GONE);
        imgSearchNoResult.setImageResource(R.drawable.ill_search_friend);
        imgSearchNoResult.setVisibility(View.VISIBLE);
          tvSearchNoResultFound.setText("Search Friends!");

        etSearchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
                ivSearchCancel.setVisibility(View.VISIBLE);
                ArrayList<HashMap<String, String>> searchResult = new ArrayList<>();

                if (s.toString().length() > 0)
                    searchResult = searchMessages(s.toString());

                if (searchResult.size() <= 0) {
                    rlNoResultFound.setVisibility(View.VISIBLE);
                    imgSearchNoResult.setImageResource(R.drawable.ill_no_result);
                    imgSearchNoResult.setVisibility(View.VISIBLE);
                    tvSearchNoResultFound.setText("No Result Found");

                } else {
                    if (mChatDialogueList.size()>0)
                        mChatDialogueList.clear();
                    rlNoResultFound.setVisibility(View.GONE);
                    mChatDialogueList.addAll(searchResult);
//                    messageSearchAdapter.updateList(searchResult);
                   // mRecyclerView.setAdapter(messageSearchAdapter);
                    messageSearchAdapter.notifyDataSetChanged();
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
                if (etSearchBar.getText().length() <= 0) {
                    rlNoResultFound.setVisibility(View.GONE);
                    ivSearchCancel.setVisibility(View.INVISIBLE);

                    if (mChatDialogueList.size()>0) {
                        mChatDialogueList.clear();
                    }
                    mChatDialogueList.addAll(OldMessagesFragment.chatDialogueList);
                    messageSearchAdapter.notifyDataSetChanged();
                    imgSearchNoResult.setImageResource(R.drawable.ill_search_friend);
                    imgSearchNoResult.setVisibility(View.VISIBLE);
                    tvSearchNoResultFound.setText("Search Friends!");
                    rlNoResultFound.setVisibility(View.VISIBLE);

                }
            }
        });


    }

    private void initUI() {

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_activity_message_search_recyclerview);

        etSearchBar = (EditText) findViewById(R.id.edit_frinds_search);
        etSearchBar.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        rlNoResultFound = (RelativeLayout) findViewById(R.id.rl_search_no_result);
        ivSearchCancel = (ImageView) findViewById(R.id.img_cancel_search);
        llCancel = (LinearLayout) findViewById(R.id.ll_search_activity_cancel);

        imgSearchNoResult = (ImageView) findViewById(R.id.img_search_no_result);
        tvSearchNoResultFound = (TextView) findViewById(R.id.tv_search_no_result_found);




        MessageSearchClickInterface messageSearchClickInterface = new MessageSearchClickInterface() {
            @Override
            public void onClick(int position) {

                Intent intent;
                intent = new Intent(MessageSearchActivity.this, MessageTimelineActivity.class);
                intent.putExtra(Config.CHAT_DIALOGUE_ID, mChatDialogueList.get(position).get(Config.CHAT_DIALOGUE_ID));
                intent.putExtra(Config.FULL_NAME, mChatDialogueList.get(position).get(Config.FULL_NAME));
                intent.putExtra(Config.MESSAGE_TYPE, mChatDialogueList.get(position).get(Config.MESSAGE_TYPE));
                intent.putExtra(Config.RECEIVER_ID, mChatDialogueList.get(position).get(Config.RECEIVER_ID));
                intent.putExtra(Config.LAST_GUESS_TIME, mChatDialogueList.get(position).get(Config.LAST_GUESS_TIME));
                intent.putExtra(Config.MESSAGE_COLOR, mChatDialogueList.get(position).get(Config.MESSAGE_COLOR));
                intent.putExtra(Config.MESSAGE_ICON, mChatDialogueList.get(position).get(Config.MESSAGE_ICON));
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_bottom, R.anim.exit_to_bottom);
            }
        };


        messageSearchAdapter = new MessageSearchAdapter(this, mChatDialogueList, messageSearchClickInterface);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(messageSearchAdapter);
        messageSearchAdapter.notifyDataSetChanged();


    }

    private void initListners() {

        ivSearchCancel.setOnClickListener(this);
        llCancel.setOnClickListener(this);
    }

//    @Override
//    public void getChatDialogueList(ArrayList<HashMap<String, String>> chatDialogueList) {
//        Log.d("chatDialogueList", String.valueOf(chatDialogueList.size()));
////        if (mChatDialogueList != null) {
////            mChatDialogueList.clear();
////        }
//        //   mChatDialogueList = chatDialogueList;
////        if (messageSearchAdapter != null)
////            messageSearchAdapter.notifyDataSetChanged();
//    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.img_cancel_search:
                etSearchBar.setText("");
                ivSearchCancel.setVisibility(View.GONE);
                break;
            case R.id.ll_search_activity_cancel:
//                etSearchBar.setText("");
                onBackPressed();
               // this.finish();
        }
    }


    ArrayList<HashMap<String, String>> searchMessages(String query) {

        ArrayList<HashMap<String, String>> resultList = new ArrayList<>();
        for (int i = 0; i < OldMessagesFragment.chatDialogueList.size(); i++) {

            if (OldMessagesFragment.chatDialogueList.get(i).get(Config.FULL_NAME).toLowerCase().contains(query.toLowerCase())) {
                resultList.add(OldMessagesFragment.chatDialogueList.get(i));
            }
        }

        return resultList;

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }


    public interface MessageSearchClickInterface{
        void onClick(int position);
    }
}