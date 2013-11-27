package cloudymoose.childsplay.world.commands;

import cloudymoose.childsplay.world.Unit;
import cloudymoose.childsplay.world.World;

public class AttackCommand extends Command {
	public final int unitId;
	public final int targetId;
	
	public AttackCommand() {
		this(0,0);
	}

	public AttackCommand(int unitId, int targetId) {
		this.unitId = unitId;
		this.targetId = targetId;
	}

	@Override
	public CommandRunner execute(World world) {
		return new AttackRunner(this, world);
	}
	
	@Override
	public String toString() {
		return String.format("Attack unit %d with unit %d", targetId, unitId);
	}
	
	public static class Builder extends CommandBuilder {

		@Override
		protected TargetConstraints constraints() {
			return new TargetConstraints.HasEnemy(false, originTile.getOccupant().attackRange, originTile, originTile.getOccupant().getPlayerId());
		}

		@Override
		public Command build() {
			return new AttackCommand(originTile.getOccupant().id, targetTile.getOccupant().id);
		}

	}
	
	public static class AttackRunner extends CommandRunner {
		private final Unit attacker;
		private final Unit target;
		
		public AttackRunner(AttackCommand command, World world) {
			super(command);
			attacker = world.getUnit(command.unitId);
			target = world.getUnit(command.targetId);
		}

		@Override
		protected boolean update(float dt) {
			// Here we delay applying the command to allow the camera to be repositionned first.
			if (preferredCameraFocus != attacker.position) {
				preferredCameraFocus = attacker.position;
				return true;
			}
			attacker.attack(target);
			return false;
		}
		
	}

}
