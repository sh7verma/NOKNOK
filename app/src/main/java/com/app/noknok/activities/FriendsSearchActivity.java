package com.app.noknok.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.noknok.R;
import com.app.noknok.adapters.FriendsSearchedAdapter;
import com.app.noknok.models.FriendsModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dev on 22/6/17.
 */

public class FriendsSearchActivity extends BaseActivity implements View.OnClickListener {
    EditText etSearchBar;
    LinearLayout llCancel;
    TextView tvSearchNoResultFound;
    ListView lvFriendsearched;
    ImageView ivSearchCancel,imgSearchNoResult;
    ArrayList<FriendsModel> mFriendsArrayList = new ArrayList<>();
    RelativeLayout rlNoResultFound;

    String t;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list_search);
        initUI();
        initListners();

        t = getIntent().getStringExtra("FRIENDSDETAILS");

        JsonConversion();

//        lvFriendsearched.setAdapter(new FriendsSearchedAdapter(FriendsSearchActivity.this, mFriendsArrayList));
        rlNoResultFound.setVisibility(View.VISIBLE);
        imgSearchNoResult.setImageResource(R.drawable.ill_search_friend);
        imgSearchNoResult.setVisibility(View.VISIBLE);
        tvSearchNoResultFound.setText("Search Friends!");

        etSearchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
                ivSearchCancel.setVisibility(View.VISIBLE);
                ArrayList<FriendsModel> listClone = new ArrayList<>();
                listClone.clear();
                for (int i = 0; i < mFriendsArrayList.size(); i++) {
                    if (mFriendsArrayList.get(i).name_in_contact.contains(s.toString())) {
                        listClone.add(mFriendsArrayList.get(i));
                        Log.d("rsutt", mFriendsArrayList.get(i).getName_in_contact().toString());
                    }
                }
                if (listClone.size() == 0) {
                    rlNoResultFound.setVisibility(View.VISIBLE);
                    imgSearchNoResult.setImageResource(R.drawable.ill_no_result);
                    imgSearchNoResult.setVisibility(View.VISIBLE);
                    tvSearchNoResultFound.setText("No Result Found");
                } else {
                    lvFriendsearched.setAdapter(new FriendsSearchedAdapter(FriendsSearchActivity.this, listClone));
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
                    tvSearchNoResultFound.setText("Search Friends!");

                }
            }
        });
    }

    private void initListners() {

        ivSearchCancel.setOnClickListener(this);
        llCancel.setOnClickListener(this);
    }

    void initUI() {

        lvFriendsearched = (ListView) findViewById(R.id.friends_searched_list);
        etSearchBar = (EditText) findViewById(R.id.edit_frinds_search);
        etSearchBar.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        rlNoResultFound = (RelativeLayout) findViewById(R.id.rl_search_no_result);
        ivSearchCancel = (ImageView) findViewById(R.id.img_cancel_search);
        llCancel = (LinearLayout) findViewById(R.id.ll_search_activity_cancel);

        imgSearchNoResult= (ImageView) findViewById(R.id.img_search_no_result);
        tvSearchNoResultFound= (TextView) findViewById(R.id.tv_search_no_result_found);
    }

    public void JsonConversion() {
        Gson gson = new Gson();
        Type type = new TypeToken<List<FriendsModel>>() {
        }.getType();
        mFriendsArrayList = gson.fromJson(t, type);
        for (FriendsModel friendsModel : mFriendsArrayList) {
            Log.i("CarData", friendsModel.getName_on_app() + "-" + mFriendsArrayList.get(0).getNumber());
        }
    }

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
                this.finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

}