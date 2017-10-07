package com.mintminter.simpletwitter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mintminter.simpletwitter.R;
import com.mintminter.simpletwitter.common.Util;
import com.mintminter.simpletwitter.model.Tweet;
import com.mintminter.simpletwitter.model.User;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailActivity extends AppCompatActivity {

    private ImageView mClose;
    private ImageView mAvatar;
    private TextView mName;
    private ImageView mVerified;
    private TextView mTwitter;
    private TextView mContent;
    private ImageView mPoster;
    private TextView mCreatedTime;
    private LinearLayout mReplyArea;
    private LinearLayout mRetweetArea;
    private TextView mRetweetCount;
    private LinearLayout mFavArea;
    private ImageView mFavIcon;
    private TextView mFavCount;
    private LinearLayout mMessageArea;

    private Tweet mTweet;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if(!getIntent().hasExtra(Util.EXTRA_TWEET) || !getIntent().hasExtra(Util.EXTRA_USER)){
            finish();
        }
        mTweet = Parcels.unwrap(getIntent().getParcelableExtra(Util.EXTRA_TWEET));
        mUser = Parcels.unwrap(getIntent().getParcelableExtra(Util.EXTRA_USER));

        mClose = (ImageView) findViewById(R.id.detail_close);
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mAvatar = (ImageView) findViewById(R.id.detail_avatar);
        Glide.with(this)
                .load(mTweet.user.profile_image_url)
                .apply(RequestOptions.circleCropTransform())
                .into(mAvatar);

        mName = (TextView) findViewById(R.id.detail_name);
        mName.setText(mTweet.user.name);
        mVerified = (ImageView) findViewById(R.id.detail_verified);
        if(mTweet.user.verified){
            mVerified.setVisibility(View.VISIBLE);
        }else{
            mVerified.setVisibility(View.GONE);
        }
        mTwitter = (TextView) findViewById(R.id.detail_twitter);
        mTwitter.setText(Util.genUserTwitter(mTweet.user.screen_name));

        mContent = (TextView) findViewById(R.id.detail_content);
        mContent.setText(mTweet.text);

        mPoster = (ImageView) findViewById(R.id.detail_image);
        String imageUrl = Util.getTweetImageUrl(mTweet);
        if(!TextUtils.isEmpty(imageUrl)){
            mPoster.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(imageUrl)
                    .into(mPoster);
        }else{
            mPoster.setVisibility(View.GONE);
        }

        mCreatedTime = (TextView) findViewById(R.id.detail_createdtime);
        mCreatedTime.setText(new SimpleDateFormat(Util.PATTERN_DETAILTIME).format(new Date(mTweet.created_at_in_ms)));

        mReplyArea = (LinearLayout) findViewById(R.id.tweet_reply);
        mRetweetArea = (LinearLayout) findViewById(R.id.tweet_retweet);
        mRetweetCount = (TextView) findViewById(R.id.tweet_retweetcount);
        mFavArea = (LinearLayout) findViewById(R.id.tweet_fav);
        mFavIcon = (ImageView) findViewById(R.id.tweet_fav_icon);
        mFavCount = (TextView) findViewById(R.id.tweet_favcount);
        mRetweetCount.setText(Util.formatCount(mTweet.retweet_count));
        if(mTweet.favorited){
            mFavIcon.setImageResource(R.mipmap.ic_heart_fill);
        }else{
            mFavIcon.setImageResource(R.mipmap.ic_heart);
        }
        mFavCount.setText(Util.formatCount(mTweet.favorite_count));
        mReplyArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openReply();
            }
        });
    }

    private void openReply(){
        Intent i = new Intent(this, ComposeActivity.class);
        i.putExtra(Util.EXTRA_TWEET, Parcels.wrap(mTweet));
        i.putExtra(Util.EXTRA_USER, Parcels.wrap(mUser));
        startActivity(i);
    }
}
