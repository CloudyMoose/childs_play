package cloudymoose.childsplay.world.units;

import cloudymoose.childsplay.world.Player;

/** Movement, attack etc is set to 0 */
public abstract class StaticUnit extends Unit {

	public StaticUnit(Player owner, int size, int hp) {
		super(owner, size, hp, 0, 0, 0);
	}

}
