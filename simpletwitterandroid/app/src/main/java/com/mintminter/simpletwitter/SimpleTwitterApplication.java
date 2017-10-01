package com.mintminter.simpletwitter;

import android.app.Application;
import android.content.Context;

import com.mintminter.simpletwitter.api.TwitterClient;

/**
 * Created by Irene on 9/30/17.
 */

public class SimpleTwitterApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        SimpleTwitterApplication.context = this;
    }

    public static TwitterClient getRestClient() {
        return (TwitterClient) TwitterClient.getInstance(TwitterClient.class, SimpleTwitterApplication.context);
    }
}
