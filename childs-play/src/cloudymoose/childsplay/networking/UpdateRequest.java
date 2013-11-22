package cloudymoose.childsplay.networking;

import cloudymoose.childsplay.world.Command;

/**
 * Base class for the requests.
 * 
 * They must be registered to Kryo (see {@link NetworkUtils#registerMessages(com.esotericsoftware.kryo.Kryo)}) before
 * being used.
 * 
 * They need default constructors, but they can be private or protected, as they are accessed by reflection.
 * 
 * The {@link UpdateRequest#toString()} method should also be properly implemented, to help with debugging.
 */
public abstract class UpdateRequest {

	public static class Print extends UpdateRequest {
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

	public static class Init extends UpdateRequest {
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
			return "Request: " + (this == INIT_REQUEST ? "INIT_REQUEST" : playerId + "/" + nbPlayers);
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

	public static class StartTurn extends UpdateRequest {
		public final int turn;
		public final Command[] lastCommands;

		public StartTurn() {
			this(0, null);
		}

		public StartTurn(int turn, Command[] lastCommands) {
			this.turn = turn;
			this.lastCommands = lastCommands;
		}

		@Override
		public String toString() {
			return "Request: Start Turn #" + turn + " "
					+ (lastCommands == null ? "no commands" : lastCommands.toString());
		}
	}

	public static class Ack extends UpdateRequest {
		@Override
		public String toString() {
			return "Request: Ack";
		}
	}
}
