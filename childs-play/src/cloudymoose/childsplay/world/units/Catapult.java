package cloudymoose.childsplay.world.units;

import cloudymoose.childsplay.world.Player;
import cloudymoose.childsplay.world.World;

public class Catapult extends EnvironmentUnit {

	@Override
	public String toString() {
		return "Catapult " + id;
	}

	@Override
	public void doEnvironmentalEffect(World world) {
		for (Player p : world.getEnemyPlayers(world.getCurrentPlayer(), false)) {
			p.hit();
		}
	}

}
