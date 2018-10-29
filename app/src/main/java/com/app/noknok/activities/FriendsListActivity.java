package com.app.noknok.activities;

import android.content.Intent;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.noknok.R;
import com.app.noknok.adapters.FriendsAdapter;
import com.app.noknok.definitions.Config;
import com.app.noknok.definitions.ServerAPIs;
import com.app.noknok.dialogs.SessionExpireDialog;
import com.app.noknok.interfaces.AsyncResponse;
import com.app.noknok.interfaces.RetroInterface;
import com.app.noknok.models.FriendsModel;
import com.app.noknok.models.FriendsModelRealm;
import com.app.noknok.models.RetroHelper;
import com.app.noknok.services.GetContacts;
import com.app.noknok.utils.ConnectionDetector;
import com.app.noknok.utils.LoadingDialog;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import io.realm.Realm;
import io.realm.RealmQuery;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by dev on 21/6/17.
 */

public class FriendsListActivity extends BaseActivity implements View.OnClickListener {

    public static FriendsListActivity mFriend;
    private static float sideIndexX;
    private static float sideIndexY;
    SessionExpireDialog sessionExpireDialog;
    Realm mRealm;
    ArrayList<FriendsModel> mFriendsList = new ArrayList<>();
    ArrayList<String> mNameArraylist = new ArrayList<>();
    LinearLayout btSearch;
    FriendsAdapter adapter;
    LinearLayout btCancel;
    String mContacts;
    LinearLayout llMainFriendListContainer, llFriendsListContainer;
    ListView lvFriendList;
    ArrayList<String> mColorArrayList = new ArrayList<>();
    TextView tvFriendsTitle;
    RelativeLayout rlNoResultFound;
    ImageView imgNoResultFound;
    MyContentObserver myContentObserver;
    TextView tvNoResultFound;
    private GestureDetector mGestureDetector;
    private int sideIndexHeight;
    private List<Object[]> alphabet;
    private HashMap<String, Integer> sections = new HashMap<String, Integer>();
    private int indexListSize;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        mRealm = Realm.getDefaultInstance();
        initUI();
        initListeners();
        mFriend = this;
        mGestureDetector = new GestureDetector(this, new SideIndexGestureListener());

//        populateColor();

        myContentObserver = new MyContentObserver(new Handler());
        this.getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, myContentObserver);
        LoadingDialog.getLoader().showLoader(this);
        new GetContacts(this, new AsyncResponse() {
            @Override
            public void processFinish(String output) {

                Log.d("responsss", output);
                mContacts = output;
                try {
                    getList();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).execute();

    }

//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        if (mFriendsList != null) {
//
//            mFriendsList.clear();
//            mNameArraylist.clear();
//            mColorArrayList.clear();
//
//        }
//        new GetContacts(this, new AsyncResponse() {
//            @Override
//            public void processFinish(String output) {
//                Log.d("responsss", output);
//                mContacts = output;
//                try {
//                    getList();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).execute();
//    }

    public void initListeners() {
        btCancel.setOnClickListener(this);
        btSearch.setOnClickListener(this);
    }

    public void initUI() {

        llMainFriendListContainer = (LinearLayout) findViewById(R.id.ll_main_friends_list_container);
        llFriendsListContainer = (LinearLayout) findViewById(R.id.ll_friends_list_container);
        tvFriendsTitle = (TextView) findViewById(R.id.tv_friends_title);
        lvFriendList = (ListView) findViewById(R.id.lv_friends_list);
        btSearch = (LinearLayout) findViewById(R.id.but_search);
        btCancel = (LinearLayout) findViewById(R.id.but_cancel);
        rlNoResultFound = (RelativeLayout) findViewById(R.id.rl_no_result);
        imgNoResultFound = (ImageView) findViewById(R.id.img_no_result);
        tvNoResultFound = (TextView) findViewById(R.id.tv_no_result_found);
//        rlNoResultFound.setVisibility(View.GONE);
//        llFriendsListContainer.setVisibility(View.VISIBLE);

        tvNoResultFound.setTextColor(getResources().getColor(R.color.grey));

        tvNoResultFound.setTextSize(TypedValue.COMPLEX_UNIT_PX, mScreenwidth / 20);

        tvFriendsTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, mScreenheight / 28);
        tvFriendsTitle.setPadding(mScreenwidth / 80, mScreenheight / 90, mScreenwidth / 80, mScreenheight / 90);

    }

    public void populateColor() {
        for (int i = 0; i < (mNameArraylist.size() / Config.GETCOLOR().size()) + 1; i++) {
            mColorArrayList.addAll(Config.GETCOLOR());
            Log.d("colorsize", mColorArrayList.size() + "--" + mNameArraylist.size());
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        } else {
            return false;
        }
    }

    public void updateList() {
        LinearLayout sideIndex = (LinearLayout) findViewById(R.id.ll_sideIndex);
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
        LinearLayout sideIndex = (LinearLayout) findViewById(R.id.ll_sideIndex);
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
            lvFriendList.setSelection(subitemPosition);
        }
    }

    public void create_view(ArrayList<String> names, ArrayList<String> color) {
        Log.e("countries", "are " + names);
        Collections.sort(names);
        //  String output = input.substring(0, 1).toUpperCase() + input.substring(1);
        alphabet = new ArrayList<Object[]>();
        //lv.invalidate();
        List<FriendsAdapter.Row> rows = new ArrayList<FriendsAdapter.Row>();
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
                rows.add(new FriendsAdapter.Section(firstLetter));
                sections.put(firstLetter, start);
            }
            // Add the country to the list
            rows.add(new FriendsAdapter.Item(country, color.get(namesCount)));

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
        adapter = new FriendsAdapter(FriendsListActivity.this);
        adapter.setRows(rows);
        lvFriendList.setAdapter(adapter);
        updateList();
    }

    public void getList() throws JSONException {
        Log.d("access_token", sp.getString(Config.ACCESS_TOKEN, ""));
        Log.d("REdpmm", String.valueOf(mContacts));

        if ((new ConnectionDetector(this).isConnectingToInternet())) {
            RestAdapter mAdpater = new RestAdapter.Builder().setEndpoint(ServerAPIs.BASE_URL).setLogLevel(RestAdapter.LogLevel.FULL).build();
            RetroInterface mRetroInterface = mAdpater.create(RetroInterface.class);
            mRetroInterface.get_friends_list(sp.getString(Config.ACCESS_TOKEN, ""), mContacts, new Callback<RetroHelper.GetFriendsList>() {
                @Override
                public void success(RetroHelper.GetFriendsList getfriends, Response response) {
                    if (getfriends.response != null && getfriends.response.size() > 0) {
                        if (mFriendsList != null)
                            mFriendsList.clear();
                        if (mNameArraylist != null)
                            mNameArraylist.clear();
                        if (mColorArrayList != null)
                            mColorArrayList.clear();

                        for (int i = 0; i < getfriends.response.size(); i++) {
                            FriendsModel friendsModel = new FriendsModel();
                            RetroHelper.GetFriendsList.Response res = getfriends.response.get(i);

                            if (!checkIfFriendsExists(res.user_id)) {
                                mRealm.beginTransaction();
                                FriendsModelRealm friendsModelRealm = mRealm.createObject(FriendsModelRealm.class);
                                friendsModelRealm.setUser_id(res.user_id);
                                friendsModelRealm.setName_guessed(res.name_guessed);
                                friendsModelRealm.setName_on_app(res.name_on_app);
                                friendsModelRealm.setRight_guess(res.right_guess);
                                friendsModelRealm.setNumber(res.number);
                                friendsModelRealm.setName_in_contact(res.name_in_contact);
                                mRealm.commitTransaction();
                            }
                            friendsModel.setNumber(res.number);
                            friendsModel.setName_in_contact(res.name_in_contact.toUpperCase());
                            friendsModel.setName_on_app(res.name_on_app.toUpperCase());
                            mFriendsList.add(friendsModel);
                            mNameArraylist.add(friendsModel.getName_in_contact());
                            Log.d("responsee", String.valueOf(mNameArraylist));

                            if (mNameArraylist != null || mNameArraylist.size() != 0) {
                                populateColor();
                                create_view(mNameArraylist, mColorArrayList);
                                llFriendsListContainer.setVisibility(View.VISIBLE);
                                rlNoResultFound.setVisibility(View.GONE);
                            } else {
                                llFriendsListContainer.setVisibility(View.GONE);
                                rlNoResultFound.setVisibility(View.VISIBLE);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        LoadingDialog.getLoader().dismissLoader();
                    } else {
//                        adapter.notifyDataSetChanged();
                        LoadingDialog.getLoader().dismissLoader();
//                        ServerAPIs.showServerErrorSnackbar(llMainFriendListContainer, getfriends.error);
                        llFriendsListContainer.setVisibility(View.GONE);
                        rlNoResultFound.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    LoadingDialog.getLoader().dismissLoader();
                    ServerAPIs.showServerErrorSnackbar(llMainFriendListContainer, error.getMessage());
                    error.printStackTrace();
                }
            });
        } else {
            LoadingDialog.getLoader().dismissLoader();
            llFriendsListContainer.setVisibility(View.GONE);
            rlNoResultFound.setVisibility(View.VISIBLE);
            ServerAPIs.noInternetConnection(llMainFriendListContainer);
        }
    }

    private boolean checkIfFriendsExists(String user_id) {
        RealmQuery<FriendsModelRealm> query = mRealm.where(FriendsModelRealm.class)
                .equalTo("user_id", user_id);
        return query.count() != 0;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.but_cancel: {
                onBackPressed();
                break;
            }
            case R.id.but_search: {
                String frnd = new Gson().toJson(mFriendsList);
                Intent intent = new Intent(FriendsListActivity.this, FriendsSearchActivity.class);
                intent.putExtra("FRIENDSDETAILS", frnd);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mFriendsList.clear();
//        mNameArraylist.clear();
//        mColorArrayList.clear();
//        LoadingDialog.getLoader().showLoader(this);
//        try {
//            getList();
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        sessionExpireDialog = new SessionExpireDialog(FriendsListActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.getContentResolver().unregisterContentObserver(myContentObserver);

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

    public class MyContentObserver extends ContentObserver {
        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
//            try {
//                    mFriendsList.clear();
//                    mNameArraylist.clear();
//                    mColorArrayList.clear();
//                    getList();
////                adapter.notifyDataSetChanged();
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }

            new GetContacts(FriendsListActivity.this, new AsyncResponse() {
                @Override
                public void processFinish(String output) {

                    Log.d("responsss", output);
                    mContacts = output;
                    try {
                        getList();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).execute();

            Log.d("contaclist", String.valueOf(selfChange));
        }
    }
}
