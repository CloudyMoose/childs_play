package cloudymoose.childsplay.networking;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cloudymoose.childsplay.world.commands.Command;

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
		gameStarted = false;
	}

	public void start() throws IOException {
		server.start();
		server.bind(54555, 54777);

		server.addListener(new ConnectionListener());
	}

	protected void startGame() {
		if (gameStarted) return;
		for (int i = 0; i < server.getConnections().length; i++) {
			server.getConnections()[i].addListener(new TurnListener(i));
		}
		System.err.println("Start Game!");
		gameStarted = true;
		startNextPlayerTurn(null);
	}

	protected void startNextPlayerTurn(Command[] lastCommandSet) {
		if (lastCommandSet != null) {
			// Register the last commands
			currentTurn.commands[currentPlayer] = lastCommandSet;
		}

		// Select the next player
		if (currentPlayer >= nbMaxPlayers - 1) {
			currentPlayer = 0;

			// Register the next commands as part of a new turn
			currentTurn = new TurnCommands(actionLog.size());
			actionLog.add(currentTurn);
		} else {
			currentPlayer += 1;
		}
		// Send the commands to the next player
		System.err.println("Sending StartTurn #" + currentTurn.turnNb + " to player " + (currentPlayer + 1) + " ("
				+ server.getConnections()[currentPlayer] + ")");
		server.getConnections()[currentPlayer].sendTCP(new Message.TurnRecap(currentTurn.turnNb, lastCommandSet));
	}

	/** When a new client connects, sends him the init info, and starts the game if all clients are connected. */
	private class ConnectionListener extends Listener {
		@Override
		public void connected(Connection connection) {
			connection.addListener(new Listener() {
				@Override
				public void received(Connection connection, Object object) {

					if (Message.Init.INIT_REQUEST.equals(object)) {
						connection.sendTCP(new Message.Init(server.getConnections().length, nbMaxPlayers));

						if (server.getConnections().length == nbMaxPlayers) {
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
