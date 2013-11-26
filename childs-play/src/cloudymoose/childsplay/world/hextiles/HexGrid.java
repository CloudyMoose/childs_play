package cloudymoose.childsplay.world.hextiles;

import java.util.AbstractCollection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.math.Vector3;

/**
 * Represents a hexagonal grid, where each of the tiles holds a value of a
 * generic type. Contains methods to add and get tiles
 * 
 * @param <T>
 *            the value type to store in the tiles.
 */
public class HexGrid<T> extends AbstractCollection<HexTile<T>> {
	private final Map<Integer, Map<Integer, HexTile<T>>> tiles;
	private final float tileSize;
	private final Set<HexTile<T>> tileSet;

	/**
	 * Creates a new, empty hexagonal grid.
	 * 
	 * @param tileSize
	 *            the size of a tile, in world coordinates.
	 */
	public HexGrid(float tileSize) {
		this.tiles = new HashMap<Integer, Map<Integer, HexTile<T>>>();
		this.tileSize = tileSize;
		this.tileSet = new HashSet<HexTile<T>>();
	}

	/**
	 * Gets the tile size of this grid, in world coordinates.
	 * 
	 * @return the tile size of this grid, in world coordinates.
	 */
	public float getTileSize() {
		return tileSize;
	}

	/**
	 * Adds a value to a tile in this grid.
	 * 
	 * @param q
	 *            the q coordinate.
	 * @param r
	 *            the r coordinate.
	 * @param value
	 *            the value to put at that tile.
	 * @return the newly created tile.
	 */
	public HexTile<T> addValue(int q, int r, T value) {
		HexTile<T> tile = new HexTile<T>(q, r, value, this);
		addTile(tile);
		return tile;
	}

	/**
	 * Gets the value of a tile in the grid.
	 * 
	 * @param q
	 *            the q coordinate.
	 * @param r
	 *            the r coordinate.
	 * @return the value of the tile at that grid location.
	 */
	public T getValue(int q, int r) {
		HexTile<T> tile = getTile(q, r);
		return tile == null ? null : tile.getValue();
	}

	private void addTile(HexTile<T> tile) {
		Map<Integer, HexTile<T>> qAxis = tiles.get(tile.getQ());

		if (qAxis == null) {
			qAxis = new HashMap<Integer, HexTile<T>>();
			tiles.put(tile.getQ(), qAxis);
		}

		qAxis.put(tile.getR(), tile);
		tileSet.add(tile);
	}

	/**
	 * Gets a tile at a specific location in the grid.
	 * 
	 * @param q
	 *            the q coordinate.
	 * @param r
	 *            the r coordinate.
	 * @return the tile located at that location.
	 */
	public HexTile<T> getTile(int q, int r) {
		Map<Integer, HexTile<T>> qAxis = tiles.get(q);

		if (qAxis == null)
			return null;

		return qAxis.get(r);
	}

	/**
	 * Gets the tile containing a world position.
	 * 
	 * @param position
	 *            the world position.
	 * @return the tile that contains that world position, or null, if no tile
	 *         exists there.
	 */
	public HexTile<T> getTileFromPosition(Vector3 position) {
		float x = -position.y * 2f / (3f * tileSize);
		float y = (float) (1f / 3f * Math.sqrt(3) * position.x + (1f / 3f)
				* position.y)
				/ tileSize;
		return getTile(Math.round(x), Math.round(y));
	}

	@Override
	public String toString() {
		return tiles.toString();
	}

	@Override
	public Iterator<HexTile<T>> iterator() {
		return tileSet.iterator();
	}

	@Override
	public int size() {
		return tileSet.size();
	}
}
