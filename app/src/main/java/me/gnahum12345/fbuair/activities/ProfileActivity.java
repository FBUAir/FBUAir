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
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.models.User;

public class ProfileActivity extends AppCompatActivity {
    final int REQUEST_IMAGE_SELECT = 1;
    final int REQUEST_IMAGE_CAPTURE = 2;
    // views
    EditText etName;
    EditText etOrganization;
    EditText etPhoneNumber;
    EditText etEmail;
    EditText etFacebookUrl;
    EditText etLinkedInUrl;
    EditText etInstagramUrl;
    Button btEditProfile;
    Button btSubmit;
    ImageButton btnProfileImage;
    TextView tvNameError;
    TextView tvPhoneError;
    TextView tvEmailError;
    TextView tvFacebookError;
    TextView tvInstagramError;
    TextView tvLinkedInError;
    // name of preferences file
    String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedpreferences;
    // current user info
    User user;
    String name;
    String email;
    String organization;
    String phone;
    Bitmap profileImageBitmap;
    String facebookUrl;
    String linkedInUrl;
    String instagramUrl;
    Dialog dialog;

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

    public static boolean isValidInstagramUrl(String instagramUrlString) {
        return (Patterns.WEB_URL.matcher(instagramUrlString).matches() && instagramUrlString.toLowerCase().contains("instagram"));
    }

    public static boolean isValidLinkedInUrl(String linkedInUrlString) {
        return (Patterns.WEB_URL.matcher(linkedInUrlString).matches() && linkedInUrlString.toLowerCase().contains("linkedin"));
    }

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
        etFacebookUrl = findViewById(R.id.etFacebookUrl);
        etInstagramUrl = findViewById(R.id.etInstagramUrl);
        etLinkedInUrl = findViewById(R.id.etLinkedInUrl);
        btnProfileImage = findViewById(R.id.btnProfileImage);
        tvNameError = findViewById(R.id.tvNameError);
        tvEmailError = findViewById(R.id.tvEmailError);
        tvPhoneError = findViewById(R.id.tvPhoneError);
        tvFacebookError = findViewById(R.id.tvFacebookError);
        tvInstagramError = findViewById(R.id.tvInstagramError);
        tvLinkedInError = findViewById(R.id.tvLinkedInError);
        btEditProfile = findViewById(R.id.btEditProfile);
        btSubmit = findViewById(R.id.btSubmit);

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
            etFacebookUrl.setText(user.getFacebookURL());
            etInstagramUrl.setText(user.getInstagramURL());
            etLinkedInUrl.setText(user.getLinkedInURL());
            btnProfileImage.setImageBitmap(user.getProfileImage());
        } else {
            // go to sign up activity if no current user
            Intent intent = new Intent(this, SignUpContact.class);
            startActivity(intent);
            finish();
        }

    }

    // saves user profile to be edit text fields if valid
    void saveProfile() {
        name = etName.getText().toString();
        organization = etOrganization.getText().toString();
        phone = etPhoneNumber.getText().toString();
        email = etEmail.getText().toString();
        facebookUrl = etFacebookUrl.getText().toString();
        instagramUrl = etInstagramUrl.getText().toString();
        linkedInUrl = etLinkedInUrl.getText().toString();

        if (isValidProfile()) {
            setEditable(false);
            user.setName(name);
            user.setPhoneNumber(phone);
            user.setEmail(email);
            user.setOrganization(organization);
            user.setFacebookURL(facebookUrl);
            if (profileImageBitmap != null)
                user.setProfileImage(profileImageBitmap);
            ;
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
        if (!linkedInUrl.isEmpty() && !isValidLinkedInUrl(linkedInUrl)) {
            tvLinkedInError.setText(getResources().getString(R.string.bad_linked_in_url_error));
            valid = false;
        }
        if (!instagramUrl.isEmpty() && !isValidInstagramUrl(instagramUrl)) {
            tvInstagramError.setText(getResources().getString(R.string.bad_instagram_url_error));
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
        etFacebookUrl.setEnabled(flag);
        etLinkedInUrl.setEnabled(flag);
        etInstagramUrl.setEnabled(flag);
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
        CharSequence options[] = new CharSequence[]{"Select from pictures", "Capture picture"};

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
