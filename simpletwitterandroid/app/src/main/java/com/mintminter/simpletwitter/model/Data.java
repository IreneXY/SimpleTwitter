package com.mintminter.simpletwitter.model;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Irene on 9/29/17.
 */

public abstract class Data {

    public abstract void fromJson(JSONObject json);

    public void fromString(String jsonString) {
        if(TextUtils.isEmpty(jsonString)){
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            fromJson(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public abstract JSONObject toJson();

    @Override
    public String toString(){
         JSONObject json = toJson();
        if(json != null){
            return json.toString();
        }else{
            return null;
        }
    }
}
