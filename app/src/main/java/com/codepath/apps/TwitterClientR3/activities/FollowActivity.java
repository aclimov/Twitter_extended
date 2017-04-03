package com.codepath.apps.TwitterClientR3.activities;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.codepath.apps.TwitterClientR3.R;
import com.codepath.apps.TwitterClientR3.TwitterApp;
import com.codepath.apps.TwitterClientR3.models.User;
import com.codepath.apps.TwitterClientR3.net.TwitterClient;
import com.codepath.apps.TwitterClientR3.fragments.FollowListFragment;

import org.parceler.Parcels;

public class FollowActivity extends AppCompatActivity {
    Toolbar toolbar;
    FollowListFragment fragmentFollowList;

    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);

        toolbar = (Toolbar) findViewById(R.id.toolbar);


        if(toolbar!=null){
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        client = TwitterApp.getRestClient();

        String screenName = getIntent().getStringExtra("screen_name");
        String followType = getIntent().getStringExtra("follow_type");

        if (savedInstanceState == null) {
            FollowListFragment fUserFollow = FollowListFragment.newInstance(screenName,followType);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, fUserFollow);
            fUserFollow.setFollowFragmentListener(new FollowListFragment.FollowFragmentListener()
            {
                @Override
                public void showProfile(User user) {
                    lunchProfileActivity(user);
                }
            });
            ft.commit();
        }
    }

    public void lunchProfileActivity(User user) {
        Intent i = new Intent(this, ProfileActivity.class);
        i.putExtra("userObj", Parcels.wrap(user));
        startActivity(i);
    }
}
