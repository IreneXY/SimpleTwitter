package com.mintminter.simpletwitter.model;

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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
