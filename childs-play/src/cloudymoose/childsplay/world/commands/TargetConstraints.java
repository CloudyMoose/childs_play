package cloudymoose.childsplay.world.commands;

import cloudymoose.childsplay.world.TileData;
import cloudymoose.childsplay.world.hextiles.HexTile;
import cloudymoose.childsplay.world.units.EnvironmentUnit;
import cloudymoose.childsplay.world.units.Unit;

public abstract class TargetConstraints {
	public static final int INFINITE_RANGE = -1;

	public final boolean considerObstacles;
	public final int maxRange;
	public final HexTile<TileData> origin;

	public TargetConstraints(boolean considerObstacles, int maxRange, HexTile<TileData> origin) {
		this.considerObstacles = considerObstacles;
		this.maxRange = maxRange;
		this.origin = origin;
	}

	public abstract boolean isTileTargetable(HexTile<TileData> tile);

	public static class Empty extends TargetConstraints {

		public Empty(boolean considerObstacles, int maxRange, HexTile<TileData> origin) {
			super(considerObstacles, maxRange, origin);
		}

		@Override
		public boolean isTileTargetable(HexTile<TileData> tile) {
			return !tile.value.isOccupied();
		}

	}

	public static class HasEnemy extends TargetConstraints {

		public final int currentPlayerId;

		public HasEnemy(boolean considerObstacles, int maxRange, HexTile<TileData> origin, int currentPlayerId) {
			super(considerObstacles, maxRange, origin);
			this.currentPlayerId = currentPlayerId;
		}

		@Override
		public boolean isTileTargetable(HexTile<TileData> tile) {
			Unit occupant = tile.value.getOccupant();
			if (occupant == null) return false;
			if (occupant instanceof EnvironmentUnit) return false;
			if (occupant.getPlayerId() == currentPlayerId) return false;
			return true;
		}
	}
}
