package com.mintminter.simpletwitter.interfaces;

import com.mintminter.simpletwitter.model.Tweet;

/**
 * Created by Irene on 9/30/17.
 */

public interface RequestTweetsCallback {
    void requestMoreTweets(Tweet lastTweet);
    void requestNewTweets(Tweet sinceTweet);
    void setLoadingUi();
    void openDetail(Tweet tweet);
}
