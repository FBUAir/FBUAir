package me.gnahum12345.fbuair.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.activities.SignUpActivity;
import me.gnahum12345.fbuair.models.User;

import static me.gnahum12345.fbuair.utilities.Utility.isValidEmail;
import static me.gnahum12345.fbuair.utilities.Utility.isValidPhoneNumber;

public class SignUpContactFragment extends Fragment {
    // views
    EditText etName;
    EditText etOrganization;
    EditText etPhoneNumber;
    EditText etEmail;
    Button btNext;
    ImageButton btnProfileImage;
    TextView tvNameError;
    TextView tvPhoneError;
    TextView tvEmailError;

    Bitmap profileImage;
    Dialog dialog;

    final int REQUEST_IMAGE_SELECT = 1;
    final int REQUEST_IMAGE_CAPTURE = 2;

    // reference to Sign Up Activity
    SignUpActivity activity;

    public SignUpContactFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up_contact, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // get reference to activity
        activity = (SignUpActivity) getActivity();

        // show toolbar
        activity.getSupportActionBar().show();

        // get references to views
        etName = view.findViewById(R.id.etName);
        etOrganization = view.findViewById(R.id.etOrganization);
        etPhoneNumber = view.findViewById(R.id.etPhone);
        etEmail = view.findViewById(R.id.etEmail);
        btNext = view.findViewById(R.id.btNext);
        btnProfileImage = view.findViewById(R.id.btnProfileImage);

        tvNameError = view.findViewById(R.id.tvNameError);
        tvEmailError = view.findViewById(R.id.tvEmailError);
        tvPhoneError = view.findViewById(R.id.tvPhoneError);

        // clear placeholder text in errors
        clearErrors();

        // add formatter to phone number field
        etPhoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        // go to next sign up screen when user clicks on button
        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get values user submitted
                final String name = etName.getText().toString();
                final String organization = etOrganization.getText().toString();
                final String phone = etPhoneNumber.getText().toString();
                final String email = etEmail.getText().toString();
                if (profileImage == null) {
                    profileImage = BitmapFactory.decodeResource(getResources(),
                            R.drawable.default_profile);
                }
                // go to next sign up page if contact info is valid
                if (isValidContact(name, phone, email)) {
                    activity.profileImage = profileImage;
                    User user = new User();
                    user.setName(name);
                    user.setOrganization(organization);
                    user.setPhoneNumber(phone);
                    user.setEmail(email);
                    user.setProfileImage(profileImage);
                    activity.launchSignUpSocialMedia(user);
                }
            }
        });

        btnProfileImage.setOnClickListener(new View.OnClickListener() {
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
        // check fields and set appropriate error messages
        boolean valid = true;
        if (name.isEmpty()) {
            tvNameError.setText(getResources().getString(R.string.no_name_error));
            valid = false;
        }
        if (!email.isEmpty() && !isValidEmail(email)) {
            tvEmailError.setText(getResources().getString(R.string.bad_email_error));
            valid = false;
        }
        if (!isValidPhoneNumber(phone)) {
            tvPhoneError.setText(getResources().getString(R.string.bad_phone_error));
            valid = false;
        }
        if (phone.isEmpty()) {
            tvPhoneError.setText(getResources().getString(R.string.no_phone_error));
            valid = false;
        }
        return valid;
    }

    void clearErrors() {
        tvNameError.setText("");
        tvPhoneError.setText("");
        tvEmailError.setText("");
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
                btnProfileImage.setImageBitmap(bitmap);
                profileImage = bitmap;

            } else if (requestCode == REQUEST_IMAGE_SELECT && resultCode == Activity.RESULT_OK) {
                InputStream stream = activity.getContentResolver().openInputStream(
                        Objects.requireNonNull(data.getData()));
                bitmap = BitmapFactory.decodeStream(stream);
                // set image icon to newly selected image
                profileImage = bitmap;
                btnProfileImage.setImageBitmap(bitmap);
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
}
