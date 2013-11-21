package cloudymoose.childsplay.world;

import java.util.HashSet;
import java.util.Set;

public class Player {

	public final int id;
	public final Set<Unit> units;

	private int unitCreationCount;

	public Player(int id) {
		units = new HashSet<Unit>();
		this.id = id;
		unitCreationCount = 0;
	}

	public int generateUnitId() {
		return id * 1000 + (unitCreationCount++);
	}

}
