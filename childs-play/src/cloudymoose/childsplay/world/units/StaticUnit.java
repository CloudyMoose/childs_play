package cloudymoose.childsplay.world.units;

import cloudymoose.childsplay.world.Player;
import cloudymoose.childsplay.world.TileData;
import cloudymoose.childsplay.world.hextiles.HexTile;

/** Movement, attack etc is set to 0 */
public abstract class StaticUnit extends Unit {

	public StaticUnit(Player owner, HexTile<TileData> tile, int size, int hp) {
		super(owner, tile, size, hp, 0, 0, 0);
	}

}
