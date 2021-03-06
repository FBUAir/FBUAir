package me.gnahum12345.fbuair.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.parceler.Parcels;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.activities.SignUpActivity;
import me.gnahum12345.fbuair.databinding.FragmentValidateProfileBinding;
import me.gnahum12345.fbuair.interfaces.OnSignUpScreenChangeListener;
import me.gnahum12345.fbuair.models.SocialMedia;
import me.gnahum12345.fbuair.utils.Utils;

public class ValidateProfileFragment extends Fragment {
    // the fragment initialization parameters
    private static final String ARG_SOCIAL_MEDIA = "socialMedia";

    private SocialMedia socialMedia;

    private OnSignUpScreenChangeListener onSignUpScreenChangeListener;

    FragmentValidateProfileBinding bind;

    SignUpActivity activity;

    public ValidateProfileFragment() {
        // Required empty public constructor
    }

    public static ValidateProfileFragment newInstance(SocialMedia socialMedia) {
        ValidateProfileFragment fragment = new ValidateProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_SOCIAL_MEDIA, Parcels.wrap(socialMedia));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            socialMedia = Parcels.unwrap(getArguments().getParcelable(ARG_SOCIAL_MEDIA));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        bind = DataBindingUtil.inflate
                (inflater, R.layout.fragment_validate_profile, container, false);
        return bind.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = (SignUpActivity) getActivity();
        //Utils.hideSoftKeyboard(activity);

        // load users profile into webview
        String profileUrl = socialMedia.getProfileUrl();
        bind.wvProfile.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if( URLUtil.isNetworkUrl(url) ) {
                    return false;
                }
                if (appInstalledOrNot(url)) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } else {
                    view.loadUrl(url);
                }
                return true;
            }

        });
        bind.wvProfile.getSettings().setJavaScriptEnabled(true);
        bind.wvProfile.loadUrl(profileUrl);

        bind.btReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSignUpScreenChangeListener.finishValidateProfile(false);
            }
        });
        bind.btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.user.addSocialMedia(socialMedia);
                onSignUpScreenChangeListener.finishValidateProfile(true);
            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSignUpScreenChangeListener) {
            onSignUpScreenChangeListener = (OnSignUpScreenChangeListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = activity.getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onSignUpScreenChangeListener = null;
    }
}
