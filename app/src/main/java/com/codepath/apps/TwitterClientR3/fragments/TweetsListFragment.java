package com.codepath.apps.TwitterClientR3.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.TwitterClientR3.listeners.EndlessRecyclerViewScrollListener;
import com.codepath.apps.TwitterClientR3.helpers.ItemClickSupport;
import com.codepath.apps.TwitterClientR3.R;
import com.codepath.apps.TwitterClientR3.activities.TweetActivity;
import com.codepath.apps.TwitterClientR3.adapters.TweetsArrayAdapter;
import com.codepath.apps.TwitterClientR3.models.Tweet;
import com.codepath.apps.TwitterClientR3.models.User;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
import static com.codepath.apps.TwitterClientR3.R.id.fabCreate;
import static com.codepath.apps.TwitterClientR3.R.id.toolbar;
*/

/**
 * Created by alex_ on 3/30/2017.
 */

public abstract class TweetsListFragment extends Fragment {

    private ArrayList<Tweet> tweets;
    private Set tweetIds;
    private TweetsArrayAdapter aTweets;

    protected SwipeRefreshLayout swipeContainer;
    private RecyclerView lvTweets;

    EndlessRecyclerViewScrollListener scrollListener;
    LinearLayoutManager linearLayoutManager;

    public TweetsListFragment(){
        listener=null;
    }

    //inflation logic
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets_list,container, false);
        findControls(v);
        initControls();
        appendListeners();
        return v;
    }

    //creation lifecycle events
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAdapter();
    }

    private void findControls(View v){
        swipeContainer = (SwipeRefreshLayout)v.findViewById(R.id.swipeContainer);
        lvTweets = (RecyclerView) v.findViewById(R.id.lvTweets);
        linearLayoutManager = new LinearLayoutManager(getActivity());
    }

    private void initAdapter(){
        tweetIds=new HashSet();
        //init controls

        //init arraylist
        tweets = new ArrayList<Tweet>();
        //construct adapter from datasource
        aTweets = new TweetsArrayAdapter(getActivity(), tweets);
    }

    private void initControls(){

        if(swipeContainer!=null) {
            swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                    R.color.colorGrey,
                    R.color.colorWhite
            );
        }

        //connect adapter with recyclerView
        lvTweets.setAdapter(aTweets);
        lvTweets.setLayoutManager(linearLayoutManager);
    }

    private void appendListeners(){
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                refreshTweets();
            }
        });

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                populateTimeline();
            }

           /* @Override
            public void onScrolled(RecyclerView view, int dx, int dy) {
                if (dy > 0)
                    fabCreate.hide();
                else if (dy < 0)
                    fabCreate.show();
            }*/
        };
        lvTweets.addOnScrollListener(scrollListener);


        ItemClickSupport.addTo(lvTweets).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Intent i = new Intent(getActivity(), TweetActivity.class);
                Tweet tweet = tweets.get(position);
                i.putExtra("tweet", Parcels.wrap(tweet));
                startActivity(i);
            }
        });

        aTweets.setTweetsListListener(new TweetsArrayAdapter.TweetsListListener(){
            @Override
            public void onProfileImageClick(User user) {
                if(listener!=null){
                    listener.onShowProfile(user);
                }
            }
        });


    }

    private void refreshTweets(){
        if(tweetIds.size()>0){
            getNewTweets((long) Collections.max(tweetIds));
        }
    }

   public void insertTweets(List<Tweet> newTweets) {
        //get item count fomr RecyclerView Adapter
        //int curSize = aTweets.getItemCount();
        // replace this line with wherever you get new records
        Collections.reverse(newTweets);
        for(Tweet newTweet :newTweets){
            if(!tweetIds.contains(newTweet.getUid())){
                tweets.add(0,newTweet);
                tweetIds.add(newTweet.getUid());
            }
        }
        //notify adapter to reflect changes
        aTweets.notifyItemRangeInserted(0, newTweets.size());
        swipeContainer.setRefreshing(false);
    }

    public long getMinTweetId(){
        long minId =0;
        if(tweets==null||tweets.size()>0) {
            minId = (long)Collections.min(tweetIds);
        }
        return minId;
    }

    public void addAll(ArrayList<Tweet> newTweets){
        //get item count fomr RecyclerView Adapter
        int curSize = aTweets.getItemCount();
        // replace this line with wherever you get new records
        for(Tweet ntweet :newTweets){
            if(!tweetIds.contains(ntweet.getUid())){
                tweets.add(ntweet);
                tweetIds.add(ntweet.getUid());
            }
        }
        //notify adapter to reflect changes
        aTweets.notifyItemRangeInserted(curSize, newTweets.size());
    }

    public void add(Tweet newTweet){
        if(!tweetIds.contains(newTweet.getUid())){
            tweets.add(0,newTweet);
            tweetIds.add(newTweet.getUid());
        }
        aTweets.notifyItemInserted(0);
        lvTweets.scrollToPosition(0);
    }

    // Abstract method to be overridden
    protected abstract void populateTimeline();

    protected abstract void getNewTweets(long maxId);



    public interface TweetsListFragmentListener{
        public void onShowProfile(User user);
    }

    // Assign the listener implementing events interface that will receive the events
    public void setTweetsListFragmentListener(TweetsListFragmentListener listener) {
        this.listener = listener;
    }

    private TweetsListFragmentListener listener;



}
