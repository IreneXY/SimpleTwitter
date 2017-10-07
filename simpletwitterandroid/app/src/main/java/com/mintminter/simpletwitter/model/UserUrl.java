package com.mintminter.simpletwitter.model;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by Irene on 10/6/17.
 */
@Parcel
public class UserUrl extends Data {
    public String display_url = "";
    public String expanded_url = "";

    @Override
    public void fromJson(JSONObject json) {
        if(json == null || json.length() == 0){
            return;
        }
        display_url = json.optString("display_url", "");
        expanded_url = json.optString("expanded_url", "");
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("display_url", display_url);
            json.put("expanded_url", expanded_url);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }
}
