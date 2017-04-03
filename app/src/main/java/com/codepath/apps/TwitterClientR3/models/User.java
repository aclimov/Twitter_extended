package com.codepath.apps.TwitterClientR3.models;

import com.codepath.apps.TwitterClientR3.db.MyDatabase;
import com.codepath.apps.TwitterClientR3.helpers.StringHelper;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by alex_ on 3/21/2017.
 */

@Table(database = MyDatabase.class)
@Parcel(analyze = User.class)
public class User extends BaseModel {

    //List of attributes;
    @Column
    private String name;

    private String tagLine;
    private int followers;
    private int followings;

    public boolean isFollowing() {
        return isFollowing;
    }

    private boolean isFollowing;

    public String getTagLine() {
        return tagLine;
    }

    public void setTagLine(String tagLine) {
        this.tagLine = tagLine;
    }

    public String getFollowers() {
        return StringHelper.format( followers);
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public String getFollowings() {
        return StringHelper.format(followings);
    }

    public void setFollowings(int followings) {
        this.followings = followings;
    }

    public String getBackImageUrl() {
        return backImageUrl;
    }

    public void setBackImageUrl(String backImageUrl) {
        this.backImageUrl = backImageUrl;
    }

    private String backImageUrl;

    @Column
    @PrimaryKey
    private long uid;

    @Column
    private String screenName;

    public void setName(String name) {
        this.name = name;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    @Column
    private String profileImageUrl;

    public String getName() {
        return name;
    }

    public long getUid() {
        return uid;
    }

    public String getScreenName() {
        return "@" + screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl.replace("_normal", "");
    }

    //deserialize the user json=> User
    public static User fromJsonObject(JSONObject jsonObject) {
        User user = new User();

        try {
            user.name = jsonObject.getString("name");
            user.uid = jsonObject.getLong("id");
            user.screenName = jsonObject.getString("screen_name");
            user.profileImageUrl = jsonObject.getString("profile_image_url");

            user.followers = jsonObject.getInt("followers_count");
            user.followings = jsonObject.getInt("friends_count");
            user.isFollowing =jsonObject.getBoolean("following");
            user.tagLine = jsonObject.getString("description");
            if(jsonObject.has("profile_banner_url")) {
                user.backImageUrl = jsonObject.getString("profile_banner_url");
            }else {
                user.backImageUrl = jsonObject.getString("profile_background_image_url");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return user;
    }

    public static ArrayList<User> fromJsonArray(JSONArray jsonArray){
        ArrayList<User> users = new ArrayList<>();

        for (int i=0;i<jsonArray.length();i++)
        {
            try {
                JSONObject userJson = jsonArray.getJSONObject(i);
                User user = User.fromJsonObject(userJson);

                if(user!=null){
                    users.add(user);
                }
            }
            catch(JSONException e)
            {
                e.printStackTrace();
                continue;
            }
        }

        return users;
    }


}
