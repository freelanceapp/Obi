package com.obiapp.share;

import android.content.Context;


import com.obiapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Time_Ago {
    private static int second = 1000;
    private static int minute = second * 60;
    private static int hour = minute * 60;
    private static int day = hour * 24;

    public static String getTimeAgo(long time, Context context) {
        long now = Calendar.getInstance().getTimeInMillis();
        if (time <= 0||time>now) {
            return "";
        }


        long diff =now-time;

        if (diff < minute) {
            return context.getString(R.string.just_now);

        } else if (diff < 2 * minute) {
            return context.getString(R.string.a_minute_ago);

        } else if (diff < hour) {
            return diff / minute + " " + context.getString(R.string.min);

        } else if (diff < 2 * hour) {
            return context.getString(R.string.an_hour_ago);

        } else if (diff < day) {

            String t = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH).format(new Date(diff));

            return t;

        } else if (diff < 2 * day) {
            return context.getString(R.string.yesterday);
        } else {
            String t = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH).format(new Date(time));
            return t;
        }

    }

}
