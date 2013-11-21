package cloudymoose.childsplay.world;

import java.util.HashSet;
import java.util.Set;

import cloudymoose.childsplay.networking.UpdateRequest;

public class LocalPlayer extends Player {

	protected Set<Unit> selection;
	protected World world;

	public LocalPlayer(int id) {
		super(id);
		selection = new HashSet<Unit>();
		world = World.getInstance();
	}

	/** @return true if the unit is has been added to the selection */
	public boolean toggleSelect(Unit unit) {
		boolean wasPresent = selection.remove(unit);
		if (!wasPresent) {
			selection.add(unit);
		}
		return !wasPresent;
	}

	public void moveSelectionTo(float x, float y) {
		for (Unit unit : selection) {
			unit.setDestination((int) x, (int) y);
			world.addOutgoingUpdateRequest(new UpdateRequest.Move(unit.id, x, y));
		}
	}

	public boolean isSelected(Unit unit) {
		return selection.contains(unit);
	}

	public void resetUnits() {
		// TODO: Dubious code
		selection.clear();
		units.clear();
		units.add(new Child(this, 10, 10));
		units.add(new Child(this, -10, 10));
		units.add(new Child(this, 10, -10));
		units.add(new Child(this, -10, -10));
	}

	public void clearSelection() {
		selection.clear();
	}
}
