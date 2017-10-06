package com.mintminter.simpletwitter.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mintminter.simpletwitter.R;
import com.mintminter.simpletwitter.fragment.MentionFragment;
import com.mintminter.simpletwitter.fragment.TimelineFragment;
import com.mintminter.simpletwitter.model.User;

/**
 * Created by Irene on 10/5/17.
 */

public class FragmentAdapter extends FragmentPagerAdapter {
    public static final int PAGE_COUNT = 2;
    public static final int[] PAGEICONS = {R.mipmap.ic_home, R.mipmap.ic_at};
    private Context mContext;
    private User mUser;

    public FragmentAdapter(Context context, FragmentManager fm, User user) {
        super(fm);
        mContext = context;
        mUser = user;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0){
            return TimelineFragment.newInstance(mUser);
        }else{
            return MentionFragment.newInstance(mUser);
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }
}
