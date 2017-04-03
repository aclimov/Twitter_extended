package com.codepath.apps.TwitterClientR3.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by alex_ on 3/24/2017.
 */
@Parcel(analyze = Media.class)
public class Media {

    long id;
    String mediaUrl;
    String url;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    String type;


    public static Media fromJson(JSONObject jsonObject) {
        Media media = new Media();

        try {
            media.id = jsonObject.getLong("id");
            media.mediaUrl = jsonObject.getString("media_url");
            media.url = jsonObject.getString("url");
            media.type = jsonObject.getString("type");
            //[video_info][variants][0][url]

            if (media.type.equals("video")) {
                JSONObject videoInfo = jsonObject.getJSONObject("video_info");
                JSONArray variants = videoInfo.getJSONArray("variants");
                media.url = ((JSONObject) variants.get(0)).getString("url");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return media;
    }

    public static ArrayList<Media> fromJsonArray(JSONObject jsonObject) {
        ArrayList<Media> mediaArr = new ArrayList<>();
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("media");
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject mediaJson = jsonArray.getJSONObject(i);
                    Media media = Media.fromJson(mediaJson);

                    if (media != null) {
                        mediaArr.add(media);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();

        }

        return mediaArr;
    }
}


