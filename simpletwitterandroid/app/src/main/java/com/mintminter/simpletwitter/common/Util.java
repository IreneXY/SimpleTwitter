package com.mintminter.simpletwitter.common;

import android.content.Context;

import com.mintminter.simpletwitter.R;
import com.mintminter.simpletwitter.model.Media;
import com.mintminter.simpletwitter.model.Tweet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Irene on 9/29/17.
 */

public class Util {
    public static final String PATTERN_CREATEDTIME = "EEE MMM dd HH:mm:ss Z yyyy";
    public static final String PATTERN_SHORTTIME = "MMM dd";

    public static String getString(Context context, int strId){
        return context.getResources().getString(strId);
    }

    public static String getTwitterRestCallbackUrl(Context context){
        return String.format(getString(context, R.string.fullcallback), getString(context, R.string.twitter_oauth_host),
                getString(context, R.string.twitter_oauth_scheme), context.getPackageName(), getString(context, R.string.twitter_oauth_host));
    }

    public static String genUserTwitter(String screenName){
        return "@" + screenName;
    }

    public static long getTimeDiff(long date1, long date2, TimeUnit timeUnit){
        return timeUnit.convert(date2-date1, TimeUnit.MILLISECONDS);
    }

    public static String formatCreatedTime(long createdTime){
        long currentTime = Calendar.getInstance().getTimeInMillis();
        long diff = getTimeDiff(createdTime, currentTime, TimeUnit.SECONDS);
        if(diff < 60){
            return diff + "s";
        }else{
            diff = getTimeDiff(createdTime, currentTime, TimeUnit.MINUTES);
            if(diff < 60){
                return diff + "m";
            }else{
                diff = getTimeDiff(createdTime, currentTime, TimeUnit.HOURS);
                if(diff < 24){
                    return diff + "h";
                }else{
                    return new SimpleDateFormat(Util.PATTERN_SHORTTIME).format(new Date(createdTime)).toString();
                }
            }
        }
    }

    public static String getTweetImageUrl(Tweet tweet){
        String url = "";
        if(tweet.entities != null
                && tweet.entities.media != null
                && tweet.entities.media.size() > 0){
            ArrayList<Media> medias = tweet.entities.media;
            for(Media m : medias){
                if(m.isPhoto){
                    url = m.media_url;
                    break;
                }
            }
        }
        return url;
    }
}
