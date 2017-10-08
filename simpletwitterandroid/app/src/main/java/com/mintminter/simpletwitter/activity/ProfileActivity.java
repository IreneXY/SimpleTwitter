package com.mintminter.simpletwitter.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mintminter.simpletwitter.R;
import com.mintminter.simpletwitter.SimpleTwitterApplication;
import com.mintminter.simpletwitter.adapter.TimelineAdapter;
import com.mintminter.simpletwitter.common.Util;
import com.mintminter.simpletwitter.interfaces.RequestTweetsCallback;
import com.mintminter.simpletwitter.model.Tweet;
import com.mintminter.simpletwitter.model.User;
import com.mintminter.simpletwitter.model.UserUrl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Irene on 10/6/17.
 */

public class ProfileActivity extends AppCompatActivity implements RequestTweetsCallback {
    private LinearLayout mToolbar;
    private ImageView mClose;
    private TextView mTitle;
    private ImageView mProfileImage;
    private ImageView mProfileAvatar;
    private TextView mProfileName;
    private ImageView mProfileVerified;
    private TextView mProfileTwitter;
    private TextView mProfileLink;
    private ImageView mProfileLinkIcon;
    private TextView mProfileFollowing;
    private TextView mProfileFollower;
    private TabLayout mProfileTab;
    private RecyclerView mProfileTimeline;
    private LinearLayoutManager mLinearLayoutManager;
    private TimelineAdapter mTweetsAdapter;
    private TimelineAdapter mFavoritesAdapter;
    private NestedScrollView mProfileScrollView;

    private ProgressDialog mProgressDialog;

    private User mUser;
    private Tweet mPreviousLastTweet;
    private boolean mGettingTimeLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if(!getIntent().hasExtra(Util.EXTRA_USER)){
            finish();
        }
        mUser = Parcels.unwrap(getIntent().getParcelableExtra(Util.EXTRA_USER));

        mToolbar = (LinearLayout) findViewById(R.id.profile_toolbar);
        mToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProfileScrollView.scrollTo(0,0);
            }
        });

        mClose = (ImageView) findViewById(R.id.toolbar_back);
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mTitle = (TextView) findViewById(R.id.toolbar_title);
        mTitle.setText(mUser.name);

        mProfileImage = (ImageView) findViewById(R.id.profile_background);
        Glide.with(this)
                .load(mUser.profile_banner_url)
                .into(mProfileImage);

        mProfileAvatar = (ImageView) findViewById(R.id.profile_avatar);
        Glide.with(this)
                .load(mUser.profile_image_url)
                .apply(RequestOptions.circleCropTransform())
                .into(mProfileAvatar);

        mProfileName = (TextView) findViewById(R.id.profile_name);
        mProfileName.setText(mUser.name);

        mProfileVerified = (ImageView) findViewById(R.id.profile_verified);
        if(mUser.verified){
            mProfileVerified.setVisibility(View.VISIBLE);
        }else{
            mProfileVerified.setVisibility(View.GONE);
        }

        mProfileTwitter = (TextView) findViewById(R.id.profile_twitter);
        mProfileTwitter.setText(Util.genUserTwitter(mUser.screen_name));

        mProfileLinkIcon = (ImageView) findViewById(R.id.profile_link_icon);
        mProfileLink = (TextView) findViewById(R.id.profile_link);
        final UserUrl userUrl = mUser.getUserUrl();
        if(userUrl == null){
            mProfileLinkIcon.setVisibility(View.GONE);
            mProfileLink.setVisibility(View.GONE);
        }else{
            mProfileLinkIcon.setVisibility(View.VISIBLE);
            mProfileLink.setVisibility(View.VISIBLE);
            mProfileLink.setText(userUrl.display_url);
            mProfileLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Util.openBrowser(getApplicationContext(), userUrl.expanded_url);
                }
            });
        }

        mProfileFollowing = (TextView) findViewById(R.id.profile_following);
        mProfileFollowing.setText(mUser.friends_count + "");
        mProfileFollower = (TextView) findViewById(R.id.profile_followers);
        mProfileFollower.setText(mUser.followers_count + "");

        mProfileTab = (TabLayout) findViewById(R.id.profile_tabs);
        mProfileTab.addTab(mProfileTab.newTab().setText(Util.getString(this, R.string.profile_tweets)), true);
        mProfileTab.addTab(mProfileTab.newTab().setText(Util.getString(this, R.string.profile_photos)), false);
        mProfileTab.addTab(mProfileTab.newTab().setText(Util.getString(this, R.string.profile_likes)), false);
        mProfileTab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int index = tab.getPosition();
                Toast.makeText(getApplicationContext(), tab.getText(), Toast.LENGTH_SHORT).show();
                switch (index){
                    case 0:
                        if(mTweetsAdapter != null){
                            mProfileTimeline.setAdapter(mTweetsAdapter);
                        }else{
                            requestMoreTweets(null);
                        }
                        break;
                    case 1:

                        break;
                    case 2:
                        if(mFavoritesAdapter != null){
                            mProfileTimeline.setAdapter(mFavoritesAdapter);
                        }else{
                            requestMoreTweets(null);
                        }
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mProfileTimeline = (RecyclerView) findViewById(R.id.profile_timeline);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mProfileTimeline.setLayoutManager(mLinearLayoutManager);

        mProfileScrollView = (NestedScrollView) findViewById(R.id.profile_scrollview);
        mProfileScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                int bottom = mProfileTimeline.getBottom();
                int distanceToEnd = (mProfileTimeline.getBottom() - (v.getHeight() + scrollY));
                int currentTab = mProfileTab.getSelectedTabPosition();
                // if diff is zero, then the bottom has been reached
                if (scrollY > oldScrollY && distanceToEnd < bottom/5) {
                    switch (currentTab){
                        case 0:
                            requestMoreTweets(mTweetsAdapter.getLastTweet());
                            break;
                        case 1:

                            break;
                        case 2:
                            requestMoreTweets(mFavoritesAdapter.getLastTweet());
                            break;
                    }
                }
            }
        });

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(Util.getString(this, R.string.loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        requestMoreTweets(null);
    }

    private JsonHttpResponseHandler mTweetsTimelineHandler = new JsonHttpResponseHandler(){
        @Override
        public void onSuccess(int statusCode, Header[] headers, final JSONArray json){
            ProfileActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new GetTimelineTask(json).execute();
                }
            });
        }
    };

    private void getTweetsTimeline(long user_id, int count, long since_id, long max_id){
        if(!mGettingTimeLine) {
            mGettingTimeLine = true;
            SimpleTwitterApplication.getRestClient().getUserTimeline(user_id, count, since_id, max_id, mTweetsTimelineHandler);
            Util.setApiRequestTime(this, Util.REQUESTTYPE_USERTIMELINE);
        }
    }

    private JsonHttpResponseHandler mFavoritesTimelineHandler = new JsonHttpResponseHandler(){
        @Override
        public void onSuccess(int statusCode, Header[] headers, final JSONArray json){
            ProfileActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new GetTimelineTask(json).execute();
                }
            });
        }
    };

    private void getFavoritesTimeline(long user_id, int count, long since_id, long max_id){
        if(!mGettingTimeLine) {
            mGettingTimeLine = true;
            SimpleTwitterApplication.getRestClient().getFavoritesTimeline(user_id, count, since_id, max_id, mFavoritesTimelineHandler);
            Util.setApiRequestTime(this, Util.REQUESTTYPE_FAVORITE);
        }
    }

    @Override
    public void requestMoreTweets(final Tweet lastTweet) {
        mPreviousLastTweet = lastTweet;
        int currentTab = mProfileTab.getSelectedTabPosition();
        switch (currentTab){
            case 0:
                requestMoreTweetsTimeline();
                break;
            case 1:
                break;
            case 2:
                requestFavoritesTimeline();
                break;
        }

    }

    private void requestMoreTweetsTimeline(){
        if(mPreviousLastTweet == null){
            mProgressDialog.show();
            getTweetsTimeline(mUser.id, Util.PROFILETIMELINECOUNT, 0, 0);
        }else {
            final long interval = Util.nextRequestInterval(this, Util.WINDOW_USERTIMELINE, Util.REQUESTTYPE_USERTIMELINE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getTweetsTimeline(mUser.id, Util.PROFILETIMELINECOUNT, 0, mPreviousLastTweet.id);
                }
            }, interval);
        }
    }

    private void requestFavoritesTimeline(){
        if(mPreviousLastTweet == null){
            mProgressDialog.show();
            getFavoritesTimeline(mUser.id, Util.PROFILETIMELINECOUNT, 0, 0);
        }else {
            final long interval = Util.nextRequestInterval(this, Util.WINDOW_FAVORITE, Util.REQUESTTYPE_FAVORITE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getFavoritesTimeline(mUser.id, Util.PROFILETIMELINECOUNT, 0, mPreviousLastTweet.id);
                }
            }, interval);
        }
    }

    @Override
    public void requestNewTweets(Tweet sinceTweet) {

    }

    @Override
    public void setLoadingUi() {

    }

    @Override
    public void openDetail(Tweet tweet) {
        Intent i = new Intent(this, DetailActivity.class);
        i.putExtra(Util.EXTRA_TWEET, Parcels.wrap(tweet));
        i.putExtra(Util.EXTRA_USER, Parcels.wrap(mUser));
        startActivity(i);
    }

    class GetTimelineTask extends AsyncTask<Void, Void, Boolean> {
        private JSONArray mTweetsJsonArray;
        private ArrayList<Tweet> mTweets = new ArrayList<>();

        public GetTimelineTask(JSONArray tweetsJsonArray){
            mTweetsJsonArray = tweetsJsonArray;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            int size = mTweetsJsonArray.length();
            for(int i = 0; i < size; i++){
                try {
                    JSONObject tweetJson = (JSONObject) mTweetsJsonArray.get(i);
                    Tweet tweet = new Tweet();
                    tweet.fromJson(tweetJson);
                    mTweets.add(tweet);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            mGettingTimeLine = false;
            return true;
        }

        @Override
        protected void onPostExecute(Boolean res){
            int currentTab = mProfileTab.getSelectedTabPosition();
            switch(currentTab){
                case 0:
                    if(mTweetsAdapter == null) {
                        mTweetsAdapter = new TimelineAdapter(ProfileActivity.this, mTweets, mUser, false, ProfileActivity.this);
                        mProfileTimeline.setAdapter(mTweetsAdapter);
                    }else{
                        if(mPreviousLastTweet == null){
                            mTweetsAdapter.insertNewTweets(mTweets);
                        }else{
                            mTweetsAdapter.appendOldTweets(mTweets);
                            mPreviousLastTweet = null;
                        }
                    }
                    break;
                case 1:
                    break;
                case 2:
                    if(mFavoritesAdapter == null) {
                        mFavoritesAdapter = new TimelineAdapter(ProfileActivity.this, mTweets, mUser, false, ProfileActivity.this);
                        mProfileTimeline.setAdapter(mFavoritesAdapter);
                    }else{
                        if(mPreviousLastTweet == null){
                            mFavoritesAdapter.insertNewTweets(mTweets);
                        }else{
                            mFavoritesAdapter.appendOldTweets(mTweets);
                            mPreviousLastTweet = null;
                        }
                    }
                    break;
            }

            if(mProgressDialog.isShowing()){
                mProgressDialog.dismiss();
            }
        }
    }

}
