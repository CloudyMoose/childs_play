package cloudymoose.childsplay.world.commands;

import cloudymoose.childsplay.world.World;

/**
 * Should contain only the most basic information about the command, as it will be sent over the network. For the
 * execution of the command, {@link #execute(World)} allows to get an instance that will hold all the info necessary for
 * the command execution.
 */
public abstract class Command {

	public abstract CommandRunner execute(World world);
}
