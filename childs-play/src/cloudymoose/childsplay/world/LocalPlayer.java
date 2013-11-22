package cloudymoose.childsplay.world;

public class LocalPlayer extends Player {

	protected Unit selection;
	protected World world;
	private int remainingTickets;

	public LocalPlayer(int id, World world) {
		super(id);
		this.world = world;
	}

	public void select(Unit unit) {
		selection = unit;
	}

	public void moveSelectionTo(float x, float y) {
		if (selection != null) {
			world.runCommand(new Command.Move(selection.id, x, y));
		}
	}

}
