package cloudymoose.childsplay.world.units;

import cloudymoose.childsplay.world.TileData;
import cloudymoose.childsplay.world.World;
import cloudymoose.childsplay.world.hextiles.HexTile;

public class AppleTree extends EnvironmentUnit {

	public AppleTree(HexTile<TileData> tile) {
		super(tile);
	}

	@Override
	public String toString() {
		return "Tree " + id;
	}

	@Override
	public void doEnvironmentalEffect(World world) {
		world.getCurrentPlayer().setResourcePoints(world.getCurrentPlayer().getResourcePoints() + 1);
	}

}
