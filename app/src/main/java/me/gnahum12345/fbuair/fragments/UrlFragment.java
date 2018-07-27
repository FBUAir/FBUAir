package me.gnahum12345.fbuair.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.parceler.Parcels;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.activities.SignUpActivity;
import me.gnahum12345.fbuair.databinding.FragmentUrlBinding;
import me.gnahum12345.fbuair.interfaces.OnSignUpScreenChangeListener;
import me.gnahum12345.fbuair.models.SocialMedia;
import me.gnahum12345.fbuair.utils.SocialMediaUtils;

public class UrlFragment extends Fragment {
    // the fragment initialization parameters
    private static final String ARG_SOCIAL_MEDIA = "socialMedia";

    private SocialMedia socialMedia;

    private OnSignUpScreenChangeListener onSignUpScreenChangeListener;

    FragmentUrlBinding bind;

    SignUpActivity activity;

    public UrlFragment() {
        // Required empty public constructor
    }

    public static UrlFragment newInstance(SocialMedia socialMedia) {
        UrlFragment fragment = new UrlFragment();
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
                (inflater, R.layout.fragment_url, container, false);
        return bind.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (SignUpActivity) getActivity();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String title;
        // if username is not null, user already added this social media
        if (socialMedia.getUsername() != null) {
            title = "Edit ";
            bind.etUsername.setText(socialMedia.getUsername());
            bind.btRemove.setVisibility(View.VISIBLE);
        }
        else {
            title = "Add ";
            bind.etUsername.setText("");
            bind.btRemove.setVisibility(View.GONE);
        }

        bind.tvTitle.setText(title + socialMedia.getName());
        bind.etUsername.setHint(socialMedia.getName() + " username");

        bind.etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty()) {
                    bind.ivUsernameCheck.setVisibility(View.GONE);
                    bind.tvUsernameError.setText("A username is required.");
                    bind.btSubmit.setEnabled(false);
                }
                else {
                    bind.ivUsernameCheck.setVisibility(View.VISIBLE);
                    bind.tvUsernameError.setText("");
                    bind.btSubmit.setEnabled(true);
                }
            }
        });

        bind.btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSignUpScreenChangeListener.finishUrl();
            }
        });
        bind.btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                socialMedia.setUsername(bind.etUsername.getText().toString());
                activity.user.addSocialMedia(socialMedia);
                onSignUpScreenChangeListener.finishUrl();
            }
        });
        bind.btRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.user.removeSocialMedia(socialMedia);
                onSignUpScreenChangeListener.finishUrl();
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

    @Override
    public void onDetach() {
        super.onDetach();
        onSignUpScreenChangeListener = null;
    }
}
