package me.gnahum12345.fbuair.fragments;

import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import org.parceler.Parcels;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.GridViewWithHeaderAndFooter;
import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.activities.SignUpActivity;
import me.gnahum12345.fbuair.adapters.IconAdapter;
import me.gnahum12345.fbuair.databinding.FragmentSignUpSocialMediaBinding;
import me.gnahum12345.fbuair.databinding.IconsFooterBinding;
import me.gnahum12345.fbuair.models.Icon;
import me.gnahum12345.fbuair.models.User;
import me.gnahum12345.fbuair.utils.Utils;

public class SignUpSocialMediaFragment extends Fragment {
    // reference to activity
    SignUpActivity activity;

    ViewGroup container;

    User user;

    GridView gvIcons;
    IconAdapter iconAdapter;
    List<Icon> icons;
    // make sure drawable resource "ic_[social media name (lowercase)]" exists
    String[] socialMediaNames = {"Facebook", "Instagram", "Twitter", "Snapchat", "LinkedIn", "Google",
            "WhatsApp", "Youtube", "Reddit", "Pinterest", "Tumblr", "Soundcloud", "Github",
            "DeviantArt", "Dribbble"};

    // data bind to skip find view by id
    FragmentSignUpSocialMediaBinding bind;
    IconsFooterBinding bindFooter;

    public SignUpSocialMediaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        bind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_sign_up_social_media, container, false);
        View view = bind.getRoot();
        this.container = container;
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = (SignUpActivity) getActivity();
        // hide keyboard
        Utils.hideSoftKeyboard(activity);

        GridViewWithHeaderAndFooter gridView = (GridViewWithHeaderAndFooter) bind.gvIcons;

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        bindFooter = DataBindingUtil.inflate(layoutInflater, R.layout.icons_footer, container,
                false);
        View footerView = bindFooter.getRoot();
        gridView.addFooterView(footerView);

        icons = makeIcons(socialMediaNames);
        iconAdapter = new IconAdapter(getContext(), icons);
        bind.gvIcons.setAdapter(iconAdapter);

        // get info from last sign up screen
        if (getArguments() != null) {
            user = Parcels.unwrap(getArguments().getParcelable("user"));
        }
        // CLICK HANDLERS
        footerView.findViewById(R.id.btNext);
        bindFooter.btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.launchMainActivity(user);
            }
        });

        bindFooter.tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.launchMainActivity(user);
            }
        });
    }

    List<Icon> makeIcons(String[] iconNames) {
        List<Icon> icons = new ArrayList<>();
        for (String iconName : iconNames) {
            Icon icon = new Icon();
            String drawableName = "ic_" + iconName.toLowerCase();
            int resId = getResources().getIdentifier
                    (drawableName, "drawable", activity.getPackageName());
            icon.setDrawable(getResources().getDrawable(resId, null));
            icon.setName(iconName);
            icons.add(icon);
        }
        return icons;
    }
}
