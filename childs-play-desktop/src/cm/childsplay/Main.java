package cm.childsplay;

import java.awt.Dimension;
import java.awt.Toolkit;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Child's Play";
		cfg.useGL20 = false;
		cfg.width = ChildsPlayGame.VIEWPORT_WIDTH;
		cfg.height = ChildsPlayGame.VIEWPORT_HEIGHT;

		// put the window at center-top to have the console visible
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		cfg.x = (screenSize.width - cfg.width) / 2;
		cfg.y = 20;

		new LwjglApplication(new ChildsPlayGame(), cfg);

	}
}
