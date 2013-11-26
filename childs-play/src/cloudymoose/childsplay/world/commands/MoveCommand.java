package cloudymoose.childsplay.world.commands;

import cloudymoose.childsplay.world.Unit;
import cloudymoose.childsplay.world.World;

import com.badlogic.gdx.math.Vector2;

public class MoveCommand extends Command {
	public final int unitId;
	public final int destQ;
	public final int destR;

	protected MoveCommand() {
		this(0, 0, 0);
	}

	/**
	 * 
	 * @param unitId
	 * @param destQ destination tile Q coordinate
	 * @param destR destination tile R coordinate
	 */
	public MoveCommand(int unitId, int destQ, int destR) {
		super();
		this.unitId = unitId;
		this.destQ = destQ;
		this.destR = destR;
	}

	@Override
	public String toString() {
		return String.format("Move %d to tile(%d,%d)", unitId, destQ, destR);
	}

	@Override
	public CommandRunner execute(World world) {
		return new MoveRunner(this, world);
	}

	public static class MoveRunner extends CommandRunner {
		private Vector2 destination;
		private Vector2 currentMovement;
		private Unit unit;
		protected final float POSITION_EPSILON = 5f; // Position accuracy

		public MoveRunner(MoveCommand command, World world) {
			super(command);
			this.unit = world.getUnit(command.unitId);
			this.destination = new Vector2(world.map.getTile(command.destQ, command.destR).getPosition());

			// TODO replace with pathfinding
			// angle between the line made by the 2 points and the x-axis
			double angle = Math.atan2((destination.y - unit.position.y), destination.x - unit.position.x);
			currentMovement = new Vector2((float) (unit.movementSpeed * Math.cos(angle)),
					(float) (unit.movementSpeed * Math.sin(angle)));
		}

		@Override
		protected boolean update(float dt) {
			unit.move(currentMovement);

			boolean closeEnough = Math.abs(unit.position.x - destination.x) < POSITION_EPSILON && Math.abs(unit.position.y
					- destination.y) < POSITION_EPSILON;
			
			if (closeEnough) {
				unit.setPosition((int)destination.x, (int)destination.y);
				return false;
			} else {
				return true;
			}
		}
	}

}