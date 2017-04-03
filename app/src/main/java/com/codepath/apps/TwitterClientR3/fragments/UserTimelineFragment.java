package com.codepath.apps.TwitterClientR3.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.codepath.apps.TwitterClientR3.TwitterApp;
import com.codepath.apps.TwitterClientR3.net.TwitterClient;
import com.codepath.apps.TwitterClientR3.models.Tweet;
import com.codepath.apps.TwitterClientR3.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by alex_ on 4/2/2017.
 */

public class UserTimelineFragment extends TweetsListFragment {

    private TwitterClient client;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApp.getRestClient();
        populateTimeline();
        storeUserImage();
    }

    public static UserTimelineFragment newInstance(String screenName) {
        UserTimelineFragment userFrament = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString("screenName", screenName);
        userFrament.setArguments(args);
        return userFrament;
    }

    protected void populateTimeline() {

        String screenName = getArguments().getString("screenName");
        client.getUserTimeLine(screenName, getMinTweetId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                addAll(Tweet.fromJsonArray(response));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (errorResponse != null) {
                    Log.d("DEBUG", errorResponse.toString());
                }
            }
        });
    }

    protected void getNewTweets(long maxId) {
        client.getNewMentionsTimeline(maxId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                ArrayList<Tweet> newTweets = Tweet.fromJsonArray(response);
                insertTweets(newTweets);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
                swipeContainer.setRefreshing(false);
            }
        });
    }

    public void storeUserImage() {
        client.getCurrentUser(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                User currentUser = User.fromJsonObject(response);
                //set curent user image

                if (currentUser != null) {
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    SharedPreferences.Editor edit = pref.edit();
                    edit.putString("userUrl", currentUser.getProfileImageUrl());
                    edit.commit();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (errorResponse != null) {
                    Log.d("DEBUG", errorResponse.toString());
                }
            }
        });
    }
}
