package cloudymoose.childsplay.world.commands;

import cloudymoose.childsplay.world.World;
import cloudymoose.childsplay.world.units.Unit;

public class AttackCommand extends Command {
	public final int targetId;

	public AttackCommand() {
		this(0, 0);
	}

	public AttackCommand(int unitId, int targetId) {
		super(unitId);
		this.targetId = targetId;
	}

	@Override
	public CommandRunner execute(World world) {
		return new AttackRunner(this, world);
	}

	@Override
	public String toString() {
		return String.format("Attack unit %d with unit %d", targetId, actorId);
	}

	public static class Builder extends CommandBuilder {

		@Override
		protected TargetConstraints constraints() {
			return new TargetConstraints.HasEnemy(false, originTile.value.getOccupant().getAttackRange(), originTile,
					originTile.value.getOccupant().getPlayerId());
		}

		@Override
		public Command build(World world) {
			return new AttackCommand(originTile.value.getOccupant().id, targetTile.value.getOccupant().id);
		}

	}

	public static class AttackRunner extends CommandRunner {
		private final Unit target;

		public AttackRunner(AttackCommand command, World world) {
			super(command, world);
			target = world.getUnit(command.targetId);
		}

		@Override
		protected boolean update(float dt) {
			// Here we delay applying the command to allow the camera to be repositionned first.
			if (!actor.position.equals(preferredCameraFocus)) {
				preferredCameraFocus = actor.position;
				return true;
			}
			actor.attack(target);
			return false;
		}

	}

}
