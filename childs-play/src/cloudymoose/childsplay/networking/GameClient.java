package cloudymoose.childsplay.networking;

import java.io.IOException;
import java.net.InetAddress;

import cloudymoose.childsplay.ChildsPlayGame;
import cloudymoose.childsplay.NotificationService;
import cloudymoose.childsplay.networking.Message.EndGame;
import cloudymoose.childsplay.networking.Message.TurnRecap;
import cloudymoose.childsplay.world.commands.Command;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage.KeepAlive;
import com.esotericsoftware.kryonet.Listener;

public class GameClient {

	private static final String TAG = "GameClient";

	private Connection connection;
	private ChildsPlayGame game;
	private TurnListener turnListener;
	private int playerId;
	private int nbPlayers;
	private boolean terminating = false;
	private NotificationService notificationService;

	public GameClient(ChildsPlayGame game, NotificationService notificationService) {
		this.game = game;
		this.notificationService = notificationService;
	}

	/** @return <code>true</code> is the client found a server and is successfully connected */
	public boolean connect() throws IOException {

		Client client = new Client();
		connection = client;

		NetworkUtils.registerMessages(client.getKryo());

		client.start();
		InetAddress serverAddress = client.discoverHost(54777, 5000);

		if (serverAddress == null) {
			Gdx.app.error("Client", "No server found");
			return false;
		} else {
			client.connect(5000, serverAddress, 54555, 54777);
			return true;
		}
	}

	public void init() {
		connection.addListener(new Listener() {
			@Override
			public void received(Connection connection, final Object object) {
				Gdx.app.log(TAG, "InitListener - " + object);

				if (object instanceof Message.Init) {
					final Message.Init initData = (Message.Init) object;
					playerId = initData.playerId;
					nbPlayers = initData.nbPlayers;

					connection.removeListener(this);
					turnListener = new TurnListener();

					connection.addListener(turnListener);

					// Execute it in the Gdx thread (graphic calls are not valid in other threads)
					Gdx.app.postRunnable(new Runnable() {
						public void run() {
							game.initWorld(initData);
						};
					});
				}
			}
		});
		connection.sendTCP(Message.Init.INIT_REQUEST);
	}

	public void send(Command[] commands) {
		send(commands, false);
	}
	
	public void send(Command[] commands, boolean isLast) {
		connection.sendTCP(new Message.TurnRecap(isLast? NetworkUtils.LAST_TURN: NetworkUtils.IDGAF_TURN, commands, playerId, nbPlayers));
		turnListener.enabled = true;
	}

	private class TurnListener extends Listener {
		/** Used to avoid problems while a turn is being played on the client */
		public boolean enabled = true;

		@Override
		public void received(Connection connection, final Object object) {
			if (object instanceof KeepAlive) return;
			if (!enabled) {
				Gdx.app.log(TAG, "Turn listener OFF, received: " + object);
				return;
			}

			if (object instanceof TurnRecap) {
				
				// On android the gdx thread is paused, the notification will not fire.
				if (!game.hasFocus()) notificationService.notifySomething();

				// For calls to libgdx, use postRunnable to make sure it will be run in the gdx thread
				Gdx.app.postRunnable(new Runnable() {
					public void run() {
						game.notifyTurnRecapReceived((TurnRecap) object);
					}
				});
				enabled = false;
			}

			if (object instanceof EndGame) {
				Gdx.app.postRunnable(new Runnable() {
					public void run() {
						game.notifyEndGameReceived();
					}
				});
				enabled = false;
			}
		}

		@Override
		public void disconnected(Connection connection) {
			if (!terminating) game.onClientDisconnection();
		}
	}

	public void terminateConnection() {
		terminating = true;
		connection.sendTCP(new Message.EndGame());
		connection.close();
	}
}
