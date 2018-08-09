package me.gnahum12345.fbuair.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.TelephonyManager;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.activities.SignUpActivity;
import me.gnahum12345.fbuair.databinding.FragmentSignUpContactTwoBinding;
import me.gnahum12345.fbuair.interfaces.OnSignUpScreenChangeListener;
import me.gnahum12345.fbuair.models.User;

import static me.gnahum12345.fbuair.utils.Utils.isValidEmail;
import static me.gnahum12345.fbuair.utils.Utils.isValidPhoneNumber;

public class SignUpContactFragmentTwo extends Fragment {

    final static int MY_PERMISSIONS_REQUEST_CONTACTS = 4;

    // reference to Sign Up Activity
    SignUpActivity activity;

    OnSignUpScreenChangeListener onSignUpScreenChangeListener;

    FragmentSignUpContactTwoBinding bind;

    public SignUpContactFragmentTwo() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        try {
            onSignUpScreenChangeListener = (OnSignUpScreenChangeListener) context;
        } catch (ClassCastException e) {
            Log.e("SignUpContactFragment2",
                    "Sign Up Activity must implement onSignUpScreenChangeListener");
        }
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        bind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_sign_up_contact_two, container, false);
        return bind.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // get reference to activity
        activity = (SignUpActivity) getActivity();

        // show menu
        onSignUpScreenChangeListener.setMenuVisible(true);

        bind.etPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        //working with getting the phones number programmatically
        String phone = getPhoneNum();
        if (phone != null) {
            bind.etPhone.setText(phone);
        }

        // go to next sign up screen when user clicks on button
        bind.btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get values user submitted
                final String email = bind.etEmail.getText().toString();
                String phone = bind.etPhone.getText().toString();

                // go to next sign up page if contact info is valid. shows error messages if needed
                if (isValidContact(phone, email)) {
                    User user = activity.user;
                    user.setPhoneNumber(phone);
                    user.setEmail(email);
                    activity.user = user;
                    onSignUpScreenChangeListener.launchSignUpSocialMedia();
                }
            }
        });
    }

    // checks if profile is valid before submitting. if not, shows error messages
    public boolean isValidContact(String phone, String email) {
        // hide previous errors
        hideErrors();
        // check fields and show appropriate error messages
        boolean valid = true;
        if (!isValidEmail(email)) {
            bind.tvEmailError.setVisibility(View.VISIBLE);
            bind.tvEmailError.setText(getResources().getString(R.string.bad_email_error));
            valid = false;
        }
        if (!isValidPhoneNumber(phone)) {
            bind.tvPhoneError.setVisibility(View.VISIBLE);
            bind.tvPhoneError.setText(getResources().getString(R.string.bad_phone_error));
            valid = false;
        }
        return valid;
    }

    void hideErrors() {
        bind.tvPhoneError.setVisibility(View.GONE);
        bind.tvEmailError.setVisibility(View.GONE);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onSignUpScreenChangeListener = null;
    }

    //programmatically getting the phone number IGNORE REDDDD
    public String getPhoneNum() {
        TelephonyManager tMgr = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]
                            {Manifest.permission.READ_SMS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_PHONE_NUMBERS, Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_REQUEST_CONTACTS);
            String mPhoneNum = tMgr.getLine1Number();
            return mPhoneNum;
        }
        String mPhoneNum = tMgr.getLine1Number();
        return mPhoneNum;
    }
}
