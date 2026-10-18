package com.saturn7.chipspuzzle.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.saturn7.chipspuzzle.R;
import com.saturn7.chipspuzzle.game.Board;
import com.saturn7.chipspuzzle.game.Chip;
import com.saturn7.chipspuzzle.game.GameSession;
import com.saturn7.chipspuzzle.game.Tile;

public class PuzzleBoardView extends View {
    public interface Listener {
        void onBoardChanged();

        void onLevelSolved();

        void onSliding(boolean sliding);
    }

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private final RectF boardDest = new RectF(0, 0, Board.LOGICAL, Board.LOGICAL);
    private final RectF chipDest = new RectF();
    private final Bitmap background;
    private final Bitmap[] chips = new Bitmap[4];

    private GameSession session;
    private Listener listener;
    private float scale = 1f;
    private float originX;
    private float originY;

    public PuzzleBoardView(Context context) {
        this(context, null);
    }

    public PuzzleBoardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PuzzleBoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        background = BitmapFactory.decodeResource(getResources(), R.drawable.bg);
        chips[0] = BitmapFactory.decodeResource(getResources(), R.drawable.bchip);
        chips[1] = BitmapFactory.decodeResource(getResources(), R.drawable.rchip);
        chips[2] = BitmapFactory.decodeResource(getResources(), R.drawable.ychip);
        chips[3] = BitmapFactory.decodeResource(getResources(), R.drawable.gchip);
    }

    public void setSession(GameSession session) {
        this.session = session;
        invalidate();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        scale = Math.min(w / (float) Board.LOGICAL, h / (float) Board.LOGICAL);
        originX = (w - Board.LOGICAL * scale) / 2f;
        originY = (h - Board.LOGICAL * scale) / 2f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (session == null) {
            return;
        }
        canvas.save();
        canvas.translate(originX, originY);
        canvas.scale(scale, scale);
        canvas.drawBitmap(background, null, boardDest, paint);

        Board board = session.board();
        Chip dragged = session.draggedChip();
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                int tile = board.get(row, col);
                if (Tile.isChip(tile)) {
                    drawChip(canvas, tile, col * Board.CELL, row * Board.CELL);
                }
            }
        }
        if (dragged != null) {
            drawChip(canvas, dragged.type, dragged.x, dragged.y);
        }
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (session == null) {
            return false;
        }
        float boardX = toBoardX(event.getX());
        float boardY = toBoardY(event.getY());
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                session.touchDown(boardX, boardY);
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                boolean sliding = session.touchMove(boardX, boardY);
                if (listener != null) {
                    listener.onSliding(sliding);
                }
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                if (listener != null) {
                    listener.onSliding(false);
                }
                boolean solved = session.touchUp();
                invalidate();
                if (listener != null) {
                    listener.onBoardChanged();
                    if (solved) {
                        listener.onLevelSolved();
                    }
                }
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    private void drawChip(Canvas canvas, int type, float x, float y) {
        Bitmap bitmap = chips[type - Tile.BLUE];
        float left = Board.PAD + x;
        float top = Board.LOGICAL - Board.PAD - Board.CELL - y;
        chipDest.set(left, top, left + Board.CELL, top + Board.CELL);
        canvas.drawBitmap(bitmap, null, chipDest, paint);
    }

    private float toBoardX(float viewX) {
        return (viewX - originX) / scale - Board.PAD;
    }

    private float toBoardY(float viewY) {
        float logicalY = (viewY - originY) / scale;
        return Board.LOGICAL - logicalY - Board.PAD;
    }
}
