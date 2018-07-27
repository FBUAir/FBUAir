package me.gnahum12345.fbuair.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.activities.SignUpActivity;
import me.gnahum12345.fbuair.adapters.SocialMediaAdapter;
import me.gnahum12345.fbuair.databinding.FragmentSignUpSocialMediaBinding;
import me.gnahum12345.fbuair.databinding.IconsFooterBinding;
import me.gnahum12345.fbuair.interfaces.OnSignUpScreenChangeListener;
import me.gnahum12345.fbuair.models.SocialMedia;
import me.gnahum12345.fbuair.utils.SocialMediaUtils;
import me.gnahum12345.fbuair.utils.Utils;

public class SignUpSocialMediaFragment extends Fragment{
    // reference to activity
    SignUpActivity activity;

    ViewGroup container;
    OnSignUpScreenChangeListener onSignUpScreenChangeListener;

    public SocialMediaAdapter socialMediaAdapter;
    List<SocialMedia> socialMedias;

    // data bind to skip find view by id
    FragmentSignUpSocialMediaBinding bind;
    IconsFooterBinding bindFooter;

    public SignUpSocialMediaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // create socialMedias list with supported social media platforms and create adapter
        socialMedias = SocialMediaUtils.getAllSocialMedias();
        socialMediaAdapter = new SocialMediaAdapter(getContext(), socialMedias);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onSignUpScreenChangeListener = (OnSignUpScreenChangeListener) context;
        } catch (ClassCastException e) {
            Log.e("SignUpSocialMedia",
                    "Sign Up Activity must implement onSignUpScreenChangeListener");
        }
        super.onAttach(context);
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

        // set footer (buttons for skip and next) in grid view
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        bindFooter = DataBindingUtil.inflate(layoutInflater, R.layout.icons_footer, container,
                false);
        View footerView = bindFooter.getRoot();
        bind.gvSocialMedias.addFooterView(footerView);

        // attach adapter
        bind.gvSocialMedias.setAdapter(socialMediaAdapter);

        // bindFooter.btNext.setEnabled(!activity.user.getSocialMedias().isEmpty());

        // CLICK HANDLERS
        footerView.findViewById(R.id.btNext);
        bindFooter.btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSignUpScreenChangeListener.launchMainActivity();
            }
        });

        bindFooter.tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSignUpScreenChangeListener.launchMainActivity();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onSignUpScreenChangeListener = null;
    }
}
