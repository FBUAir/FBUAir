package me.gnahum12345.fbuair.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.adapters.ConfigureAdapter;
import me.gnahum12345.fbuair.databinding.ConfigureFragmentsFooterBinding;
import me.gnahum12345.fbuair.databinding.FragmentConfigureBinding;

public class ConfigureFragment extends DialogFragment {

    ConfigureAdapter adapter;
    ViewGroup container;
    FragmentConfigureBinding bind;
    ConfigureFragmentsFooterBinding bindFooter;

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
        bindFooter = DataBindingUtil.inflate(inflater, R.layout.configure_fragments_footer, container, false);
        View footerView = bindFooter.getRoot();

        bind.gvSocialMedias.addFooterView(footerView);
        bind.gvSocialMedias.setAdapter(adapter);

        bindFooter.btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: handle this..
                Toast.makeText(getContext(), "I will handle this eventually", Toast.LENGTH_SHORT).show();
            }
        });

        return bind.getRoot();
    }

    // TODO: Rename method, update argument and hook method into UI event

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
