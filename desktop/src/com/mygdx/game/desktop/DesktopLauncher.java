package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.MyGdxGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 480;
        config.height = 800;
//        config.vSyncEnabled = false;
//        config.foregroundFPS = 0;
//        config.backgroundFPS = 0;
		new LwjglApplication(new MyGdxGame(), config);
	}
}
