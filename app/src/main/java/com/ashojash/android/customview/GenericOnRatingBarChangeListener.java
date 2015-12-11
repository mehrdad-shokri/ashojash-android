package com.ashojash.android.customview;

import android.widget.RatingBar;

/**
 * Created by admin on 1/30/2016.
 */
public class GenericOnRatingBarChangeListener implements RatingBar.OnRatingBarChangeListener {

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating,
                                boolean fromUser) {
        if (rating < 1.f)
            ratingBar.setRating(1.f);
    }
}
