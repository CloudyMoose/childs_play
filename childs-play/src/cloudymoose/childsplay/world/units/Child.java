package cloudymoose.childsplay.world.units;

import cloudymoose.childsplay.world.Player;
import cloudymoose.childsplay.world.TileData;
import cloudymoose.childsplay.world.hextiles.HexTile;

public class Child extends Unit {

	public Child(Player owner, HexTile<TileData> position) {
		super(owner, position, 20, 1, 5, 1, 1);
	}

	@Override
	public String toString() {
		return "Child " + id;
	}

}
