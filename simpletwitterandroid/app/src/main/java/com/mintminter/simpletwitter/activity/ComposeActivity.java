package com.mintminter.simpletwitter.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
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
import com.mintminter.simpletwitter.common.Util;
import com.mintminter.simpletwitter.model.Tweet;
import com.mintminter.simpletwitter.model.User;

import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {

    private ImageView mClose;
    private ImageView mAvatar;
    private TextView mCount;
    private TextView mSendTweet;
    private EditText mEdit;

    private User mUser = new User();
    private Tweet mTweet = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!getIntent().hasExtra(Util.EXTRA_USER)){
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
        mUser = Parcels.unwrap(getIntent().getParcelableExtra(Util.EXTRA_USER));

        if(getIntent().hasExtra(Util.EXTRA_TWEET)){
            mTweet = Parcels.unwrap(getIntent().getParcelableExtra(Util.EXTRA_TWEET));
        }

        setContentView(R.layout.activity_compose);

        mClose = (ImageView) findViewById(R.id.compose_close);
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mEdit.getText().length() > 0) {
                    checkDrafting();
                }else {
                    setResult(Activity.RESULT_CANCELED);
                    finish();
                }
            }
        });

        mAvatar = (ImageView) findViewById(R.id.compose_avatar);
        Glide.with(this)
                .load(mUser.profile_image_url)
                .apply(RequestOptions.circleCropTransform())
                .into(mAvatar);

        mCount = (TextView) findViewById(R.id.compose_count);
        mEdit = (EditText) findViewById(R.id.compose_edit);
        String sDraft = Util.getDraft(this);
        if(mTweet != null){
            sDraft = Util.genUserTwitter(mTweet.user.screen_name) + " " + sDraft;
        }
        mEdit.setText(sDraft);
        mCount.setText((Util.CHARACTERCOUNT_MAX - sDraft.length())+"");
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

        mSendTweet = (TextView) findViewById(R.id.compose_tweet);
        mSendTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.setDraft(ComposeActivity.this, "");
                post();
            }
        });
    }

    private void checkDrafting(){
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle(Util.getString(this, R.string.draft_title))
                .setMessage(Util.getString(this, R.string.draft_des))
                .setPositiveButton(Util.getString(ComposeActivity.this, R.string.draft_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Util.setDraft(ComposeActivity.this, mEdit.getText().toString());
                        setResult(Activity.RESULT_CANCELED);
                        finish();
                    }
                })
                .setNegativeButton(Util.getString(ComposeActivity.this, R.string.draft_no), new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Util.setDraft(ComposeActivity.this, "");
                        setResult(Activity.RESULT_CANCELED);
                        finish();
                    }
                })
                .setIcon(R.mipmap.ic_alert);
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Util.getColor(ComposeActivity.this, R.color.colorPrimary));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Util.getColor(ComposeActivity.this, R.color.colorPrimary));
            }
        });
        dialog.show();
    }


    private void post(){
        //TwitterClient twitterClient = (TwitterClient) TwitterClient.getInstance(TwitterClient.class, this);
        String sContent = mEdit.getText().toString();
        long replyId = 0;
        if(mTweet != null){
            replyId = mTweet.id;
        }
        SimpleTwitterApplication.getRestClient().postTweet(sContent, replyId, new JsonHttpResponseHandler(){
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
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject json){
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
