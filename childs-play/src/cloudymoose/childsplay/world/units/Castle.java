package cloudymoose.childsplay.world.units;

import java.util.List;

import cloudymoose.childsplay.world.Player;
import cloudymoose.childsplay.world.TileData;
import cloudymoose.childsplay.world.commands.Command;
import cloudymoose.childsplay.world.hextiles.HexTile;

public class Castle extends StaticUnit {

	private Player owner;

	public Castle(Player owner, HexTile<TileData> tile) {
		super(owner, tile, 25, 1);
		this.owner = owner;
	}

	@Override
	protected void takeDamage(int damage) {
		owner.hit();
	}

	@Override
	public String toString() {
		return "Castle " + owner.id;
	}

	@Override
	public List<Class<? extends Command>> getSupportedCommands() {
		return null;
	}

}
