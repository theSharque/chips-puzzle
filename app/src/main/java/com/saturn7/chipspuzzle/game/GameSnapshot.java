package com.saturn7.chipspuzzle.game;

public final class GameSnapshot {
    public final int level;
    public final float timeSeconds;
    public final int[][] map;
    public final int[] bestTimes;

    public GameSnapshot(int level, float timeSeconds, int[][] map, int[] bestTimes) {
        this.level = level;
        this.timeSeconds = timeSeconds;
        this.map = map;
        this.bestTimes = bestTimes;
    }
}
