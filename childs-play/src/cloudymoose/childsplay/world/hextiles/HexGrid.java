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

	public HexGrid(float tileSize) {
		this.tiles = new HashMap<Integer, Map<Integer, HexTile<T>>>();
		this.tileSize = tileSize;
		this.tileSet = new HashSet<HexTile<T>>();
	}

	public float getTileSize() {
		return tileSize;
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param value
	 * @return
	 */
	public HexTile<T> addValue(int x, int y, T value) {
		HexTile<T> tile = new HexTile<T>(x, y, value, this);
		addTile(tile);
		return tile;
	}

	public T getValue(int x, int y) {
		HexTile<T> tile = getTile(x, y);
		return tile == null ? null : tile.getValue();
	}

	private void addTile(HexTile<T> tile) {
		Map<Integer, HexTile<T>> xAxis = tiles.get(tile.getX());

		if (xAxis == null) {
			xAxis = new HashMap<Integer, HexTile<T>>();
			tiles.put(tile.getX(), xAxis);
		}

		xAxis.put(tile.getY(), tile);
		tileSet.add(tile);
	}

	public HexTile<T> getTile(int x, int y) {
		Map<Integer, HexTile<T>> xAxis = tiles.get(x);

		if (xAxis == null)
			return null;

		return xAxis.get(y);
	}

	public HexTile<T> getTileFromPosition(Vector3 position) {
		float x = -position.y * 2f / (3f * tileSize);
		float y = (float) (1f / 3f * Math.sqrt(3) * position.x + (1f / 3f)
				* position.y)
				/ tileSize;
		//x /= 2;
		//y /= 2;
		return getTile(Math.round(x), Math.round(y));
	}

	public T getNeighbor(int x, int y, Direction dir) {
		HexTile<T> tile = getTile(x, y);
		return tile == null ? null : tile.getNeighbor(dir).getValue();
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
