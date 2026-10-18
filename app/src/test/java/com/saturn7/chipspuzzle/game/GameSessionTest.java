package com.saturn7.chipspuzzle.game;

import com.saturn7.chipspuzzle.persistence.GameStorage;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class GameSessionTest {
    @Test
    public void dragMovesChipAndDropsOnEmptyCell() {
        GameSession session = sessionWith(level0());
        assertTrue(session.touchDown(Board.CELL * 4 + 10, Board.CELL + 10));
        assertNotNull(session.draggedChip());
        session.touchMove(Board.CELL * 3 + 10, Board.CELL + 10);
        boolean solved = session.touchUp();
        assertFalse(solved);
        assertNull(session.draggedChip());
        assertEquals(Tile.GREEN, session.board().get(1, 3));
        assertEquals(Tile.EMPTY, session.board().get(1, 4));
    }

    @Test
    public void completingGoalOpensCompleteScreen() {
        GameSession session = sessionWith(Levels.GOAL);
        session.touchDown(10, 10);
        session.touchUp();
        assertEquals(Screen.COMPLETE, session.screen());
    }

    @Test
    public void pausePutsDraggedChipBackOnBoard() {
        GameSession session = sessionWith(level0());
        assertTrue(session.touchDown(Board.CELL * 4 + 10, Board.CELL + 10));
        assertNotNull(session.draggedChip());
        session.pause();
        assertNull(session.draggedChip());
        assertEquals(4, count(session.board(), Tile.GREEN));
    }

    @Test
    public void selectLevelResetsMap() {
        GameSession session = sessionWith(Levels.GOAL);
        session.selectLevel(0);
        assertEquals(Screen.PLAY, session.screen());
        assertFalse(session.board().matches(Levels.GOAL));
        assertEquals(0, session.level());
    }

    private static GameSession sessionWith(int[][] map) {
        MemoryStorage storage = new MemoryStorage(new GameSnapshot(0, 0, copy(map), new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1}));
        GameSession session = new GameSession(storage);
        session.restore();
        return session;
    }

    private static int[][] level0() {
        return Levels.copy(0);
    }

    private static int count(Board board, int tile) {
        int n = 0;
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                if (board.get(row, col) == tile) {
                    n++;
                }
            }
        }
        return n;
    }

    private static int[][] copy(int[][] source) {
        int[][] copy = new int[source.length][source[0].length];
        for (int i = 0; i < source.length; i++) {
            System.arraycopy(source[i], 0, copy[i], 0, source[i].length);
        }
        return copy;
    }

    private static final class MemoryStorage implements GameStorage {
        private GameSnapshot snapshot;

        MemoryStorage(GameSnapshot snapshot) {
            this.snapshot = snapshot;
        }

        @Override
        public GameSnapshot load() {
            return snapshot;
        }

        @Override
        public void save(GameSnapshot snapshot) {
            this.snapshot = snapshot;
        }
    }
}
