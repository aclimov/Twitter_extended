package com.codepath.apps.TwitterClientR3.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.TwitterClientR3.R;
import com.codepath.apps.TwitterClientR3.helpers.RoundedCornersTransformation;
import com.codepath.apps.TwitterClientR3.helpers.LinkifiedTextView;
import com.codepath.apps.TwitterClientR3.models.User;

import org.parceler.Parcels;

/**
 * Created by alex_ on 4/2/2017.
 */

public abstract class UserFragment extends Fragment {

    User user;

    private UserFragmentListener listener;

    ImageView ivUserBackImage;
    ImageView ivProfileImage;
    TextView tvDisplayName;
    TextView tvName;
    LinkifiedTextView tvDescription;
    TextView tvFollowing;
    TextView tvFollowers;
    TextView tvFollowingStr;
    TextView tvFollowersStr;

    //inflation logic
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_info, container, false);

        if(!getArguments().isEmpty()) {
            user = Parcels.unwrap(getArguments().getParcelable("userObj"));
        }
        findControls(v);
        if(user!=null) {
            populateUserData(user);
        }else{
            populateData();
        }

        appendListeners();
        return v;
    }

    public UserFragment() {
        // set null or default listener or accept as argument to constructor
        this.listener = null;
    }

    private void findControls(View v) {
        ivUserBackImage = (ImageView) v.findViewById(R.id.ivUserBackImage);
        ivProfileImage = (ImageView) v.findViewById(R.id.ivProfileImage);
        tvDisplayName = (TextView) v.findViewById(R.id.tvDisplayName);
        tvName = (TextView) v.findViewById(R.id.tvName);
        tvDescription = (LinkifiedTextView) v.findViewById(R.id.tvDescription);
        tvFollowing = (TextView) v.findViewById(R.id.tvFollowing);
        tvFollowers = (TextView) v.findViewById(R.id.tvFollowers);
        tvFollowingStr = (TextView) v.findViewById(R.id.tvFollowingStr);
        tvFollowersStr = (TextView) v.findViewById(R.id.tvFollowersStr);
    }

    public void populateUserData(User user) {
        this.user = user;
        Glide.with(getContext()).load(user.getProfileImageUrl())
                .bitmapTransform(new RoundedCornersTransformation(getContext(), 15, 2))
                .into(ivProfileImage);
        Glide.with(getContext()).load(user.getBackImageUrl())
                .placeholder(R.drawable.tback)
                .into(ivUserBackImage);
        tvDisplayName.setText(user.getName());
        tvName.setText(user.getScreenName());
        tvDescription.setText(user.getTagLine());
        tvFollowing.setText(user.getFollowings());
        tvFollowers.setText(user.getFollowers());
    }

    private void appendListeners() {

        tvFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFollowers();
            }
        });
        tvFollowersStr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFollowers();
            }
        });

        tvFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFollowings();
            }
        });
        tvFollowingStr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFollowings();
            }
        });



    }

    private void showFollowings() {
        if (listener != null) {
            listener.onFollowingsClicked(user.getScreenName());
        }
    }

    private void showFollowers() {
        if (listener != null) {
            listener.onFollowersClicked(user.getScreenName());
        }
    }

    public interface UserFragmentListener {
        // These methods are the different events and
        // need to pass relevant arguments related to the event triggered
        public void onFollowersClicked(String screenName);

        // or when data has been loaded
        public void onFollowingsClicked(String screenName);
    }

    // Assign the listener implementing events interface that will receive the events
    public void setUserFragmentListener(UserFragmentListener listener) {
        this.listener = listener;
    }

    public abstract void populateData();

}
