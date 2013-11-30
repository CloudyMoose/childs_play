package cloudymoose.childsplay.world;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import cloudymoose.childsplay.ChildsPlayGame;
import cloudymoose.childsplay.networking.Message.Init;
import cloudymoose.childsplay.networking.Message.TurnRecap;
import cloudymoose.childsplay.world.commands.Command;
import cloudymoose.childsplay.world.commands.CommandBuilder;
import cloudymoose.childsplay.world.commands.CommandRunner;
import cloudymoose.childsplay.world.hextiles.Direction;
import cloudymoose.childsplay.world.hextiles.HexTile;
import cloudymoose.childsplay.world.units.Child;
import cloudymoose.childsplay.world.units.Unit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

/**
 * Holds the state of the units, map, etc. It doesn't do anything on its own. It can start running a {@link Command} by
 * using {@link #runCommand(Command)} or {@link #replayNextCommand()}. Its {@link CommandRunner} will then be used to
 * update the world's state at each {@link #fixedUpdate(float)} call
 */
public class World {
	public enum Phase {
		ReplayEnvironment,
		Replay,
		Environment,
		Command,
		Wait
	}

	private static final String TAG = "World";

	// Constant data
	private WorldMap map;
	private List<Player> players;
	private LocalPlayer localPlayer;

	// Current turn data

	// Current phase data
	private Phase currentPhase = Phase.Wait;
	private Player currentPlayer; // will be the enemy during the replay phase
	private final Queue<Command> commands = new LinkedList<Command>();
	private boolean phaseFinished;

	// Command execution and creation data
	private CommandBuilder selectedCommandBuilder;
	private CommandRunner ongoingCommand;
	public final Set<HexTile<?>> targetableTiles = new HashSet<HexTile<?>>();
	private Vector3 preferredCameraFocus;

	// =================================================================================================================
	// Initialization
	// =================================================================================================================

	public World(Init initData) {
		Gdx.app.log(TAG, "Init data: " + initData.toString());
		createDemoWorld(initData);
		currentPlayer = localPlayer;
	}

	/** TODO: will be replaced by a proper initialization from the map info */
	private void createDemoWorld(Init initData) {
		Player.setGaia(new Player(Player.GAIA_ID));
		map = createEmptyMap(12, 10);

		players = new ArrayList<Player>(initData.nbPlayers + 1);

		// Gaia init
		Player gaia = Player.Gaia();
		gaia.addUnit(new Child(gaia, map.getTile(4, 2)));
		players.add(gaia);

		// Other players
		int idOffset = Player.GAIA_ID + 1;
		for (int i = idOffset; i < initData.nbPlayers + idOffset; i++) {
			Player player;
			if (initData.playerId == i) {
				localPlayer = new LocalPlayer(i, this);
				player = localPlayer;
			} else {
				player = new Player(i);
			}

			int r = (i == 1) ? 1 : 9;
			player.addUnit(new Child(player, map.getTile(1, r)));
			player.addUnit(new Child(player, map.getTile(7, r - 3)));

			players.add(player);
		}
	}

	private WorldMap createEmptyMap(int width, int height) {
		final int nbAreas = Constants.NB_MAP_AREAS;
		WorldMap newMap = new WorldMap(nbAreas);

		List<List<HexTile<TileData>>> areaTiles = new ArrayList<List<HexTile<TileData>>>(nbAreas);
		areaTiles.add(new ArrayList<HexTile<TileData>>());
		areaTiles.add(new ArrayList<HexTile<TileData>>());
		areaTiles.add(new ArrayList<HexTile<TileData>>());

		int[] areaLimits = new int[] { (width / nbAreas), width - (width / nbAreas) };
		Gdx.app.log(TAG, "areaLimits: " + areaLimits[0] + " " + areaLimits[1]);

		HexTile<TileData> columnHead = newMap.addValue(0, 0, new TileData(Color.GREEN));
		for (int y = 0; y < height; y++) {
			areaTiles.get(0).add(columnHead);
			HexTile<TileData> tmp = columnHead;
			for (int x = 1 /* The first tile is manually added */; x < width; x++) {
				TileData tileData = new TileData(Color.GREEN);
				tmp = tmp.setNeighbor(Direction.Right, tileData);
				if (x <= areaLimits[0] - 1) {
					areaTiles.get(0).add(tmp);
					if (x == areaLimits[0] - 1) {
						tileData.addBorders(Direction.Right);
						if (y % 2 == 1) tileData.addBorders(Direction.UpRight, Direction.DownRight);
					}
				} else if (x >= areaLimits[1]) {
					areaTiles.get(2).add(tmp);
					// tileData.color = Color.MAGENTA;
					if (x == areaLimits[1]) {
						tileData.addBorders(Direction.Left);
						if (y % 2 == 0) tileData.addBorders(Direction.UpLeft, Direction.DownLeft);
					}

				} else {
					areaTiles.get(1).add(tmp);
					tileData.color = Color.LIGHT_GRAY;
					if (x == areaLimits[0]) {
						tileData.addBorders(Direction.Left);
						if (y % 2 == 0) tileData.addBorders(Direction.UpLeft, Direction.DownLeft);
					} else if (x == areaLimits[1] - 1) {
						tileData.addBorders(Direction.Right);
						if (y % 2 == 1) tileData.addBorders(Direction.UpRight, Direction.DownRight);
					}
				}
			}
			if (y != height - 1) {
				Direction indentation = y % 2 == 0 ? Direction.DownRight : Direction.DownLeft;
				columnHead = columnHead.setNeighbor(indentation, new TileData(Color.GREEN));
			}
		}

		Random r = ChildsPlayGame.getRandom();
		for (int i = 0; i < nbAreas; i++) {
			int nbControlPoints = 1 + r.nextInt(3);
			List<HexTile<TileData>> pickPool = new LinkedList<HexTile<TileData>>(areaTiles.get(i));
			List<HexTile<TileData>> controlPoints = new ArrayList<HexTile<TileData>>(nbControlPoints);
			for (int j = 0; j < nbControlPoints; j++) {
				controlPoints.add(pickPool.remove(r.nextInt(pickPool.size())));
			}
			newMap.areas[i] = new Area(areaTiles.get(i), controlPoints);
			Gdx.app.debug(TAG, newMap.areas[i].toString() + " " + controlPoints);
		}

		return newMap;
	}

	// =================================================================================================================
	// World update
	// =================================================================================================================

	public void fixedUpdate(float dt) {
		if (currentPhase == Phase.Command || currentPhase == Phase.Replay) {
			updateCommandState(dt);
		} else {
			updateEnvironmentState(dt);
		}
	}

	private void updateCommandState(float dt) {
		if (ongoingCommand != null) {
			boolean running = ongoingCommand.run(dt);
			preferredCameraFocus = ongoingCommand.getPreferredCameraFocus();
			if (!running) { // The command just stopped
				preferredCameraFocus = null;
				ongoingCommand = null;

				for (Player p : players) {
					for (Iterator<Entry<Integer, Unit>> it = p.units.entrySet().iterator(); it.hasNext();) {
						Entry<Integer, Unit> entry = it.next();

						if (entry.getValue().isDead()) {
							entry.getValue().updateOccupiedTile(null);
							it.remove();
						}
					}

				}
			}
		}
	}

	/** Environment stuff: area control etc. */
	private void updateEnvironmentState(float dt) {
		// Do whatever each area controlled by the player has to do
		for (Area a : map.areas) {
			if (a.getOwner() == currentPlayer && !a.isContested()) {
				Gdx.app.log(TAG,
						String.format("Player %d is getting the benefits of controlling %s", currentPlayer.id, a));
			}
		}

		// Update the area ownership
		for (Unit unit : currentPlayer.units.values()) {
			HexTile<TileData> position = unit.getOccupiedTile();
			if (map.isControlPoint(position)) {
				position.value.getArea().doControlAttempt(currentPlayer);
			}
		}

		phaseFinished = true;

	}

	// =================================================================================================================
	// Phase switch
	// =================================================================================================================

	public void prepareReplays(TurnRecap turnData) {
		commands.clear();

		if (turnData.commands.length == 0) return;

		for (int i = 0; i < turnData.playerCommands.length; i++) {
			int nbCommands = turnData.playerCommands[i];
			if (nbCommands > 0) {
				currentPlayer = players.get(i);
				break;
			}
		}

		for (int i = 0; i < turnData.commands.length; i++) {
			commands.add(turnData.commands[i]);
		}
	}

	/**
	 * Registers the commands and prepares the world to play them (reset tickets). Warning: there must actually be
	 * commands to replay in the turn data, as the non nullity of the command array is not checked.
	 */
	public void startReplayPhase() {
		currentPhase = Phase.Replay;
		phaseFinished = false;
		startTurn();
	}

	public void startCommandPhase() {
		currentPhase = Phase.Command;
		phaseFinished = false;
		currentPlayer = localPlayer;
		startTurn();
		Gdx.app.log(TAG, "Player #" + localPlayer.id + " can now give his commands.");
	}

	public void startEnvironmentPhase() {
		currentPhase = Phase.Environment;
		phaseFinished = false;
		currentPlayer = localPlayer;
	}

	public void startReplayEnvironmentPhase() {
		currentPhase = Phase.ReplayEnvironment;
		phaseFinished = false;
	}

	// =================================================================================================================
	// Command registration and stuff
	// =================================================================================================================

	public boolean replayNextCommand() {
		if (commands.isEmpty()) {
			phaseFinished = true;
			return false;
		} else {
			Command c = commands.remove();
			runCommand(c, true);
			Gdx.app.log(TAG, "Replaying command " + c);
			return true;
		}
	}

	public void runCommand(Command command, boolean replayMode) {
		Gdx.app.log(TAG, "remainingTickets (before action): " + currentPlayer.getRemainingTickets());

		// Try to use a ticket
		if (!currentPlayer.useTicket()) return;

		// Start the command
		ongoingCommand = command.execute(this);
		ongoingCommand.start();

		if (!replayMode) {
			// Register it
			commands.add(command);
		}
	}

	/** Currently only resets the tickets */
	public void startTurn() {
		Gdx.app.log(TAG, "startTurn");
		currentPlayer.resetTickets();
	}

	// =================================================================================================================
	// Utility functions
	// =================================================================================================================

	public Unit hit(Vector3 worldCoordinates) {
		// TODO: change to a better way to look for the clicked unit (using map areas or something similar)
		for (Player p : players) {
			for (Unit u : p.units.values()) {
				if (u.isHit(worldCoordinates)) { return u; }
			}
		}
		return null;
	}

	public void setSelectedCommand(CommandBuilder commandBuilder) {
		selectedCommandBuilder = commandBuilder;
		targetableTiles.addAll(map.findTiles(commandBuilder.getTargetConstraints()));
	}

	public void cancelCommand() {
		selectedCommandBuilder = null;
		targetableTiles.clear();
	}

	public void selectTargetTile(HexTile<TileData> target) {
		selectedCommandBuilder.setTarget(target);
		runCommand(selectedCommandBuilder.build(), false);
		cancelCommand();
	}

	/**
	 * Returns true if there is a winner for the game. The winner is not returned here. The context of the call has to
	 * be used to know who is the winner.
	 */
	public boolean isEndGameState() {
		for (Player p : players) {
			if (p.id == Player.GAIA_ID && players.size() > 2) {
				// Single player (current + GAIA): we stil want to end when the NPCs are defeated
				continue;
			}
			if (p.units.isEmpty()) return true;
		}
		return false;
	}

	// =================================================================================================================
	// Getters and Setters
	// =================================================================================================================

	public boolean hasRunningCommand() {
		return ongoingCommand != null && ongoingCommand.isRunning();
	}

	public Command[] exportCommands() {
		Command[] c = commands.toArray(new Command[commands.size()]);
		return c;
	}

	public Unit getUnit(int unitId) {
		int playerId = unitId / Player.UNIT_ID_OFFSET;
		return players.get(playerId).units.get(unitId);
	}

	public WorldMap getMap() {
		return map;
	}

	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	public Phase getPhase() {
		return currentPhase;
	}

	public LocalPlayer getLocalPlayer() {
		return localPlayer;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public Vector3 getPreferredCameraFocus() {
		return preferredCameraFocus;
	}

	public boolean isPhaseFinished() {
		return phaseFinished;
	}

}
