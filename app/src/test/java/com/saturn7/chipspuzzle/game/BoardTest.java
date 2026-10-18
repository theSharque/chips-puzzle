package com.saturn7.chipspuzzle.game;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BoardTest {
    @Test
    public void firstLevelIsNotSolved() {
        Board board = new Board();
        board.copyFrom(Levels.copy(0));
        assertFalse(board.matches(Levels.GOAL));
    }

    @Test
    public void goalBoardIsSolved() {
        Board board = new Board();
        board.copyFrom(Levels.GOAL);
        assertTrue(board.matches(Levels.GOAL));
    }

    @Test
    public void pickUpClearsCellAndPlaceSnapsToCenter() {
        Board board = new Board();
        board.copyFrom(Levels.GOAL);
        Chip chip = board.pickUp(0, 0);
        assertEquals(Tile.YELLOW, chip.type);
        assertEquals(Tile.EMPTY, board.get(0, 0));
        chip.x = 8;
        chip.y = 8;
        board.place(chip);
        assertEquals(Tile.YELLOW, board.get(0, 0));
        assertFalse(chip.dragging);
    }
}
