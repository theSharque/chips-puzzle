package com.game.puzzle;

import com.badlogic.gdx.backends.android.AndroidApplication;

public class GameandroidActivity extends AndroidApplication {
	public void onCreate (android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initialize(new MainGame( null ), false);
	}
}