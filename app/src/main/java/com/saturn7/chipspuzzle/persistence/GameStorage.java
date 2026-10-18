package com.saturn7.chipspuzzle.persistence;

import com.saturn7.chipspuzzle.game.GameSnapshot;

public interface GameStorage {
    GameSnapshot load();

    void save(GameSnapshot snapshot);
}
