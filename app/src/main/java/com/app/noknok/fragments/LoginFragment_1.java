package com.app.noknok.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.noknok.R;
import com.app.noknok.activities.CountryCodeActivity;
import com.app.noknok.activities.LoginActivity;
import com.app.noknok.definitions.Config;
import com.app.noknok.definitions.ServerAPIs;
import com.app.noknok.interfaces.RetroInterface;
import com.app.noknok.models.RetroHelper;
import com.app.noknok.utils.ConnectionDetector;
import com.app.noknok.utils.LoadingDialog;

import java.util.Locale;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static android.app.Activity.RESULT_OK;

/**
 * Created by dev on 20/6/17.
 */

public class LoginFragment_1 extends BaseFragment implements View.OnClickListener {

    static String defaultCountryCode = "+44";
    public static final int REQUEST_COUNTRY_CODE = 101;
    RelativeLayout rlRegistrationFormContainer;
    LinearLayout llRegistrationChild, llFullNameContainer, llPhoneNumberContainer;
    TextView tvIam, tvPhoneNumber, tvCountryCode, tvNextButton;
    EditText etFullName, etEnterPhone;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login_1, container, false);
        initUI();
        initListeners();
        return view;
    }

    private void initListeners() {
        tvCountryCode.setOnClickListener(this);
        tvNextButton.setOnClickListener(this);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getCountryCode();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_COUNTRY_CODE && resultCode == RESULT_OK && data != null) {
            if (data.getStringExtra("Country_code") != null && !data.getStringExtra("Country_code").equals("")) {
                defaultCountryCode = data.getStringExtra("Country_code");
                tvCountryCode.setText(data.getStringExtra("Country_code"));

            }
        }
    }

    private void initUI() {

        rlRegistrationFormContainer = (RelativeLayout) view.findViewById(R.id.rl_registration_form_container);
        llRegistrationChild = (LinearLayout) view.findViewById(R.id.ll_registration_form_child);
        llFullNameContainer = (LinearLayout) view.findViewById(R.id.ll_full_name_container);
        llPhoneNumberContainer = (LinearLayout) view.findViewById(R.id.ll_phone_number_container);
        tvIam = (TextView) view.findViewById(R.id.tv_iam);
        tvPhoneNumber = (TextView) view.findViewById(R.id.tv_phone_number);
        tvCountryCode = (TextView) view.findViewById(R.id.tv_country_code);
        tvNextButton = (TextView) view.findViewById(R.id.tv_next_button);
        etFullName = (EditText) view.findViewById(R.id.et_full_name);
        etEnterPhone = (EditText) view.findViewById(R.id.et_enter_phone);

        float boldTextSize = (int) (mScreenwidth * 0.050);

        RelativeLayout.LayoutParams registrationFormParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        registrationFormParams.setMargins(mScreenwidth / 10, mScreenheight / 4, mScreenwidth / 10, mScreenheight / 5);
        rlRegistrationFormContainer.setLayoutParams(registrationFormParams);


        LinearLayout.LayoutParams fullNameParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        fullNameParams.setMargins(mScreenwidth / 15, mScreenheight / 20, mScreenwidth / 15, 0);
        llFullNameContainer.setLayoutParams(fullNameParams);
        llPhoneNumberContainer.setLayoutParams(fullNameParams);


        RelativeLayout.LayoutParams registrationChildParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        registrationChildParams.setMargins(0, 0, 0, mScreenheight / 22);
        llRegistrationChild.setLayoutParams(registrationChildParams);

        tvNextButton.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                (int) (mScreenwidth * 0.060));

        tvNextButton.setPadding((int) (mScreenwidth * 0.08), (int) (mScreenheight * 0.03),
                (int) (mScreenwidth * 0.08), (int) (mScreenheight * 0.03));
        tvIam.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                (int) (mScreenwidth * 0.040));
        tvIam.setAllCaps(true);
        tvPhoneNumber.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                (int) (mScreenwidth * 0.040));

//        etFullName.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        /*InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isLetterOrDigit(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }
        };
        etFullName.setFilters(new InputFilter[] { filter });*/
       /* etFullName.setFilters(new InputFilter[]{
                new InputFilter() {
                    public CharSequence filter(CharSequence src, int start,
                                               int end, Spanned dst, int dstart, int dend) {
                        Log.d("login", "characters " + src);
                        if (src.equals("")) { // for backspace
                            return src;
                        }
                        if (src.toString().matches("[A-Z ,a-z]+")) {
                            String a = src.toString();
                            String upp = a.toUpperCase();
                            CharSequence cs = upp;
                            return cs;
                        }
                        return "";
                    }
                }
        });*/


        etFullName.setTextSize(TypedValue.COMPLEX_UNIT_PX, boldTextSize);
        etEnterPhone.setTextSize(TypedValue.COMPLEX_UNIT_PX, boldTextSize);
        tvCountryCode.setTextSize(TypedValue.COMPLEX_UNIT_PX, boldTextSize);

        noError(etFullName);
        noError(etEnterPhone);
        noError(tvCountryCode);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_country_code:
                startActivityForResult(new Intent(getActivity(), CountryCodeActivity.class), REQUEST_COUNTRY_CODE);
                break;

            case R.id.tv_next_button:
                if (LoginActivity.mPermissionCheck) {
                    goToSecondLoginScreen();
                } else {
                    ((LoginActivity) getActivity()).snackBarLocation();
                }
        }
    }


    void goToSecondLoginScreen() {

        final String name = etFullName.getText().toString();
        final String phoneNumber = etEnterPhone.getText().toString();

        if (name.equals("")) {
//            noError(etEnterPhone);
            showError(etFullName);
            etFullName.requestFocus();
        } else if (name.length() < 3) {
            etFullName.setError("Min Limit 3");
            showError(etFullName);
            etEnterPhone.requestFocus();
        } else if (name.length() > 30) {

            etFullName.setError("Max Limit 30");
            showError(etFullName);
            etEnterPhone.requestFocus();
        } else if (phoneNumber.length() < 5) {
            noError(etFullName);
            showError(etEnterPhone);
            etEnterPhone.requestFocus();
        } else if (phoneNumber.equals("")) {
            noError(etFullName);
            showError(etEnterPhone);
            etEnterPhone.requestFocus();
        } else if (phoneNumber.startsWith("0")) {
            noError(etFullName);
            showError(etEnterPhone);
            etEnterPhone.requestFocus();
        } else {
            noError(etFullName);
            noError(etEnterPhone);
            if ((new ConnectionDetector(getActivity()).isConnectingToInternet())) {
                LoadingDialog.getLoader().showLoader(getActivity());
                RestAdapter mAdpater = new RestAdapter.Builder().setEndpoint(ServerAPIs.BASE_URL).build();
                RetroInterface mRetroInterface = mAdpater.create(RetroInterface.class);
                mRetroInterface.register_number(name, tvCountryCode.getText().toString(),
                        phoneNumber, new Callback<RetroHelper.RegisterNumber>() {
                            @Override
                            public void success(RetroHelper.RegisterNumber registerNumber, Response response) {

                                LoadingDialog.getLoader().dismissLoader();
                                if (registerNumber.response != null) {

                                    ServerAPIs.showSuccessSnackbar(rlRegistrationFormContainer, getString(R.string.otp_sent), getResources().getColor(R.color.green));
                                    Bundle bundle = new Bundle();
                                    bundle.putString("Name", name);
                                    bundle.putString("Phone", phoneNumber);
                                    bundle.putString("CountryCode", tvCountryCode.getText().toString());
                                    LoginFragment_2 loginFragment_2 = new LoginFragment_2();
                                    loginFragment_2.setArguments(bundle);

                                    SharedPreferences.Editor editor = getActivity().getSharedPreferences(getActivity().getPackageName(),
                                            Context.MODE_PRIVATE).edit();
                                    editor.putString(Config.MYCOUNTRYCODE, tvCountryCode.getText().toString());
                                    editor.apply();

                                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                                    fragmentTransaction.replace(R.id.fl_login_fragment_container, loginFragment_2, "LoginFragment_2");
                                    fragmentTransaction.addToBackStack(null);
                                    fragmentTransaction.commit();

                                } else {

                                    ServerAPIs.showServerErrorSnackbar(rlRegistrationFormContainer, registerNumber.error);

                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {

                                LoadingDialog.getLoader().dismissLoader();
                                ServerAPIs.showServerErrorSnackbar(rlRegistrationFormContainer, error.getMessage());

                            }
                        });
            } else {
                ServerAPIs.noInternetConnection(rlRegistrationFormContainer);
            }


        }

    }


    private void showError(View v) {
        Vibrator vibe = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
        rlRegistrationFormContainer.startAnimation(shake);
        vibe.vibrate(200);
        ((GradientDrawable) v.getBackground()).setColor(getResources().getColor(R.color.red));
    }


    private void noError(View v) {
        ((GradientDrawable) v.getBackground()).setColor(Color.TRANSPARENT);
        v.setBackgroundResource(R.drawable.edit_text_round_corner);
    }

    void getCountryCode() {
        CountryCodeActivity countryCodeActivity = new CountryCodeActivity();


//        String locale = Locale.getDefault().getDisplayCountry();
        TelephonyManager tm = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        String countryCode = tm.getSimCountryIso();
        Locale locale = new Locale("", countryCode);


        if (!locale.getDisplayCountry().equals("")) {

            String[] countryName = countryCodeActivity.CountryName;
            for (int i = 0; i < countryName.length; i++) {
                if (countryName[i].equals(locale.getDisplayCountry())) {
                    defaultCountryCode = countryCodeActivity.CountryCode[i];
                    tvCountryCode.setText("+" + defaultCountryCode);
                    break;
                }
            }
        } else {
            tvCountryCode.setText(defaultCountryCode);
        }
    }

//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putString("COUNTRYCODE", defaultCountryCode);
//    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvCountryCode.setText("+" + defaultCountryCode);
        Log.d("login", "onViewCreated");
    }
}
