package cloudymoose.childsplay.world;

import com.badlogic.gdx.graphics.Color;

public class TileData {
	public final Color color;
	protected Unit occupant;

	public TileData(Color color) {
		this.color = color;
	}

	public Unit getOccupant() {
		return occupant;
	}

	/** Set the occupant of this tile. Set it to null to remove it. */
	public void setOccupant(Unit occupant) {
		this.occupant = occupant;
	}

	public boolean isOccupied() {
		return occupant != null;
	}
}
