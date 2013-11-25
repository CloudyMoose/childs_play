package cloudymoose.childsplay.world.commands;

import cloudymoose.childsplay.world.Unit;
import cloudymoose.childsplay.world.World;

import com.badlogic.gdx.math.Vector2;

public class MoveCommand extends Command {
	public final int unitId;
	public final float destX;
	public final float destY;

	protected MoveCommand() {
		this(0, 0, 0);
	}

	public MoveCommand(int unitId, float destX, float destY) {
		super();
		this.unitId = unitId;
		this.destX = destX;
		this.destY = destY;
	}

	@Override
	public String toString() {
		return String.format("Move %d to (%f,%f)", unitId, destX, destY);
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
			this.destination = new Vector2(command.destX, command.destY);

			// TODO replace with pathfinding
			// angle between the line made by the 2 points and the x-axis
			double angle = Math.atan2((command.destY - unit.position.y), command.destX - unit.position.x);
			currentMovement = new Vector2((float) (unit.movementSpeed * Math.cos(angle)),
					(float) (unit.movementSpeed * Math.sin(angle)));
		}

		@Override
		protected boolean update(float dt) {
			unit.move(currentMovement);

			return !(Math.abs(unit.position.x - destination.x) < POSITION_EPSILON && Math.abs(unit.position.y
					- destination.y) < POSITION_EPSILON);

		}
	}

}