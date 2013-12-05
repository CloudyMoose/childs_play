package cloudymoose.childsplay.world.commands;

import cloudymoose.childsplay.world.World;
import cloudymoose.childsplay.world.units.Unit;

import com.badlogic.gdx.math.Vector3;

/** Contains the information required to update the status of the command at each game loop iteration. */
public abstract class CommandRunner {
	public final Command command;
	public final Unit actor;
	protected boolean running = false;
	protected Vector3 preferredCameraFocus;
	protected boolean autoFocusCamera;

	public CommandRunner(Command command, World world) {
		this(command, world, true);
	}

	public CommandRunner(Command command, World world, boolean autoFocusCamera) {
		this.autoFocusCamera = autoFocusCamera;
		this.command = command;
		if (command.actorId != Command.NO_ACTOR) {
			this.actor = world.getUnit(command.actorId);
		} else {
			this.actor = null;
		}
	}

	public void start() {
		running = true;
	}

	public boolean run(float dt) {
		if (running) {
			// Here we delay applying the command to allow the camera to be repositionned first.
			if (autoFocusCamera && actor != null && !actor.position.equals(preferredCameraFocus)) {
				preferredCameraFocus = actor.position;
			} else {
				running = update(dt);
			}
		}
		return running;
	}

	public boolean isRunning() {
		return running;
	}

	public Vector3 getPreferredCameraFocus() {
		return preferredCameraFocus;
	}

	/**
	 * Method called at each loop iteration. If {@link #autoFocusCamera} was set to <code>true</code>, the camera will
	 * be focused on the actor one iteration before calling this method. If the actor is constantly moving, it's better
	 * to disable the auto focus.
	 */
	protected abstract boolean update(float dt);
}