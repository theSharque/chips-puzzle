package com.saturn7.chipspuzzle.persistence;

import android.content.Context;
import android.content.SharedPreferences;

import com.saturn7.chipspuzzle.game.Board;
import com.saturn7.chipspuzzle.game.GameSnapshot;
import com.saturn7.chipspuzzle.game.Levels;

public class PreferenceGameStorage implements GameStorage {
    private static final String PREFS = "chips_puzzle";
    private static final String KEY_TIMER = "timer";
    private static final String KEY_LEVEL = "level";
    private static final String KEY_TOP = "top";
    private static final String DEFAULT_TOP = "-1,-1,-1,-1,-1,-1,-1,-1,-1,-1";

    private final SharedPreferences prefs;

    public PreferenceGameStorage(Context context) {
        prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    @Override
    public GameSnapshot load() {
        int level = prefs.getInt(KEY_LEVEL, 0);
        float time = prefs.getFloat(KEY_TIMER, 0);
        int[] best = parseBest(prefs.getString(KEY_TOP, DEFAULT_TOP));
        int[][] map = loadMap(level);
        return new GameSnapshot(level, time, map, best);
    }

    @Override
    public void save(GameSnapshot snapshot) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(KEY_TIMER, snapshot.timeSeconds);
        editor.putInt(KEY_LEVEL, snapshot.level);
        for (int row = 0; row < Board.SIZE; row++) {
            StringBuilder line = new StringBuilder();
            for (int col = 0; col < Board.SIZE; col++) {
                line.append(snapshot.map[row][col]);
            }
            editor.putString("map_" + row, line.toString());
        }
        StringBuilder top = new StringBuilder();
        for (int best : snapshot.bestTimes) {
            top.append(best).append(',');
        }
        editor.putString(KEY_TOP, top.toString());
        editor.apply();
    }

    private int[][] loadMap(int level) {
        String[] rows = new String[Board.SIZE];
        boolean complete = true;
        for (int row = 0; row < Board.SIZE; row++) {
            rows[row] = prefs.getString("map_" + row, "");
            if (rows[row].length() != Board.SIZE) {
                complete = false;
            }
        }
        if (!complete) {
            return Levels.copy(level);
        }
        int[][] map = new int[Board.SIZE][Board.SIZE];
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                map[row][col] = rows[row].charAt(col) - '0';
            }
        }
        return map;
    }

    private static int[] parseBest(String raw) {
        int[] best = new int[Levels.PREDEFINED_COUNT];
        for (int i = 0; i < best.length; i++) {
            best[i] = -1;
        }
        String[] parts = raw.split(",");
        int n = Math.min(parts.length, best.length);
        for (int i = 0; i < n; i++) {
            if (parts[i].isEmpty()) {
                continue;
            }
            best[i] = Integer.parseInt(parts[i]);
        }
        return best;
    }
}
