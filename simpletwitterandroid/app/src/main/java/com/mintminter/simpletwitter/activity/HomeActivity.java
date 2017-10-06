package com.mintminter.simpletwitter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mintminter.simpletwitter.R;
import com.mintminter.simpletwitter.SimpleTwitterApplication;
import com.mintminter.simpletwitter.common.Util;
import com.mintminter.simpletwitter.fragment.TimelineFragment;
import com.mintminter.simpletwitter.model.User;

import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class HomeActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageView mUserAvtar;
    private ImageView mNewTweet;
    private RelativeLayout mFragmentContainer;

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        mUserAvtar = (ImageView) findViewById(R.id.main_toolbar_avatar);
        mNewTweet = (ImageView) findViewById(R.id.main_toolbar_write);
        mNewTweet.setVisibility(View.GONE);
        mNewTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, ComposeActivity.class);
                if(mUser != null) {
                    i.putExtra(Util.EXTRA_USER, Parcels.wrap(mUser));
                }
                startActivityForResult(i,Util.REQUESTCODE_COMPOSE);
            }
        });

        mFragmentContainer = (RelativeLayout) findViewById(R.id.main_container);

        getUser();

    }

    private void addTimelineFragment(){
        FragmentManager fragMan = getSupportFragmentManager();
        FragmentTransaction fragTransaction = fragMan.beginTransaction();
        Fragment timelineFragment = TimelineFragment.newInstance(mUser);
        fragTransaction.add(mFragmentContainer.getId(), timelineFragment , TimelineFragment.TAG);
        fragTransaction.commitAllowingStateLoss();
    }

    private JsonHttpResponseHandler mCredentialHandler = new JsonHttpResponseHandler(){
        @Override
        public void onSuccess(int statusCode, Header[] headers, final JSONObject json){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mUser = new User();
                    mUser.fromJson(json);
                    Glide.with(HomeActivity.this)
                            .load(mUser.profile_image_url)
                            .apply(RequestOptions.circleCropTransform())
                            .into(mUserAvtar);
                    addTimelineFragment();
                }
            });
        }
    };

    private void getUser(){
        //TwitterClient twitterClient = (TwitterClient) TwitterClient.getInstance(TwitterClient.class, this);
        SimpleTwitterApplication.getRestClient().getCredentials(mCredentialHandler);
    }

}
