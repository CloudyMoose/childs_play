package cloudymoose.childsplay.world;

import java.util.HashMap;
import java.util.Map;

public class Player {

	public static final int UNIT_ID_OFFSET = 1000;

	public final int id;
	public final Map<Integer, Unit> units;

	private int unitCreationCount;

	public Player(int id) {
		units = new HashMap<Integer, Unit>();
		this.id = id;
		unitCreationCount = 0;
	}

	public Unit addUnit(Unit u) {
		units.put(u.id, u);
		return u;
	}

	public int generateUnitId() {
		return id * UNIT_ID_OFFSET + (unitCreationCount++);
	}

}
