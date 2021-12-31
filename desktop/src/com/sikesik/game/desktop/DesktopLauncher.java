package com.sikesik.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.sikesik.game.SlimeGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "SlimeGame";
		config.width = 1600;
		config.height = 896;
		new LwjglApplication(new SlimeGame(), config);
	}
}
