package cloudymoose.childsplay.world.units;

import cloudymoose.childsplay.world.AnimationData;
import cloudymoose.childsplay.world.World;

public class AppleTree extends EnvironmentUnit {

	@Override
	public String toString() {
		return "AppleTree";
	}

	@Override
	public AnimationData doEnvironmentalEffect(World world) {
		world.getCurrentPlayer().setResourcePoints(world.getCurrentPlayer().getResourcePoints() + 1);
		return null;
	}

}
