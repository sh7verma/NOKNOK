package com.app.noknok.activities;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.noknok.R;
import com.app.noknok.adapters.GuessFriendAdapter;
import com.app.noknok.definitions.Config;
import com.app.noknok.definitions.ServerAPIs;
import com.app.noknok.dialogs.GuessNoticeDialog;
import com.app.noknok.dialogs.GuessResponseDialog;
import com.app.noknok.interfaces.GuessInterface;
import com.app.noknok.interfaces.GuessSearchInterface;
import com.app.noknok.interfaces.RetroInterface;
import com.app.noknok.models.ChatRealm;
import com.app.noknok.models.FriendsModel;
import com.app.noknok.models.FriendsModelRealm;
import com.app.noknok.models.RetroHelper;
import com.app.noknok.utils.ConnectionDetector;
import com.app.noknok.utils.LoadingDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class GuessActivity extends BaseActivity implements View.OnClickListener, GuessSearchInterface, GuessInterface {

    public static final int FRIENDSSEARCHCODE = 777;
    public static int mFriendsSelectedCounter = 0;

    private static float sideIndexX;
    private static float sideIndexY;
    Realm mRealm;
    ArrayList<FriendsModel> mAllFriendsList = new ArrayList<>();
    ListView listView;
    LinearLayout llCancel, llSearch;
    GuessFriendAdapter guessFriendAdapter;
    ArrayList<String> mNameArrayList = new ArrayList<>();
    GuessDoneInterface mGuessDoneInterface;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    Button btDone;
    String mRecieverId, mChatDialogueId;
    DatabaseReference mChatReference;
    TextView tvGuessFriends, tvSelectContacts;
    LinearLayout llUnderLine, llMainContainer;
    ArrayList<FriendsModel> mCheckedfriendsList = new ArrayList<>();
    HashMap<String, Boolean> mCheckMap = new HashMap<>();
    int mRightGuessCount = 0;
    private int indexListSize;
    private List<Object[]> alphabet;
    private GestureDetector mGestureDetector;
    private int sideIndexHeight;
    private HashMap<String, Integer> sections = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess);
        mRealm = Realm.getDefaultInstance();
        mGestureDetector = new GestureDetector(this, new GuessActivity.SideIndexGestureListener());
        mRecieverId = getIntent().getStringExtra(Config.RECEIVER_ID);
        mChatDialogueId = getIntent().getStringExtra(Config.CHAT_DIALOGUE_ID);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mChatReference = mDatabaseReference.child("Chats").getRef();

        mFriendsSelectedCounter = 0;

        getFriends();
        for (int i = 0; i < mAllFriendsList.size(); i++) {
            mCheckMap.put(mAllFriendsList.get(i).getUser_id(), false);
        }

        new GuessNoticeDialog(this,0,"").showDialog();
        initUi();
        initListeners();
        create_view(mNameArrayList, mAllFriendsList);
    }

    private void initListeners() {
        llCancel.setOnClickListener(this);
        llSearch.setOnClickListener(this);
    }

    void initUi() {
        listView = (ListView) findViewById(R.id.lv_guess_friends_list);
        btDone = (Button) findViewById(R.id.bt_activity_guess_done);
        llCancel = (LinearLayout) findViewById(R.id.ll_activity_guess_cancel);
        llSearch = (LinearLayout) findViewById(R.id.but_guess_search);
        tvGuessFriends = (TextView) findViewById(R.id.tv_activity_guess_friends);
        tvSelectContacts = (TextView) findViewById(R.id.tv_activity_guess_select_contacts);
        llUnderLine = (LinearLayout) findViewById(R.id.ll_activity_guess_underline);
        llMainContainer = (LinearLayout) findViewById(R.id.ll_main_guess_friends_list_container);

        tvGuessFriends.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (mScreenwidth * 0.060));
        tvSelectContacts.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (mScreenwidth * 0.035));

        LinearLayout.LayoutParams underLineParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (mScreenheight * 0.005));
        underLineParams.setMargins((int) (mScreenwidth * 0.09), 0, (int) (mScreenwidth * 0.09), 0);
        llUnderLine.setLayoutParams(underLineParams);

        RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (mScreenheight * 0.08));
        buttonParams.setMargins((int) (mScreenwidth * 0.08), 0, (int) (mScreenwidth * 0.08), (int) (mScreenwidth * 0.04));
        buttonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        btDone.setTextColor(getResources().getColor(R.color.white));
      //  btDone.setLetterSpacing(0.1f);
        btDone.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (mScreenwidth * 0.050));
        btDone.setLayoutParams(buttonParams);

        GuessSearchActivity.setGuessSearchInterface(this);
        GuessFriendAdapter.setGuessInterface(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        } else {
            return false;
        }
    }

    void getFriends() {
        if (mAllFriendsList != null)
            mAllFriendsList.clear();

        if (mNameArrayList != null)
            mNameArrayList.clear();

        RealmResults<FriendsModelRealm> friendsRealmsResult = mRealm.where(FriendsModelRealm.class).findAll();
        for (FriendsModelRealm friendsModelRealm : friendsRealmsResult.sort("name_in_contact", Sort.ASCENDING)) {
            FriendsModel friendsModel = new FriendsModel();
            friendsModel.setName_on_app(friendsModelRealm.getName_on_app().toUpperCase());
            friendsModel.setName_in_contact(friendsModelRealm.getName_in_contact().toUpperCase());
            friendsModel.setNumber(friendsModelRealm.getNumber());
            friendsModel.setUser_id(friendsModelRealm.getUser_id());
            friendsModel.setRight_guess(friendsModelRealm.getRight_guess());
            mAllFriendsList.add(friendsModel);
            mNameArrayList.add(friendsModel.getName_in_contact().toUpperCase());
        }

        Log.d("CHECKEDITEM", "all friendslist: " + mAllFriendsList.size());
    }


    public void updateList() {
        LinearLayout sideIndex = (LinearLayout) findViewById(R.id.ll_guess_sideIndex);
        sideIndex.removeAllViews();
        indexListSize = alphabet.size();
        if (indexListSize < 1) {
            return;
        }

        int indexMaxSize = (int) Math.floor(sideIndex.getHeight() / 20);
        int tmpIndexListSize = indexListSize;
        while (tmpIndexListSize > indexMaxSize) {
            tmpIndexListSize = tmpIndexListSize / 2;
        }
        double delta;
        if (tmpIndexListSize > 0) {
            delta = indexListSize / tmpIndexListSize;
        } else {
            delta = 1;
        }

        TextView tmpTV;

        for (double i = 1; i <= indexListSize; i = i + delta) {
            Object[] tmpIndexItem = alphabet.get((int) i - 1);
            String tmpLetter = tmpIndexItem[0].toString();

            tmpTV = new TextView(this);
            tmpTV.setText(tmpLetter);

            tmpTV.setTextColor(getResources().getColor(R.color.grey));

            tmpTV.setText(tmpLetter);
            tmpTV.setGravity(Gravity.CENTER_VERTICAL);
            tmpTV.setTextSize(13);
            //   tmpTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (w * 0.035));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, (int) (mScreenwidth * 0.005), 0);
            tmpTV.setLayoutParams(params);
            sideIndex.addView(tmpTV);
        }

        sideIndexHeight = sideIndex.getHeight();
        sideIndex.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // now you know coordinates of touch
                sideIndexX = event.getX();
                sideIndexY = event.getY();
                // and can display a proper item it country list
                displayListItem();
                return false;
            }
        });
    }

    public void displayListItem() {

        LinearLayout sideIndex = (LinearLayout) findViewById(R.id.ll_guess_sideIndex);
        sideIndexHeight = sideIndex.getHeight();
        // compute number of pixels for every side index item
        double pixelPerIndexItem = (double) sideIndexHeight / indexListSize;

        // compute the item index for given event position belongs to
        int itemPosition = (int) (sideIndexY / pixelPerIndexItem);

        // get the item (we can do it since we know item index)
        if (itemPosition < alphabet.size()) {
            Object[] indexItem = alphabet.get(itemPosition);
            String key = String.valueOf(indexItem[0]);
            int subitemPosition = sections.get(key);

            // ListView listView = (ListView) findViewById(android.R.id.list);
            listView.setSelection(subitemPosition);
        }
    }

    public void create_view(ArrayList<String> names, ArrayList<FriendsModel> friendsModels) {
        Log.e("countries", "are " + names);
        Collections.sort(names);
        //  String output = input.substring(0, 1).toUpperCase() + input.substring(1);
        List<GuessFriendAdapter.Row> rows = new ArrayList<>();
        alphabet = new ArrayList<Object[]>();
        //lv.invalidate();
        int start = 0;
        int end = 0;
        String previousLetter = null;
        Object[] tmpIndexItem = null;
        Pattern numberPattern = Pattern.compile("[0-9]");

        int namesCount = 0;
        for (String country : names) {

            String firstLetter = country.substring(0, 1);

            // Group numbers together in the scroller
            if (numberPattern.matcher(firstLetter).matches()) {
                firstLetter = "#";
            }

            // If we've changed to a new letter, add the previous letter to the
            // alphabet scroller
            if (previousLetter != null && !firstLetter.equals(previousLetter)) {
                end = rows.size() - 1;
                tmpIndexItem = new Object[3];
                tmpIndexItem[0] = previousLetter.toUpperCase(Locale.UK);
                tmpIndexItem[1] = start;
                tmpIndexItem[2] = end;
                alphabet.add(tmpIndexItem);
                start = end + 1;
            }
            // Check if we need to add a header row
            if (!firstLetter.equals(previousLetter)) {
                rows.add(new GuessFriendAdapter.Section(firstLetter));
                sections.put(firstLetter, start);
            }
            // Add the country to the list
            rows.add(new GuessFriendAdapter.Item(country, friendsModels.get(namesCount)));

            previousLetter = firstLetter;
            namesCount++;
        }

        if (previousLetter != null) {
            // Save the last letter
            tmpIndexItem = new Object[3];
            tmpIndexItem[0] = previousLetter.toUpperCase(Locale.UK);
            tmpIndexItem[1] = start;
            tmpIndexItem[2] = rows.size() - 1;
            alphabet.add(tmpIndexItem);
        }

        mGuessDoneInterface = new GuessDoneInterface() {
            @Override
            public void getCheckedFriends(ArrayList<FriendsModel> friendsCheckedList) {

                if (friendsCheckedList.size() == 0) {
                    Snackbar.make(findViewById(android.R.id.content), "Please select atleast 1 contact",
                            Snackbar.LENGTH_SHORT).show();
                } else {

//                    if(ConnectionDetector)


                    LoadingDialog.getLoader().showLoader(GuessActivity.this);

                    boolean userFound = false;

                    for (int i = 0; i < friendsCheckedList.size(); i++) {
                        Log.d("dostcheck", friendsCheckedList.get(i).getName_in_contact());
                        if (friendsCheckedList.get(i).getUser_id().equals(mRecieverId)) {
                            userFound = true;
                            break;
                        } else {
                            userFound = false;
                        }
                    }


                        if ((new ConnectionDetector(GuessActivity.this).isConnectingToInternet())) {
                            if (userFound) {
                            mChatReference.child(mChatDialogueId).child("guess_ids").child(sp.getString(Config.USER_ID, "")).setValue("1").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    RealmResults<ChatRealm> results = mRealm.where(ChatRealm.class)
                                            .equalTo("chatDialogueId", mChatDialogueId).findAll();

                                    Date date = new Date();
                                    String s = String.valueOf(date.getTime());
                                    mChatReference.child(mChatDialogueId).child("last_guess_time").child(sp.getString(Config.USER_ID, "")).setValue(s);


                                    mRealm.beginTransaction();
                                    for (int i = 0; i < results.size(); i++) {
                                        ChatRealm chatRealm = results.get(i);
                                        chatRealm.setGuessId("1");
                                        chatRealm.setLastGuessTime(s);
                                    }
                                    mRealm.commitTransaction();

                                    updateRightGuessCount();

                                    LoadingDialog.getLoader().dismissLoader();
                                    new GuessResponseDialog(GuessActivity.this, true).showDialog();
                                }
                            });

                        } else {
                            Date date = new Date();
                            mChatReference.child(mChatDialogueId).child("last_guess_time").child(sp.getString(Config.USER_ID, "")).setValue(String.valueOf(date.getTime())).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    RealmResults<ChatRealm> results = mRealm.where(ChatRealm.class)
                                            .equalTo("chatDialogueId", mChatDialogueId).findAll();
                                    Date date = new Date();
                                    String s = String.valueOf(date.getTime());
                                    mRealm.beginTransaction();
                                    for (int i = 0; i < results.size(); i++) {
                                        ChatRealm messageRealm = results.get(i);
                                        messageRealm.setLastGuessTime(s);
                                    }
                                    mRealm.commitTransaction();
                                    LoadingDialog.getLoader().dismissLoader();
                                    new GuessResponseDialog(GuessActivity.this, false).showDialog();

                                }
                            });

                        }

                    }else{
                        LoadingDialog.getLoader().dismissLoader();
                        ServerAPIs.noInternetConnection(llMainContainer);
                    }
                }
            }
        };

        guessFriendAdapter = new GuessFriendAdapter(GuessActivity.this, rows,
                mGuessDoneInterface, btDone, mCheckedfriendsList,
                mCheckMap);
        //guessFriendAdapter.setRows(rows);
        listView.setAdapter(guessFriendAdapter);
        updateList();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ll_activity_guess_cancel:
                onBackPressed();
                break;
            case R.id.but_guess_search:
                Log.d("GUESSTEST", "total friends list before remove in guessactivity: " + mAllFriendsList.size());
                mAllFriendsList.removeAll(mCheckedfriendsList);

                Log.d("GUESSTEST", "total friends list after remove in guessactivity: " + mAllFriendsList.size());

                Log.d("GUESSTEST", "mcounter from guessactivity: " + mFriendsSelectedCounter);
                //     guessFriendAdapter.notifyDataSetChanged();
                String frnd = new Gson().toJson(mAllFriendsList);
                Intent intent = new Intent(GuessActivity.this, GuessSearchActivity.class);
                intent.putExtra(Config.FRIENDSDETAILS, frnd);
                intent.putExtra("CHECKMAP", mCheckMap);
                intent.putExtra(Config.SELECTED_COUNTER, mFriendsSelectedCounter);
                startActivityForResult(intent, FRIENDSSEARCHCODE);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    public void selectedFriends(ArrayList<FriendsModel> selectedFriendsList, int selectedCounter, HashMap<String, Boolean> checkMap) {

        if (mCheckMap != null) {
            mCheckMap.clear();
            mCheckMap.putAll(checkMap);
        }
        getFriends();


        for (int i = 0; i < selectedFriendsList.size(); i++) {
            mCheckedfriendsList.add(selectedFriendsList.get(i));
        }

        mFriendsSelectedCounter = selectedCounter;
        guessFriendAdapter.notifyDataSetChanged();

    }

    @Override
    public void checkedItem(FriendsModel friendsModel) {

        mCheckedfriendsList.add(friendsModel);
        mCheckMap.put(friendsModel.getUser_id(), true);

        mFriendsSelectedCounter++;
        guessFriendAdapter.notifyDataSetChanged();

        Log.d("CHECKEDITEM", "selected counter: " + mFriendsSelectedCounter);
    }

    @Override
    public void unCheckedItem(FriendsModel friendsModel) {

        for(int i=0;i<mCheckedfriendsList.size();i++){
            if(mCheckedfriendsList.get(i).getUser_id().equals(friendsModel.getUser_id())){
                mCheckedfriendsList.remove(i);
            }
        }

        mCheckMap.put(friendsModel.getUser_id(), false);
        mFriendsSelectedCounter--;
        guessFriendAdapter.notifyDataSetChanged();

        Log.d("CHECKEDITEM", "selected counter: " + mFriendsSelectedCounter);
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mFriendsSelectedCounter = 0;
//    }

    void updateRightGuessCount() {
        String access_token = sp.getString(Config.ACCESS_TOKEN, "");

       // if ((new ConnectionDetector(GuessActivity.this).isConnectingToInternet())) {
            RestAdapter mAdpater = new RestAdapter.Builder().setEndpoint(ServerAPIs.BASE_URL).build();
            RetroInterface mRetroInterface = mAdpater.create(RetroInterface.class);
            mRetroInterface.update_right_guess(access_token, new Callback<RetroHelper.UpdateRightGuess>() {
                @Override
                public void success(RetroHelper.UpdateRightGuess updateRightGuess, Response response) {
                    Log.d(Config.RIGHT_GUESS_COUNT, response.toString());

                    mRightGuessCount = Integer.parseInt(sp.getString(Config.RIGHT_GUESS_COUNT, "0")) + 1;
                    SharedPreferences.Editor editor = GuessActivity.this.getSharedPreferences(GuessActivity.this.getPackageName(),
                            Context.MODE_PRIVATE).edit();
                    editor.putString(Config.RIGHT_GUESS_COUNT, String.valueOf(mRightGuessCount));
                    editor.apply();
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });

       // }
    }

    public interface GuessDoneInterface {
        void getCheckedFriends(ArrayList<FriendsModel> friendsCheckedList);
    }

    class SideIndexGestureListener extends
            GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            sideIndexX = sideIndexX - distanceX;
            sideIndexY = sideIndexY - distanceY;
            if (sideIndexX >= 0 && sideIndexY >= 0) {
                displayListItem();
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }
}