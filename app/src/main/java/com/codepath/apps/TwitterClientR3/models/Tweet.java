package com.codepath.apps.TwitterClientR3.models;

import android.text.TextUtils;
import android.text.format.DateUtils;

import com.codepath.apps.TwitterClientR3.db.MyDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

@Table(database = MyDatabase.class)
@Parcel(analyze = Tweet.class)
public class Tweet extends BaseModel    {

    public int getRetweetCount() {
        return retweetCount;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    private int retweetCount;
    private int favoriteCount;


    public void setBody(String body) {
        this.body = body;
    }

    public String getRetweetedBy() {
        if(TextUtils.isEmpty(retweetedBy)) return "";
        return String.format("%s Retweeted",  retweetedBy);
    }

    public void setRetweetedBy(String retweetedBy) {
        this.retweetedBy = retweetedBy;
    }

    public ArrayList<Media> getMediaObjects() {
        return mediaObjects;
    }

    public void setMediaObjects(ArrayList<Media> mediaObjects) {
        this.mediaObjects = mediaObjects;
    }

    @Column
    private String retweetedBy;
    @Column
    private String body;

    public String getType() {
        if(type==null) {
            return "";
        }
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String type;

    public void setUid(long uid) {
        this.uid = uid;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Column
    @PrimaryKey
    long uid;

    @Column
    @ForeignKey(saveForeignKeyModel = false)
    User user;

    @Column
    String createdAt;

    ArrayList<Media> mediaObjects;

    ArrayList<Hashtag> hashtags;

    public ArrayList<Hashtag> getHashtags()
    {
        if(hashtags==null) {return new ArrayList<Hashtag>();}
        return hashtags;}

    ArrayList<Mention> mentions;

    public ArrayList<Mention> getMentions()
    { if(mentions==null){ return new ArrayList<Mention>();}
        return mentions;}

    public String getBody() {
        return body;
    }

    public long getUid() {
        return uid;
    }

    public User getUser() {
        return user;
    }

    public String getCreatedAt() {

        String returnDate="";
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        try {
            Date date = sf.parse(createdAt);
            DateFormat df = new SimpleDateFormat("MMM-dd HH:mm");


            returnDate = df.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return returnDate;
    }

    public String getTimestamp() {
        return getRelativeTimeAgo(createdAt);
    }

    public String getRelativeTimeAgo(String rawJsonDate) {

        String relativeDate = "";
        if(rawJsonDate==null) return relativeDate;
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);


        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS).toString();
            relativeDate = relativeDate.replace(" minutes ago","m")
                                        .replace(" minute ago","m")
                                        .replace(" hours ago","h")
                                        .replace(" hour ago","h")
                                        .replace(" days ago","d")
                                        .replace(" day ago","d");

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    public static Tweet fromJson(JSONObject jsonObj){
        Tweet tweet = new Tweet();
        JSONObject jsonObject;

        try {

            if(jsonObj.has("retweeted_status")){
                User user=User.fromJsonObject(jsonObj.getJSONObject("user"));
                tweet.retweetedBy=user.getName();
                jsonObject=jsonObj.getJSONObject("retweeted_status");
            }else{
                jsonObject=jsonObj;
            }

            String fullText =jsonObject.getString("full_text");
            JSONArray range = jsonObject.getJSONArray("display_text_range");
            tweet.body =fullText.substring((Integer)range.get(0),(Integer)range.get(1));
            tweet.uid = jsonObject.getLong("id");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.user = User.fromJsonObject(jsonObject.getJSONObject("user"));
            tweet.retweetCount=jsonObject.getInt("retweet_count");
            tweet.favoriteCount=jsonObject.getInt("favorite_count");
            tweet.type="simple";
           if(jsonObject.has("extended_entities")){
                tweet.mediaObjects = Media.fromJsonArray(jsonObject.getJSONObject("extended_entities"));
               tweet.type=tweet.mediaObjects.get(0).getType();
            }
            if(jsonObject.has("entities")){
               JSONObject entityObject = jsonObject.getJSONObject("entities");
                if(entityObject.has("hashtags")){
                    tweet.hashtags =Hashtag.fromJsonArray(entityObject.getJSONArray("hashtags"));
                }
                if(entityObject.has("user_mentions")){
                    tweet.mentions =Mention.fromJsonArray(entityObject.getJSONArray("user_mentions"));
                }

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }



        return tweet;
    }

    public static ArrayList<Tweet> fromJsonArray(JSONArray jsonArray){
        ArrayList<Tweet> tweets = new ArrayList<>();

        for (int i=0;i<jsonArray.length();i++)
        {
            try {
                JSONObject tweetJson = jsonArray.getJSONObject(i);
                Tweet tweet = Tweet.fromJson(tweetJson);

                if(tweet!=null){
                    tweets.add(tweet);
                }
            }
            catch(JSONException e)
            {
                e.printStackTrace();
                continue;
            }
        }

        return tweets;
    }

}
