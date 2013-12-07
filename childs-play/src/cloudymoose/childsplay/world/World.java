package cloudymoose.childsplay.world;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

import cloudymoose.childsplay.networking.Message.Init;
import cloudymoose.childsplay.networking.Message.TurnRecap;
import cloudymoose.childsplay.world.commands.Command;
import cloudymoose.childsplay.world.commands.CommandBuilder;
import cloudymoose.childsplay.world.commands.CommandCreationException;
import cloudymoose.childsplay.world.commands.CommandRunner;
import cloudymoose.childsplay.world.hextiles.Direction;
import cloudymoose.childsplay.world.hextiles.HexTile;
import cloudymoose.childsplay.world.units.AppleTree;
import cloudymoose.childsplay.world.units.Castle;
import cloudymoose.childsplay.world.units.Catapult;
import cloudymoose.childsplay.world.units.Child;
import cloudymoose.childsplay.world.units.Unit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

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
	private Queue<String> infoLog = new LinkedList<String>();

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
		map = createEmptyMap(15, 9);

		players = new ArrayList<Player>(initData.nbPlayers + 1);
		players.add(Player.Gaia());

		// Players
		int idOffset = Player.GAIA_ID + 1;
		for (int i = idOffset; i < initData.nbPlayers + idOffset; i++) {
			Player player;
			if (initData.playerId == i) {
				localPlayer = new LocalPlayer(i, this);
				player = localPlayer;
			} else {
				player = new Player(i);
			}

			int r;
			if (i == 1) {
				r = 1;
				player.addUnit(new Castle(player, map.getTile(4, -2)));
			} else {
				r = 13;
				player.addUnit(new Castle(player, map.getTile(4, 12)));
			}
			player.addUnit(new Child(player, map.getTile(1, r)));
			player.addUnit(new Child(player, map.getTile(7, r - 3)));

			player.setResourcePoints(Constants.STARTING_RESOURCE_POINTS);

			players.add(player);
		}
	}

	private WorldMap createEmptyMap(int width, int height) {
		final int nbAreas = 3;
		WorldMap newMap = new WorldMap();

		List<List<HexTile<TileData>>> areaTiles = new ArrayList<List<HexTile<TileData>>>(nbAreas);
		Array<Integer> areaLimits = new Array<Integer>(nbAreas);

		for (int i = 0; i < nbAreas; i++) {
			areaTiles.add(new ArrayList<HexTile<TileData>>());
			areaLimits.add((width * (i + 1) / nbAreas) - 1);
		}

		Gdx.app.log(TAG, "areaLimits: " + areaLimits);

		List<HexTile<TileData>> controlTiles = new ArrayList<HexTile<TileData>>();
		TileType tileType = TileType.Grass;
		HexTile<TileData> columnHead = newMap.addValue(0, 0, new TileData(tileType));
		int currentArea = 0;
		for (int x = 0; x < width; x++) {
			boolean lastColOfArea = areaLimits.contains(x, false);
			areaTiles.get(currentArea).add(columnHead);
			HexTile<TileData> tmp = columnHead;

			// Creation of each tile of the column
			for (int y = 1; y < height; y++) {
				TileData tileData = new TileData(tileType);
				tmp = tmp.setNeighbor(y % 2 == 0 ? Direction.DownLeft : Direction.DownRight, tileData);
				areaTiles.get(currentArea).add(tmp);

				if ((tmp.getQ() == 3 && tmp.getR() == 1 + currentArea * 5)
						|| (tmp.getQ() == 5 && tmp.getR() == currentArea * 5)) {
					controlTiles.add(tmp);
				}

				if (tmp.getQ() == 4 && tmp.getR() == currentArea * 5) {
					if (currentArea % 2 == 0) {
						new AppleTree(tmp);
					} else {
						new Catapult(tmp);
					}
				}

				if (lastColOfArea) {
					// Add borders
					tileData.addBorders(Direction.Right);
					if (y % 2 == 1) tileData.addBorders(Direction.UpRight, Direction.DownRight);
				}

			}

			if (lastColOfArea) {
				newMap.areas.add(new Area(areaTiles.get(currentArea), controlTiles));
				controlTiles.clear();
				currentArea += 1;
			}

			if (x < width - 1) {
				tileType = ((currentArea) % 2 == 0 ? TileType.Grass : TileType.Sand);
				columnHead = columnHead.setNeighbor(Direction.Right, new TileData(tileType));
			}
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
							infoLog.add(entry.getValue().toString() + " has been defeated.");
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
		Set<Area> areaSet = new HashSet<Area>();
		for (Area a : map.areas) {
			if (a.getOwner() == currentPlayer && !a.isContested()) {
				a.getBenefits(this);
				areaSet.add(a);
			}
		}

		if (!areaSet.isEmpty()) {
			infoLog.add(String.format("%s is getting the benefits of controlling %s", currentPlayer, areaSet));
		}

		// Update the area ownership
		areaSet.clear();
		for (Unit unit : currentPlayer.units.values()) {
			HexTile<TileData> position = unit.getOccupiedTile();
			if (map.isControlPoint(position)) {
				Area a = position.value.getArea();
				a.doControlAttempt(currentPlayer);
				areaSet.add(a);
			}
		}

		for (Area a : areaSet) {
			infoLog.add(a.getStatusMessage(currentPlayer));
		}

		phaseFinished = true;

	}

	// =================================================================================================================
	// Phase switch
	// =================================================================================================================

	public void prepareReplays(TurnRecap turnData) {
		commands.clear();

		for (int i = 0; i < turnData.playerCommands.length; i++) {
			int nbCommands = turnData.playerCommands[i];
			if (nbCommands >= 0) {
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

		// See if we can use a ticket
		if (currentPlayer.getRemainingTickets() <= 0) return;

		// Start the command
		ongoingCommand = command.execute(this);
		currentPlayer.registerTicketUsage(ongoingCommand.actor);
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

		try {
			runCommand(selectedCommandBuilder.build(this), false);
		} catch (CommandCreationException e) {
			infoLog.add(e.getMessage());
		} finally {
			cancelCommand();
		}
	}

	/**
	 * Returns true if there is a winner for the game. The winner is not returned here. The context of the call has to
	 * be used to know who is the winner.
	 */
	public boolean isEndGameState() {
		for (Player p : players) {
			if (p.id == Player.GAIA_ID && players.size() > 2) {
				// Single player (current + GAIA): we still want to end when the NPCs are defeated
				continue;
			}
			if (p.getHp() == 0) return true;
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

	/**
	 * Returns the next log entry. There can be many of them waiting to be displayed.
	 * 
	 * @return <code>null</code> if there is no log entry
	 */
	public String getInfoLogEntry() {
		return infoLog.poll();
	}

	public Player getPlayerById(int id) {
		for (Player p : players) {
			if (p.id == id) return p;
		}
		return null;
	}

	/**
	 * Returns the list of enemies of the player
	 * 
	 * @param p1
	 *            player that will have his enemies listed
	 * @param orGaia
	 *            if <code>true</code>, at least Gaia will be returned in the list (ignored otherwise)
	 */
	public List<Player> getEnemyPlayers(Player p1, boolean orGaia) {
		List<Player> enemyPlayers = new ArrayList<Player>(players);
		enemyPlayers.remove(p1);
		enemyPlayers.remove(Player.Gaia());
		if (orGaia && enemyPlayers.isEmpty()) enemyPlayers.add(Player.Gaia());
		return enemyPlayers;
	}

}
