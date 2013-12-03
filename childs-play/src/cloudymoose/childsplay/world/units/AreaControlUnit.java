package cloudymoose.childsplay.world.units;

import cloudymoose.childsplay.world.Area;
import cloudymoose.childsplay.world.Player;
import cloudymoose.childsplay.world.TileData;
import cloudymoose.childsplay.world.hextiles.HexTile;

public abstract class AreaControlUnit extends Unit {

	protected final Area area;
	protected Player owner;
	protected int incomingControlRequests;

	public AreaControlUnit(Player owner, HexTile<TileData> tile) {
		super(owner, tile, 25, 3, 0, 0, 0);
		this.area = tile.value.getArea();
		this.owner = owner;
	}

	@Override
	/** Overriden since HP here is used as control indicator. The unit cannot die */
	public boolean isDead() {
		return false;
	}

	public String toString() {
		return "ACU " + id;
	}

}
