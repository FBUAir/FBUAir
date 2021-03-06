package me.gnahum12345.fbuair.fragments;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Path;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.activities.SignUpActivity;
import me.gnahum12345.fbuair.interfaces.OnSignUpScreenChangeListener;
import me.gnahum12345.fbuair.utils.Utils;

public class WelcomeFragment extends Fragment {

    Button btGetStarted;
    ImageView ivLogo;
    SignUpActivity activity;
    OnSignUpScreenChangeListener onSignUpScreenChangeListener;

    public WelcomeFragment() {
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
        return inflater.inflate(R.layout.fragment_welcome, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // get reference to activity
        activity = (SignUpActivity) getActivity();

        // hide keyboard and menu
        //Utils.hideSoftKeyboard(activity);
        onSignUpScreenChangeListener.setMenuVisible(false);

        // go to signup if user presses 'get started'
        ivLogo = view.findViewById(R.id.ivLogo);


        btGetStarted = view.findViewById(R.id.btGetStarted);
        btGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Path path = new Path();
                float x = ivLogo.getX();
                float y = ivLogo.getY();
                path.moveTo(x, y);
                path.cubicTo(500, 500, 600, 10, 1500, 0);
                ObjectAnimator animator = ObjectAnimator.ofFloat(ivLogo, View.X, View.Y, path);
                animator.setDuration(2000);
                animator.start();
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        onSignUpScreenChangeListener.launchSignUpContact();
                    }
                }, 1300);
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onSignUpScreenChangeListener = null;
    }

}
