package com.mintminter.simpletwitter.api;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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

    public void getHomeTimeline(int count, long since_id, long max_id, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/home_timeline.json");
        RequestParams params = new RequestParams();
        if(count > 0 && count <= Util.TWITTERCOUNT_MAX) {
            params.put("count", count);
        }else{
            params.put("count", Util.TWITTERCOUNT_MAX);
        }
        if(since_id > 0) {
            params.put("since_id", since_id);
        }
        if(max_id > 0) {
            params.put("max_id", max_id);
        }
        getClient().get(apiUrl, params, handler);
    }

    public void getCredentials(AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("account/verify_credentials.json");
        RequestParams params = new RequestParams();
        params.put("include_email", true);
        getClient().get(apiUrl, params, handler);
    }

    public void postTweet(String content, long replyId, AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status", content);
        if(replyId > 0){
            params.put("in_reply_to_status_id", replyId);
        }
        getClient().post(apiUrl, params, handler);
    }

}
