package com.codepath.apps.TwitterClientR3.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;


@Parcel(analyze = Mention.class)
public class Mention {
    public int start;
    public int end;
    public String val;
    public static ArrayList<Mention> fromJsonArray(JSONArray jsonArray) {
        ArrayList<Mention> mentions = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject mJson = jsonArray.getJSONObject(i);
                Mention tag = new Mention();
                tag.val = mJson .getString("screen_name");
                tag.start = mJson .getJSONArray("indices").getInt(0);
                tag.end = mJson .getJSONArray("indices").getInt(1);
                mentions.add(tag);
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }

        return mentions;
    }
}
