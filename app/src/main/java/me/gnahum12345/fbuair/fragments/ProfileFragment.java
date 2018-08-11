package me.gnahum12345.fbuair.fragments;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.adapters.ProfileAdapter;
import me.gnahum12345.fbuair.databinding.FragmentProfileBinding;
import me.gnahum12345.fbuair.interfaces.OnFragmentChangeListener;
import me.gnahum12345.fbuair.managers.MyUserManager;
import me.gnahum12345.fbuair.models.Header;
import me.gnahum12345.fbuair.models.SocialMedia;
import me.gnahum12345.fbuair.models.User;

import static me.gnahum12345.fbuair.utils.Utils.hideSoftKeyboard;

public class ProfileFragment extends Fragment {

    final static String ARG_UID = "uid";
    FragmentProfileBinding bind;
    Activity activity;
    ArrayList<SocialMedia> socialMedias;
    ProfileAdapter profileAdapter;
    ViewGroup container;
    User user;
    private ProfileFragmentListener mListener;
    OnFragmentChangeListener onFragmentChangeListener;

    boolean isCurrentUserProfile;

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

        setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));

        activity = getActivity();
        String uid;
        // if no arguments were passed in, assume current user profile
        if (getArguments() != null) {
            uid = getArguments().getString(ARG_UID);
            MyUserManager.getInstance().seenUser(uid);
            user = MyUserManager.getInstance().getUser(getArguments().getString(ARG_UID));
            isCurrentUserProfile = (user.equals(MyUserManager.getInstance().tryToGetCurrentUser()));
        } else {
            user = MyUserManager.getInstance().tryToGetCurrentUser();
            isCurrentUserProfile = true;
        }

        socialMedias = user.getSocialMedias();
        Header header = new Header(user.getId());
        profileAdapter = new ProfileAdapter(getContext(), header, socialMedias,
                isCurrentUserProfile);
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

        //hideSoftKeyboard(getActivity());
        onFragmentChangeListener.hideProgressBar();

        if (isCurrentUserProfile) {
            ActivityCompat.startPostponedEnterTransition(getActivity());
            bind.ivBack.setVisibility(View.GONE);
        }

        bind.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFragmentChangeListener.onDetailsBackPressed();
            }
        });

        bind.btDeleteProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFragmentChangeListener.deleteAccount();
            }
        });
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
        if (context instanceof OnFragmentChangeListener) {
            onFragmentChangeListener = (OnFragmentChangeListener) context;
        } else {
            throw new RuntimeException(context.toString() +
                    " must implement ProfileFragmentListener");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        onFragmentChangeListener.setBottomNavigationVisible(true);
        onFragmentChangeListener.setMenuVisible(true);
    }

    public interface ProfileFragmentListener {
        void sendBack(String uid);
    }
}
