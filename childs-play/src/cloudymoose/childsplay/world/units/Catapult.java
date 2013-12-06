package cloudymoose.childsplay.world.units;

import cloudymoose.childsplay.world.Player;
import cloudymoose.childsplay.world.TileData;
import cloudymoose.childsplay.world.World;
import cloudymoose.childsplay.world.hextiles.HexTile;

public class Catapult extends EnvironmentUnit {

	public Catapult(HexTile<TileData> tile) {
		super(tile);
	}

	@Override
	public String toString() {
		return "Catapult" + id;
	}

	@Override
	public void doEnvironmentalEffect(World world) {
		for (Player p : world.getEnemyPlayers(world.getCurrentPlayer(), false)) {
			p.hit();
		}
	}

}
