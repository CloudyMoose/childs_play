package cloudymoose.childsplay.world;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import cloudymoose.childsplay.world.commands.TargetConstraints;
import cloudymoose.childsplay.world.hextiles.Direction;
import cloudymoose.childsplay.world.hextiles.HexGrid;
import cloudymoose.childsplay.world.hextiles.HexTile;

public class WorldMap extends HexGrid<TileData> {

	public WorldMap() {
		super(Constants.TILE_SIZE);
	}

	public Set<HexTile<TileData>> findTiles(TargetConstraints targetConstraints) {
		Set<HexTile<TileData>> results = new HashSet<HexTile<TileData>>();

		Queue<HexTile<TileData>> fringe = new LinkedList<HexTile<TileData>>();
		Queue<HexTile<TileData>> fringe2 = new LinkedList<HexTile<TileData>>();
		fringe.add(targetConstraints.origin);

		for (int i = 0; i < targetConstraints.maxRange; i++) {
			HexTile<TileData> fringeTile;
			while (!fringe.isEmpty()) {
				fringeTile = fringe.remove();
				for (Direction d : Direction.values()) {
					HexTile<TileData> tile = fringeTile.getNeighbor(d);

					if (tile != null && targetConstraints.isTileTargetable(tile)) {
						boolean isNew = results.add(tile);
						if (isNew) {
							fringe2.add(tile);
						}
					}
				}
			}

			fringe.addAll(fringe2);
			fringe2.clear();
		}

		return results;
	}
}
