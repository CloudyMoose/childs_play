package cloudymoose.childsplay.world.hextiles;

/**
 * Represents a direction in a hexagonal grid system. This class contains six static objects representing all possible
 * directions.
 * 
 * Changed to an enum to be able to use utility methods such as {@link #values()}
 */
public enum Direction {
	UpLeft(-1, 0),
	UpRight(-1, 1),
	Right(0, 1),
	DownRight(1, 0),
	DownLeft(1, -1),
	Left(0, -1);

	public final int deltaX, deltaY;

	private Direction(int deltaX, int deltaY) {
		this.deltaX = deltaX;
		this.deltaY = deltaY;
	}
}
