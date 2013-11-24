package cloudymoose.childsplay.world.hextiles;

import com.badlogic.gdx.math.Vector2;

/**
 * Represents a hexagonal tile in a hexagonal tile. Every tile can also be
 * associated with a value that contains additional information about this tile.
 * Contains method to show and set neighboring tiles, which is a convenient way
 * of building up maps. Internally, this is implemented with axial coordinates.
 * 
 * @param <T>
 *            the value type.
 */
public class HexTile<T> {
	private final int x, y;
	private final T value;
	private final HexGrid<T> grid;

	/**
	 * Get the x position on the grid.
	 * 
	 * @return the x position on the grid.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Get the y position on the grid.
	 * 
	 * @return the y position on the grid.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Get the value of this tile.
	 * 
	 * @return the value of this tile.
	 */
	public T getValue() {
		return value;
	}

	public Vector2 getPosition() {
		return new Vector2(
				(float) (grid.getTileSize() * Math.sqrt(3) * (y + x / 2.0)),
				-grid.getTileSize() * (3.0f / 2.0f) * x);
	}

	/**
	 * Creates a new hexagonal tile, belonging to a specific hexagonal grid.
	 * 
	 * @param x
	 *            the x position on the grid.
	 * @param y
	 *            the y position on the grid.
	 * @param value
	 *            the value of this tile.
	 * @param grid
	 *            the hexagonal grid this tile belongs to.
	 */
	protected HexTile(int x, int y, T value, HexGrid<T> grid) {
		this.grid = grid;
		this.x = x;
		this.y = y;
		this.value = value;
	}

	/**
	 * Gets the tile that neighbors this tile in a specific direction, or null,
	 * if no tile is there.
	 * 
	 * @param dir
	 *            the direction to look in.
	 * @return the neighboring tile in that direction, or null of no tile.
	 */
	public HexTile<T> getNeighbor(Direction dir) {
		return grid.getTile(this.x + dir.deltaX, this.y + dir.deltaY);
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
		int x = this.x + dir.deltaX;
		int y = this.y + dir.deltaY;
		return grid.addValue(x, y, value);
	}

	@Override
	public String toString() {
		return value.toString();
	}
}
