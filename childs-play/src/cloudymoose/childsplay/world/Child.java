package cloudymoose.childsplay.world;

import cloudymoose.childsplay.world.hextiles.HexTile;

public class Child extends Unit {

	public Child(Player owner, HexTile<?> position) {
		super(owner, position, 20, 5, 0);
	}

}
