package com.codepath.apps.TwitterClientR3.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.codepath.apps.TwitterClientR3.TwitterApp;
import com.codepath.apps.TwitterClientR3.net.TwitterClient;
import com.codepath.apps.TwitterClientR3.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by alex_ on 4/2/2017.
 */

public class UserInfoFragment extends UserFragment {
    private TwitterClient client;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApp.getRestClient();
    }


    public static UserInfoFragment newInstance() {
        UserInfoFragment userInfoFrament = new UserInfoFragment();
        return userInfoFrament;
    }

    public void populateData() {
        client.getUserInfo( new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                populateUserData(User.fromJsonObject(response));
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
