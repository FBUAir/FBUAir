package me.gnahum12345.fbuair.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.activities.SignUpActivity;
import me.gnahum12345.fbuair.databinding.FragmentSignUpContactBinding;
import me.gnahum12345.fbuair.interfaces.OnSignUpScreenChangeListener;
import me.gnahum12345.fbuair.models.User;

import static me.gnahum12345.fbuair.utils.Utils.getCircularBitmap;
import static me.gnahum12345.fbuair.utils.Utils.isValidEmail;
import static me.gnahum12345.fbuair.utils.Utils.isValidPhoneNumber;

public class SignUpContactFragment extends Fragment {

    Bitmap profileImage;
    Dialog dialog;

    final int REQUEST_IMAGE_SELECT = 1;
    final int REQUEST_IMAGE_CAPTURE = 2;
    final static int MY_PERMISSIONS_REQUEST_CONTACTS = 4;

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

        // show toolbar
        if (activity != null) {
            Objects.requireNonNull(activity.getSupportActionBar()).show();
        }

        // clear placeholder text in errors
        clearErrors();

        //working with getting the phones number programmatically
        final String phone;
        final String phoneID;
        bind.etPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        if (getPhoneNum() != null) {
            phone = getPhoneNum();
            bind.etPhone.setText(phone);
        } else {
            phoneID = getPhoneID();
            Toast.makeText(getContext(), "Did not have a phone num", Toast.LENGTH_SHORT).show();
            Toast.makeText(getContext(), "Phone id is" + phoneID, Toast.LENGTH_SHORT).show();
            phone = bind.etPhone.getText().toString();
        }

        // go to next sign up screen when user clicks on button
        bind.btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get values user submitted
                final String name = bind.etName.getText().toString();
                final String organization = bind.etOrganization.getText().toString();
                final String email = bind.etEmail.getText().toString();
                // go to next sign up page if contact info is valid. shows error messages if needed
                if (isValidContact(name, phone, email)) {
                    User user = activity.user;
                    user.setName(name);
                    user.setOrganization(organization);
                    user.setPhoneNumber(phone);
                    user.setEmail(email);
                    user.setProfileImage(profileImage);
                    activity.user = user;
                    onSignUpScreenChangeListener.launchSignUpSocialMedia();
                }
            }
        });

        bind.btnProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    // checks if profile is valid before submitting. if not, shows error messages
    public boolean isValidContact(String name, String phone, String email) {
        // clear previous errors
        clearErrors();
        // check fields and show appropriate error messages
        boolean valid = true;
        if (name.isEmpty()) {
            bind.tvNameError.setText(getResources().getString(R.string.no_name_error));
            valid = false;
        }
        if (!email.isEmpty() && !isValidEmail(email)) {
            bind.tvEmailError.setText(getResources().getString(R.string.bad_email_error));
            valid = false;
        }
        if (!phone.isEmpty() && !isValidPhoneNumber(phone)) {
            bind.tvPhoneError.setText(getResources().getString(R.string.bad_phone_error));
            valid = false;
        }
        return valid;
    }

    void clearErrors() {
        bind.tvNameError.setText("");
        bind.tvPhoneError.setText("");
        bind.tvEmailError.setText("");
    }

    //following are profile image methods
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap;
        try {
            // if user captured image
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
                bitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                // set image icon to newly selected image
                bind.btnProfileImage.setImageBitmap(bitmap);
                profileImage = getCircularBitmap(bitmap);

            } else if (requestCode == REQUEST_IMAGE_SELECT && resultCode == Activity.RESULT_OK) {
                InputStream stream = activity.getContentResolver().openInputStream(
                        Objects.requireNonNull(data.getData()));
                bitmap = BitmapFactory.decodeStream(stream);
                // set image icon to newly selected image
                profileImage = getCircularBitmap(bitmap);
                bind.btnProfileImage.setImageBitmap(bitmap);
                if (stream != null) {
                    stream.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showDialog() {
        CharSequence options[] = new CharSequence[]{"Select from pictures", "Capture picture"};
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Edit profile picture");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int option) {
                // when "select from pictures" button is pressed, select picture
                if (option == 0) {
                    launchImageSelect();
                } else {
                    // when "capture picture" option is pressed, take picture
                    launchImageCapture();
                }

            }
        });

        // dismiss old dialogs
        if (dialog != null) {
            dialog.dismiss();
        }

        // show new dialog
        dialog = builder.show();
    }

    public void launchImageSelect() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_IMAGE_SELECT);
    }

    public void launchImageCapture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
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

    //programmatically getting the phone ID IGNORE REDDDD
    public String getPhoneID() {
        TelephonyManager tMgr = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]
                            {Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_REQUEST_CONTACTS);
            String mPhoneID = tMgr.getDeviceId();
            return mPhoneID;
        }
        String mPhoneID = tMgr.getDeviceId();
        return mPhoneID;
    }
}
