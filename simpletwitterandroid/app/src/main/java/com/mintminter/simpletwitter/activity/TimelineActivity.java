package com.mintminter.simpletwitter.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.mintminter.simpletwitter.R;
import com.mintminter.simpletwitter.adapter.TimelineAdapter;
import com.mintminter.simpletwitter.api.TwitterClient;
import com.mintminter.simpletwitter.common.Util;
import com.mintminter.simpletwitter.model.Tweet;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TimelineActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;
    private RecyclerView mTimelineList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        mTimelineList = (RecyclerView) findViewById(R.id.main_timeline);
        mTimelineList.setLayoutManager(new LinearLayoutManager(this));

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(Util.getString(this, R.string.loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        getTimeline(30, 0, 0);
    }

    private JsonHttpResponseHandler mHttpHandler = new JsonHttpResponseHandler(){
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

    private void getTimeline(int count, long since_id, long max_id){
        TwitterClient twitterClient = (TwitterClient) TwitterClient.getInstance(TwitterClient.class, this);
        twitterClient.getHomeTimeline(count, since_id, max_id, mHttpHandler);
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
            mTimelineList.setAdapter(new TimelineAdapter(TimelineActivity.this, mTweets));
        }
    }

}
