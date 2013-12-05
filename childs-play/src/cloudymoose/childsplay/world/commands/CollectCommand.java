package cloudymoose.childsplay.world.commands;

import cloudymoose.childsplay.world.Player;
import cloudymoose.childsplay.world.TileData;
import cloudymoose.childsplay.world.World;
import cloudymoose.childsplay.world.hextiles.HexTile;
import cloudymoose.childsplay.world.units.AppleTree;

public class CollectCommand extends Command {

	public final int sourceId;

	public CollectCommand() {
		this(0, 0);
	}

	public CollectCommand(int unitId, int sourceId) {
		super(unitId);
		this.sourceId = sourceId;
	}

	@Override
	public CommandRunner execute(World world) {
		return new CollectRunner(this, world);
	}

	public static class Builder extends CommandBuilder {

		@Override
		protected TargetConstraints constraints() {
			return new TargetConstraints(false, 1, originTile) {

				@Override
				public boolean isTileTargetable(HexTile<TileData> tile) {
					return tile.value.isOccupied() && tile.value.getOccupant() instanceof AppleTree;
				}
			};
		}

		@Override
		public Command build(World world) {
			return new CollectCommand(originTile.value.getOccupant().id, targetTile.value.getOccupant().id);
		}

	}

	public static class CollectRunner extends CommandRunner {
		private final Player player;

		public CollectRunner(CollectCommand command, World world) {
			super(command, world);
			player = world.getPlayerById(actor.getPlayerId());
		}

		@Override
		protected boolean update(float dt) {
			player.setResourcePoints(player.getResourcePoints() + 1);
			return false;
		}

	}

}
