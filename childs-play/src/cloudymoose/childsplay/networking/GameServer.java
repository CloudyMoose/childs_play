package cloudymoose.childsplay.networking;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cloudymoose.childsplay.world.commands.Command;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class GameServer {

	// TODO: set to 2 for 2 players (duh)
	private final int nbMaxPlayers;
	private final Server server;

	private TurnCommands currentTurn;
	private int currentPlayer;
	private List<TurnCommands> actionLog;
	private boolean gameStarted;

	/**
	 * Connections are stored there because Kryo's ones are weirdly managed: the lastest one is the first, array
	 * recreated at every new connection, etc.
	 */
	Map<Integer, Connection> connections;

	public GameServer() {
		this(2);
	}

	public GameServer(int nbPlayers) {
		nbMaxPlayers = nbPlayers;
		server = new Server();
		NetworkUtils.registerMessages(server.getKryo());

		currentTurn = null;
		currentPlayer = nbMaxPlayers;
		actionLog = new ArrayList<TurnCommands>();
		connections = new HashMap<Integer, Connection>();
		gameStarted = false;
	}

	public void start() throws IOException {
		server.start();
		server.bind(54555, 54777);

		server.addListener(new ConnectionListener());
	}

	protected void startGame() {
		if (gameStarted) return;
		for (Map.Entry<Integer, Connection> me : connections.entrySet()) {
			me.getValue().addListener(new TurnListener(me.getKey()));
		}
		log("Start Game!");
		gameStarted = true;
		startNextPlayerTurn(null);
	}

	protected void startNextPlayerTurn(Command[] lastCommandSet) {
		if (lastCommandSet != null) {
			// Register the last commands
			currentTurn.commands[currentPlayer - 1] = lastCommandSet;
			log("Registering the commands of player #" + (currentPlayer));
		}

		// Select the next player
		int previousPlayer = currentPlayer;
		if (currentPlayer >= nbMaxPlayers) {
			currentPlayer = 1;

			// Register the next commands as part of a new turn
			currentTurn = new TurnCommands(actionLog.size());
			actionLog.add(currentTurn);
			log("A new turn is starting");
		} else {
			currentPlayer += 1;
		}
		log("The next player is player #" + currentPlayer);
		// Send the commands to the next player

		Command[] commandsToSend = nbMaxPlayers == 1 ? null : lastCommandSet;

		log("Sending StartTurn #%d to player %d, (%s) with %d commands.", currentTurn.turnNb, currentPlayer,
				connections.get(currentPlayer), (commandsToSend != null ? commandsToSend.length : 0));

		connections.get(currentPlayer).sendTCP(
				new Message.TurnRecap(currentTurn.turnNb, commandsToSend, previousPlayer, nbMaxPlayers));
	}

	/** When a new client connects, sends him the init info, and starts the game if all clients are connected. */
	private class ConnectionListener extends Listener {
		@Override
		public void connected(Connection connection) {
			connection.addListener(new Listener() {
				@Override
				public void received(Connection connection, Object object) {

					if (Message.Init.INIT_REQUEST.equals(object)) {
						int playerId = connections.size() + 1;
						connections.put(playerId, connection);
						connection.sendTCP(new Message.Init(playerId, nbMaxPlayers));

						if (connections.size() == nbMaxPlayers) {
							server.removeListener(ConnectionListener.this);
							startGame();
						}
					}

				}
			});
		}
	}

	/** Allows the succession of the turns */
	private class TurnListener extends Listener {
		private int playerId;

		public TurnListener(int playerId) {
			this.playerId = playerId;
		}

		@Override
		public void received(Connection connection, Object object) {
			if (currentPlayer != playerId) return;

			if (object instanceof Message.TurnRecap) {
				connection.sendTCP(new Message.Ack());
				startNextPlayerTurn(((Message.TurnRecap) object).commands);
			}

		}
	}

	/** Custom method to make it easier to switch the logger in case the server is started without libgdx */
	private static void log(String message, Object... params) {
		Gdx.app.log("GameServer", String.format(message, params));
	}

	/** Data structure used to store the actions */
	private class TurnCommands {
		public final int turnNb;
		public final Command[][] commands;

		public TurnCommands(int turnNb) {
			this.turnNb = turnNb;
			this.commands = new Command[nbMaxPlayers][];
		}
	}
}
