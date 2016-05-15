package com.ashojash.android.customview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.ashojash.android.R;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.ui.UiUtils;
import com.ashojash.android.ui.VenueUtils;

public class VenueScoreArc extends ImageView {
    private Paint paint;
    private Path mPath;

    public VenueScoreArc(Context context) {
        super(context);
        initialize(context);
    }

    private void initialize(Context context) {
        mPath = new Path();
        paint = new Paint();
        paint.setColor(getResources().getColor(R.color.level_0));
    }

    public VenueScoreArc(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public VenueScoreArc(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public VenueScoreArc(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPath.moveTo(0, getHeight() - UiUtils.dp2px(115));
        mPath.cubicTo(0, getHeight() - UiUtils.dp2px(115), getWidth() / 2, 50 * getHeight() / 100, getWidth(), getHeight() - UiUtils.dp2px(115)); /*the anchors you want, the curve will tend to reach these anchor points; look at the wikipedia article to understand more */
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(7); //the width you want
        canvas.drawPath(mPath, paint);
        /*mPath.reset();
        mPath.moveTo(0, getHeight());
        mPath.cubicTo(0, getHeight(), getWidth() / 2, (80 * getHeight() / 100), getWidth(), getHeight()); *//*the anchors you want, the curve will tend to reach these anchor points; look at the wikipedia article to understand more *//*
        paint.setColor(getResources().getVenue(R.color.default_white));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(mPath, paint);*/
    }

    private String TAG = AppController.TAG;

    public void updateVenueArc(double score) {
        paint.setColor(VenueUtils.getVenueScoreColor(score));
        invalidate();
    }
}
