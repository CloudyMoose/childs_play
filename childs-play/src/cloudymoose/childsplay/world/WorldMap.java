package cloudymoose.childsplay.world;

import cloudymoose.childsplay.world.hextiles.HexGrid;

import com.badlogic.gdx.graphics.Color;

public class WorldMap extends HexGrid<Color> {

	public WorldMap() {
		super(25.0f);
	}
}
