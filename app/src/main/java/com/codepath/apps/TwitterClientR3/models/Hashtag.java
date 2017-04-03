package com.codepath.apps.TwitterClientR3.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by alex_ on 3/26/2017.
 */

@Parcel(analyze = Hashtag.class)
public class Hashtag {
    public int start;
    public int end;
    public String val;

    public static ArrayList<Hashtag> fromJsonArray(JSONArray jsonArray) {
        ArrayList<Hashtag> hashtags = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject hJson = jsonArray.getJSONObject(i);
                Hashtag tag = new Hashtag();
                tag.val = hJson.getString("text");
                tag.start = hJson.getJSONArray("indices").getInt(0);
                tag.end = hJson.getJSONArray("indices").getInt(1);
                hashtags.add(tag);
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }

        return hashtags;
    }
}


