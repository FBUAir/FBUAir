package me.gnahum12345.fbuair.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.activities.SignUpActivity;
import me.gnahum12345.fbuair.databinding.FragmentSignUpContactBinding;
import me.gnahum12345.fbuair.interfaces.OnSignUpScreenChangeListener;
import me.gnahum12345.fbuair.models.User;

import static me.gnahum12345.fbuair.models.User.NO_COLOR;
import static me.gnahum12345.fbuair.utils.ImageUtils.drawableToBitmap;
import static me.gnahum12345.fbuair.utils.ImageUtils.getCircularBitmap;

public class SignUpContactFragment extends Fragment {

    Bitmap selectedProfileImage;
    Dialog dialog;
    final int REQUEST_IMAGE_SELECT = 1;
    final int REQUEST_IMAGE_CAPTURE = 2;

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

        // focus on name field
        bind.etName.requestFocus();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(bind.etName, InputMethodManager.SHOW_IMPLICIT);

        // go to next sign up screen when user clicks on button
        bind.btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get values user submitted
                final String name = bind.etName.getText().toString();
                final String organization = bind.etOrganization.getText().toString();
                int color = NO_COLOR;
                Bitmap profileImage = selectedProfileImage;
                // go to next sign up page if contact info is valid. shows error messages if needed
                if (isValidContact(name)) {
                    if (profileImage == null) {
                        ColorGenerator generator = ColorGenerator.MATERIAL;
                        color = generator.getRandomColor();
                        TextDrawable drawable = TextDrawable.builder()
                                .buildRound(Character.toString(name.toCharArray()[0]).toUpperCase(),
                                        color);
                        profileImage = drawableToBitmap(drawable);
                    }
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

        bind.ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
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

    //following are profile image methods
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap;
        try {
            // if user captured image
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
                bitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                // set image icon to newly captured image
                selectedProfileImage = bitmap;
                bind.ivProfileImage.setImageBitmap(getCircularBitmap(bitmap));
            } else if (requestCode == REQUEST_IMAGE_SELECT && resultCode == Activity.RESULT_OK) {
                InputStream stream = activity.getContentResolver().openInputStream(
                        Objects.requireNonNull(data.getData()));
                bitmap = BitmapFactory.decodeStream(stream);
                // set image icon to newly selected image
                selectedProfileImage = bitmap;
                bind.ivProfileImage.setImageBitmap(getCircularBitmap(bitmap));
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

}
