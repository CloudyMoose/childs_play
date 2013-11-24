package cloudymoose.childsplay.world;

import cloudymoose.childsplay.world.commands.MoveCommand;

import com.badlogic.gdx.math.Vector2;

public class LocalPlayer extends Player {

	protected Unit selection;
	protected World world;
	protected Vector2 currentPosition;

	public LocalPlayer(int id, World world) {
		super(id);
		this.world = world;
		this.currentPosition = new Vector2();
	}

	public void select(Unit unit) {
		selection = unit;
	}

	public void setCurrentPosition(Vector2 position) {
		currentPosition = position;
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
