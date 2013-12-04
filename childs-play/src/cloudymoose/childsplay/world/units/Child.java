package cloudymoose.childsplay.world.units;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cloudymoose.childsplay.world.Player;
import cloudymoose.childsplay.world.TileData;
import cloudymoose.childsplay.world.commands.AttackCommand;
import cloudymoose.childsplay.world.commands.CollectCommand;
import cloudymoose.childsplay.world.commands.Command;
import cloudymoose.childsplay.world.commands.MoveCommand;
import cloudymoose.childsplay.world.hextiles.HexTile;

public class Child extends Unit {

	@SuppressWarnings("unchecked")
	private static List<Class<? extends Command>> supportedCommands = Collections.unmodifiableList(Arrays.asList(
			MoveCommand.class, AttackCommand.class, CollectCommand.class));

	public Child(Player owner, HexTile<TileData> position) {
		super(owner, position, 20, 1, 5, 1, 1);
	}

	@Override
	public String toString() {
		return "Child " + id;
	}

	@Override
	public List<Class<? extends Command>> getSupportedCommands() {
		return supportedCommands;
	}

	public static int getCost() {
		return 1;
	}

}
