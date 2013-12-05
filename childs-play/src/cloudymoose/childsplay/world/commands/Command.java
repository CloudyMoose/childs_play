package cloudymoose.childsplay.world.commands;

import cloudymoose.childsplay.world.World;
import cloudymoose.childsplay.world.units.Unit;

import com.badlogic.gdx.Gdx;

/**
 * Should contain only the most basic information about the command, as it will be sent over the network. For the
 * execution of the command, {@link #execute(World)} allows to get an instance that will hold all the info necessary for
 * the command execution.
 * 
 * Every subclass should declare an inner class called "Builder", extending {@link CommandBuilder}, that will be used to
 * instanciate the command and gather the required data for that. (see {@link Command#builder(Class)}
 */
public abstract class Command {

	public static final int NO_ACTOR = -1;
	/**
	 * Id of the {@link Unit} that is the actor of the command. It will be used for various purposes outside of actually
	 * running the command, such as camera positioning and ticket counting. Can be <code>null</code>
	 */
	protected final int actorId;

	/**
	 * 
	 * @param actorId
	 *            id of the {@link Unit} that is the actor of the command. If there is no actor, please provide
	 *            {@link #NO_ACTOR} as value.
	 */
	public Command(int actorId) {
		this.actorId = actorId;
	}

	protected final String TAG = getClass().getSimpleName();

	/**
	 * @return object that holds all the runtime information
	 * @see CommandRunner
	 */
	public abstract CommandRunner execute(World world);

	/**
	 * Instanciates the {@link CommandBuilder} for the provided {@link Command}. It must be an inner class to that
	 * command and be named "Builder"
	 */
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
