package com.ashojash.android.customview;

import android.support.annotation.NonNull;
import android.widget.RatingBar;
import android.widget.TextView;
import com.ashojash.android.R;

/**
 * Created by admin on 1/30/2016.
 */
public class CostRatingOnRatingBarChangeListener extends GenericOnRatingBarChangeListener {
    private TextView txtCostReviewIndicator;

    public CostRatingOnRatingBarChangeListener(@NonNull TextView txtCostReviewIndicator) {
        this.txtCostReviewIndicator = txtCostReviewIndicator;
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        super.onRatingChanged(ratingBar, rating, fromUser);
        switch ((int) rating) {
            case 1:
                txtCostReviewIndicator.setText(R.string.cost_very_low);
                break;
            case 2:
                txtCostReviewIndicator.setText(R.string.cost_low);
                break;
            case 3:
                txtCostReviewIndicator.setText(R.string.cost_medium);
                break;
            case 4:
                txtCostReviewIndicator.setText(R.string.cost_high);
                break;
            case 5:
                txtCostReviewIndicator.setText(R.string.cost_very_high);
                break;
        }
    }
}
