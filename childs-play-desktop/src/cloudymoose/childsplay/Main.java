package cloudymoose.childsplay;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.BindException;

import cloudymoose.childsplay.networking.GameClient;
import cloudymoose.childsplay.networking.GameServer;
import cloudymoose.childsplay.networking.NetworkPeer;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) throws IOException {

		NetworkPeer peer;
		try {
			GameServer server = new GameServer();
			server.start();
			peer = server;
		} catch (BindException e) {
			System.out.println("Address already in use. Starting a client...");
			GameClient client = new GameClient();
			client.connect();
			peer = client;
		}

		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Child's Play";
		cfg.useGL20 = false;
		cfg.width = ChildsPlayGame.VIEWPORT_WIDTH;
		cfg.height = ChildsPlayGame.VIEWPORT_HEIGHT;

		// put the window at center-top to have the console visible
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		cfg.x = (screenSize.width - cfg.width) / 2;
		cfg.y = 20;

		new LwjglApplication(ChildsPlayGame.createInstance(peer), cfg);

	}
}
