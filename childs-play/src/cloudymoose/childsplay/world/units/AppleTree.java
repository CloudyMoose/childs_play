package cloudymoose.childsplay.world.units;

import java.util.List;

import cloudymoose.childsplay.world.Player;
import cloudymoose.childsplay.world.TileData;
import cloudymoose.childsplay.world.commands.Command;
import cloudymoose.childsplay.world.hextiles.HexTile;

public class AppleTree extends StaticUnit {

	public AppleTree(HexTile<TileData> tile) {
		super(Player.Gaia(), tile, 25, 1);
	}

	@Override
	public List<Class<? extends Command>> getSupportedCommands() {
		return null;
	}

	@Override
	public String toString() {
		return "Tree " + id;
	}

	/** The tree is indestructible! */
	@Override
	public void takeDamage(int damage) {
	}

}
