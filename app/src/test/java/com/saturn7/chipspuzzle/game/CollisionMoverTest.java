package com.saturn7.chipspuzzle.game;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CollisionMoverTest {
    @Test
    public void slidesIntoEmptyCell() {
        Board board = maze(
                2, 0, 1, 0, 0,
                0, 0, 0, 0, 0,
                1, 0, 1, 0, 1,
                0, 0, 0, 0, 0,
                0, 0, 1, 0, 0
        );
        Chip chip = board.pickUp(0, 0);
        boolean moved = CollisionMover.slideToward(chip, board.cells(), Board.CELL, 0);
        assertTrue(moved);
        assertEquals(Board.CELL, chip.x, 0.01f);
        assertEquals(0, chip.y, 0.01f);
    }

    @Test
    public void stopsBeforeWall() {
        Board board = maze(
                2, 0, 1, 0, 0,
                0, 0, 0, 0, 0,
                1, 0, 1, 0, 1,
                0, 0, 0, 0, 0,
                0, 0, 1, 0, 0
        );
        Chip chip = board.pickUp(0, 0);
        CollisionMover.slideToward(chip, board.cells(), 0, Board.CELL * 3);
        assertEquals(0, chip.x, 0.01f);
        assertEquals(Board.CELL, chip.y, 0.01f);
    }

    @Test
    public void stopsBeforeOtherChip() {
        Board board = maze(
                2, 3, 1, 0, 0,
                0, 0, 0, 0, 0,
                1, 0, 1, 0, 1,
                0, 0, 0, 0, 0,
                0, 0, 1, 0, 0
        );
        Chip chip = board.pickUp(0, 0);
        boolean moved = CollisionMover.slideToward(chip, board.cells(), Board.CELL * 2, 0);
        assertFalse(moved);
        assertEquals(0, chip.x, 0.01f);
    }

    @Test(timeout = 1000)
    public void fractionalFingerTargetDoesNotHang() {
        Board board = maze(
                2, 0, 1, 0, 0,
                0, 0, 0, 0, 0,
                1, 0, 1, 0, 1,
                0, 0, 0, 0, 0,
                0, 0, 1, 0, 0
        );
        Chip chip = board.pickUp(0, 0);
        boolean moved = CollisionMover.slideToward(chip, board.cells(), 10.5f, 0.25f);
        assertTrue(moved);
        assertEquals(10.5f, chip.x, 0.01f);
        assertEquals(0.25f, chip.y, 0.01f);
    }

    @Test
    public void emptyRejectsOutOfBounds() {
        int[][] map = new int[Board.SIZE][Board.SIZE];
        assertFalse(CollisionMover.empty(map, 0, -1));
        assertFalse(CollisionMover.empty(map, Board.CELL * Board.SIZE, 0));
        assertTrue(CollisionMover.empty(map, 0, 0));
    }

    private static Board maze(int... tiles) {
        Board board = new Board();
        for (int i = 0; i < tiles.length; i++) {
            board.set(i / Board.SIZE, i % Board.SIZE, tiles[i]);
        }
        return board;
    }
}
