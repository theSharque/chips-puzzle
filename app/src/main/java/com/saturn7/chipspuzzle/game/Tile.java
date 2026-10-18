package com.saturn7.chipspuzzle.game;

public final class Tile {
    public static final int EMPTY = 0;
    public static final int WALL = 1;
    public static final int BLUE = 2;
    public static final int RED = 3;
    public static final int YELLOW = 4;
    public static final int GREEN = 5;

    private Tile() {
    }

    public static boolean isChip(int tile) {
        return tile >= BLUE;
    }
}
