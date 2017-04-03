package com.codepath.apps.TwitterClientR3.activities;


import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.TwitterClientR3.R;
import com.codepath.apps.TwitterClientR3.TwitterApp;
import com.codepath.apps.TwitterClientR3.fragments.UserFragment;
import com.codepath.apps.TwitterClientR3.net.TwitterClient;
import com.codepath.apps.TwitterClientR3.adapters.TweetFragmentPagerAdapter;
import com.codepath.apps.TwitterClientR3.fragments.TweetsListFragment;
import com.codepath.apps.TwitterClientR3.models.Tweet;
import com.codepath.apps.TwitterClientR3.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

import static android.widget.Toast.makeText;
import static com.codepath.apps.TwitterClientR3.models.User_Table.screenName;

public class TimelineActivity extends AppCompatActivity {

    // REQUEST_CODE can be any value we like, used to determine the result type later
    private final int REQUEST_CODE = 20;
    Toolbar toolbar;
    FloatingActionButton fabCreate;
    TwitterClient client;
    TweetsListFragment fragmentTweetsList;

   /* static final int POLL_INTERVAL = 10000; // milliseconds
    Handler myHandler = new Handler();  // android.os.Handler
    Runnable mRefreshMessagesRunnable = new Runnable() {
        @Override
        public void run() {
            refreshTweets();
            myHandler.postDelayed(this, POLL_INTERVAL);
        }
    };
*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        TweetFragmentPagerAdapter adapter = new TweetFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        adapter.setTweetsListPagerFragmentListener(new TweetFragmentPagerAdapter.TweetsListPagerFragmentListener(){
            @Override
            public void onShowProfile(User user) {
                lunchProfileActivity(user);
            }
        });

        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabStrip.setViewPager(viewPager);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        fabCreate = (FloatingActionButton) findViewById(R.id.fabCreate);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        fabCreate.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));

        fabCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(TimelineActivity.this, NewTweetActivity.class);
                startActivityForResult(i, REQUEST_CODE);
            }
        });

        client = TwitterApp.getRestClient();

       /* if(savedInstanceState==null) {
            fragmentTweetsList = (TweetsListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_timeline);
        }*/

        //myHandler.postDelayed(mRefreshMessagesRunnable, POLL_INTERVAL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
          /*  case R.id.compose:
                Intent i = new Intent(TimelineActivity.this,NewTweetActivity.class);
                startActivityForResult(i, REQUEST_CODE);

                return true;*/

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            // Extract name value from result extras
            Tweet tweet = Parcels.unwrap(data.getExtras().getParcelable("tweet"));
            //add tweet to fragment_tweets_list
            fragmentTweetsList.add(tweet);
        }
    }

    public void lunchProfileActivity(View v) {

        client.getUserInfo(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                User user = User.fromJsonObject(response);
                lunchProfileActivity(user);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if(errorResponse!=null) {
                    Log.d("DEBUG", errorResponse.toString());
                }
            }
        });
    }

    public void lunchProfileActivity(User user) {
        Intent i = new Intent(this, ProfileActivity.class);
        i.putExtra("userObj",Parcels.wrap(user));
        startActivity(i);
    }

}
