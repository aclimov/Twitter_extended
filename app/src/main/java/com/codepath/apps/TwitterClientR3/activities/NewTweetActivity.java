package com.codepath.apps.TwitterClientR3.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.TwitterClientR3.R;
import com.codepath.apps.TwitterClientR3.helpers.RoundedCornersTransformation;
import com.codepath.apps.TwitterClientR3.TwitterApp;
import com.codepath.apps.TwitterClientR3.net.TwitterClient;
import com.codepath.apps.TwitterClientR3.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import cz.msebera.android.httpclient.Header;

public class NewTweetActivity extends AppCompatActivity {

    private final String FILE_NAME = "draft.txt";
    private TwitterClient client;

    EditText editText;
    TextView tvCharsLeft;
    Button button;
    ImageView ivProfileImage;
    ImageView ivClose;

    DialogInterface.OnClickListener dialogClickListener;

    private void findControls() {
        button = (Button) findViewById(R.id.button);
        tvCharsLeft = (TextView) findViewById(R.id.textView);
        editText = (EditText) findViewById(R.id.etBody);
        ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        ivClose = (ImageView) findViewById(R.id.ivClose);
    }

    private void attachListeners() {
        //prepare dialog for exit
        dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        String tweetBody = editText.getText().toString();
                        writeDraftTweet(tweetBody);

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        writeDraftTweet("");
                        break;
                }

                finish();
            }
        };

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                int charLeft = 140 - editText.length();
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
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusUpdate();
                writeDraftTweet("");
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_tweet);
        findControls();


        String draftData = readDraftData();
        if(!TextUtils.isEmpty(draftData))
        {
            editText.setText(draftData);
            int charLeft = 140 - editText.length();
            tvCharsLeft.setText(String.valueOf(charLeft));
        }

        client = TwitterApp.getRestClient();

        //retrieve user image from shared settings
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String usernameUrl = pref.getString("userUrl", "");

        if (!TextUtils.isEmpty(usernameUrl)) {
            Glide.with(NewTweetActivity.this).load(usernameUrl)
                    .bitmapTransform(new RoundedCornersTransformation(NewTweetActivity.this, 15, 2))
                    .into(ivProfileImage);
        }

        attachListeners();

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeWindow();
            }
        });
    }

    private void closeWindow() {
        if(editText!=null&&!TextUtils.isEmpty(editText.getText().toString())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(NewTweetActivity.this);
            builder.setMessage("Do you want save text as draft").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }else
        {
            finish();
        }
    }

    private void statusUpdate() {
        Tweet tweet = new Tweet();
        tweet.setBody(editText.getText().toString());
        client.createTweet(tweet, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Tweet newTweet = Tweet.fromJson(response);
                //return Tweet object back to timeline
                //it now has Ids and can be inserted into mTweets list

                Intent data = new Intent();
                // Pass relevant data back as a result
                data.putExtra("tweet", Parcels.wrap(newTweet));

                // Activity finished ok, return the data
                setResult(RESULT_OK, data); // set result code and bundle data for response
                finish(); // closes the activity, pass data to parent
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });

    }

    private String readDraftData() {
        String line;
        StringBuffer buffer = new StringBuffer();
        BufferedReader input = null;
        try {
            input = new BufferedReader(
                    new InputStreamReader(openFileInput(FILE_NAME)));

            while ((line = input.readLine()) != null) {
                buffer.append(line + "\n");
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return buffer.toString();

    }

    private void writeDraftTweet(String tweetBody) {

        FileOutputStream fos = null;
        try {
            fos = openFileOutput(FILE_NAME, MODE_WORLD_WRITEABLE);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
            writer.write(tweetBody);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
