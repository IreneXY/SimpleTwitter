package com.mintminter.simpletwitter.model;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by Irene on 9/29/17.
 */
@Parcel
public class User extends Data {
    public String name = "";
    public String id_str = "";
    public String profile_image_url_https = "";
    public String profile_image_url = "";
    public String profile_background_color = "";
    public String profile_background_image_url = "";
    public String profile_background_image_url_https = "";
    public boolean profile_background_tile = false;
    public String profile_banner_url = "";
    public Entities entities = new Entities();
    public long friends_count = 0;
    public long followers_count = 0;
    public long statuses_count = 0;

    public long id = 0;
    public String screen_name = "";
    public boolean verified = false;

    @Override
    public void fromJson(JSONObject json) {
        if(json == null || json.length() == 0){
            return;
        }
        name = json.optString("name", "");
        id_str = json.optString("id_str", "");
        profile_image_url = json.optString("profile_image_url", "");
        profile_image_url_https = json.optString("profile_image_url_https", "");
        id = json.optLong("id", -1);
        screen_name = json.optString("screen_name", "");
        verified = json.optBoolean("verified", false);
        profile_background_color = json.optString("profile_background_color", "");
        profile_background_image_url = json.optString("profile_background_image_url", "");
        profile_background_image_url_https = json.optString("profile_background_image_url_https", "");
        profile_background_tile = json.optBoolean("profile_background_tile", false);
        profile_banner_url = json.optString("profile_banner_url", "");

        JSONObject entitiesJson = json.optJSONObject("entities");
        if(entitiesJson != null && entitiesJson.length() != 0){
            entities.fromJson(entitiesJson);
        }

        statuses_count = json.optLong("statuses_count", 0);
        followers_count = json.optLong("followers_count", 0);
        friends_count = json.optLong("friends_count", 0);

    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("name", name);
            json.put("screen_name", screen_name);
            json.put("id_str", id_str);
            json.put("id", id);
            json.put("profile_image_url", profile_image_url);
            json.put("profile_image_url_https", profile_image_url_https);
            json.put("verified", verified);
            json.put("profile_background_color", profile_background_color);
            json.put("profile_background_image_url", profile_background_image_url);
            json.put("profile_background_image_url_https", profile_background_image_url_https);
            json.put("profile_background_tile", profile_background_tile);
            json.put("profile_banner_url", profile_banner_url);
            json.put("entities", entities.toJson());
            json.put("friends_count", friends_count);
            json.put("followers_count", followers_count);
            json.put("statuses_count", statuses_count);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public UserUrl getUserUrl(){
        if(entities != null && entities.urls!= null && entities.urls.size() != 0){
            return entities.urls.get(0);
        }
        return null;
    }
}
