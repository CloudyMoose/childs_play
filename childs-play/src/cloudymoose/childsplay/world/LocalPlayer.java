package cloudymoose.childsplay.world;

import cloudymoose.childsplay.world.commands.MoveCommand;

public class LocalPlayer extends Player {

	protected Unit selection;
	protected World world;

	public LocalPlayer(int id, World world) {
		super(id);
		this.world = world;
	}

	public void select(Unit unit) {
		selection = unit;
	}

	public void moveSelectionTo(float x, float y) {
		if (selection != null) {
			world.runCommand(new MoveCommand(selection.id, x, y));
		}
	}

	public boolean owns(Unit unit) {
		return id == unit.getPlayerId();
	}

}
