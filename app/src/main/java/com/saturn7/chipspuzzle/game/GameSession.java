package com.saturn7.chipspuzzle.game;

import com.saturn7.chipspuzzle.persistence.GameStorage;

public class GameSession {
    private final GameStorage storage;
    private final Board board = new Board();
    private final int[] bestTimes = new int[Levels.PREDEFINED_COUNT];
    private final Chip idleChip = new Chip();

    private int level;
    private float timeSeconds;
    private long lastTickMs;
    private boolean timerRunning;
    private Screen screen = Screen.PLAY;
    private Chip chip = idleChip;
    private boolean dragging;
    private boolean sliding;

    public GameSession(GameStorage storage) {
        this.storage = storage;
        for (int i = 0; i < bestTimes.length; i++) {
            bestTimes[i] = -1;
        }
    }

    public void restore() {
        GameSnapshot snapshot = storage.load();
        level = snapshot.level;
        timeSeconds = snapshot.timeSeconds;
        board.copyFrom(snapshot.map);
        System.arraycopy(snapshot.bestTimes, 0, bestTimes, 0, bestTimes.length);
        screen = Screen.PLAY;
        dragging = false;
        chip = idleChip;
        lastTickMs = 0;
        timerRunning = true;
    }

    public void persist() {
        tick();
        storage.save(new GameSnapshot(level, timeSeconds, board.copy(), bestTimes.clone()));
    }

    public void pause() {
        if (dragging) {
            touchUp();
        }
        tick();
        timerRunning = false;
        persist();
    }

    public void resume() {
        lastTickMs = 0;
        timerRunning = screen == Screen.PLAY;
    }

    public void tick() {
        if (!timerRunning || screen != Screen.PLAY) {
            lastTickMs = System.currentTimeMillis();
            return;
        }
        long now = System.currentTimeMillis();
        if (lastTickMs > 0) {
            timeSeconds += (now - lastTickMs) / 1000f;
        }
        lastTickMs = now;
    }

    public boolean touchDown(float boardX, float boardY) {
        if (screen != Screen.PLAY || dragging) {
            return false;
        }
        int col = (int) (boardX / Board.CELL);
        int row = (int) (boardY / Board.CELL);
        if (!board.inBounds(row, col) || !Tile.isChip(board.get(row, col))) {
            return false;
        }
        chip = board.pickUp(row, col);
        chip.grabOffsetX = boardX - chip.x;
        chip.grabOffsetY = boardY - chip.y;
        dragging = true;
        sliding = false;
        return true;
    }

    public boolean touchMove(float boardX, float boardY) {
        if (!dragging) {
            return false;
        }
        sliding = CollisionMover.slideToward(chip, board.cells(), boardX, boardY);
        return sliding;
    }

    public boolean touchUp() {
        sliding = false;
        if (!dragging) {
            return false;
        }
        dragging = false;
        board.place(chip);
        chip = idleChip;
        if (board.matches(Levels.GOAL)) {
            timerRunning = false;
            screen = Screen.COMPLETE;
            return true;
        }
        return false;
    }

    public void openLevels() {
        if (dragging) {
            return;
        }
        tick();
        timerRunning = false;
        screen = Screen.LEVELS;
    }

    public void closeLevels() {
        screen = Screen.PLAY;
        lastTickMs = 0;
        timerRunning = true;
    }

    public void selectLevel(int index) {
        if (index < 0 || index >= Levels.PREDEFINED_COUNT) {
            return;
        }
        level = index;
        timeSeconds = 0;
        board.copyFrom(Levels.copy(level));
        screen = Screen.PLAY;
        lastTickMs = 0;
        timerRunning = true;
    }

    public void continueAfterComplete() {
        recordBest();
        level++;
        timeSeconds = 0;
        board.copyFrom(Levels.copy(level));
        screen = Screen.PLAY;
        lastTickMs = 0;
        timerRunning = true;
    }

    public Board board() {
        return board;
    }

    public Chip draggedChip() {
        return dragging ? chip : null;
    }

    public boolean isSliding() {
        return sliding;
    }

    public Screen screen() {
        return screen;
    }

    public int level() {
        return level;
    }

    public boolean isRandomLevel() {
        return level >= Levels.PREDEFINED_COUNT;
    }

    public int elapsedSeconds() {
        tick();
        return (int) timeSeconds;
    }

    public int bestSeconds() {
        if (level < 0 || level >= bestTimes.length) {
            return -1;
        }
        return bestTimes[level];
    }

    public int bestSeconds(int levelIndex) {
        if (levelIndex < 0 || levelIndex >= bestTimes.length) {
            return -1;
        }
        return bestTimes[levelIndex];
    }

    private void recordBest() {
        if (level < 0 || level >= bestTimes.length) {
            return;
        }
        int current = (int) timeSeconds;
        if (bestTimes[level] < 0 || current < bestTimes[level]) {
            bestTimes[level] = current;
        }
    }
}
