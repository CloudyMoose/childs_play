package cloudymoose.childsplay.world;

import java.util.Arrays;
import java.util.EnumSet;

import cloudymoose.childsplay.world.hextiles.Direction;
import cloudymoose.childsplay.world.units.Unit;

public class TileData {
	public TileType type;
	protected Unit occupant;
	public final EnumSet<Direction> borders;
	protected Area area;

	public TileData(TileType type) {
		this.type = type;
		borders = EnumSet.noneOf(Direction.class);
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

	public void addBorders(Direction... directions) {
		borders.addAll(Arrays.asList(directions));
	}

	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public String toString() {
		return String.format("{area: %s, occupant: %s}", area, occupant);
	}

	public boolean isControlTile() {
		// TODO Auto-generated method stub
		return false;
	}
}
