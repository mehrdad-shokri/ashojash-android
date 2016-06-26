package com.ashojash.android.customview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ashojash.android.R;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.ui.UiUtils;
import com.ashojash.android.ui.VenueUtil;

public class VenueScoreIndicator extends LinearLayout {
    private TextView txtVenueScore;
    private TextView txtVenueScoreCount;
    private VenueScoreArc arc;

    public VenueScoreIndicator(Context context) {
        super(context);
        initialize(context);
    }

    public VenueScoreIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public VenueScoreIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public VenueScoreIndicator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(context);
    }

    public void initialize(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.venue_score_indicator, this, true);
        txtVenueScore = (TextView) findViewById(R.id.txtVenueScoreVenueScoreIndicatorView);
        txtVenueScoreCount = (TextView) findViewById(R.id.txtVenueScoreCountVenueScoreIndicatorView);
        arc = (VenueScoreArc) findViewById(R.id.venueScoreArcVenueScoreIndicatorView);
    }

    public void setScore(double score, int reviewsCount) {
        updateVenueText(score);
        updateVenueScoreCount(reviewsCount);
        arc.updateVenueArc(score);
    }

    private void updateVenueScoreCount(int count) {
        if (count == 0) {
            txtVenueScoreCount.setText(getResources().getString(R.string.review_count_not_enough));
        } else {
            txtVenueScoreCount.setText(getResources().getString(R.string.review_based_on_count).replace("{{venueReviewCount}}", UiUtils.toPersianNumber(String.valueOf(count))));
        }
    }


    private void updateVenueText(double score) {
        txtVenueScore.setText(VenueUtil.getVenueScoreText(score));
        txtVenueScore.setTextColor(Color.WHITE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            txtVenueScore.setBackgroundResource(VenueUtil.getVenueScoreDrawableId(score));
        } else {
            txtVenueScore.setBackgroundDrawable(AppController.context.getResources().getDrawable(VenueUtil.getVenueScoreDrawableId(score)));
        }
        invalidate();
    }

}
