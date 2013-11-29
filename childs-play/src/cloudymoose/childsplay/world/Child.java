package cloudymoose.childsplay.world;

import cloudymoose.childsplay.world.hextiles.HexTile;

public class Child extends Unit {

	public Child(Player owner, HexTile<TileData> position) {
		super(owner, position, 20, 1, 5, 1, 1);
	}

}
