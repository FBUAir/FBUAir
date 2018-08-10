package me.gnahum12345.fbuair.fragments;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;


import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.adapters.ConfigureAdapter;
import me.gnahum12345.fbuair.databinding.ConfigureFragmentsFooterBinding;
import me.gnahum12345.fbuair.databinding.ConfigureFragmentsHeaderBinding;
import me.gnahum12345.fbuair.databinding.FragmentConfigureBinding;

public class ConfigureFragment extends DialogFragment {

    ConfigureAdapter adapter;
    ViewGroup container;
    FragmentConfigureBinding bind;
    ConfigureFragmentsFooterBinding bindFooter;
    ConfigureFragmentsHeaderBinding bindHeader;

    public ConfigureFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ConfigureAdapter(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        bind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_configure, container, false);
        this.container = container;

        // set footer (buttons for skip and next) in grid view
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        bindFooter = DataBindingUtil.inflate(layoutInflater, R.layout.configure_fragments_footer, container,
                false);
        bindHeader = DataBindingUtil.inflate(layoutInflater, R.layout.configure_fragments_header, container,
                false);
        View footerView = bindFooter.getRoot();
        View headerView = bindHeader.getRoot();
        bind.gvProfiles.addFooterView(footerView);
        bind.gvProfiles.addHeaderView(headerView);

        // attach adapter
        bind.gvProfiles.setAdapter(adapter);

        // Set transparent background and no title
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.CENTER;

            dialog.getWindow().setAttributes(lp);

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        // set dismiss listeners to submit and cancel buttons
        View.OnClickListener dismissListener = view -> dismiss();
        bindFooter.btSubmit.setOnClickListener(dismissListener);
        bindFooter.btCancel.setOnClickListener(dismissListener);

        return bind.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
