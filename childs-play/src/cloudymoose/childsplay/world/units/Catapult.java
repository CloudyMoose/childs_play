package cloudymoose.childsplay.world.units;

import cloudymoose.childsplay.world.AnimationData;
import cloudymoose.childsplay.world.AnimationType;
import cloudymoose.childsplay.world.Player;
import cloudymoose.childsplay.world.World;

public class Catapult extends EnvironmentUnit {

	@Override
	public String toString() {
		return "Catapult " + id;
	}

	@Override
	public AnimationData doEnvironmentalEffect(World world) {
		for (Player p : world.getEnemyPlayers(world.getCurrentPlayer(), true)) {
			p.hit();
		}
		// return new AnimationData(AnimationType.CatapultFire, new Vector3(hitbox.x, hitbox.y, 0), true, false);
		return new AnimationData(AnimationType.CatapultFire, position, true, false, this);
	}

}
