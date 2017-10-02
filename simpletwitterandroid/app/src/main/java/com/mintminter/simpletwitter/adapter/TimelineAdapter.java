package com.mintminter.simpletwitter.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mintminter.simpletwitter.R;
import com.mintminter.simpletwitter.activity.DetailActivity;
import com.mintminter.simpletwitter.common.Util;
import com.mintminter.simpletwitter.interfaces.RequestTweetsCallback;
import com.mintminter.simpletwitter.model.Tweet;

import org.parceler.Parcel;
import org.parceler.Parcels;

import java.util.ArrayList;

/**
 * Created by Irene on 9/30/17.
 */

public class TimelineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<Tweet> mTweets = new ArrayList<>();
    private RequestTweetsCallback mRequestTweetsCallback;

    public TimelineAdapter(Context context, ArrayList<Tweet> tweets, RequestTweetsCallback callback){
        mContext = context;
        mTweets = tweets;
        mRequestTweetsCallback = callback;
    }

    public void insertNewTweets(ArrayList<Tweet> newTweets){
        mTweets.addAll(0,newTweets);
        notifyDataSetChanged();
    }

    public void appendOldTweets(ArrayList<Tweet> newTweets){
        mTweets.addAll(newTweets);
        notifyDataSetChanged();
    }

    public Tweet getFirstTweet(){
        return mTweets.get(0);
    }

    public Tweet getLastTweet(){
        return mTweets.get(mTweets.size()-1);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TweetViewHolder((LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tweet, parent, false)));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((TweetViewHolder) holder).bind(position);
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    class TweetViewHolder extends RecyclerView.ViewHolder{
        private ImageView mAvatar;
        private TextView mUsername;
        private ImageView mVerified;
        private TextView mUserTwitter;
        private TextView mTime;
        private TextView mContent;
        private ImageView mBanner;
        private LinearLayout mReplyArea;
        private LinearLayout mRetweetArea;
        private TextView mRetweetCount;
        private LinearLayout mFavArea;
        private ImageView mFavIcon;
        private TextView mFavCount;
        private LinearLayout mMessageArea;
        private View mItemView;

        public TweetViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            mAvatar = (ImageView) itemView.findViewById(R.id.tweet_avatar);
            mUsername = (TextView) itemView.findViewById(R.id.tweet_username);
            mVerified = (ImageView) itemView.findViewById(R.id.tweet_verified);
            mUserTwitter = (TextView) itemView.findViewById(R.id.tweet_address);
            mTime = (TextView) itemView.findViewById(R.id.tweet_time);
            mContent = (TextView) itemView.findViewById(R.id.tweet_text);
            mBanner = (ImageView) itemView.findViewById(R.id.tweet_image);
            mReplyArea = (LinearLayout) itemView.findViewById(R.id.tweet_reply);
            mRetweetArea = (LinearLayout) itemView.findViewById(R.id.tweet_retweet);
            mRetweetCount = (TextView) itemView.findViewById(R.id.tweet_retweetcount);
            mFavArea = (LinearLayout) itemView.findViewById(R.id.tweet_fav);
            mFavIcon = (ImageView) itemView.findViewById(R.id.tweet_fav_icon);
            mFavCount = (TextView) itemView.findViewById(R.id.tweet_favcount);
            mMessageArea = (LinearLayout) itemView.findViewById(R.id.tweet_message);
        }

        public void bind(int position){
            final Tweet tweet = mTweets.get(position);
            mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mRequestTweetsCallback.openDetail(tweet);
                }
            });
            Glide.with(mContext)
                    .load(tweet.user.profile_image_url)
                    .apply(RequestOptions.circleCropTransform())
                    .into(mAvatar);
            mUsername.setText(tweet.user.name);
            if(tweet.user.verified){
                mVerified.setVisibility(View.VISIBLE);
            }else{
                mVerified.setVisibility(View.GONE);
            }
            mUserTwitter.setText(Util.genUserTwitter(tweet.user.screen_name));
            mTime.setText(Util.formatCreatedTime(tweet.created_at_in_ms));
            mContent.setText(tweet.text);
            String imageUrl = Util.getTweetImageUrl(tweet);
            if(!TextUtils.isEmpty(imageUrl)){
                mBanner.setVisibility(View.VISIBLE);
                Glide.with(mContext)
                        .load(imageUrl)
                        .into(mBanner);
            }else{
                mBanner.setVisibility(View.GONE);
            }
            mRetweetCount.setText(Util.formatCount(tweet.retweet_count));
            if(tweet.favorited){
                mFavIcon.setImageResource(R.mipmap.ic_heart_fill);
            }else{
                mFavIcon.setImageResource(R.mipmap.ic_heart);
            }
            mFavCount.setText(Util.formatCount(tweet.favorite_count));
            mRetweetArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            mFavArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            mMessageArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            if(position == mTweets.size() - 5){
                mRequestTweetsCallback.requestMoreTweets(getLastTweet());
            }

            if(position == mTweets.size() - 1){
                mRequestTweetsCallback.setLoadingUi();
            }
        }
    }
}
