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
import com.mintminter.simpletwitter.model.UserUrl;

import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Irene on 10/6/17.
 */

public class ProfileActivity extends AppCompatActivity {
    private ImageView mClose;
    private TextView mTitle;
    private ImageView mProfileImage;
    private ImageView mProfileAvatar;
    private TextView mProfileName;
    private ImageView mProfileVerified;
    private TextView mProfileTwitter;
    private TextView mProfileLink;
    private ImageView mProfileLinkIcon;
    private TextView mProfileFollowing;
    private TextView mProfileFollower;

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if(!getIntent().hasExtra(Util.EXTRA_USER)){
            finish();
        }

        mUser = Parcels.unwrap(getIntent().getParcelableExtra(Util.EXTRA_USER));

        mClose = (ImageView) findViewById(R.id.toolbar_back);
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mTitle = (TextView) findViewById(R.id.toolbar_title);
        mTitle.setText(mUser.name);

        mProfileImage = (ImageView) findViewById(R.id.profile_background);
        Glide.with(this)
                .load(mUser.profile_banner_url)
                .into(mProfileImage);

        mProfileAvatar = (ImageView) findViewById(R.id.profile_avatar);
        Glide.with(this)
                .load(mUser.profile_image_url)
                .apply(RequestOptions.circleCropTransform())
                .into(mProfileAvatar);

        mProfileName = (TextView) findViewById(R.id.profile_name);
        mProfileName.setText(mUser.name);

        mProfileVerified = (ImageView) findViewById(R.id.profile_verified);
        if(mUser.verified){
            mProfileVerified.setVisibility(View.VISIBLE);
        }else{
            mProfileVerified.setVisibility(View.GONE);
        }

        mProfileTwitter = (TextView) findViewById(R.id.profile_twitter);
        mProfileTwitter.setText(Util.genUserTwitter(mUser.screen_name));

        mProfileLinkIcon = (ImageView) findViewById(R.id.profile_link_icon);
        mProfileLink = (TextView) findViewById(R.id.profile_link);
        final UserUrl userUrl = mUser.getUserUrl();
        if(userUrl == null){
            mProfileLinkIcon.setVisibility(View.GONE);
            mProfileLink.setVisibility(View.GONE);
        }else{
            mProfileLinkIcon.setVisibility(View.VISIBLE);
            mProfileLink.setVisibility(View.VISIBLE);
            mProfileLink.setText(userUrl.display_url);
            mProfileLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Util.openBrowser(getApplicationContext(), userUrl.expanded_url);
                }
            });
        }

        mProfileFollowing = (TextView) findViewById(R.id.profile_following);
        mProfileFollowing.setText(mUser.friends_count + "");
        mProfileFollower = (TextView) findViewById(R.id.profile_followers);
        mProfileFollower.setText(mUser.followers_count + "");


    }


}
