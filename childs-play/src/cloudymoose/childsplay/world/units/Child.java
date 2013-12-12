package cloudymoose.childsplay.world.units;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cloudymoose.childsplay.world.Constants;
import cloudymoose.childsplay.world.Player;
import cloudymoose.childsplay.world.commands.AttackCommand;
import cloudymoose.childsplay.world.commands.Command;
import cloudymoose.childsplay.world.commands.MoveCommand;

public class Child extends Unit {

	@SuppressWarnings("unchecked")
	private static List<Class<? extends Command>> supportedCommands = Collections.unmodifiableList(Arrays.asList(
			MoveCommand.class, AttackCommand.class));

	public Child(Player owner) {
		super(owner, Constants.CHILD_SIZE, 1, Constants.CHILD_MOVE_RANGE, 1, 1);
	}

	@Override
	public String toString() {
		return "Child " + id;
	}

	@Override
	public List<Class<? extends Command>> getSupportedCommands() {
		if (getAttackRange() > 0) {
			return supportedCommands;
		} else {
			// TODO: handle this in a way that makes it easier to add new
			// commands
			List<Class<? extends Command>> list = new ArrayList<Class<? extends Command>>();
			list.add(MoveCommand.class);
			return list;
		}
	}

	public static int getCost() {
		return Constants.CHILD_COST;
	}

}
