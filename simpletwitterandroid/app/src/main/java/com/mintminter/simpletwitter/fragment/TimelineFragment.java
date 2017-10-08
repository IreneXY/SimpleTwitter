package com.mintminter.simpletwitter.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.mintminter.simpletwitter.R;
import com.mintminter.simpletwitter.SimpleTwitterApplication;
import com.mintminter.simpletwitter.activity.ComposeActivity;
import com.mintminter.simpletwitter.activity.DetailActivity;
import com.mintminter.simpletwitter.adapter.TimelineAdapter;
import com.mintminter.simpletwitter.common.Util;
import com.mintminter.simpletwitter.interfaces.RequestTweetsCallback;
import com.mintminter.simpletwitter.model.Tweet;
import com.mintminter.simpletwitter.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Irene on 10/5/17.
 */

public class TimelineFragment extends Fragment implements RequestTweetsCallback {

    public static final String TAG = "TimelineFragment";

    private View mFragmentView;
    private ProgressDialog mProgressDialog;
    private FloatingActionButton mWrite;
    private SwipeRefreshLayout mSwipe;
    private RecyclerView mTimelineList;
    private TimelineAdapter mTimeLineAdapter = null;
    private LinearLayout mLoadingArea;

    private Tweet mPreviousLastTweet;
    private User mUser;

    private static TimelineFragment mFragment;

    public static TimelineFragment getInstance(User user){
        if(mFragment == null) {
            mFragment = new TimelineFragment();
            Bundle args = new Bundle();
            args.putParcelable(Util.EXTRA_USER, Parcels.wrap(user));
            mFragment.setArguments(args);
        }

        return mFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUser = Parcels.unwrap(getArguments().getParcelable(Util.EXTRA_USER));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFragmentView = inflater.inflate(R.layout.fragment_timeline, container, false);
        mWrite = (FloatingActionButton) mFragmentView.findViewById(R.id.main_write);
        mWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), ComposeActivity.class);
                if(mUser != null) {
                    i.putExtra(Util.EXTRA_USER, Parcels.wrap(mUser));
                }
                startActivityForResult(i,Util.REQUESTCODE_COMPOSE);
            }
        });

        mLoadingArea = (LinearLayout) mFragmentView.findViewById(R.id.main_loading);
        mLoadingArea.setVisibility(View.GONE);
        mSwipe = (SwipeRefreshLayout) mFragmentView.findViewById(R.id.main_swiperefresh);
        mTimelineList = (RecyclerView) mFragmentView.findViewById(R.id.main_timeline);
        mTimelineList.setLayoutManager(new LinearLayoutManager(getActivity()));

        mSwipe.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mSwipe.setRefreshing(true);
                                if(mTimeLineAdapter != null){
                                    requestNewTweets(mTimeLineAdapter.getFirstTweet());
                                }else{
                                    mSwipe.setRefreshing(false);
                                }
                            }
                        });
                    }
                }
        );


        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(Util.getString(getActivity(), R.string.loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        getTimeline(Util.TWITTERCOUNT, 0, 0);
        return mFragmentView;
    }

    private JsonHttpResponseHandler mTimelineHandler = new JsonHttpResponseHandler(){
        @Override
        public void onSuccess(int statusCode, Header[] headers, final JSONArray json){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new GetTweetsTask(json).execute();
                }
            });
        }
    };

    private void getTimeline(int count, long since_id, long max_id){
        //TwitterClient twitterClient = (TwitterClient) TwitterClient.getInstance(TwitterClient.class, this);
        SimpleTwitterApplication.getRestClient().getHomeTimeline(count, since_id, max_id, mTimelineHandler);
        Util.setApiRequestTime(getActivity(), Util.REQUESTTYPE_HOMETIMELINE);
    }

    @Override
    public void requestMoreTweets(Tweet lastTweet) {
        mPreviousLastTweet = lastTweet;
        final long interval = Util.nextRequestInterval(getActivity(), Util.WINDOW_TIMELINE, Util.REQUESTTYPE_HOMETIMELINE);
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
        final long interval = Util.nextRequestInterval(getActivity(), Util.WINDOW_TIMELINE, Util.REQUESTTYPE_HOMETIMELINE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getTimeline(Util.TWITTERCOUNT, sinceTweet.id, 0);
            }
        }, interval);
    }

    @Override
    public void setLoadingUi() {
        mLoadingArea.setVisibility(View.VISIBLE);
    }

    @Override
    public void openDetail(Tweet tweet) {
        Intent i = new Intent(getActivity(), DetailActivity.class);
        i.putExtra(Util.EXTRA_TWEET, Parcels.wrap(tweet));
        i.putExtra(Util.EXTRA_USER, Parcels.wrap(mUser));
        startActivityForResult(i, Util.REQUESTCODE_COMPOSE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Util.REQUESTCODE_COMPOSE) {
            if (resultCode == Activity.RESULT_OK) {
                String sTweet = data.getStringExtra(Util.EXTRA_TWEET);
                if(!TextUtils.isEmpty(sTweet)){
                    Tweet tweet = new Tweet();
                    tweet.fromString(sTweet);
                    ArrayList<Tweet> tweets = new ArrayList<>();
                    tweets.add(tweet);
                    if(mTimeLineAdapter != null){
                        mTimeLineAdapter.insertNewTweets(tweets);
                        mTimelineList.scrollToPosition(0);
                    }
                }
            }
        }
    }

    class GetTweetsTask extends AsyncTask<Void, Void, Boolean> {
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
            mSwipe.setRefreshing(false);
            if(mTimeLineAdapter == null) {
                mTimeLineAdapter = new TimelineAdapter(getActivity(), mTweets, mUser, TimelineFragment.this);
                mTimelineList.setAdapter(mTimeLineAdapter);
            }else{
                if(mPreviousLastTweet == null){
                    mTimeLineAdapter.insertNewTweets(mTweets);
                }else{
                    mTimeLineAdapter.appendOldTweets(mTweets);
                    mPreviousLastTweet = null;
                }
            }
        }
    }
}
