package cloudymoose.childsplay.world.commands;

import cloudymoose.childsplay.world.AnimationData;
import cloudymoose.childsplay.world.AnimationType;
import cloudymoose.childsplay.world.World;
import cloudymoose.childsplay.world.units.Unit;

import com.badlogic.gdx.math.Vector3;

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
		private AnimationData meleeAnimation;

		public AttackRunner(AttackCommand command, World world) {
			super(command, world);
			target = world.getUnit(command.targetId);

		}

		@Override
		protected boolean update(float dt) {
			actor.attack(target);
			return false;
		}

		@Override
		public AnimationData getAnimationData() {
			// Return it only once.
			if (meleeAnimation == null) {
				Vector3 meleePosition = new Vector3(actor.position).add(target.position).scl(.5f);
				meleeAnimation = new AnimationData(AnimationType.Melee, meleePosition, true, false);
				return meleeAnimation;
			} else {
				return null;
			}
		}

	}

}
