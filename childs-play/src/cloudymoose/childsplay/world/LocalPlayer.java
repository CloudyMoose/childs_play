package cloudymoose.childsplay.world;

import cloudymoose.childsplay.world.commands.CommandBuilder;
import cloudymoose.childsplay.world.hextiles.HexTile;

import com.badlogic.gdx.math.Vector3;

public class LocalPlayer extends Player {

	protected World world;
	protected Vector3 currentPosition;
	protected HexTile<TileData> selectedTile;

	public LocalPlayer(int id, World world) {
		super(id);
		this.world = world;
		this.currentPosition = new Vector3();
	}

	// public void select(Unit unit) {
	// selection = unit;
	// }
	//
	// public void clearSelectedUnit() {
	// selection = null;
	// }
	//
	// public boolean hasSelectedUnit() {
	// return selection != null;
	// }

	public boolean owns(Unit unit) {
		return id == unit.getPlayerId();
	}

	public void setCurrentPosition(Vector3 position) {
		currentPosition = position;
	}

	public void setSelectedCommand(CommandBuilder commandBuilder) {
		world.setSelectedCommand(commandBuilder);
	}

	public void selectTile(HexTile<TileData> tile) {
		selectedTile = tile;
	}

	public HexTile<TileData> getSelectedTile() {
		return selectedTile;
	}
}
