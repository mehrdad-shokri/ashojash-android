package com.ashojash.android.customview;

import android.widget.RatingBar;

public class GenericOnRatingBarChangeListener implements RatingBar.OnRatingBarChangeListener {

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating,
                                boolean fromUser) {
        if (rating < 1.f)
            ratingBar.setRating(1.f);
    }
}
