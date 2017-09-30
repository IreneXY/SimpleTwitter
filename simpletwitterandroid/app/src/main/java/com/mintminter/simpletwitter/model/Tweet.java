package com.mintminter.simpletwitter.model;

import android.text.TextUtils;

import com.mintminter.simpletwitter.common.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by Irene on 9/29/17.
 */

public class Tweet extends Data {
    public String created_at = "";
    public long created_at_in_ms = 0;
    public String id_str = "";
    public long id = -1;
    public String text = "";
    public long retweet_count = 0;
    public long favorite_count = 0;
    public boolean favorited = false;
    public Entities entities = new Entities();
    public User user = new User();
    @Override
    public void fromJson(JSONObject json) {
        if(json == null || json.length() == 0){
            return;
        }
        created_at = json.optString("created_at", "");
        if(!TextUtils.isEmpty(created_at)){
            try {
                created_at_in_ms = new SimpleDateFormat(Util.PATTERN_CREATEDTIME).parse(created_at).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        id_str= json.optString("id_str", "");
        id = json.optLong("id", -1);
        text = json.optString("text", "");
        retweet_count = json.optLong("retweet_count", 0);
        favorite_count = json.optLong("favorite_count", 0);
        favorited = json.optBoolean("favorited", false);
        entities.fromJson(json.optJSONObject("entities"));
        user.fromJson(json.optJSONObject("user"));
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("created_at", created_at);
            json.put("id_str", id_str);
            json.put("id", id);
            json.put("text", text);
            json.put("retweet_count", retweet_count);
            json.put("favorite_count",favorite_count);
            json.put("favorited", favorited);
            json.put("entities", entities.toJson());
            json.put("user", user.toJson());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }
}
