package cloudymoose.childsplay.world.commands;

import cloudymoose.childsplay.world.World;

import com.badlogic.gdx.Gdx;

/**
 * Should contain only the most basic information about the command, as it will be sent over the network. For the
 * execution of the command, {@link #execute(World)} allows to get an instance that will hold all the info necessary for
 * the command execution.
 */
public abstract class Command {

	public static final int NO_ACTOR = -1;
	protected final int actorId;

	public Command(int actorId) {
		this.actorId = actorId;
	}

	protected final String TAG = getClass().getSimpleName();

	public abstract CommandRunner execute(World world);

	public static CommandBuilder builder(Class<? extends Command> clazz) {
		try {
			Class<?> bc = null;
			bc = Class.forName(clazz.getCanonicalName() + "$Builder");
			if (CommandBuilder.class.isAssignableFrom(bc)) { return (CommandBuilder) bc.newInstance(); }

		} catch (ClassNotFoundException e) {
			Gdx.app.error("Command", e.getLocalizedMessage(), e);
			Gdx.app.exit();
		} catch (InstantiationException e) {
			Gdx.app.error("Command", e.getLocalizedMessage(), e);
			Gdx.app.exit();
		} catch (IllegalAccessException e) {
			Gdx.app.error("Command", e.getLocalizedMessage(), e);
			Gdx.app.exit();
		}

		return null;
	}
}
