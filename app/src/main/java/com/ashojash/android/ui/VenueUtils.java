package com.ashojash.android.ui;

import android.graphics.Color;
import com.ashojash.android.R;

public final class VenueUtils {
    public static String getVenueScoreText(double score) {
        if (score == 0)
            return "-";
        return String.valueOf(score);
    }


    public static int getVenueScoreDrawableId(double score) {
        if (score < 1 || score > 5)
            return R.drawable.level_0;
        if (score < 1.25)
            return R.drawable.level_1;
        if (score < 1.75)
            return R.drawable.level_2;
        if (score < 2.25)
            return R.drawable.level_3;
        if (score < 2.75)
            return R.drawable.level_4;
        if (score < 3.25)
            return R.drawable.level_5;
        if (score < 3.75)
            return R.drawable.level_6;
        if (score < 4.25)
            return R.drawable.level_7;
        if (score < 4.75)
            return R.drawable.level_8;
        if (score <= 5)
            return R.drawable.level_9;
        return R.drawable.level_0;
    }

    public static int getVenueScoreColor(double score) {
        if (score < 1 || score > 5)
            return Color.parseColor("#c1c1c1");
        if (score < 1.25)
            return Color.parseColor("#cb202d");
        if (score < 1.75)
            return Color.parseColor("#de1d0f");
        if (score < 2.25)
            return Color.parseColor("#ff7800");
        if (score < 2.75)
            return Color.parseColor("#ffba00");
        if (score < 3.25)
            return Color.parseColor("#edd614");
        if (score < 3.75)
            return Color.parseColor("#9acd32");
        if (score < 4.25)
            return Color.parseColor("#5ba829");
        if (score < 4.75)
            return Color.parseColor("#3f7e00");
        if (score <= 5)
        {
            return Color.parseColor("#305d02");
        }
        return Color.parseColor("#c1c1c1");
    }

    public static String getCostSign(int cost) {
        String dollars = "<font color=#666>";
        for (int i = 0; i < cost; i++) {
            dollars += "$ ";
        }
        dollars += "</font> <font color=#c0c0c0>";
        for (int i = 0; i < 5 - cost; i++) {
            dollars += "$ ";
        }
        dollars += "</font>";
        return dollars;
    }
}
