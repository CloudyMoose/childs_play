package cloudymoose.childsplay.world;

import java.util.HashSet;
import java.util.Set;

public class LocalPlayer extends Player {

	protected Set<Unit> selection;

	public LocalPlayer() {
		super();
		selection = new HashSet<Unit>();
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
			unit.moveTo(x, y);
		}
	}

	public boolean isSelected(Unit unit) {
		return selection.contains(unit);
	}
}
