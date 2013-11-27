package cloudymoose.childsplay.world.commands;

import cloudymoose.childsplay.world.hextiles.HexTile;

/**
 * An inner class named <code>Builder</code> should be declared in each command and should extend this class. The
 * builder instance would be obtained through reflection.
 */
public abstract class CommandBuilder {

	protected HexTile<?> originTile;
	protected HexTile<?> targetTile;
	protected TargetConstraints targetConstraints;

	/** Overrride it to return the max range of the command. Uses the {@link #targetConstraints}' range */
	public int getCommandRange() {
		return getTargetConstraints().maxRange;
	}

	public CommandBuilder from(HexTile<?> originTile) {
		this.originTile = originTile;
		return this;
	}

	public void setTarget(HexTile<?> target) {
		targetTile = target;
	}

	public abstract Command build();

	protected abstract TargetConstraints constraints();

	public TargetConstraints getTargetConstraints() {
		if (targetConstraints == null) {
			targetConstraints = constraints();
		}
		return targetConstraints;
	}
}
