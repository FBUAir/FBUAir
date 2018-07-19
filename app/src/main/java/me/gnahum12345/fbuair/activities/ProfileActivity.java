package me.gnahum12345.fbuair.activities;

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
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.models.User;

public class ProfileActivity extends AppCompatActivity {
    // views
    EditText etName;
    EditText etOrganization;
    EditText etPhoneNumber;
    EditText etEmail;
    EditText etFacebookURL;
    Button btEditProfile;
    Button btSubmit;
    ImageButton btnProfileImage;
    Bitmap profileImageBitmap;

    TextView tvNameError;
    TextView tvPhoneError;
    TextView tvEmailError;
    TextView tvFacebookError;

    // name of preferences file
    String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedpreferences;

    // current user
    User user;

    Dialog dialog;
    final int REQUEST_IMAGE_SELECT = 1;
    final int REQUEST_IMAGE_CAPTURE = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // get shared preferences from filename
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        // get references to views
        etName = findViewById(R.id.etName);
        etOrganization = findViewById(R.id.etOrganization);
        etPhoneNumber = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etFacebookURL = findViewById(R.id.etFacebookURL);
        btSubmit = findViewById(R.id.btSubmit);
        btnProfileImage = findViewById(R.id.btnProfileImage);
        btEditProfile = findViewById(R.id.btEditProfile);

        etName = findViewById(R.id.etName);
        etOrganization = findViewById(R.id.etOrganization);
        etPhoneNumber = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etFacebookURL = findViewById(R.id.etFacebookURL);

        tvNameError = findViewById(R.id.tvNameError);
        tvEmailError = findViewById(R.id.tvEmailError);
        tvPhoneError = findViewById(R.id.tvPhoneError);
        tvFacebookError = findViewById(R.id.tvFacebookError);

        // clear placeholder text in errors
        clearErrors();

        // set user's info in views
        try {
            setUserInfo();
        } catch (JSONException e) {
            Toast.makeText(this, "json exception", Toast.LENGTH_LONG).show();
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

        btnProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
    }

    // saves user profile to be edit text fields if valid
    void saveProfile() {
        final String name = etName.getText().toString();
        final String organization = etOrganization.getText().toString();
        final String phoneNumber = etPhoneNumber.getText().toString();
        final String email = etEmail.getText().toString();
        final String facebookURL = etFacebookURL.getText().toString();
        if (isValidProfile(name, phoneNumber, email, facebookURL)) {
            setEditable(false);
            user.setName(name);
            user.setPhoneNumber(phoneNumber);
            user.setEmail(email);
            user.setOrganization(organization);
            user.setFacebookURL(facebookURL);
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
        }
        else {
            // go to sign up activity if no current user
            Intent intent = new Intent (this, SignUpActivity.class);
            startActivity(intent);
            finish();
        }

    }

    // makes edittexts editable and shows submit changes button
    void setEditable(boolean flag) {
        if (flag) {
            etName.setEnabled(true);
            etOrganization.setEnabled(true);
            etPhoneNumber.setEnabled(true);
            etEmail.setEnabled(true);
            etFacebookURL.setEnabled(true);
            // replace edit profile button with submit changes option
            btEditProfile.setVisibility(View.GONE);
            btSubmit.setVisibility(View.VISIBLE);
        } else {
            etName.setEnabled(false);
            etOrganization.setEnabled(false);
            etPhoneNumber.setEnabled(false);
            etEmail.setEnabled(false);
            etFacebookURL.setEnabled(false);
            // replace submit changes button with edit profile button
            btEditProfile.setVisibility(View.VISIBLE);
            btSubmit.setVisibility(View.GONE);
        }
    }

    // checks if profile is valid before submitting. if not, shows appropriate error messages
    public boolean isValidProfile(String name, String phone, String email, String facebookUrl){
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
        if (!facebookUrl.isEmpty() && !isValidFacebookUrl(facebookUrl)) {
            tvFacebookError.setText(getResources().getString(R.string.bad_fb_url_error));
            valid = false;
        }
        return valid;
    }

    // clear all error messages
    void clearErrors() {
        tvNameError.setText("");
        tvPhoneError.setText("");
        tvEmailError.setText("");
        tvFacebookError.setText("");
    }

    // validity checkers
    public static boolean isValidEmail(CharSequence email) {
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    public static boolean isValidPhoneNumber(String number) {
        return android.util.Patterns.PHONE.matcher(number).matches();
    }

    public static boolean isValidFacebookUrl(String facebookUrlString) {
        return (Patterns.WEB_URL.matcher(facebookUrlString).matches() && facebookUrlString.toLowerCase().contains("facebook"));
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
                InputStream stream = this.getContentResolver().openInputStream(
                        data.getData());
                bitmap = BitmapFactory.decodeStream(stream);
                // set image icon to newly selected image
                profileImageBitmap = bitmap;
                btnProfileImage.setImageBitmap(bitmap);


                stream.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // show dialog for capturing or selecting photo for new profile image
    public void showDialog() {
        CharSequence options[] = new CharSequence[] {"Select from pictures", "Capture picture"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }
}
