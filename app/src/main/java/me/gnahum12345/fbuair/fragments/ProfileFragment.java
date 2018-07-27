package me.gnahum12345.fbuair.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.activities.SignUpActivity;
import me.gnahum12345.fbuair.models.SocialMedia;
import me.gnahum12345.fbuair.models.User;

import static me.gnahum12345.fbuair.utils.Utils.CURRENT_USER_KEY;
import static me.gnahum12345.fbuair.utils.Utils.PREFERENCES_FILE_NAME_KEY;

public class ProfileFragment extends Fragment {
    // views
    EditText etName;
    EditText etOrganization;
    EditText etPhoneNumber;
    EditText etEmail;
    Button btEditProfile;
    Button btSubmit;
    Button btDeleteProfile;
    ImageButton btnProfileImage;

    TextView tvNameError;
    TextView tvPhoneError;
    TextView tvEmailError;
    TextView tvFacebookError;
    TextView tvInstagramError;
    TextView tvLinkedInError;

    TextView tvSocialMedias;

    // shared preferences
    SharedPreferences sharedpreferences;

    // current user info
    User user;
    String name;
    String email;
    String organization;
    String phone;
    Bitmap profileImageBitmap;

    // reference to main activity
    Activity activity;

    Dialog dialog;
    final int REQUEST_IMAGE_SELECT = 1;
    final int REQUEST_IMAGE_CAPTURE = 2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // get reference to main activity
        activity = getActivity();

        // get shared preferences
        if (activity != null) {
            sharedpreferences = activity.getSharedPreferences(PREFERENCES_FILE_NAME_KEY, Context.MODE_PRIVATE);
        }

        // get references to views
        etName = view.findViewById(R.id.etName);
        etOrganization = view.findViewById(R.id.etOrganization);
        etPhoneNumber = view.findViewById(R.id.etPhone);
        etEmail = view.findViewById(R.id.etEmail);
        btnProfileImage = view.findViewById(R.id.btnProfileImage);
        tvNameError = view.findViewById(R.id.tvNameError);
        tvEmailError = view.findViewById(R.id.tvEmailError);
        tvPhoneError = view.findViewById(R.id.tvPhoneError);
        tvFacebookError = view.findViewById(R.id.tvFacebookError);
        tvInstagramError = view.findViewById(R.id.tvInstagramError);
        tvLinkedInError = view.findViewById(R.id.tvLinkedInError);
        btEditProfile = view.findViewById(R.id.btEditProfile);
        btSubmit = view.findViewById(R.id.btSubmit);
        btDeleteProfile = view.findViewById(R.id.btDeleteProfile);
        tvSocialMedias = view.findViewById(R.id.tvSocialMedias);

        // clear placeholder text in errors
        clearErrors();

        // set user's info in views
        try {
            setUserInfo();
        } catch (JSONException e) {
            Toast.makeText(activity, "json exception", Toast.LENGTH_LONG).show();
        }

        // add formatter to phone number field
        etPhoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        // CLICK LISTENERS
        // make edittext views editable when user clicks edit profile
        btEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEditable(true);
            }
        });

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProfile();
            }
        });

        btDeleteProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(CURRENT_USER_KEY, null);
                editor.commit();
                startActivity(new Intent(activity, SignUpActivity.class));
            }
        });
    }

    // gets current user and sets text views to display current user info
    void setUserInfo() throws JSONException {
        // get json user from preferences and convert to user java object
        String userJsonString = sharedpreferences.getString("current_user", null);
        if (userJsonString != null) {
            JSONObject userJson = new JSONObject(userJsonString);
            user = User.fromJson(userJson);
            // set views to display info
            etName.setText(user.getName());
            etPhoneNumber.setText(user.getPhoneNumber());
            etEmail.setText(user.getEmail());
            etOrganization.setText(user.getOrganization());
            btnProfileImage.setImageBitmap(user.getProfileImage());
            String socialMedias = "SOCIALMEDIAS\n";
            for (SocialMedia socialMedia : user.getSocialMedias()) {
                socialMedias = socialMedias +
                        "Name: " + socialMedia.getName() +
                        " Username: " + socialMedia.getUsername() +
                        " Url: " + socialMedia.getProfileUrl() + "\n";
            }
            tvSocialMedias.setText(socialMedias);
        }
        else {
            // go to sign up activity if no current user
            Intent intent = new Intent(activity, SignUpActivity.class);
            startActivity(intent);
            activity.finish();
        }
    }
    // saves user profile to be edit text fields if valid
    void saveProfile() {
        name = etName.getText().toString();
        organization = etOrganization.getText().toString();
        phone = etPhoneNumber.getText().toString();
        email = etEmail.getText().toString();

        if (isValidProfile()) {
            setEditable(false);
            user.setName(name);
            user.setPhoneNumber(phone);
            user.setEmail(email);
            user.setOrganization(organization);
            if (profileImageBitmap != null)
                user.setProfileImage(profileImageBitmap);;
            // save changes to shared preferences
            SharedPreferences.Editor editor = sharedpreferences.edit();
            try {
                editor.putString("current_user", User.toJson(user).toString());
                editor.commit();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // checks if profile is valid before submitting. if not, shows appropriate error messages
    public boolean isValidProfile() {
        // clear previous errors
        clearErrors();
        return true;
    }

    void clearErrors() {
        tvNameError.setText("");
        tvPhoneError.setText("");
        tvEmailError.setText("");
        tvFacebookError.setText("");
        tvInstagramError.setText("");
        tvLinkedInError.setText("");
    }

    // go to edit profile mode
    void setEditable(final boolean flag) {
        // make edit texts editable/not editable
        etName.setEnabled(flag);
        etOrganization.setEnabled(flag);
        etPhoneNumber.setEnabled(flag);
        etEmail.setEnabled(flag);
        // show appropriate button
        btEditProfile.setVisibility(flag ? View.GONE : View.VISIBLE);
        btSubmit.setVisibility(flag ? View.VISIBLE : View.GONE);
        // if in edit profile mode, can click profile image to change it
        btnProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag) showDialog();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap;
        try {
            // if user captured image
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
                bitmap = (Bitmap) data.getExtras().get("data");
                // set image icon to newly selected image
                btnProfileImage.setImageBitmap(bitmap);
                profileImageBitmap = bitmap;

            } else if (requestCode == REQUEST_IMAGE_SELECT && resultCode == Activity.RESULT_OK) {
                InputStream stream = activity.getContentResolver().openInputStream(
                        Objects.requireNonNull(data.getData()));
                bitmap = BitmapFactory.decodeStream(stream);
                // set image icon to newly selected image
                profileImageBitmap = bitmap;
                btnProfileImage.setImageBitmap(bitmap);
                if (stream != null) {
                    stream.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // show dialog for capturing or selecting photo for new profile image
    public void showDialog() {
        CharSequence options[] = new CharSequence[] {"Select from pictures", "Capture picture"};

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
