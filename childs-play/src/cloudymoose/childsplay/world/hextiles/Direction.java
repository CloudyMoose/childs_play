package cloudymoose.childsplay.world.hextiles;

/**
 * Represents a direction in a hexagonal grid system. This class contains six
 * static objects representing all possible directions.
 */
public class Direction {
	public final int deltaX, deltaY;

	private Direction(int deltaX, int deltaY) {
		this.deltaX = deltaX;
		this.deltaY = deltaY;
	}

	public final static Direction UpLeft = new Direction(-1, 0);
	public final static Direction UpRight = new Direction(-1, 1);
	public final static Direction Right = new Direction(0, 1);
	public final static Direction DownRight = new Direction(1, 0);
	public final static Direction DownLeft = new Direction(1, -1);
	public final static Direction Left = new Direction(0, -1);
}
