package me.gnahum12345.fbuair.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.activities.SignUpActivity;
import me.gnahum12345.fbuair.databinding.FragmentSignUpContactBinding;
import me.gnahum12345.fbuair.interfaces.OnSignUpScreenChangeListener;
import me.gnahum12345.fbuair.models.User;

import static me.gnahum12345.fbuair.models.User.NO_COLOR;
import static me.gnahum12345.fbuair.utils.ImageUtils.drawableToBitmap;
import static me.gnahum12345.fbuair.utils.Utils.isValidEmail;
import static me.gnahum12345.fbuair.utils.Utils.isValidPhoneNumber;

public class SignUpContactFragment extends Fragment {

    Bitmap profileImage;

    // reference to Sign Up Activity
    SignUpActivity activity;

    OnSignUpScreenChangeListener onSignUpScreenChangeListener;

    FragmentSignUpContactBinding bind;

    public SignUpContactFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        try {
            onSignUpScreenChangeListener = (OnSignUpScreenChangeListener) context;
        } catch (ClassCastException e) {
            Log.e("SignUpContactFragment",
                    "Sign Up Activity must implement onSignUpScreenChangeListener");
        }
        super.onAttach(context);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        bind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_sign_up_contact, container, false);
        return bind.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // get reference to activity
        activity = (SignUpActivity) getActivity();

        // show menu
        onSignUpScreenChangeListener.setMenuVisible(true);

        // go to next sign up screen when user clicks on button
        bind.btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get values user submitted
                final String name = bind.etName.getText().toString();
                final String organization = bind.etOrganization.getText().toString();
                int color = NO_COLOR;
                // change to remove profile image check if no more profile image
                if (profileImage == null && !name.isEmpty()) {
                    ColorGenerator generator = ColorGenerator.MATERIAL;
                    color = generator.getRandomColor();
                    TextDrawable drawable = TextDrawable.builder()
                            .buildRound(Character.toString(name.toCharArray()[0]).toUpperCase(),
                                    color);
                    profileImage = drawableToBitmap(drawable);
                }
                // go to next sign up page if contact info is valid. shows error messages if needed
                if (isValidContact(name)) {
                    User user = activity.user;
                    user.setName(name);
                    user.setOrganization(organization);
                    user.setProfileImage(profileImage);
                    user.setColor(color);
                    activity.user = user;
                    onSignUpScreenChangeListener.launchSignUpContactTwo();
                }
            }
        });
    }

    // checks if profile is valid before submitting. if not, shows error messages
    public boolean isValidContact(String name) {
        // hide previous errors
        hideErrors();
        // check fields and show appropriate error messages
        boolean valid = true;
        if (name.isEmpty()) {
            bind.tvNameError.setVisibility(View.VISIBLE);
            bind.tvNameError.setText(getResources().getString(R.string.no_name_error));
            valid = false;
        }
        return valid;
    }

    void hideErrors() {
        bind.tvNameError.setVisibility(View.GONE);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onSignUpScreenChangeListener = null;
    }
}
