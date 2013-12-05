package cloudymoose.childsplay.world.commands;

import java.util.List;

import cloudymoose.childsplay.world.Constants;
import cloudymoose.childsplay.world.ShortestPathSolver;
import cloudymoose.childsplay.world.TileData;
import cloudymoose.childsplay.world.World;
import cloudymoose.childsplay.world.hextiles.HexTile;

import com.badlogic.gdx.math.Vector3;

public class MoveCommand extends Command {
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
		super(unitId);
		this.destQ = destQ;
		this.destR = destR;
	}

	@Override
	public String toString() {
		return String.format("Move %d to tile(%d,%d)", actorId, destQ, destR);
	}

	@Override
	public CommandRunner execute(World world) {
		return new MoveRunner(this, world);
	}

	public static class Builder extends CommandBuilder {

		@Override
		protected TargetConstraints constraints() {
			return new TargetConstraints.Empty(true, originTile.value.getOccupant().getMovementRange(), originTile);
		}

		@Override
		public Command build(World world) {
			return new MoveCommand(originTile.value.getOccupant().id, targetTile.getQ(), targetTile.getR());
		}

	}

	public static class MoveRunner extends CommandRunner {
		private Vector3 destination;
		private Vector3 currentMovement;
		protected final float POSITION_EPSILON = 5f; // Position accuracy
		private List<HexTile<TileData>> path;

		public MoveRunner(MoveCommand command, World world) {
			super(command, world, false);
			HexTile<TileData> startTile = world.getMap().getTileFromPosition(actor.position);
			HexTile<TileData> destTile = world.getMap().getTile(command.destQ, command.destR);
			this.path = ShortestPathSolver.solve(startTile, destTile);
			setNextTarget();
		}

		private boolean setNextTarget() {
			HexTile<TileData> current = path.remove(0);
			actor.updateOccupiedTile(current);
			destination = current.getPosition();
			actor.setPosition((int) destination.x, (int) destination.y);

			if (path.isEmpty()) return false;

			HexTile<TileData> target = path.get(0);
			destination = target.getPosition();

			double angle = Math.atan2((destination.y - actor.position.y), destination.x - actor.position.x);
			currentMovement = new Vector3((float) (Constants.UNIT_MOVEMENT_SPEED * Math.cos(angle)),
					(float) (Constants.UNIT_MOVEMENT_SPEED * Math.sin(angle)), 0);

			return true;
		}

		@Override
		protected boolean update(float dt) {
			actor.move(currentMovement);
			preferredCameraFocus = actor.position;

			boolean closeEnough = Math.abs(actor.position.x - destination.x) < POSITION_EPSILON
					&& Math.abs(actor.position.y - destination.y) < POSITION_EPSILON;

			if (closeEnough) {
				return setNextTarget();
			} else {
				return true;
			}
		}
	}

}