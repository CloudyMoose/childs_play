package cloudymoose.childsplay.world.units;

import java.util.List;

import cloudymoose.childsplay.world.Constants;
import cloudymoose.childsplay.world.Player;
import cloudymoose.childsplay.world.commands.Command;

public class Obstacle extends StaticUnit {

	public Obstacle() {
		super(Player.Gaia(), Constants.OBSTACLE_SIZE, 1);
	}

	@Override
	public List<Class<? extends Command>> getSupportedCommands() {
		return null;
	}

}
