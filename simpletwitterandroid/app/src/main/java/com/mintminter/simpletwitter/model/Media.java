package com.mintminter.simpletwitter.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Irene on 9/30/17.
 */

public class Media extends Data {
    public String type = "";
    public boolean isPhoto = false;
    public String media_url_https = "";
    public String media_url = "";
    public String id_str = "";
    public long id = 0;

    @Override
    public void fromJson(JSONObject json) {
        if(json == null || json.length() == 0){
            return;
        }
        type = json.optString("type", "");
        if(type.toLowerCase().equals("photo")){
            isPhoto = true;
        }
        media_url_https = json.optString("media_url_https", "");
        media_url = json.optString("media_url", "");
        id_str = json.optString("id_str", "");
        id = json.optLong("id", 0);
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("type", type);
            json.put("media_url_https", media_url_https);
            json.put("media_url", media_url);
            json.put("id_str", id_str);
            json.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
