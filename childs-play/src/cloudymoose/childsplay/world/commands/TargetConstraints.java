package cloudymoose.childsplay.world.commands;

import cloudymoose.childsplay.world.Player;
import cloudymoose.childsplay.world.hextiles.HexTile;

public abstract class TargetConstraints {
	public static final int INFINITE_RANGE = -1;

	public final boolean considerObstacles;
	public final int maxRange;
	public final HexTile<?> origin;

	public TargetConstraints(boolean considerObstacles, int maxRange, HexTile<?> origin) {
		this.considerObstacles = considerObstacles;
		this.maxRange = maxRange;
		this.origin = origin;
	}

	public abstract boolean isTileTargetable(HexTile<?> tile);

	public static class Empty extends TargetConstraints {

		public Empty(boolean considerObstacles, int maxRange, HexTile<?> origin) {
			super(considerObstacles, maxRange, origin);
		}

		@Override
		public boolean isTileTargetable(HexTile<?> tile) {
			return !tile.isOccupied();
		}

	}

	public static class HasEnemy extends TargetConstraints {

		public final int currentPlayerId;

		public HasEnemy(boolean considerObstacles, int maxRange, HexTile<?> origin, int currentPlayerId) {
			super(considerObstacles, maxRange, origin);
			this.currentPlayerId = currentPlayerId;
		}

		@Override
		public boolean isTileTargetable(HexTile<?> tile) {
			return tile.isOccupied() && tile.getOccupant().getPlayerId() != currentPlayerId
					&& tile.getOccupant().getPlayerId() >= Player.GAIA_ID;
		}
	}
}
