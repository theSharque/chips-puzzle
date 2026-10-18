package com.saturn7.chipspuzzle.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.saturn7.chipspuzzle.R;
import com.saturn7.chipspuzzle.game.Levels;

public class LevelSelectView extends View {
    public interface Listener {
        void onLevelChosen(int level);
    }

    private static final Rect PORTRAIT_CROP = new Rect(0, 0, 320, 512);
    private static final Rect LANDSCAPE_CROP = new Rect(0, 0, 512, 320);
    private static final RectF[] PORTRAIT = {
            box(20, 114, 100, 193),
            box(117, 114, 195, 193),
            box(213, 114, 290, 193),
            box(20, 204, 100, 283),
            box(117, 204, 195, 283),
            box(213, 204, 290, 283),
            box(20, 295, 100, 373),
            box(117, 295, 195, 373),
            box(213, 295, 290, 373),
            box(117, 385, 195, 464)
    };
    private static final RectF[] LANDSCAPE = {
            box(8, 97, 87, 175),
            box(104, 97, 183, 175),
            box(200, 97, 279, 175),
            box(296, 97, 375, 175),
            box(392, 97, 471, 175),
            box(8, 193, 87, 272),
            box(104, 193, 183, 272),
            box(200, 193, 279, 272),
            box(296, 193, 375, 272),
            box(392, 193, 471, 272)
    };

    private final Paint bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private final Paint timePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF dest = new RectF();
    private final Bitmap landscapeArt;
    private final Bitmap portraitArt;
    private final int[] bestTimes = new int[Levels.PREDEFINED_COUNT];

    private Listener listener;

    public LevelSelectView(Context context) {
        this(context, null);
    }

    public LevelSelectView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LevelSelectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        landscapeArt = BitmapFactory.decodeResource(getResources(), R.drawable.levels);
        portraitArt = BitmapFactory.decodeResource(getResources(), R.drawable.levelsv);
        timePaint.setColor(0xFFFFFFFF);
        timePaint.setTextAlign(Paint.Align.CENTER);
        timePaint.setFakeBoldText(true);
        timePaint.setShadowLayer(2f, 0f, 1f, 0xCC000000);
        for (int i = 0; i < bestTimes.length; i++) {
            bestTimes[i] = -1;
        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setBestTimes(int[] times) {
        int n = Math.min(bestTimes.length, times.length);
        System.arraycopy(times, 0, bestTimes, 0, n);
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        dest.set(0, 0, w, h);
        timePaint.setTextSize(18f * h / crop().height());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(0xFF000000);
        Bitmap art = portrait() ? portraitArt : landscapeArt;
        Rect crop = crop();
        canvas.drawBitmap(art, crop, dest, bitmapPaint);
        RectF[] spots = spots();
        float scaleX = getWidth() / (float) crop.width();
        float scaleY = getHeight() / (float) crop.height();
        for (int i = 0; i < spots.length; i++) {
            if (bestTimes[i] < 0) {
                continue;
            }
            float cx = (spots[i].centerX() - crop.left) * scaleX;
            float cy = (spots[i].bottom - 12f - crop.top) * scaleY;
            canvas.drawText(formatTime(bestTimes[i]), cx, cy, timePaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getActionMasked() != MotionEvent.ACTION_UP || listener == null || getWidth() == 0 || getHeight() == 0) {
            return true;
        }
        Rect crop = crop();
        float imageX = crop.left + event.getX() / getWidth() * crop.width();
        float imageY = crop.top + event.getY() / getHeight() * crop.height();
        RectF[] spots = spots();
        for (int i = 0; i < spots.length; i++) {
            if (spots[i].contains(imageX, imageY)) {
                listener.onLevelChosen(i);
                return true;
            }
        }
        return true;
    }

    private boolean portrait() {
        return getHeight() >= getWidth();
    }

    private Rect crop() {
        return portrait() ? PORTRAIT_CROP : LANDSCAPE_CROP;
    }

    private RectF[] spots() {
        return portrait() ? PORTRAIT : LANDSCAPE;
    }

    private static RectF box(float left, float top, float right, float bottom) {
        return new RectF(left, top, right, bottom);
    }

    private static String formatTime(int seconds) {
        int minutes = seconds / 60;
        int rest = seconds % 60;
        return (minutes < 10 ? "0" : "") + minutes + ":" + (rest < 10 ? "0" : "") + rest;
    }
}
