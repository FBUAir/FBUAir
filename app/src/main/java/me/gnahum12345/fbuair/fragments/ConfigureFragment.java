package me.gnahum12345.fbuair.fragments;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;


import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.adapters.ConfigureAdapter;
import me.gnahum12345.fbuair.databinding.FragmentConfigureBinding;

public class ConfigureFragment extends DialogFragment {

    ConfigureAdapter adapter;
    ViewGroup container;
    FragmentConfigureBinding bind;

    public ConfigureFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // initialize adapter
        adapter = new ConfigureAdapter(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setDialogSettings(getDialog());

        // Inflate the layout for this fragment
        bind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_configure, container, false);
        this.container = container;

        // attach adapter
        bind.gvProfiles.setAdapter(adapter);

        // set dismiss listeners to submit and cancel buttons
        View.OnClickListener dismissListener = view -> dismiss();
        bind.btDone.setOnClickListener(dismissListener);

        return bind.getRoot();
    }

    void setDialogSettings(Dialog dialog) {
        Window window;
        if (dialog != null) {
            window = dialog.getWindow();
            if (window != null) {
                window.setWindowAnimations(R.style.PauseDialogAnimation);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                window.requestFeature(Window.FEATURE_NO_TITLE);
            }
        }
    }
}
