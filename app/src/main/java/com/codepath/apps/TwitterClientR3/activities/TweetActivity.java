package com.codepath.apps.TwitterClientR3.activities;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.TwitterClientR3.helpers.LinkifiedTextView;
import com.codepath.apps.TwitterClientR3.R;
import com.codepath.apps.TwitterClientR3.helpers.RoundedCornersTransformation;
import com.codepath.apps.TwitterClientR3.TwitterApp;
import com.codepath.apps.TwitterClientR3.net.TwitterClient;
import com.codepath.apps.TwitterClientR3.models.Hashtag;
import com.codepath.apps.TwitterClientR3.models.Mention;
import com.codepath.apps.TwitterClientR3.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class TweetActivity extends AppCompatActivity {

    TextView tvUserName;
    TextView tvFullName;
    TextView tvTweetDate;
    LinkifiedTextView tvBody;

    TextView tvCharsLeft;
    TextView tvReply;
    TextView tvRetweet;
    TextView tvLike;

    ImageView ivProfileImage;
    ImageView ivTweetMedia;
    Button btnSend;
    EditText etReply;
    private Tweet tweet;

    private TwitterClient client;
    Toolbar toolbar;


    private void findControls(){
        btnSend = (Button) findViewById(R.id.btnSend);

        tvUserName=(TextView)findViewById(R.id.tvDisplayName);
        tvFullName=(TextView )findViewById(R.id.tvName);
        tvTweetDate=(TextView )findViewById(R.id.tvTweetDate);
        tvReply=(TextView )findViewById(R.id.tvReply);
        tvRetweet=(TextView )findViewById(R.id.tvRetweet);
        tvLike=(TextView )findViewById(R.id.tvLike);
        tvBody=(LinkifiedTextView)findViewById(R.id.tvBody);
        ivProfileImage=(ImageView)findViewById(R.id.ivProfileImage);
        ivTweetMedia=(ImageView)findViewById(R.id.ivTweetMedia);
        etReply = (EditText) findViewById(R.id.etReply);
        tvCharsLeft=(TextView) findViewById(R.id.tvCharsLeft);
    }

    private void initControls(){
        tvUserName.setText(tweet.getUser().getName());
        tvFullName.setText(tweet.getUser().getScreenName());
        tvTweetDate.setText(tweet.getCreatedAt());

        if(!TextUtils.isEmpty(tweet.getUser().getProfileImageUrl())){


            Glide.with(TweetActivity.this).load(tweet.getUser().getProfileImageUrl())
                    .bitmapTransform(new RoundedCornersTransformation(TweetActivity.this, 15, 2))
                    .into(ivProfileImage);
        }

        if(tweet.getType().equals("photo")){
            Glide.with(TweetActivity.this).load(tweet.getMediaObjects().get(0).getMediaUrl())
                    .bitmapTransform(new RoundedCornersTransformation(TweetActivity.this, 15, 2))
                    .into(ivTweetMedia);
        }

        if(tweet.getFavoriteCount()>0){
            tvLike.setText(String.valueOf(tweet.getFavoriteCount()));
        }

        if(tweet.getRetweetCount()>0){
            tvRetweet.setText(String.valueOf(tweet.getRetweetCount()));
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        if(toolbar!=null){
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        findControls();
        tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));
        fillTweetBody(tweet.getBody());
        initControls();


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tweetBody = etReply.getText().toString();
                if(TextUtils.isEmpty(tweetBody))
                {                    Toast.makeText(TweetActivity.this ,"Empty reply? Really?", Toast.LENGTH_SHORT).show();
                }else{
                    replyToTweet(tweetBody);
                    etReply.setText("");
                }
            }
        });

        etReply.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                int charLeft = 140 - etReply.length();
                tvCharsLeft.setText(String.valueOf(charLeft));
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

         client = TwitterApp.getRestClient();
    }

    private void replyToTweet(String text) {
        Tweet newTweet = new Tweet();
        newTweet.setBody(text);
        client.replyToTweet(tweet,newTweet , new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                tvReply.setText("1");
                Toast.makeText(TweetActivity.this, "Reply was sent", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

    private void fillTweetBody(String body)
    {
        // Set item views based on your views and data model

        SpannableString ss = new SpannableString(tweet.getBody());
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                // startActivity(new Intent(MyActivity.this, NextActivity.class));
                // Toast.makeText(getContext(), "clickable span", Toast.LENGTH_LONG).show();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };

        for(Hashtag ht:tweet.getHashtags()){
            ss.setSpan(clickableSpan, ht.start, ht.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        for(Mention mt:tweet.getMentions()){
            ss.setSpan(clickableSpan, mt.start, mt.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        tvBody.setText(ss);
        tvBody.setMovementMethod(LinkMovementMethod.getInstance());
        tvBody.setHighlightColor(Color.CYAN);
    }
}
