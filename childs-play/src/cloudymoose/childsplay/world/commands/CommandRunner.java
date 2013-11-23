package cloudymoose.childsplay.world.commands;


/** Contains the information required to update the status of the command at each game loop iteration. */
public abstract class CommandRunner {
	public final Command command;
	protected boolean running = false;

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

	protected abstract boolean update(float dt);
}