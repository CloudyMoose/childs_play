package cloudymoose.childsplay.world.commands;

import cloudymoose.childsplay.world.TileData;
import cloudymoose.childsplay.world.World;
import cloudymoose.childsplay.world.hextiles.HexTile;
import cloudymoose.childsplay.world.units.Unit;

import com.badlogic.gdx.math.Vector3;

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
	 * @param destQ
	 *            destination tile Q coordinate
	 * @param destR
	 *            destination tile R coordinate
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

	public static class Builder extends CommandBuilder {

		@Override
		protected TargetConstraints constraints() {
			return new TargetConstraints.Empty(true, originTile.value.getOccupant().movementRange, originTile);
		}

		@Override
		public Command build(World world) {
			return new MoveCommand(originTile.value.getOccupant().id, targetTile.getQ(), targetTile.getR());
		}

	}

	public static class MoveRunner extends CommandRunner {
		private Vector3 destination;
		private Vector3 currentMovement;
		private HexTile<TileData> destTile;
		private Unit unit;
		protected final float POSITION_EPSILON = 5f; // Position accuracy

		public MoveRunner(MoveCommand command, World world) {
			super(command);
			this.unit = world.getUnit(command.unitId);
			this.destTile = world.getMap().getTile(command.destQ, command.destR);
			this.destination = destTile.getPosition();

			// TODO replace with pathfinding
			// angle between the line made by the 2 points and the x-axis
			double angle = Math.atan2((destination.y - unit.position.y), destination.x - unit.position.x);
			currentMovement = new Vector3((float) (unit.movementRange * Math.cos(angle)),
					(float) (unit.movementRange * Math.sin(angle)), 0);
		}

		@Override
		protected boolean update(float dt) {
			unit.move(currentMovement);
			preferredCameraFocus = unit.position;

			boolean closeEnough = Math.abs(unit.position.x - destination.x) < POSITION_EPSILON
					&& Math.abs(unit.position.y - destination.y) < POSITION_EPSILON;

			if (closeEnough) {
				unit.setPosition((int) destination.x, (int) destination.y);
				unit.updateOccupiedTile(destTile);
				return false;
			} else {
				return true;
			}
		}
	}

}