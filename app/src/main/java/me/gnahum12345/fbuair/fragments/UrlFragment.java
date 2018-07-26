package me.gnahum12345.fbuair.fragments;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.adapters.TextViewBindingAdapter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.parceler.Parcels;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.activities.SignUpActivity;
import me.gnahum12345.fbuair.databinding.FragmentUrlBinding;
import me.gnahum12345.fbuair.interfaces.OnSignUpScreenChangeListener;
import me.gnahum12345.fbuair.models.Icon;
import me.gnahum12345.fbuair.models.SocialMedia;
import me.gnahum12345.fbuair.utils.IconUtils;

public class UrlFragment extends Fragment {
    // the fragment initialization parameters
    private static final String ARG_ICON_NAME = "iconName";

    private Icon icon;
    private String iconName;

    private OnSignUpScreenChangeListener onSignUpScreenChangeListener;

    FragmentUrlBinding bind;

    SignUpActivity activity;

    public UrlFragment() {
        // Required empty public constructor
    }

    public static UrlFragment newInstance(String iconName) {
        UrlFragment fragment = new UrlFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ICON_NAME, iconName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            iconName = getArguments().getString(ARG_ICON_NAME);
            icon = IconUtils.getIcon(getContext(), iconName);
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

        bind.tvTitle.setText("Add " + iconName);
        bind.etUrl.setHint(iconName + " Profile URL");

        bind.etUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                String input = editable.toString();
                if (input.isEmpty()) {
                    bind.ivUrlCheck.setVisibility(View.GONE);
                    bind.tvUrlError.setText("A profile URL is required.");
                }
                else if (!(Patterns.WEB_URL.matcher(input).matches() &&
                        input.toLowerCase().contains(iconName.toLowerCase()))) {
                    bind.ivUrlCheck.setVisibility(View.GONE);
                    bind.tvUrlError.setText("Enter a valid " + iconName + " profile URL.");
                }
                else {
                    bind.ivUrlCheck.setVisibility(View.VISIBLE);
                    bind.tvUrlError.setText("");
                }
                bind.btSubmit.setEnabled(isValid());
            }
        });

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
                }
                else {
                    bind.ivUsernameCheck.setVisibility(View.VISIBLE);
                    bind.tvUsernameError.setText("");
                }
                bind.btSubmit.setEnabled(isValid());
            }
        });

        bind.btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSignUpScreenChangeListener.finishUrl(false);
            }
        });
        bind.btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SocialMedia socialMedia = new SocialMedia();
                socialMedia.setIcon(icon);
                icon.setAdded(true);
                socialMedia.setProfileUrl(bind.etUrl.getText().toString());
                socialMedia.setUsername(bind.etUsername.getText().toString());
                activity.user.addSocialMedia(socialMedia);
                onSignUpScreenChangeListener.finishUrl(true);
            }
        });
    }

    boolean isValid() {
        return (bind.ivUsernameCheck.getVisibility() == View.VISIBLE &&
                bind.ivUrlCheck.getVisibility() == View.VISIBLE);
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
