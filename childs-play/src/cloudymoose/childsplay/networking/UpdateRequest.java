package cloudymoose.childsplay.networking;

/**
 * Base class for the requests.
 * 
 * They must be registered to Kryo (see {@link NetworkPeer#registerMessages(com.esotericsoftware.kryo.Kryo)}) before
 * being used.
 * 
 * They need default constructors, but they can be private or protected, as they are accessed by reflection.
 * 
 * The {@link UpdateRequest#toString()} method should also be properly implemented, to help with debugging.
 */
public abstract class UpdateRequest {

	public static class Move extends UpdateRequest {
		public final int unitId;
		public final float destX;
		public final float destY;

		protected Move() {
			this(0, 0, 0);
		}

		public Move(int unitId, float destX, float destY) {
			super();
			this.unitId = unitId;
			this.destX = destX;
			this.destY = destY;
		}

		@Override
		public String toString() {
			return String.format("Request: Move %d to (%f,%f)", unitId, destX, destY);
		}

	}

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
}
