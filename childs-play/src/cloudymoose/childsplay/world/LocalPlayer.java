package cloudymoose.childsplay.world;

import cloudymoose.childsplay.world.commands.CommandBuilder;
import cloudymoose.childsplay.world.commands.MoveCommand;
import cloudymoose.childsplay.world.hextiles.HexTile;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public class LocalPlayer extends Player {

	protected Unit selection;
	protected World world;
	protected Vector3 currentPosition;
	public HexTile<Color> touchedTile;

	public LocalPlayer(int id, World world) {
		super(id);
		this.world = world;
		this.currentPosition = new Vector3();
	}

	public void select(Unit unit) {
		selection = unit;
	}

	public void clearSelectedUnit() {
		selection = null;
	}

	public boolean hasSelectedUnit() {
		return selection != null;
	}

	public boolean owns(Unit unit) {
		return id == unit.getPlayerId();
	}

	public void setCurrentPosition(Vector3 position) {
		currentPosition = position;
	}

	// __ Command Creation _______________________________________

	public void moveSelectionTo(int q, int r) {
		if (selection != null) {
			world.runCommand(new MoveCommand(selection.id, q, r));
		}
	}

	public void moveSelectionTo(HexTile<?> clickedTile) {
		moveSelectionTo(clickedTile.getQ(), clickedTile.getR());
	}

	public void setSelectedCommand(CommandBuilder commandBuilder) {
		world.setSelectedCommand(commandBuilder);
	}
}
