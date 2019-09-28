package com.game.puzzle;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

public class DesktopGame {
	public static void main(String[] args) {
		new LwjglApplication(new MainGame( null ), "Game", 320, 480, false);
	}
}
