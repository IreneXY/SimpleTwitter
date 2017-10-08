package com.mintminter.simpletwitter.fragment;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.mintminter.simpletwitter.R;
import com.mintminter.simpletwitter.SimpleTwitterApplication;
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

public class MentionFragment extends Fragment implements RequestTweetsCallback {
    public static final String TAG = "TimelineFragment";

    private View mFragmentView;
    private ProgressDialog mProgressDialog;
    private SwipeRefreshLayout mSwipe;
    private RecyclerView mTimelineList;
    private TimelineAdapter mTimeLineAdapter = null;
    private LinearLayout mLoadingArea;

    private Tweet mPreviousLastTweet;
    private User mUser;

    public static MentionFragment newInstance(User user){
        MentionFragment fragment = new MentionFragment();
        Bundle args = new Bundle();
        args.putParcelable(Util.EXTRA_USER, Parcels.wrap(user));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUser = Parcels.unwrap(getArguments().getParcelable(Util.EXTRA_USER));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFragmentView = inflater.inflate(R.layout.fragment_mention, container, false);

        mLoadingArea = (LinearLayout) mFragmentView.findViewById(R.id.mention_loading);
        mLoadingArea.setVisibility(View.GONE);
        mSwipe = (SwipeRefreshLayout) mFragmentView.findViewById(R.id.mention_swiperefresh);
        mTimelineList = (RecyclerView) mFragmentView.findViewById(R.id.mention_timeline);
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
        getMention(Util.TWITTERCOUNT, 0, 0);
        return mFragmentView;
    }

    private JsonHttpResponseHandler mMentionHandler = new JsonHttpResponseHandler(){
        @Override
        public void onSuccess(int statusCode, Header[] headers, final JSONArray json){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new GetMentionTask(json).execute();
                }
            });
        }
    };

    private void getMention(int count, long since_id, long max_id){
        //TwitterClient twitterClient = (TwitterClient) TwitterClient.getInstance(TwitterClient.class, this);
        SimpleTwitterApplication.getRestClient().getMention(count, since_id, max_id, mMentionHandler);
        Util.setApiRequestTime(getActivity(), Util.REQUESTTYPE_MENTIONTIMELINE);
    }

    @Override
    public void requestMoreTweets(Tweet lastTweet) {
        mPreviousLastTweet = lastTweet;
        final long interval = Util.nextRequestInterval(getActivity(), Util.WINDOW_MENTION, Util.REQUESTTYPE_MENTIONTIMELINE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getMention(Util.TWITTERCOUNT, 0, mPreviousLastTweet.id);
            }
        }, interval);
    }

    @Override
    public void requestNewTweets(final Tweet sinceTweet) {
        //mUpdatingArea.setVisibility(View.VISIBLE);
        final long interval = Util.nextRequestInterval(getActivity(), Util.WINDOW_MENTION, Util.REQUESTTYPE_MENTIONTIMELINE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getMention(Util.TWITTERCOUNT, sinceTweet.id, 0);
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
        startActivity(i);
    }

    class GetMentionTask extends AsyncTask<Void, Void, Boolean> {
        private JSONArray mTweetsJsonArray;
        private ArrayList<Tweet> mTweets = new ArrayList<>();

        public GetMentionTask(JSONArray tweetsJsonArray){
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
                mTimeLineAdapter = new TimelineAdapter(getActivity(), mTweets, mUser, MentionFragment.this);
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
