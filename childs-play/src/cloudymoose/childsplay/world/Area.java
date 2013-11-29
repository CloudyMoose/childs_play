package cloudymoose.childsplay.world;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import cloudymoose.childsplay.world.hextiles.HexTile;

public class Area extends AbstractCollection<HexTile<TileData>> {

	private final HashSet<HexTile<TileData>> tiles;
	/** Tile that has to be occupied to get the control of the area */
	private final HexTile<TileData> controlTile;

	private Player owner;

	public Area(Collection<HexTile<TileData>> tiles, HexTile<TileData> controlTile) {
		this.tiles = new HashSet<HexTile<TileData>>(tiles);
		this.controlTile = controlTile;

		for (HexTile<TileData> tile : tiles) {
			tile.value.setArea(this);
		}
	}

	public Player getOwner() {
		return owner;
	}

	/** Set it to <code>null</code> to make it uncontrolled */
	public void setOwner(Player owner) {
		this.owner = owner;
	}

	public HexTile<TileData> getControlTile() {
		return controlTile;
	}

	@Override
	public boolean add(HexTile<TileData> tile) {
		tile.value.setArea(this);
		return super.add(tile);
	}

	@Override
	public Iterator<HexTile<TileData>> iterator() {
		return tiles.iterator();
	}

	@Override
	public int size() {
		return tiles.size();
	}

}
