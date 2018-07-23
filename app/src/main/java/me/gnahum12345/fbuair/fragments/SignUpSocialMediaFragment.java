package me.gnahum12345.fbuair.fragments;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.parceler.Parcels;

import java.util.ArrayList;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.activities.SignUpActivity;
import me.gnahum12345.fbuair.databinding.FragmentSignUpSocialMediaBinding;
import me.gnahum12345.fbuair.models.User;
import me.gnahum12345.fbuair.utilities.Utility;

public class SignUpSocialMediaFragment extends Fragment {
    // reference to activity
    SignUpActivity activity;

    // data binding to skip find view by id
    FragmentSignUpSocialMediaBinding binding;

    public SignUpSocialMediaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_sign_up_social_media, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = (SignUpActivity) getActivity();
        // hide keyboard
        Utility.hideSoftKeyboard(activity);

        // get info from last sign up screen
        assert getArguments() != null;
        final User user = Parcels.unwrap(getArguments().getParcelable("user"));

        // CLICK HANDLERS
        binding.btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.launchSignUpUrl(user, new ArrayList<String>());
            }
        });

        binding.tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.launchMainActivity(user);
            }
        });
    }
}
