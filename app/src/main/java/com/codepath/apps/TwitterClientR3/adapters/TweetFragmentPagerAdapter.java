package com.codepath.apps.TwitterClientR3.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codepath.apps.TwitterClientR3.fragments.HomeTimelineFragment;
import com.codepath.apps.TwitterClientR3.fragments.MentionsTimelineFragment;
import com.codepath.apps.TwitterClientR3.fragments.TweetsListFragment;
import com.codepath.apps.TwitterClientR3.models.User;

/**
 * Created by alex_ on 3/31/2017.
 */

public class TweetFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "Home", "Mentions"};

    public TweetFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        listener = null;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {

        TweetsListFragment f;
       if(position==0){
           f= new HomeTimelineFragment();
       }else if(position==1){
           f= new MentionsTimelineFragment();
       }else{
           return null;
       }
       f.setTweetsListFragmentListener(new TweetsListFragment.TweetsListFragmentListener()
       {
           @Override
           public void onShowProfile(User user) {
               if(listener!=null){
                   listener.onShowProfile(user);
               }
           }
       });

        return f;

    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }


    public interface TweetsListPagerFragmentListener{
        public void onShowProfile(User user);
    }

    // Assign the listener implementing events interface that will receive the events
    public void setTweetsListPagerFragmentListener(TweetsListPagerFragmentListener listener) {
        this.listener = listener;
    }

    private TweetsListPagerFragmentListener listener;
}