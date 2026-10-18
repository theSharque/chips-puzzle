package com.saturn7.chipspuzzle.game;

public class Board {
    public static final int SIZE = 5;
    public static final int CELL = 56;
    public static final int PAD = 20;
    public static final int LOGICAL = 320;
    public static final int MAX_POS = (SIZE - 1) * CELL;

    private final int[][] cells = new int[SIZE][SIZE];

    public int[][] cells() {
        return cells;
    }

    public int get(int row, int col) {
        return cells[row][col];
    }

    public void set(int row, int col, int value) {
        cells[row][col] = value;
    }

    public boolean inBounds(int row, int col) {
        return row >= 0 && col >= 0 && row < SIZE && col < SIZE;
    }

    public void copyFrom(int[][] source) {
        for (int row = 0; row < SIZE; row++) {
            System.arraycopy(source[row], 0, cells[row], 0, SIZE);
        }
    }

    public int[][] copy() {
        int[][] copy = new int[SIZE][SIZE];
        for (int row = 0; row < SIZE; row++) {
            System.arraycopy(cells[row], 0, copy[row], 0, SIZE);
        }
        return copy;
    }

    public Chip pickUp(int row, int col) {
        Chip chip = new Chip();
        chip.dragging = true;
        chip.type = cells[row][col];
        chip.x = col * CELL;
        chip.y = row * CELL;
        cells[row][col] = Tile.EMPTY;
        return chip;
    }

    public void place(Chip chip) {
        int row = (int) ((chip.y + CELL / 2f) / CELL);
        int col = (int) ((chip.x + CELL / 2f) / CELL);
        row = clampCell(row);
        col = clampCell(col);
        cells[row][col] = chip.type;
        chip.dragging = false;
    }

    public boolean matches(int[][] target) {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (cells[row][col] != target[row][col]) {
                    return false;
                }
            }
        }
        return true;
    }

    private static int clampCell(int index) {
        if (index < 0) {
            return 0;
        }
        if (index >= SIZE) {
            return SIZE - 1;
        }
        return index;
    }
}
