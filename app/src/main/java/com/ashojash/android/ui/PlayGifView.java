package com.ashojash.android.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import com.ashojash.android.helper.AppController;

public class PlayGifView extends ImageView {

    private static final int DEFAULT_MOVIEW_DURATION = 1000;

    private int mMovieResourceId;
    private View view;
    private Movie mMovie;

    private long mMovieStart = 0;
    private int mCurrentAnimationTime = 0;

    @SuppressLint("NewApi")
    public PlayGifView(Context context, AttributeSet attrs) {
        super(context, attrs);
        /**
         * Starting from HONEYCOMB have to turn off HardWare acceleration to draw
         * Movie on Canvas.
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    @Override
    public void setImageResource(int resId) {
        if (this.view == null)
            super.setImageResource(resId);
        else {
            mMovie = Movie.decodeStream(this.view.getResources().openRawResource(mMovieResourceId));
            AppController.HANDLER.post(new Runnable() {
                @Override
                public void run() {
                    requestLayout();
                    setBackgroundResource(android.R.color.transparent);
                }
            });
        }
    }

    public void setImageResource(int mvId, View view) {
        this.mMovieResourceId = mvId;
        this.view = view;
        setImageResource(mvId);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mMovie != null) {
            setMeasuredDimension(mMovie.width(), mMovie.height());
        } else {
            setMeasuredDimension(getSuggestedMinimumWidth(), getSuggestedMinimumHeight());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mMovie != null) {
            updateAnimtionTime();
            drawGif(canvas);
            invalidate();
        } else {
            drawGif(canvas);
        }
    }

    private void updateAnimtionTime() {
        long now = android.os.SystemClock.uptimeMillis();

        if (mMovieStart == 0) {
            mMovieStart = now;
        }
        int dur = mMovie.duration();
        if (dur == 0) {
            dur = DEFAULT_MOVIEW_DURATION;
        }
        mCurrentAnimationTime = (int) ((now - mMovieStart) % dur);
    }

    private void drawGif(Canvas canvas) {
        if (mMovie != null) {
            mMovie.setTime(mCurrentAnimationTime);
            mMovie.draw(canvas, 0, 0);
            canvas.restore();
        }
    }
}
