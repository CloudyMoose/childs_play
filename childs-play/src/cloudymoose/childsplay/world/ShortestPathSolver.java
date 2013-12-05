package cloudymoose.childsplay.world;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import cloudymoose.childsplay.world.hextiles.Direction;
import cloudymoose.childsplay.world.hextiles.HexTile;

/**
 * This class can solve a shortest path problem between hex tiles. It also takes
 * into account occupied tiles.
 */
public class ShortestPathSolver {
	/**
	 * Find the closest path between two tiles. The path will not use occupied
	 * tiles.
	 * 
	 * @param start
	 *            the start tile.
	 * @param end
	 *            the end tile.
	 * @return a list of {@link HexTile}s, representing the path. The first
	 *         element of the list is the start tile and the last element is the
	 *         end tile. If no path exists, null is returned.
	 */
	public static List<HexTile<TileData>> solve(HexTile<TileData> start,
			HexTile<TileData> end) {
		Set<HexTile<TileData>> visited = new HashSet<HexTile<TileData>>();
		Queue<Path> queue = new LinkedList<Path>();
		queue.add(new Path(start));

		while (!queue.isEmpty()) {
			Path p = queue.remove();

			// Have we already been here?
			if (visited.contains(p.head))
				continue;

			// Did we find the end?
			if (p.head == end)
				return p.toList();

			// Mark this tile as visited
			visited.add(p.head);

			// Add neighbors to queue
			for (Direction d : Direction.values()) {
				HexTile<TileData> neighbor = p.head.getNeighbor(d);
				if (neighbor == null)
					continue;

				if (neighbor.value.isOccupied())
					continue;

				queue.add(new Path(neighbor, p));
			}
		}

		return null;
	}

	/**
	 * Represents a path in reverse, the head object is the last tile of the
	 * path and the tail is the path leading up to that point.
	 */
	private static class Path {
		final HexTile<TileData> head;
		final Path tail;

		/**
		 * Creates a new path.
		 * 
		 * @param head
		 *            the head of the path.
		 * @param tail
		 *            the tail of the path.
		 */
		public Path(HexTile<TileData> head, Path tail) {
			this.head = head;
			this.tail = tail;
		}

		/**
		 * Creates a new path with no tail.
		 * 
		 * @param head
		 *            the head of the path.
		 */
		public Path(HexTile<TileData> head) {
			this(head, null);
		}

		/**
		 * Converts the tile to a list, in the correct order.
		 * 
		 * @return the path as a list, with the first element of the list is the
		 *         first tile of the path and the last element of the list is
		 *         the last element of the path.
		 */
		public List<HexTile<TileData>> toList() {
			if (tail == null) {
				List<HexTile<TileData>> list = new LinkedList<HexTile<TileData>>();
				list.add(head);
				return list;
			} else {
				List<HexTile<TileData>> list = tail.toList();
				list.add(head);
				return list;
			}
		}
	}
}
