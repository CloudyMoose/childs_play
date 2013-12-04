package cloudymoose.childsplay.world.commands;

import cloudymoose.childsplay.world.Player;
import cloudymoose.childsplay.world.TileData;
import cloudymoose.childsplay.world.World;
import cloudymoose.childsplay.world.hextiles.HexTile;
import cloudymoose.childsplay.world.units.Child;

public class RecruitCommand extends Command {

	public int tileR;
	public int tileQ;
	public int playerId;

	public RecruitCommand() {
		this(0, 0, 0);
	}

	public RecruitCommand(int playerId, int q, int r) {
		this.playerId = playerId;
		this.tileR = r;
		this.tileQ = q;
	}

	@Override
	public CommandRunner execute(World world) {
		return new RecruitRunner(this, world);
	}

	public static class Builder extends CommandBuilder {

		@Override
		protected TargetConstraints constraints() {
			return new TargetConstraints.Empty(true, 1, originTile);
		}

		@Override
		public Command build() {
			return new RecruitCommand(originTile.value.getOccupant().getPlayerId(), targetTile.getQ(),
					targetTile.getR());
		}

	}

	public static class RecruitRunner extends CommandRunner {
		Player player;
		HexTile<TileData> destinationTile;

		public RecruitRunner(RecruitCommand command, World world) {
			super(command);
			for (Player p : world.getPlayers()) {
				if (p.id == command.playerId) {
					player = p;
					break;
				}
			}
			destinationTile = world.getMap().getTile(command.tileQ, command.tileR);
		}

		@Override
		protected boolean update(float dt) {
			// Here we delay applying the command to allow the camera to be repositionned first.
			if (destinationTile.getPosition().equals(preferredCameraFocus)) {
				preferredCameraFocus = destinationTile.getPosition();
				return true;
			}
			player.addUnit(new Child(player, destinationTile));
			return false;
		}

	}

}
