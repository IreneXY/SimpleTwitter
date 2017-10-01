package com.mintminter.simpletwitter.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mintminter.simpletwitter.R;
import com.mintminter.simpletwitter.SimpleTwitterApplication;
import com.mintminter.simpletwitter.api.TwitterClient;
import com.mintminter.simpletwitter.common.Util;
import com.mintminter.simpletwitter.model.Tweet;
import com.mintminter.simpletwitter.model.User;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {

    private ImageView mClose;
    private ImageView mAvatar;
    private TextView mCount;
    private TextView mTweet;
    private EditText mEdit;

    private User mUser = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String sUser = getIntent().getStringExtra(Util.EXTRA_USER);
        if(TextUtils.isEmpty(sUser)){
            setResult(Activity.RESULT_CANCELED);
            finish();
        }

        mUser.fromString(sUser);

        setContentView(R.layout.activity_compose);

        mClose = (ImageView) findViewById(R.id.compose_close);
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });

        mAvatar = (ImageView) findViewById(R.id.compose_avatar);
        Glide.with(this)
                .load(mUser.profile_image_url)
                .apply(RequestOptions.circleCropTransform())
                .into(mAvatar);

        mCount = (TextView) findViewById(R.id.compose_count);
        mEdit = (EditText) findViewById(R.id.compose_edit);
        mEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int length = editable.length();
                if(length < 130 ){
                    mCount.setTextColor(Util.getColor(ComposeActivity.this, R.color.colorPrimary));
                }else {
                    mCount.setTextColor(Util.getColor(ComposeActivity.this, R.color.red600));
                }
                mCount.setText((140-length) + "");
            }
        });

        mTweet = (TextView) findViewById(R.id.compose_tweet);
        mTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post();
            }
        });
    }

    private void post(){
        //TwitterClient twitterClient = (TwitterClient) TwitterClient.getInstance(TwitterClient.class, this);

        SimpleTwitterApplication.getRestClient().postTweet(mEdit.getText().toString(), new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, final JSONObject json){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setPostResult(json.toString());
                    }
                });
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject){
                Log.i("Irene", "@onFailure");
            }
        });
    }

    private void setPostResult(String jsonString){
        Intent i = new Intent();
        i.putExtra(Util.EXTRA_TWEET, jsonString);
        setResult(Activity.RESULT_OK, i);
        finish();
    }
}
