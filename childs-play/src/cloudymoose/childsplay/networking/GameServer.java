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
	private boolean terminating = false;

	/**
	 * Connections are stored there because Kryo's ones are weirdly managed: the lastest one is the first, array
	 * recreated at every new connection, etc.
	 */
	Map<Integer, Connection> connections;
	public boolean lastTurn;

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
		synchronized (connections) {
			for (Map.Entry<Integer, Connection> me : connections.entrySet()) {
				me.getValue().addListener(new TurnListener(me.getKey()));
			}
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

		Connection cpc;
		synchronized (connections) {
			cpc = connections.get(currentPlayer);
		}

		if (cpc == null) {
			if (previousPlayer == currentPlayer) return;

			removePlayer(currentPlayer);
			startNextPlayerTurn(null); // Go to the next player
		} else {
			boolean isLastConnected = false;
			synchronized (connections) {
				isLastConnected = nbMaxPlayers > 1 && (connections.size() == 1);
			}

			if (isLastConnected && !lastTurn) {
				cpc.sendTCP(new Message.EndGame());
				currentPlayer = -1;
				terminating = true;
			} else {
				log("Sending StartTurn #%d to player %d, (%s) with %d commands.", currentTurn.turnNb, currentPlayer,
						cpc, (commandsToSend != null ? commandsToSend.length : 0));

				cpc.sendTCP(new Message.TurnRecap(currentTurn.turnNb, commandsToSend, previousPlayer, nbMaxPlayers));
			}

		}

	}

	protected void removePlayer(int playerId) {
		if (terminating) return;
		synchronized (connections) {
			log("Removing player #%d", playerId);
			Connection c = connections.remove(playerId);
			if (c != null) c.close();
		}
		if (playerId == currentPlayer) startNextPlayerTurn(null);
	}

	/** When a new client connects, sends him the init info, and starts the game if all clients are connected. */
	private class ConnectionListener extends Listener {
		private long randomSeed = System.currentTimeMillis();

		@Override
		public void connected(Connection connection) {
			connection.addListener(new Listener() {
				@Override
				public void received(Connection connection, Object object) {

					if (Message.Init.INIT_REQUEST.equals(object)) {
						int nbConnectedPlayers;
						int playerId;

						synchronized (connections) {
							playerId = connections.size() + 1;
							connections.put(playerId, connection);
							nbConnectedPlayers = connections.size(); // redundant, but clearer I guess
						}
						connection.sendTCP(new Message.Init(nbMaxPlayers == 2? "duel_map" : "map",  playerId, randomSeed));

						if (nbConnectedPlayers == nbMaxPlayers) {
							startGame();
						}
					}

				}
			});
		}

		@Override
		public void disconnected(Connection connection) {
			int playerId = -1;
			synchronized (connections) {
				for (Map.Entry<Integer, Connection> entry : connections.entrySet()) {
					if (connection.equals(entry.getValue())) {
						playerId = entry.getKey();
						break;
					}
				}
			}

			if (playerId != -1) {
				removePlayer(playerId);
			} else {
				log("An unrecognized client disconnected (%s)", connection);
			}

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
				Message.TurnRecap tr = (Message.TurnRecap) object;
				if (tr.turn == NetworkUtils.LAST_TURN) {
					lastTurn = true;
				}
				connection.sendTCP(new Message.Ack());
				startNextPlayerTurn(tr.commands);
			}

			if (object instanceof Message.EndGame) {
				removePlayer(playerId);
				connection.sendTCP(new Message.Ack());
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

	public void terminateConnection() {
		terminating = true;
		server.close();
	}
}
