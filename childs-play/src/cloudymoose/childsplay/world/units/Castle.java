package cloudymoose.childsplay.world.units;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cloudymoose.childsplay.world.Player;
import cloudymoose.childsplay.world.TileData;
import cloudymoose.childsplay.world.commands.Command;
import cloudymoose.childsplay.world.commands.RecruitCommand;
import cloudymoose.childsplay.world.hextiles.HexTile;

public class Castle extends StaticUnit {

	private static final List<Class<? extends Command>> supportedCommands;
	static {
		List<Class<? extends Command>> l = new ArrayList<Class<? extends Command>>();
		l.add(RecruitCommand.class);
		supportedCommands = Collections.unmodifiableList(l);
	}

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
		return supportedCommands;
	}

}
