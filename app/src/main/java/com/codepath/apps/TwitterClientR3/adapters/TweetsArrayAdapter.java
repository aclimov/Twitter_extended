package com.codepath.apps.TwitterClientR3.adapters;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.TwitterClientR3.activities.TimelineActivity;
import com.codepath.apps.TwitterClientR3.models.Hashtag;
import com.codepath.apps.TwitterClientR3.models.Mention;
import com.codepath.apps.TwitterClientR3.R;
import com.codepath.apps.TwitterClientR3.helpers.RoundedCornersTransformation;
import com.codepath.apps.TwitterClientR3.models.Tweet;
import com.codepath.apps.TwitterClientR3.models.User;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;


import java.util.List;

/**
 * Created by alex_ on 3/21/2017.
 */

//Taking the tweets object and turning them into views displayed in the list;
public class TweetsArrayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    SimpleExoPlayer player;
    private final int IMG = 0, VID = 1, SIMPLE = 2, URL = 3;
    // List of tweets
    private List<Tweet> mTweets;
    // Store the context for easy access
    private Context mContext;

    public TweetsArrayAdapter(@NonNull Context context, @NonNull List<Tweet> objects) {
        mTweets = objects;
        mContext = context;
        player = createExoPlayer();
        listener = null;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    // inflate xml layout and return  viewHolder
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = null;
        // Inflate the custom layout
        switch (viewType) {
            case IMG:
                View v1 = inflater.inflate(R.layout.item_tweet_img, parent, false);
                viewHolder = new ViewHolder_img(v1);
                break;
            case VID:
                View v3 = inflater.inflate(R.layout.item_tweet_video, parent, false);
                viewHolder = new ViewHolder_video(v3);
                break;
            default:
                View v2 = inflater.inflate(R.layout.item_tweet, parent, false);
                viewHolder = new ViewHolder_simple(v2);
                break;
        }

        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        String type = mTweets.get(position).getType();

        switch (type) {
            case "photo":
                return IMG;
            case "video":
                return VID;
            default:
                return SIMPLE;
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case IMG:
                ViewHolder_img vh = (ViewHolder_img) viewHolder;
                configureViewHolder_img(vh, position);
                break;

            case VID:
                ViewHolder_video vh3 = (ViewHolder_video) viewHolder;
                configureViewHolder_video(vh3, position);
                break;
            default:
                ViewHolder_simple vh2 = (ViewHolder_simple) viewHolder;
                configureViewHolder_simple(vh2, position);
                break;
        }
    }

    private void configureViewHolder_img(ViewHolder_img vh, int position) {
        // Get the data model based on position
        Tweet tweet = mTweets.get(position);
        fetchCommon(vh, tweet);

        //load image from media
        Glide.with(getContext()).load(tweet.getMediaObjects().get(0).getMediaUrl())
                .bitmapTransform(new RoundedCornersTransformation(getContext(), 15, 2))
                .into(vh.ivTweetMedia);

    }

    private void configureViewHolder_video(ViewHolder_video vh, int position) {
        // Get the data model based on position
        Tweet tweet = mTweets.get(position);
        fetchCommon(vh, tweet);

        vh.playerView.setPlayer(player);

        // Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(mContext,
                Util.getUserAgent(mContext, "Twitter_client"), bandwidthMeter);
        // Produces Extractor instances for parsing the media data.
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        // This is the MediaSource representing the media to be played.
        Uri uri = Uri.parse(tweet.getMediaObjects().get(0).getUrl());

        MediaSource videoSource = new ExtractorMediaSource(uri,
                dataSourceFactory, extractorsFactory, null, null);
// Prepare the player with the source.
        player.prepare(videoSource);


       /* //load image from media
        Glide.with(getContext()).load(tweet.getMediaObjects().get(0).getMediaUrl())
                .bitmapTransform(new RoundedCornersTransformation(getContext(), 15, 2))
                .into(vh.ivTweetMedia);*/

    }

    private void configureViewHolder_simple(ViewHolder_simple vh, int position) {
        // Get the data model based on position
        Tweet tweet = mTweets.get(position);
        fetchCommon(vh, tweet);
    }

    private SimpleExoPlayer createExoPlayer() {
        // 1. Create a default TrackSelector
        Handler mainHandler = new Handler();
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        // 2. Create a default LoadControl
        LoadControl loadControl = new DefaultLoadControl();

        // 3. Create the player
        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector, loadControl);
        return player;
    }

    //populate main fields
    public void fetchCommon(ViewHolder_simple viewHolder, Tweet tweet) {

        final Tweet tweet1 = tweet;

        SpannableString ss = new SpannableString(tweet.getBody());
        ClickableSpan csHashtag = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                // startActivity(new Intent(MyActivity.this, NextActivity.class));
                Spanned spanned = (Spanned) ((TextView) textView).getText();

                String word = tweet1.getBody().substring(spanned.getSpanStart(this), spanned.getSpanEnd(this));
                Toast.makeText(mContext, word + " add listener for this Hashtag", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        ClickableSpan csUser = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                // startActivity(new Intent(MyActivity.this, NextActivity.class));
                Spanned spanned = (Spanned) ((TextView) textView).getText();

                String word = tweet1.getBody().substring(spanned.getSpanStart(this), spanned.getSpanEnd(this));
                Toast.makeText(mContext, word + " add listener for this user", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };

        for (Hashtag ht : tweet.getHashtags()) {
            ss.setSpan(csHashtag, ht.start, ht.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        for (Mention mt : tweet.getMentions()) {
            // if(mt.start>=0&&mt.end<ss.length()) {
            ss.setSpan(csUser, mt.start, mt.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            // }
        }

        viewHolder.tvBody.setText(ss);
        viewHolder.tvBody.setMovementMethod(LinkMovementMethod.getInstance());
        viewHolder.tvBody.setHighlightColor(Color.CYAN);
        viewHolder.tvTimestamp.setText(tweet.getTimestamp());
        viewHolder.tvUsername.setText(tweet.getUser().getScreenName());
        viewHolder.tvDisplayName.setText(tweet.getUser().getName());

        if (tweet.getFavoriteCount() > 0) {
            viewHolder.tvLike.setText(String.valueOf(tweet.getFavoriteCount()));
        }

        if (tweet.getRetweetCount() > 0) {
            viewHolder.tvRetweet.setText(String.valueOf(tweet.getRetweetCount()));
        }

        if (!TextUtils.isEmpty(tweet.getRetweetedBy())) {
            viewHolder.tvRetweeted.setText(tweet.getRetweetedBy());
        } else {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewHolder.llLeftColumn.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            viewHolder.tvRetweeted.setVisibility(View.INVISIBLE);
            viewHolder.llLeftColumn.setLayoutParams(params); //causes layout update*/
        }

        if (viewHolder.ivProfileImage != null) {
            Glide.with(getContext()).load(tweet.getUser().getProfileImageUrl())
                    .bitmapTransform(new RoundedCornersTransformation(getContext(), 15, 2))
                    .into(viewHolder.ivProfileImage);

            viewHolder.ivProfileImage.setClickable(true);
            viewHolder.ivProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //  Log.i(SystemSettings.APP_TAG + " : " + HomeActivity.class.getName(), "Entered onClick method");

                   /* if(mContext instanceof TimelineActivity){
                        ((TimelineActivity)mContext).lunchProfileActivity(tweet1.getUser());
                    }*/
                    if (listener != null) {
                        listener.onProfileImageClick(tweet1.getUser());
                    }
                }
            });
        }
    }

    public static class ViewHolder_simple extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView tvUsername;
        public TextView tvTimestamp;
        public TextView tvBody;
        public ImageView ivProfileImage;
        public TextView tvDisplayName;
        public TextView tvRetweeted;
        public RelativeLayout llLeftColumn;

        public TextView tvRetweet;
        public TextView tvLike;

        //Define constructor wichi accept entire row and find sub views
        public ViewHolder_simple(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            tvUsername = (TextView) itemView.findViewById(R.id.tvName);
            tvTimestamp = (TextView) itemView.findViewById(R.id.tvTimestamp);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvDisplayName = (TextView) itemView.findViewById(R.id.tvDisplayName);
            tvRetweeted = (TextView) itemView.findViewById(R.id.tvRetweeted);

            llLeftColumn = (RelativeLayout) itemView.findViewById(R.id.llLeftColumn);

            tvRetweet = (TextView) itemView.findViewById(R.id.tvRetweet);
            tvLike = (TextView) itemView.findViewById(R.id.tvLike);
        }
    }

    public static class ViewHolder_img extends ViewHolder_simple {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row

        public ImageView ivTweetMedia;

        //Define constructor wichi accept entire row and find sub views
        public ViewHolder_img(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            ivTweetMedia = (ImageView) itemView.findViewById(R.id.ivTweetMedia);
        }
    }

    public static class ViewHolder_video extends ViewHolder_simple {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row

        public com.google.android.exoplayer2.ui.SimpleExoPlayerView playerView;

        //Define constructor wichi accept entire row and find sub views
        public ViewHolder_video(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            playerView = (com.google.android.exoplayer2.ui.SimpleExoPlayerView) itemView.findViewById(R.id.playerView);
        }
    }

    public interface TweetsListListener {
        public void onProfileImageClick(User user);
    }

    // Assign the listener implementing events interface that will receive the events
    public void setTweetsListListener(TweetsListListener listener) {
        this.listener = listener;
    }

    private TweetsListListener listener;
}
