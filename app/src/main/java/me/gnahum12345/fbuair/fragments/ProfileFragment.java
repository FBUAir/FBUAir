package me.gnahum12345.fbuair.fragments;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.adapters.ProfileAdapter;
import me.gnahum12345.fbuair.databinding.FragmentProfileBinding;
import me.gnahum12345.fbuair.managers.MyUserManager;
import me.gnahum12345.fbuair.models.Contact;
import me.gnahum12345.fbuair.models.Header;
import me.gnahum12345.fbuair.models.SocialMedia;
import me.gnahum12345.fbuair.models.User;

public class ProfileFragment extends Fragment {

    final static String ARG_UID = "uid";
    FragmentProfileBinding bind;
    Activity activity;
    ArrayList<SocialMedia> socialMedias;
    ProfileAdapter profileAdapter;
    Contact contact;
    ViewGroup container;
    User user;
    private ProfileFragmentListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String uid) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_UID, uid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        String uid;
        boolean isCurrentUserProfile;
        // if no arguments were passed in, assume current user profile
        if (getArguments() != null) {
            uid = getArguments().getString(ARG_UID);
            user = MyUserManager.getInstance().getUser(getArguments().getString(ARG_UID));
            isCurrentUserProfile = (user.equals(MyUserManager.getInstance().getCurrentUser()));
        } else {
            user = MyUserManager.getInstance().getCurrentUser();
            uid = user.getId();
            isCurrentUserProfile = true;
        }
        socialMedias = user.getSocialMedias();

        Contact contact = new Contact(uid);
        Header header = new Header(uid);
        profileAdapter = new ProfileAdapter(getContext(), contact, header,
                socialMedias, isCurrentUserProfile);
        if (mListener != null) {
            profileAdapter.setListener(mListener);
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        bind = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        this.container = container;
        return bind.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // attach adapter and layout manager
        bind.rvRecyclerView.setAdapter(profileAdapter);
        bind.rvRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ProfileFragmentListener) {
            mListener = (ProfileFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() +
                    " must implement ProfileFragmentListener");
        }
    }

    public interface ProfileFragmentListener {
        public void sendBack(String uid);
    }
}
