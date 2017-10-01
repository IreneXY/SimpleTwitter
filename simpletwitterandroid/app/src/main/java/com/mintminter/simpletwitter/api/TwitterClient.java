package com.mintminter.simpletwitter.api;

import android.content.Context;
import android.os.AsyncTask;

import com.codepath.oauth.OAuthBaseClient;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.api.BaseApi;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mintminter.simpletwitter.BuildConfig;
import com.mintminter.simpletwitter.R;
import com.mintminter.simpletwitter.common.Util;

/**
 * Created by Irene on 9/29/17.
 */

public class TwitterClient extends OAuthBaseClient {
    public static final BaseApi REST_API_INSTANCE = TwitterApi.instance();
    public static final String REST_URL = "https://api.twitter.com/1.1";
    public static final String REST_CONSUMER_KEY = BuildConfig.TWITTERAPIKEY;
    public static final String REST_CONSUMER_SECRET = BuildConfig.TWITTERAPISECRET;

    public TwitterClient(Context context) {
        super(context, REST_API_INSTANCE,
                REST_URL,
                REST_CONSUMER_KEY,
                REST_CONSUMER_SECRET,
                Util.getTwitterRestCallbackUrl(context));
    }
    // ENDPOINTS BELOW

    public void getHomeTimeline(int count, long since_id, long max_id, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/home_timeline.json");
        RequestParams params = new RequestParams();
        if(count > 0 && count <= 200) {
            params.put("count", count);
        }
        if(since_id > 0) {
            params.put("since_id", since_id);
        }
        if(max_id > 0) {
            params.put("max_id", max_id);
        }
        client.get(apiUrl, params, handler);
    }

}
