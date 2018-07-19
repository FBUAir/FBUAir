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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import me.gnahum12345.fbuair.R;

public class ProfileActivity extends AppCompatActivity {
    EditText etName;
    EditText etOrganization;
    EditText etPhoneNumber;
    EditText etEmail;
    EditText etFacebookURL;
    Button btEditProfile;
    Button btSubmit;
    ImageButton btnProfileImage;
    Bitmap profileImage;

    TextView tvNameError;
    TextView tvPhoneError;
    TextView tvEmailError;
    TextView tvFacebookError;

    SharedPreferences sharedpreferences;
    String MyPREFERENCES = "MyPrefs";

    Dialog dialog;
    final int REQUEST_IMAGE_SELECT = 1;
    final int REQUEST_IMAGE_CAPTURE = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        // clear placeholder text in errors
        clearErrors();

        // add formatter to phone number field
        etPhoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

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
                final String name = etName.getText().toString();
                final String organization = etOrganization.getText().toString();
                final String phoneNumber = etPhoneNumber.getText().toString();
                final String email = etEmail.getText().toString();
                final String facebookURL = etFacebookURL.getText().toString();
                final Bitmap ivProfileImage = profileImage;
                if (isValidProfile(name, phoneNumber, email, facebookURL)) {
                    setEditable(false);
                    // todo - submit changes
                }
            }
        });

        btnProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
    }

    // sets text views to display current user info
    void setUserInfo() {
        // todo
    }

    void setEditable(boolean flag) {
        if (flag) {
            etName.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
            etOrganization.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
            etPhoneNumber.setInputType(InputType.TYPE_CLASS_PHONE);
            etEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            etFacebookURL.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
            // replace edit profile button with submit changes option
            btEditProfile.setVisibility(View.GONE);
            btSubmit.setVisibility(View.VISIBLE);
        } else {
            etName.setInputType(InputType.TYPE_CLASS_TEXT);
            etOrganization.setInputType(InputType.TYPE_CLASS_TEXT);
            etPhoneNumber.setInputType(InputType.TYPE_CLASS_TEXT);
            etEmail.setInputType(InputType.TYPE_CLASS_TEXT);
            etFacebookURL.setInputType(InputType.TYPE_CLASS_TEXT);
            // replace submit changes button with edit profile button
            btEditProfile.setVisibility(View.VISIBLE);
            btSubmit.setVisibility(View.GONE);
        }
    }

    // checks if profile is valid before submitting. if not, sets invalid fields red
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
                profileImage = bitmap;

            } else if (requestCode == REQUEST_IMAGE_SELECT && resultCode == Activity.RESULT_OK) {
                InputStream stream = this.getContentResolver().openInputStream(
                        data.getData());
                bitmap = BitmapFactory.decodeStream(stream);
                // set image icon to newly selected image
                profileImage = bitmap;
                btnProfileImage.setImageBitmap(bitmap);


                stream.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
