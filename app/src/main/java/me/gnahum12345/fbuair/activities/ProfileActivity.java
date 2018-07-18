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
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.models.User;

public class ProfileActivity extends AppCompatActivity{

    ImageView ivProfileImage;
    EditText etName;
    EditText etOrganization;
    EditText etPhoneNumber;
    EditText etEmail;
    EditText etFacebookURL;
    Context context;
    Button btnCheck;
    ImageButton btnProfileImage;

    SharedPreferences sharedpreferences;
    String MyPREFERENCES = "MyPrefs";

    Dialog dialog;
    final int REQUEST_IMAGE_SELECT = 1;
    final int REQUEST_IMAGE_CAPTURE = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        context = this;

        ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        etName = (EditText) findViewById(R.id.etName);
        etOrganization = (EditText) findViewById(R.id.etOrganization);
        etPhoneNumber = (EditText) findViewById(R.id.etPhone);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etFacebookURL = (EditText) findViewById(R.id.etFacebookURL);
        btnCheck = (Button) findViewById(R.id.btnCheck);
        btnProfileImage = (ImageButton) findViewById(R.id.btnProfileIImage);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = etName.getText().toString();
                final String organization = etOrganization.getText().toString();
                final String phoneNumber = etPhoneNumber.getText().toString();
                final String email = etEmail.getText().toString();
                final String facebookURL = etFacebookURL.getText().toString();
                try {
                    createProfile(name, organization, phoneNumber, email, facebookURL);
                    addContact(name, organization, phoneNumber, email, facebookURL);
                    Toast.makeText(ProfileActivity.this, "Profile made!!", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        btnProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), 3);
                showDialog();
            }
        });

        String current_user = sharedpreferences.getString("current_user", null);

        if(current_user != null) {
            Toast.makeText(ProfileActivity.this, "Profile already made!", Toast.LENGTH_LONG).show();
            Log.d("MadeUser", current_user);
            Intent intent = new Intent(ProfileActivity.this, DiscoverActivity.class);
            startActivity(intent);
        }

    }

    private void createProfile(String name, String organization, String phoneNumber, String email, String facebookURL) throws JSONException {
        User user = new User();
        user.setName(name);
        user.setOrganization(organization);
        user.setPhoneNumber(phoneNumber);
        user.setEmail(email);
        user.setFacebookURL(facebookURL);
        saveUserTwo(user);
    }
    
    private void addContact(String name, String organization, String phoneNumber, String email, String facebookURL){
        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
        // Sets the MIME type to match the Contacts Provider
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
        // insert user's info into intent
        intent.putExtra(ContactsContract.Intents.Insert.NAME, name)
                .putExtra(ContactsContract.Intents.Insert.COMPANY, organization)
                .putExtra(ContactsContract.Intents.Insert.EMAIL, email)
                .putExtra(ContactsContract.Intents.Insert.PHONE, phoneNumber)
                .putExtra(ContactsContract.Intents.Insert.NOTES, facebookURL)

                // insert email and phone types
                .putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                .putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_HOME);

        // send the intent
        startActivity(intent);
    }


    private void saveUserTwo(User user) throws JSONException {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("current_user", user.toJson(user).toString());
        editor.commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap;
        try {
            // if user captured image
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
                bitmap = (Bitmap) data.getExtras().get("data");
                // set image icon to newly selected image
                ivProfileImage.setImageBitmap(bitmap);

                btnProfileImage.setImageBitmap(bitmap);

            } else if (requestCode == REQUEST_IMAGE_SELECT && resultCode == Activity.RESULT_OK) {
                InputStream stream = this.getContentResolver().openInputStream(
                        data.getData());
                bitmap = BitmapFactory.decodeStream(stream);
                // set image icon to newly selected image
                ivProfileImage.setImageBitmap(bitmap);

                btnProfileImage.setImageBitmap(bitmap);


                stream.close();
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
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
                }
                // when "capture picture" option is pressed, take picture
                else {
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
