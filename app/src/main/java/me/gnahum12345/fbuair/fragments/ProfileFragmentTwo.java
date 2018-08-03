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
import android.support.v7.widget.LinearLayoutManager;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONException;
import org.parceler.Parcels;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.activities.MainActivity;
import me.gnahum12345.fbuair.activities.SignUpActivity;
import me.gnahum12345.fbuair.adapters.ProfileAdapter;
import me.gnahum12345.fbuair.adapters.SocialMediaAdapter;
import me.gnahum12345.fbuair.databinding.FragmentProfileTwoBinding;
import me.gnahum12345.fbuair.managers.UserManager;
import me.gnahum12345.fbuair.models.SocialMedia;
import me.gnahum12345.fbuair.models.User;
import me.gnahum12345.fbuair.utils.SocialMediaUtils;

import static me.gnahum12345.fbuair.utils.Utils.CURRENT_USER_KEY;
import static me.gnahum12345.fbuair.utils.Utils.PREFERENCES_FILE_NAME_KEY;

public class ProfileFragmentTwo extends Fragment {

    FragmentProfileTwoBinding bind;
    final static String ARG_UID = "uid";

    // reference to main activity
    Activity activity;
    ArrayList<SocialMedia> socialMedias;
    ProfileAdapter profileAdapter;

    public ProfileFragmentTwo() {
        // Required empty public constructor
    }

    public static ProfileFragmentTwo newInstance(String uid) {
        ProfileFragmentTwo fragment = new ProfileFragmentTwo();
        Bundle args = new Bundle();
        args.putString(ARG_UID, uid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        String uid;
        User user;
        boolean isCurrentUserProfile;
        // if no arguments were passed in, assume current user profile
        if (getArguments() != null) {
            uid = getArguments().getString(ARG_UID);
            user = UserManager.getInstance().getUser(getArguments().getString(ARG_UID));
            isCurrentUserProfile = (user.equals(UserManager.getInstance().getCurrentUser()));
        }
        else {
            user = UserManager.getInstance().getCurrentUser();
            uid = user.getId();
            isCurrentUserProfile = true;
        }
        socialMedias = user.getSocialMedias();
        profileAdapter = new ProfileAdapter(getContext(), uid, isCurrentUserProfile);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        bind = DataBindingUtil.inflate(inflater, R.layout.fragment_profile_two, container, false);
        return bind.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // attach adapter and layout manager
        bind.rvRecyclerView.setAdapter(profileAdapter);
        bind.rvRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


    }
}
