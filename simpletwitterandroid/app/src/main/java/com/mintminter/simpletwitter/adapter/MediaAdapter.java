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
import com.mintminter.simpletwitter.activity.ProfileActivity;
import com.mintminter.simpletwitter.common.Util;
import com.mintminter.simpletwitter.interfaces.RequestTweetsCallback;
import com.mintminter.simpletwitter.model.Tweet;
import com.mintminter.simpletwitter.model.User;

import org.parceler.Parcels;

import java.util.ArrayList;

/**
 * Created by Irene on 10/7/17.
 */

public class MediaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<Tweet> mTweets = new ArrayList<>();
    private ArrayList<Tweet> mMediaTweets = new ArrayList<>();
    private RequestTweetsCallback mRequestTweetsCallback;
    private User mCurrentUser;
    private boolean mAutoReload = true;

    public MediaAdapter(Context context, ArrayList<Tweet> tweets, User currentUser, RequestTweetsCallback callback){
        this(context, tweets, currentUser, true, callback);
    }

    public MediaAdapter(Context context, ArrayList<Tweet> tweets, User currentUser, boolean autoReload, RequestTweetsCallback callback){
        mContext = context;
        mTweets = tweets;
        mRequestTweetsCallback = callback;
        mCurrentUser = currentUser;
        mAutoReload = autoReload;
        extractMediaTweets(mTweets);
    }

    public boolean extractMediaTweets(ArrayList<Tweet> tweets){
        boolean res = false;
        for(Tweet t : tweets){
            String imageUrl = Util.getTweetImageUrl(t);
            if(!TextUtils.isEmpty(imageUrl)){
                mMediaTweets.add(t);
                res = true;
            }
        }
        return res;
    }

    public void insertNewTweets(ArrayList<Tweet> newTweets){
        mTweets.addAll(0,newTweets);
        extractMediaTweets(newTweets);
        notifyDataSetChanged();
    }

    public void appendOldTweets(ArrayList<Tweet> newTweets){
        mTweets.addAll(newTweets);
        extractMediaTweets(newTweets);
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
        return new MediaViewHolder((LayoutInflater.from(parent.getContext()).inflate(R.layout.item_media, parent, false)));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((MediaViewHolder) holder).bind(position);
    }

    @Override
    public int getItemCount() {
        return mMediaTweets.size();
    }

    class MediaViewHolder extends RecyclerView.ViewHolder{

        private ImageView mBanner;
        private LinearLayout mReplyArea;
        private LinearLayout mRetweetArea;
        private TextView mRetweetCount;
        private LinearLayout mFavArea;
        private ImageView mFavIcon;
        private TextView mFavCount;
        private LinearLayout mMessageArea;
        private ImageView mMessageIcon;
        private View mItemView;

        public MediaViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            mBanner = (ImageView) itemView.findViewById(R.id.tweet_image);
            mReplyArea = (LinearLayout) itemView.findViewById(R.id.tweet_reply);
            mRetweetArea = (LinearLayout) itemView.findViewById(R.id.tweet_retweet);
            mRetweetCount = (TextView) itemView.findViewById(R.id.tweet_retweetcount);
            mFavArea = (LinearLayout) itemView.findViewById(R.id.tweet_fav);
            mFavIcon = (ImageView) itemView.findViewById(R.id.tweet_fav_icon);
            mFavCount = (TextView) itemView.findViewById(R.id.tweet_favcount);
            mMessageArea = (LinearLayout) itemView.findViewById(R.id.tweet_message);
            mMessageIcon = (ImageView) itemView.findViewById(R.id.tweet_message_icon);
        }

        public void bind(int position){
            final Tweet tweet = mMediaTweets.get(position);
            mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mRequestTweetsCallback.openDetail(tweet);
                }
            });

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

            if(mCurrentUser.id == tweet.user.id){
                mMessageIcon.setImageResource(R.mipmap.ic_analytics);
            }else{
                mMessageIcon.setImageResource(R.mipmap.ic_message);
            }

            if(mAutoReload) {

                if (position == mTweets.size() - 5) {
                    mRequestTweetsCallback.requestMoreTweets(getLastTweet());
                }

                if (position == mTweets.size() - 1) {
                    mRequestTweetsCallback.setLoadingUi();
                }
            }
        }
    }
}
