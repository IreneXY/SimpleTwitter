package com.mintminter.simpletwitter.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Irene on 9/30/17.
 */

public class Entities extends Data {
    public ArrayList<Media> media = new ArrayList<>();

    @Override
    public void fromJson(JSONObject json) {
        if(json == null || json.length() == 0){
            return;
        }
        JSONArray mediaArray = json.optJSONArray("media");
        if(mediaArray != null && mediaArray.length() > 0){
            for(int i = 0; i < mediaArray.length(); i++){
                try {
                    JSONObject j = (JSONObject) mediaArray.get(i);
                    Media m = new Media();
                    m.fromJson(j);
                    media.add(m);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        JSONArray mediaArray = new JSONArray();
        for(int i = 0; i < media.size(); i++){
            mediaArray.put(media.get(i).toJson());
        }
        try {
            json.put("media", mediaArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
