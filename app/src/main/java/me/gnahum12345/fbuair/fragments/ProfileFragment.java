package me.gnahum12345.fbuair.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
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
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.activities.SignUpActivity;
import me.gnahum12345.fbuair.databinding.FragmentProfileBinding;
import me.gnahum12345.fbuair.managers.UserManager;
import me.gnahum12345.fbuair.models.SocialMedia;
import me.gnahum12345.fbuair.models.User;

import static me.gnahum12345.fbuair.utils.Utils.CURRENT_USER_KEY;
import static me.gnahum12345.fbuair.utils.Utils.PREFERENCES_FILE_NAME_KEY;

public class ProfileFragment extends Fragment {
    // shared preferences
    SharedPreferences sharedpreferences;

    FragmentProfileBinding bind;

    // current user info
    User user;
    String name;
    String email;
    String organization;
    String phone;
    Bitmap profileImageBitmap;
    Integer rating;

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
        bind = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        return bind.getRoot();
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

        // clear placeholder text in errors
        clearErrors();

        // set user's info in views
        try {
            setUserInfo();
        } catch (JSONException e) {
            Toast.makeText(activity, "json exception", Toast.LENGTH_LONG).show();
        }

        // add formatter to phone number field
        bind.etPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        // CLICK LISTENERS
        // make edittext views editable when user clicks edit profile
        bind.btEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEditable(true);
            }
        });




        bind.btSubmit.setOnClickListener(view1 -> saveProfile());

        bind.btDeleteProfile.setOnClickListener(view12 -> {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(CURRENT_USER_KEY, null);
            editor.commit();
            startActivity(new Intent(activity, SignUpActivity.class));
        });
    }

    // gets current user and sets text views to display current user info
    private void setUserInfo() throws JSONException {
        // get json user from preferences and convert to user java object
//        String userJsonString = sharedpreferences.getString("current_user", null);
        user = UserManager.getInstance().getCurrentUser();
        if (user != null) {
            // set views to display info
            bind.etName.setText(user.getName());
            bind.etPhone.setText(user.getPhoneNumber());
            bind.etEmail.setText(user.getEmail());
            bind.etOrganization.setText(user.getOrganization());
            bind.btnProfileImage.setImageBitmap(user.getProfileImage());

            float numConnection = user.getNumConnections().floatValue();
            bind.etNumConnections.setText("Number of Connections ("+user.getNumConnections()+")");
            bind.rbConnection.setRating(numConnection= numConnection > 0 ? numConnection / 5.0f : numConnection);


            String socialMedias = "SOCIAL MEDIAS\n";
            for (SocialMedia socialMedia : user.getSocialMedias()) {
                socialMedias = socialMedias + socialMedia.getName() + " - Username: " +
                        socialMedia.getUsername() + ", Url: " + socialMedia.getProfileUrl() + "\n";
            }
            bind.tvSocialMedias.setText(socialMedias);
        } else {
            // go to sign up activity if no current user
            Intent intent = new Intent(activity, SignUpActivity.class);
            startActivity(intent);
            activity.finish();
        }
    }
    // saves user profile to be edit text fields if valid
    private void saveProfile() {
        name = bind.etName.getText().toString();
        organization = bind.etOrganization.getText().toString();
        phone = bind.etPhone.getText().toString();
        email = bind.etEmail.getText().toString();
        rating = bind.rbConnection.getNumStars();

        // this will automatically return true...
        if (isValidProfile()) {
            setEditable(false);
            user.setName(name);
            user.setPhoneNumber(phone);
            user.setEmail(email);
            user.setOrganization(organization);
            user.setNumConnections(rating);
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

    private void clearErrors() {
        bind.tvNameError.setText("");
        bind.tvPhoneError.setText("");
        bind.tvEmailError.setText("");
    }

    // go to edit profile mode
    private void setEditable(final boolean flag) {
        // make edit texts editable/not editable
        bind.etName.setEnabled(flag);
        bind.etOrganization.setEnabled(flag);
        bind.etPhone.setEnabled(flag);
        bind.etEmail.setEnabled(flag);
        // show appropriate button
        bind.btEditProfile.setVisibility(flag ? View.GONE : View.VISIBLE);
        bind.btSubmit.setVisibility(flag ? View.VISIBLE : View.GONE);
        // if in edit profile mode, can click profile image to change it
        bind.btnProfileImage.setOnClickListener(new View.OnClickListener() {
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
                bind.btnProfileImage.setImageBitmap(bitmap);
                profileImageBitmap = bitmap;

            } else if (requestCode == REQUEST_IMAGE_SELECT && resultCode == Activity.RESULT_OK) {
                InputStream stream = activity.getContentResolver().openInputStream(
                        Objects.requireNonNull(data.getData()));
                bitmap = BitmapFactory.decodeStream(stream);
                // set image icon to newly selected image
                profileImageBitmap = bitmap;
                bind.btnProfileImage.setImageBitmap(bitmap);
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
