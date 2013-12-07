package cloudymoose.childsplay.world.units;

import java.util.List;

import cloudymoose.childsplay.world.Player;
import cloudymoose.childsplay.world.World;
import cloudymoose.childsplay.world.commands.Command;

/** Units that give some benefits to the player controlling the area they are in. */
public abstract class EnvironmentUnit extends StaticUnit {

	public EnvironmentUnit() {
		super(Player.Gaia(), 25, 1);
	}

	@Override
	public List<Class<? extends Command>> getSupportedCommands() {
		return null;
	}

	/** Indestructible! */
	@Override
	public void takeDamage(int damage) {
	}

	public abstract void doEnvironmentalEffect(World world);

}
