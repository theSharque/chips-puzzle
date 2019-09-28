package com.saturn7.game.puzzle.full;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.game.puzzle.MainGame;

public class PuzzleActivity  extends AndroidApplication {
	public void onCreate (android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initialize(new MainGame( null ), false);
	}
}