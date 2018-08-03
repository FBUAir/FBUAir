package me.gnahum12345.fbuair.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Objects;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.activities.SignUpActivity;
import me.gnahum12345.fbuair.interfaces.OnSignUpScreenChangeListener;

public class WelcomeFragment extends Fragment {

    Button btGetStarted;
    SignUpActivity activity;
    OnSignUpScreenChangeListener onSignUpScreenChangeListener;

    public WelcomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onSignUpScreenChangeListener = (OnSignUpScreenChangeListener) context;
        } catch (ClassCastException e) {
            Log.e("WelcomeFragment",
                    "SignUpActivity must implement OnSignUpScreenChangeListener");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_welcome_v3, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // get reference to activity
        activity = (SignUpActivity) getActivity();

        // hide toolbar
        if (activity != null) {
            Objects.requireNonNull(activity.getSupportActionBar()).hide();
        }

        // go to signup if user presses 'get started'
        btGetStarted = view.findViewById(R.id.btGetStarted);
        btGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSignUpScreenChangeListener.launchSignUpContact();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onSignUpScreenChangeListener = null;
    }
}
