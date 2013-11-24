package cloudymoose.childsplay.networking;

import cloudymoose.childsplay.world.commands.Command;

/**
 * Base class for the requests.
 * 
 * They must be registered to Kryo (see {@link NetworkUtils#registerMessages(com.esotericsoftware.kryo.Kryo)}) before
 * being used.
 * 
 * They need default constructors, but they can be private or protected, as they are accessed by reflection.
 * 
 * The {@link Message#toString()} method should also be properly implemented, to help with debugging.
 */
public abstract class Message {

	public static class Print extends Message {
		public final String text;

		protected Print() {
			this("");
		};

		public Print(String text) {
			super();
			this.text = text;
		}

		@Override
		public String toString() {
			return "Request: Print '" + text + "'";
		}

	}

	public static class Init extends Message {
		public static final Init INIT_REQUEST = new Init(-1, -1);
		public final int playerId;
		public final int nbPlayers;

		public Init() {
			this(0, 0);
		}

		public Init(int playerId, int nbPlayers) {
			this.playerId = playerId;
			this.nbPlayers = nbPlayers;
		}

		@Override
		public String toString() {
			return "Init: " + (this == INIT_REQUEST ? "INIT_REQUEST" : playerId + "/" + nbPlayers);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			Init other = (Init) obj;
			if (nbPlayers != other.nbPlayers) return false;
			if (playerId != other.playerId) return false;
			return true;
		}

	}

	/**
	 * Recap of what happened during the turn. Used to start a turn by sending the recap of the other player's action to
	 * a client, or to end a turn when the client sends his actions to the server.
	 */
	public static class TurnRecap extends Message {
		public final int turn;
		/** Warning: will be <code>null</code> for the first player on the first turn */
		public final Command[] commands;

		public TurnRecap() {
			this(0, null);
		}

		public TurnRecap(int turn, Command[] lastCommands) {
			this.turn = turn;
			this.commands = lastCommands;
		}

		@Override
		public String toString() {
			return "Request: Start Turn #" + turn + " " + (commands == null ? "no commands" : commands.toString());
		}
	}

	public static class Ack extends Message {
		@Override
		public String toString() {
			return "Request: Ack";
		}
	}
}
