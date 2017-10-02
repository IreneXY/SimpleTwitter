package com.mintminter.simpletwitter.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mintminter.simpletwitter.R;
import com.mintminter.simpletwitter.SimpleTwitterApplication;
import com.mintminter.simpletwitter.adapter.TimelineAdapter;
import com.mintminter.simpletwitter.api.TwitterClient;
import com.mintminter.simpletwitter.common.Util;
import com.mintminter.simpletwitter.interfaces.RequestTweetsCallback;
import com.mintminter.simpletwitter.model.Tweet;
import com.mintminter.simpletwitter.model.User;

import cz.msebera.android.httpclient.Header;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

public class TimelineActivity extends AppCompatActivity implements RequestTweetsCallback{

    private ProgressDialog mProgressDialog;
    private Toolbar mToolbar;
    private ImageView mUserAvtar;
    private ImageView mNewTweet;
    private FloatingActionButton mWrite;
    private SwipeRefreshLayout mSwipe;
    private RecyclerView mTimelineList;
    private TimelineAdapter mTimeLineAdapter = null;
    private LinearLayout mLoadingArea;
    private LinearLayout mUpdatingArea;
    private Tweet mPreviousLastTweet;

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        mUserAvtar = (ImageView) findViewById(R.id.main_toolbar_avatar);
        mNewTweet = (ImageView) findViewById(R.id.main_toolbar_write);
        mNewTweet.setVisibility(View.GONE);
        mNewTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(TimelineActivity.this, ComposeActivity.class);
                if(mUser != null) {
                    i.putExtra(Util.EXTRA_USER, Parcels.wrap(mUser));
                }
                startActivityForResult(i,Util.REQUESTCODE_COMPOSE);
            }
        });
        mWrite = (FloatingActionButton) findViewById(R.id.main_write);
        mWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(TimelineActivity.this, ComposeActivity.class);
                if(mUser != null) {
                    i.putExtra(Util.EXTRA_USER, Parcels.wrap(mUser));
                }
                startActivityForResult(i,Util.REQUESTCODE_COMPOSE);
            }
        });

        mLoadingArea = (LinearLayout) findViewById(R.id.main_loading);
        mLoadingArea.setVisibility(View.GONE);
        mUpdatingArea = (LinearLayout) findViewById(R.id.main_updating);
        mUpdatingArea.setVisibility(View.GONE);
        mSwipe = (SwipeRefreshLayout) findViewById(R.id.main_swiperefresh);
        mTimelineList = (RecyclerView) findViewById(R.id.main_timeline);
        mTimelineList.setLayoutManager(new LinearLayoutManager(this));

        mSwipe.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        mSwipe.setRefreshing(true);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(mTimeLineAdapter != null){
                                    requestNewTweets(mTimeLineAdapter.getFirstTweet());
                                }
                            }
                        });
                    }
                }
        );


        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(Util.getString(this, R.string.loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        getUser();
        getTimeline(Util.TWITTERCOUNT, 0, 0);
    }

    private JsonHttpResponseHandler mTimelineHandler = new JsonHttpResponseHandler(){
        @Override
        public void onSuccess(int statusCode, Header[] headers, final JSONArray json){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new GetTweetsTask(json).execute();
                }
            });
        }
    };

    private JsonHttpResponseHandler mCredentialHandler = new JsonHttpResponseHandler(){
        @Override
        public void onSuccess(int statusCode, Header[] headers, final JSONObject json){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mUser = new User();
                    mUser.fromJson(json);
                    Glide.with(TimelineActivity.this)
                            .load(mUser.profile_image_url)
                            .apply(RequestOptions.circleCropTransform())
                            .into(mUserAvtar);
                }
            });
        }
    };

    private void getTimeline(int count, long since_id, long max_id){
        //TwitterClient twitterClient = (TwitterClient) TwitterClient.getInstance(TwitterClient.class, this);
        SimpleTwitterApplication.getRestClient().getHomeTimeline(count, since_id, max_id, mTimelineHandler);
        Util.setApiRequestTime(this);
    }

    private void getUser(){
        //TwitterClient twitterClient = (TwitterClient) TwitterClient.getInstance(TwitterClient.class, this);
        SimpleTwitterApplication.getRestClient().getCredentials(mCredentialHandler);
    }

    @Override
    public void requestMoreTweets(Tweet lastTweet) {
        mPreviousLastTweet = lastTweet;
        final long interval = Util.nextRequestInterval(this);
        Log.i("Irene", "@requestMoreTweets interval = " + interval);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getTimeline(Util.TWITTERCOUNT, 0, mPreviousLastTweet.id);
            }
        }, interval);
    }

    @Override
    public void requestNewTweets(final Tweet sinceTweet) {
        //mUpdatingArea.setVisibility(View.VISIBLE);
        final long interval = Util.nextRequestInterval(this);
        Log.i("Irene", "@requestNewTweets interval = " + interval);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getTimeline(Util.TWITTERCOUNT, sinceTweet.id, 0);
            }
        }, interval);
    }

    @Override
    public void setLoadingUi() {
        Log.i("Irene", "@setLoadingUi");
        mLoadingArea.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("Irene", "requestCode = " + requestCode + " resultCode = " + resultCode);
        if (requestCode == Util.REQUESTCODE_COMPOSE) {
            if (resultCode == RESULT_OK) {
                String sTweet = data.getStringExtra(Util.EXTRA_TWEET);
                if(!TextUtils.isEmpty(sTweet)){
                    Tweet tweet = new Tweet();
                    tweet.fromString(sTweet);
                    ArrayList<Tweet> tweets = new ArrayList<>();
                    tweets.add(tweet);
                    if(mTimeLineAdapter != null){
                        mTimeLineAdapter.insertNewTweets(tweets);
                    }
                }
            }
        }
    }

    class GetTweetsTask extends AsyncTask<Void, Void, Boolean>{
        private JSONArray mTweetsJsonArray;
        private ArrayList<Tweet> mTweets = new ArrayList<>();

        public GetTweetsTask(JSONArray tweetsJsonArray){
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
            return true;
        }

        @Override
        protected void onPostExecute(Boolean res){
            if(mProgressDialog.isShowing()){
                mProgressDialog.dismiss();
            }
            mLoadingArea.setVisibility(View.GONE);
            if(mTimeLineAdapter == null) {
                mTimeLineAdapter = new TimelineAdapter(TimelineActivity.this, mTweets, TimelineActivity.this);
                mTimelineList.setAdapter(mTimeLineAdapter);
            }else{
                if(mPreviousLastTweet == null){
                    mTimeLineAdapter.insertNewTweets(mTweets);
                }else{
                    mSwipe.setRefreshing(false);
                    mTimeLineAdapter.appendOldTweets(mTweets);
                    mPreviousLastTweet = null;
                }
            }
        }
    }

}
