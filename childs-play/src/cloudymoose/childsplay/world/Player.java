package cloudymoose.childsplay.world;

import java.util.HashMap;
import java.util.Map;

import cloudymoose.childsplay.world.units.Unit;

public class Player {

	public static final int UNIT_ID_OFFSET = 1000;
	/** Player owning the npcs */
	public static final int GAIA_ID = 0;
	private static Player GAIA;

	public final int id;
	public final Map<Integer, Unit> units;

	private int unitCreationCount;
	private int remainingTickets;

	private int healthPoints;
	private int resourcePoints;

	public Player(int id) {
		units = new HashMap<Integer, Unit>();
		this.id = id;
		unitCreationCount = 0;
		healthPoints = 5;
		resourcePoints = 0;
	}

	public Unit addUnit(Unit u) {
		units.put(u.id, u);
		return u;
	}

	public int generateUnitId() {
		return id * UNIT_ID_OFFSET + (unitCreationCount++);
	}

	public int getRemainingTickets() {
		return remainingTickets;
	}

	/**
	 * Decrements the number of tickets available for the turn.
	 * 
	 * @return <code>true</code> if there were tickets left to use.
	 */
	public boolean useTicket() {
		remainingTickets -= 1;
		return remainingTickets >= 0;
	}

	public void resetTickets() {
		remainingTickets = Constants.NB_TICKETS;
	}

	/** Initialize the NPC player. */
	public static void setGaia(Player player) {
		GAIA = player;
	}

	public static Player Gaia() {
		if (GAIA == null) throw new NullPointerException("GAIA is not initialized");
		return GAIA;
	}

	@Override
	public String toString() {
		if (id == GAIA_ID) {
			return "Gaia";
		} else {
			return "Player " + id;
		}
	}

	public void hit() {
		healthPoints -= 1;
	}

	public int getHp() {
		return healthPoints;
	}

	public void setResourcePoints(int newValue) {
		resourcePoints = newValue;
	}

	public int getResourcePoints() {
		return resourcePoints;
	}

}
