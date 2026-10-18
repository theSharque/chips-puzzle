package com.saturn7.chipspuzzle.game;

public final class Levels {
    public static final int PREDEFINED_COUNT = 10;

    public static final int[][] GOAL = {
            {4, 4, 1, 5, 5},
            {4, 4, 0, 5, 5},
            {1, 0, 1, 0, 1},
            {2, 2, 0, 3, 3},
            {2, 2, 1, 3, 3}
    };

    private static final int[][] WALLS = {
            {0, 0, 1, 0, 0},
            {0, 0, 0, 0, 0},
            {1, 0, 1, 0, 1},
            {0, 0, 0, 0, 0},
            {0, 0, 1, 0, 0}
    };

    private static final int[][][] PREDEFINED = {
            {
                    {4, 4, 1, 5, 5},
                    {4, 4, 0, 0, 5},
                    {1, 5, 1, 0, 1},
                    {2, 2, 0, 3, 3},
                    {2, 2, 1, 3, 3}
            }, {
                    {5, 4, 1, 5, 3},
                    {4, 4, 0, 5, 5},
                    {1, 0, 1, 0, 1},
                    {2, 2, 0, 3, 3},
                    {4, 2, 1, 3, 2}
            }, {
                    {5, 4, 1, 4, 5},
                    {4, 5, 0, 5, 4},
                    {1, 0, 1, 0, 1},
                    {3, 2, 0, 2, 3},
                    {2, 3, 1, 3, 2}
            }, {
                    {3, 3, 1, 5, 5},
                    {3, 3, 0, 5, 5},
                    {1, 0, 1, 0, 1},
                    {2, 2, 0, 4, 4},
                    {2, 2, 1, 4, 4}
            }, {
                    {2, 2, 1, 4, 4},
                    {2, 2, 0, 4, 4},
                    {1, 0, 1, 0, 1},
                    {3, 3, 0, 5, 5},
                    {3, 3, 1, 5, 5}
            }, {
                    {2, 4, 1, 5, 4},
                    {4, 3, 0, 2, 5},
                    {1, 0, 1, 0, 1},
                    {2, 5, 0, 4, 3},
                    {3, 2, 1, 3, 5}
            }, {
                    {2, 3, 1, 2, 3},
                    {4, 5, 0, 4, 5},
                    {1, 0, 1, 0, 1},
                    {2, 3, 0, 2, 3},
                    {4, 5, 1, 4, 5}
            }, {
                    {0, 4, 1, 5, 0},
                    {4, 4, 3, 5, 5},
                    {1, 5, 1, 2, 1},
                    {2, 2, 4, 3, 3},
                    {0, 2, 1, 3, 0}
            }, {
                    {2, 2, 1, 3, 3},
                    {3, 3, 0, 2, 2},
                    {1, 0, 1, 0, 1},
                    {4, 4, 0, 5, 5},
                    {5, 5, 1, 4, 4}
            }, {
                    {2, 3, 1, 4, 5},
                    {2, 3, 0, 4, 5},
                    {1, 0, 1, 0, 1},
                    {2, 3, 0, 4, 5},
                    {2, 3, 1, 4, 5}
            }
    };

    private Levels() {
    }

    public static int[][] copy(int level) {
        if (level >= 0 && level < PREDEFINED_COUNT) {
            return copyGrid(PREDEFINED[level]);
        }
        return randomBoard();
    }

    public static int[][] randomBoard() {
        int[][] map = copyGrid(WALLS);
        int remaining = 15;
        while (remaining >= 0) {
            int col = (int) (Math.random() * Board.SIZE);
            int row = (int) (Math.random() * Board.SIZE);
            if (map[row][col] == Tile.EMPTY) {
                map[row][col] = Tile.BLUE + (remaining / 4);
                remaining--;
            }
        }
        return map;
    }

    private static int[][] copyGrid(int[][] source) {
        int[][] copy = new int[Board.SIZE][Board.SIZE];
        for (int row = 0; row < Board.SIZE; row++) {
            System.arraycopy(source[row], 0, copy[row], 0, Board.SIZE);
        }
        return copy;
    }
}
