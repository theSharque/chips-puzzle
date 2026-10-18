package com.saturn7.chipspuzzle;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.window.OnBackInvokedCallback;
import android.window.OnBackInvokedDispatcher;

import com.saturn7.chipspuzzle.audio.ScratchPlayer;
import com.saturn7.chipspuzzle.game.GameSession;
import com.saturn7.chipspuzzle.game.Levels;
import com.saturn7.chipspuzzle.game.Screen;
import com.saturn7.chipspuzzle.persistence.PreferenceGameStorage;
import com.saturn7.chipspuzzle.ui.LevelSelectView;
import com.saturn7.chipspuzzle.ui.PuzzleBoardView;

public class PuzzleActivity extends Activity implements PuzzleBoardView.Listener {
    private final Handler hudHandler = new Handler(Looper.getMainLooper());
    private GameSession session;
    private PuzzleBoardView boardView;
    private ScratchPlayer scratchPlayer;
    private TextView levelValue;
    private TextView timeValue;
    private TextView bestValue;
    private View completeOverlay;
    private LevelSelectView levelsOverlay;
    private TextView completeTitle;
    private TextView completeTime;
    private final OnBackInvokedCallback backCallback = this::handleBack;

    private final Runnable hudTick = new Runnable() {
        @Override
        public void run() {
            refreshHud();
            hudHandler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_puzzle);

        session = new GameSession(new PreferenceGameStorage(this));
        session.restore();
        scratchPlayer = new ScratchPlayer(this);

        boardView = findViewById(R.id.board);
        boardView.setSession(session);
        boardView.setListener(this);

        levelValue = findViewById(R.id.level_value);
        timeValue = findViewById(R.id.time_value);
        bestValue = findViewById(R.id.best_value);
        completeOverlay = findViewById(R.id.complete_overlay);
        levelsOverlay = findViewById(R.id.levels_overlay);
        completeTitle = findViewById(R.id.complete_title);
        completeTime = findViewById(R.id.complete_time);

        findViewById(R.id.levels_button).setOnClickListener(v -> {
            session.openLevels();
            showScreen();
        });
        completeOverlay.setOnClickListener(v -> {
            session.continueAfterComplete();
            boardView.invalidate();
            showScreen();
        });
        levelsOverlay.setListener(level -> {
            session.selectLevel(level);
            boardView.invalidate();
            showScreen();
        });

        showScreen();
        if (Build.VERSION.SDK_INT >= 33) {
            getOnBackInvokedDispatcher().registerOnBackInvokedCallback(
                    OnBackInvokedDispatcher.PRIORITY_DEFAULT, backCallback);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        session.resume();
        hudHandler.post(hudTick);
        refreshHud();
    }

    @Override
    protected void onPause() {
        hudHandler.removeCallbacks(hudTick);
        scratchPlayer.stop();
        session.pause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (Build.VERSION.SDK_INT >= 33) {
            getOnBackInvokedDispatcher().unregisterOnBackInvokedCallback(backCallback);
        }
        scratchPlayer.release();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        handleBack();
    }

    private void handleBack() {
        if (session.screen() == Screen.LEVELS) {
            session.closeLevels();
            showScreen();
            return;
        }
        finish();
    }

    @Override
    public void onBoardChanged() {
        refreshHud();
    }

    @Override
    public void onLevelSolved() {
        showScreen();
    }

    @Override
    public void onSliding(boolean sliding) {
        scratchPlayer.setSliding(sliding);
    }

    private void showScreen() {
        Screen screen = session.screen();
        completeOverlay.setVisibility(screen == Screen.COMPLETE ? View.VISIBLE : View.GONE);
        levelsOverlay.setVisibility(screen == Screen.LEVELS ? View.VISIBLE : View.GONE);
        if (screen == Screen.COMPLETE) {
            completeTitle.setText(session.isRandomLevel()
                    ? getString(R.string.random_complete)
                    : getString(R.string.level_complete, session.level() + 1));
            completeTime.setText(getString(R.string.time_label, formatTime(session.elapsedSeconds())));
        }
        if (screen == Screen.LEVELS) {
            int[] times = new int[Levels.PREDEFINED_COUNT];
            for (int i = 0; i < times.length; i++) {
                times[i] = session.bestSeconds(i);
            }
            levelsOverlay.setBestTimes(times);
        }
        refreshHud();
    }

    private void refreshHud() {
        if (session.isRandomLevel()) {
            levelValue.setText(R.string.random_level);
        } else {
            levelValue.setText(String.valueOf(session.level() + 1));
        }
        timeValue.setText(formatTime(session.elapsedSeconds()));
        int best = session.bestSeconds();
        bestValue.setText(best < 0 ? getString(R.string.best_empty) : formatTime(best));
    }

    private static String formatTime(int seconds) {
        int minutes = seconds / 60;
        int rest = seconds % 60;
        return (minutes < 10 ? "0" : "") + minutes + ":" + (rest < 10 ? "0" : "") + rest;
    }
}
