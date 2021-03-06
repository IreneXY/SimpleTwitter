package com.mintminter.simpletwitter.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.format.DateUtils;

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
    public static final String PATTERN_SHORTTIME_YEAR = "MMM dd, yyyy";
    public static final String PATTERN_DETAILTIME = "HH:mm:ss MMM dd, yyyy";

    public static final String SETTINGS = "settings";
    public static final String SETTINGSKEY_REQUESTTIME = "requesttime";

    public static final String EXTRA_USER = "user";
    public static final String EXTRA_TWEET = "tweet";

    public static final String KEY_DRAFT = "draft";

    public static final int TWITTERCOUNT = 200;
    public static final int TWITTERCOUNT_MAX = 200;
    public static final int PROFILETIMELINECOUNT = 10;

    public static final int CHARACTERCOUNT_MAX = 140;

    public static final int REQUESTCODE_COMPOSE = 1;

    public static final int WINDOW_TIMELINE = 60;
    public static final int WINDOW_MENTION = 12;
    public static final int WINDOW_USERTIMELINE = 1;
    public static final int WINDOW_FAVORITE = 12;

    public static final String REQUESTTYPE_HOMETIMELINE= "HOMETIMELINE";
    public static final String REQUESTTYPE_MENTIONTIMELINE= "MENTIONTIMELINE";
    public static final String REQUESTTYPE_USERTIMELINE= "USERTIMELINE";
    public static final String REQUESTTYPE_FAVORITE = "FAVORITE";

    public static int getColor(Context context, int colorId){
        return context.getResources().getColor(colorId);
    }

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
                    Calendar create = Calendar.getInstance();
                    create.setTimeInMillis(createdTime);
                    Calendar current = Calendar.getInstance();
                    if(create.get(Calendar.YEAR) == current.get(Calendar.YEAR)) {
                        return new SimpleDateFormat(Util.PATTERN_SHORTTIME).format(new Date(createdTime)).toString();
                    }else{
                        return new SimpleDateFormat(Util.PATTERN_SHORTTIME_YEAR).format(new Date(createdTime)).toString();
                    }
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

    public static long getLongValue(Context context, String key){
        SharedPreferences settings = context.getSharedPreferences(SETTINGS, 0);
        return settings.getLong(key, Calendar.getInstance().getTimeInMillis());
    }

    public static void setLongValue(Context context, String key, long value){
        SharedPreferences settings = context.getSharedPreferences(SETTINGS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static String getStringValue(Context context, String key){
        SharedPreferences settings = context.getSharedPreferences(SETTINGS, 0);
        return settings.getString(key, "");
    }

    public static void setStringValue(Context context, String key, String value){
        SharedPreferences settings = context.getSharedPreferences(SETTINGS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getDraft(Context context){
        return getStringValue(context, KEY_DRAFT);
    }

    public static void setDraft(Context context, String draft){
        setStringValue(context, KEY_DRAFT, draft);
    }

    public static void setApiRequestTime(Context context, String type){
        setLongValue(context, SETTINGSKEY_REQUESTTIME+type, Calendar.getInstance().getTimeInMillis());
    }

    public static long getApiRequestTime(Context context, String type){
        return getLongValue(context, SETTINGSKEY_REQUESTTIME+type);
    }

    public static long nextRequestInterval(Context context, int windowSeconds, String type){
        long lastRequestTime = getApiRequestTime(context, type);
        long currentTime = Calendar.getInstance().getTimeInMillis();
        long interval = windowSeconds*1000 + 100 - getTimeDiff(lastRequestTime, currentTime, TimeUnit.MILLISECONDS);
        return interval > 0 ? interval : 0;
    }

    public static String formatCount(long count){
        if(count < 1000){
            return count + "";
        }else{
            if(count < 1000000){
                return count/1000 + "K";
            }else{
                return count/1000000 + "M";
            }
        }
    }

    public static void openBrowser(Context context, String sUrl){
        String url = sUrl;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
