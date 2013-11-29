package cloudymoose.childsplay.world.hextiles;

import com.badlogic.gdx.math.Vector3;

/**
 * Represents a hexagonal tile in a hexagonal tile. Every tile can also be associated with a value that contains
 * additional information about this tile. Contains method to show and set neighboring tiles, which is a convenient way
 * of building up maps. Internally, this is implemented with axial coordinates.
 * 
 * @param <T>
 *            the value type.
 */
public class HexTile<T> {
	protected final int q, r;
	public final T value;
	private final HexGrid<T> grid;

	/**
	 * Get the q coordinate on the grid.
	 * 
	 * @return the q coordinate on the grid.
	 */
	public int getQ() {
		return q;
	}

	/**
	 * Get the r coordinate on the grid.
	 * 
	 * @return the r coordinate on the grid.
	 */
	public int getR() {
		return r;
	}

	/**
	 * Get the world position of the center of this tile.
	 * 
	 * @return the world position of the center of this tile.
	 */
	public Vector3 getPosition() {
		return new Vector3((float) (grid.getTileSize() * Math.sqrt(3) * (r + q / 2.0)), -grid.getTileSize()
				* (3.0f / 2.0f) * q, 0);
	}

	/**
	 * Creates a new hexagonal tile, belonging to a specific hexagonal grid.
	 * 
	 * @param q
	 *            the q coordinate on the grid.
	 * @param r
	 *            the r coordinate on the grid.
	 * @param value
	 *            the value of this tile.
	 * @param grid
	 *            the hexagonal grid this tile belongs to.
	 */
	protected HexTile(int q, int r, T value, HexGrid<T> grid) {
		this.grid = grid;
		this.q = q;
		this.r = r;
		this.value = value;
	}

	/**
	 * Gets the tile that neighbors this tile in a specific direction, or null, if no tile is there.
	 * 
	 * @param dir
	 *            the direction to look in.
	 * @return the neighboring tile in that direction, or null of no tile.
	 */
	public HexTile<T> getNeighbor(Direction dir) {
		return grid.getTile(this.q + dir.deltaX, this.r + dir.deltaY);
	}

	/**
	 * Set the neighboring tile in a specific direction.
	 * 
	 * @param dir
	 *            the direction.
	 * @param value
	 *            the value to set for the new tile.
	 * @return the created tile.
	 */
	public HexTile<T> setNeighbor(Direction dir, T value) {
		int x = this.q + dir.deltaX;
		int y = this.r + dir.deltaY;
		return grid.addValue(x, y, value);
	}

	@Override
	public String toString() {
		return value.toString();
	}
}
