package com.codepath.apps.TwitterClientR3.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.TwitterClientR3.listeners.EndlessRecyclerViewScrollListener;
import com.codepath.apps.TwitterClientR3.R;
import com.codepath.apps.TwitterClientR3.TwitterApp;
import com.codepath.apps.TwitterClientR3.net.TwitterClient;
import com.codepath.apps.TwitterClientR3.adapters.UsersArrayAdapter;
import com.codepath.apps.TwitterClientR3.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import cz.msebera.android.httpclient.Header;

/**
 * Created by alex_ on 3/30/2017.
 */

public class FollowListFragment extends Fragment {

    String screenUser;
    String type;
    TwitterClient client;

    private ArrayList<User> users;
    private Set userIds;
    private UsersArrayAdapter aUsers;

    protected SwipeRefreshLayout swipeContainer;
    private RecyclerView lvUsers;

    EndlessRecyclerViewScrollListener scrollListener;
    LinearLayoutManager linearLayoutManager;


    public FollowListFragment(){
        listener=null;
    }

    //inflation logic
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_users_list, container, false);
        findControls(v);
        initControls();
        appendListeners();
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApp.getRestClient();
        initAdapter();
        populateList();
    }

    public static FollowListFragment newInstance(String screenName, String type) {
        FollowListFragment userFrament = new FollowListFragment();
        Bundle args = new Bundle();
        args.putString("screenName", screenName);
        args.putString("follow_type", type);
        userFrament.setArguments(args);
        return userFrament;
    }

    private void findControls(View v) {
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
        lvUsers = (RecyclerView) v.findViewById(R.id.lvUsers);
        linearLayoutManager = new LinearLayoutManager(getActivity());
    }

    private void initAdapter() {
        userIds = new HashSet();
        //init controls

        //init arraylist
        users = new ArrayList<User>();
        //construct adapter from datasource
        aUsers = new UsersArrayAdapter(getActivity(), users);
    }

    private void initControls() {

        if (swipeContainer != null) {
            swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                    R.color.colorGrey,
                    R.color.colorWhite
            );
        }

        //connect adapter with recyclerView
        lvUsers.setAdapter(aUsers);
        lvUsers.setLayoutManager(linearLayoutManager);
    }

    private void appendListeners() {
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshUsers();
            }
        });

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                populateList();
            }
        };
        lvUsers.addOnScrollListener(scrollListener);

        aUsers.setAdapterListener(new UsersArrayAdapter.UserAdapterListener(){
            @Override
            public void showProfile(User user) {
               if(listener!=null){
                   listener.showProfile(user);
               }
            }
        });

       /* ItemClickSupport.addTo(lvUsers).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Intent i = new Intent(getActivity(), TweetActivity.class);
                Tweet tweet = users.get(position);
                i.putExtra("tweet", Parcels.wrap(tweet));
                startActivity(i);
            }
        });*/
    }

    private void refreshUsers() {
        if (userIds.size() > 0) {
            getNewUsers((long) Collections.max(userIds));
        }
    }

    public long getMinTweetId() {
        long minId = 0;
        if (users == null || users.size() > 0) {
            minId = (long) Collections.min(userIds);
        }
        return minId;
    }

    public void addAll(ArrayList<User> newUsers) {
        //get item count fomr RecyclerView Adapter
        int curSize = aUsers.getItemCount();
        // replace this line with wherever you get new records
        for (User nUser : newUsers) {
            if (!userIds.contains(nUser.getUid())) {
                users.add(nUser);
                userIds.add(nUser.getUid());
            }
        }
        //notify adapter to reflect changes
        aUsers.notifyItemRangeInserted(curSize, newUsers.size());
    }

    protected void populateList() {
        String screenName = getArguments().getString("screenName");
        String followType = getArguments().getString("follow_type");

        if (followType.equals("followers")) {

            client.getUserFollowers(screenName, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    if (response.has("users")) {
                        try {
                            JSONArray users = response.getJSONArray("users");
                            addAll(User.fromJsonArray(users));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    if (errorResponse != null) {
                        Log.d("DEBUG", errorResponse.toString());
                    }
                }
            });
        } else {
            client.getUserFollowings(screenName, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    if (response.has("users")) {
                        try {
                            JSONArray users = response.getJSONArray("users");
                            addAll(User.fromJsonArray(users));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

    protected void getNewUsers(long maxId) {
    }

    public interface FollowFragmentListener {
        // These methods are the different events and
        // need to pass relevant arguments related to the event triggered
        public void showProfile(User user);
    }

    // Assign the listener implementing events interface that will receive the events
    public void setFollowFragmentListener(FollowFragmentListener listener) {
        this.listener = listener;
    }

    private FollowFragmentListener listener;

}
