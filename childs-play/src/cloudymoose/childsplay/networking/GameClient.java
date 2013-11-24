package cloudymoose.childsplay.networking;

import java.io.IOException;

import cloudymoose.childsplay.ChildsPlayGame;
import cloudymoose.childsplay.networking.Message.TurnRecap;
import cloudymoose.childsplay.world.commands.Command;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage.KeepAlive;
import com.esotericsoftware.kryonet.Listener;

public class GameClient {

	private Connection connection;
	private ChildsPlayGame game;
	private TurnListener turnListener;

	public GameClient(ChildsPlayGame game) {
		this.game = game;
	}

	public void connect() throws IOException {

		Client client = new Client();
		connection = client;

		NetworkUtils.registerMessages(client.getKryo());

		client.start();
		client.connect(5000, "localhost", 54555, 54777);
	}

	public void init() {
		connection.addListener(new Listener() {
			@Override
			public void received(Connection connection, final Object object) {
				System.err.println("InitListener - " + object);

				if (object instanceof Message.Init) {
					connection.removeListener(this);
					turnListener = new TurnListener();

					connection.addListener(turnListener);

					// Execute it in the Gdx thread (graphic calls are not valid in other threads)
					Gdx.app.postRunnable(new Runnable() {
						public void run() {
							game.initWorld((Message.Init) object);
						};
					});
				}
			}
		});
		connection.sendTCP(Message.Init.INIT_REQUEST);
	}

	public void send(Command[] commands) {
		connection.sendTCP(new Message.TurnRecap(-1 /* No one cares here */, commands));
		turnListener.enabled = true;
	}

	private class TurnListener extends Listener {
		/** Used to avoid problems while a turn is being played on the client */
		public boolean enabled = true;

		@Override
		public void received(Connection connection, final Object object) {
			if (object instanceof KeepAlive) return;
			if (!enabled) {
				System.err.println("Turn listener OFF, received: " + object);
				return;
			}

			if (object instanceof TurnRecap) {
				// For calls to libgdx, use postRunnable to make sure it will be run in the gdx thread
				Gdx.app.postRunnable(new Runnable() {
					public void run() {
						game.notifyTurnRecapReceived((TurnRecap) object);
					}
				});
				enabled = false;
			}
		}
	}
}
