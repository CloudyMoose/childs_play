package cloudymoose.childsplay.world.commands;

import com.badlogic.gdx.math.Vector3;

/** Contains the information required to update the status of the command at each game loop iteration. */
public abstract class CommandRunner {
	public final Command command;
	protected boolean running = false;
	protected Vector3 preferredCameraFocus;

	public CommandRunner(Command command) {
		this.command = command;
	}

	public void start() {
		running = true;
	}

	public boolean run(float dt) {
		if (running) {
			running = update(dt);
		}
		return running;
	}

	public boolean isRunning() {
		return running;
	}

	public Vector3 getPreferredCameraFocus() {
		return preferredCameraFocus;
	}

	protected abstract boolean update(float dt);
}