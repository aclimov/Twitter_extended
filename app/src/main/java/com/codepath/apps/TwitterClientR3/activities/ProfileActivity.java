package com.codepath.apps.TwitterClientR3.activities;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.codepath.apps.TwitterClientR3.R;
import com.codepath.apps.TwitterClientR3.TwitterApp;
import com.codepath.apps.TwitterClientR3.net.TwitterClient;
import com.codepath.apps.TwitterClientR3.fragments.OtherUserInfoFragment;
import com.codepath.apps.TwitterClientR3.fragments.UserFragment;
import com.codepath.apps.TwitterClientR3.fragments.UserInfoFragment;
import com.codepath.apps.TwitterClientR3.fragments.UserTimelineFragment;
import com.codepath.apps.TwitterClientR3.models.User;

import org.parceler.Parcels;

public class ProfileActivity extends AppCompatActivity {
    TwitterClient client;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        client= TwitterApp.getRestClient();

        user = (User)Parcels.unwrap(getIntent().getParcelableExtra("userObj"));
        String screenName = user!=null?user.getScreenName():"";
        if(savedInstanceState==null) {
            UserTimelineFragment fUserTimeline = UserTimelineFragment.newInstance(screenName);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, fUserTimeline );

            UserFragment fUserInfo;

            if(user!=null ){
                fUserInfo = OtherUserInfoFragment.newInstance(user);
                ft.replace(R.id.flHeader, fUserInfo);

            }else{
                fUserInfo = UserInfoFragment.newInstance();
                ft.replace(R.id.flHeader, fUserInfo);
            }

            fUserInfo.setUserFragmentListener(new UserFragment.UserFragmentListener() {
                @Override
                public void onFollowersClicked(String screenName) {
                    Intent i = new Intent(ProfileActivity.this, FollowActivity.class);
                    i.putExtra("screen_name",screenName);
                    i.putExtra("follow_type","followers");
                    startActivity(i);
                }

                @Override
                public void onFollowingsClicked(String screenName) {
                    Intent i = new Intent(ProfileActivity.this, FollowActivity.class);
                    i.putExtra("screen_name",screenName);
                    i.putExtra("follow_type","followings");
                    startActivity(i);
                }
            });
            ft.commit();
        }
    }
}
