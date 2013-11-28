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
import cloudymoose.childsplay.world.commands.CommandRunner;
import cloudymoose.childsplay.world.hextiles.Direction;
import cloudymoose.childsplay.world.hextiles.HexTile;

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
	}

	/** TODO: will be replaced by a proper initialization from the map info */
	private void createDemoWorld(Init initData) {
		map = createEmptyMap(10, 10);

		players = new ArrayList<Player>(initData.nbPlayers + 1);

		for (int i = 0; i < initData.nbPlayers + 1; i++) {
			Player player;
			if (initData.playerId == i) {
				localPlayer = new LocalPlayer(i, this);
				player = localPlayer;
			} else {
				player = new Player(i);
			}

			if (i == 0) {
				// NPCs
				player.addUnit(new Child(player, map.getTile(4, 2)));
			} else {
				int r = (i == 1) ? 1 : 9;
				player.addUnit(new Child(player, map.getTile(1, r)));
				player.addUnit(new Child(player, map.getTile(7, r - 3)));
			}

			players.add(player);
		}
	}

	private WorldMap createEmptyMap(int width, int height) {
		WorldMap newMap = new WorldMap();
		HexTile<Color> columnHead = newMap.addValue(0, 0, Color.GREEN);
		for (int y = 0; y < height; y++) {
			HexTile<Color> tmp = columnHead;
			for (int x = 0; x < width; x++) {
				tmp = tmp.setNeighbor(Direction.Right, Color.GREEN);
			}
			if (y != height - 1) {
				Direction indentation = y % 2 == 0 ? Direction.DownRight : Direction.DownLeft;
				columnHead = columnHead.setNeighbor(indentation, Color.GREEN);
			}
		}
		return newMap;
	}

	// =================================================================================================================
	// World update
	// =================================================================================================================

	public void fixedUpdate(float dt) {
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

	// =================================================================================================================
	// Phase switch
	// =================================================================================================================

	/**
	 * Registers the commands and prepares the world to play them (reset tickets). Warning: there must actually be
	 * commands to replay in the turn data, as the non nullity of the command array is not checked.
	 */
	public void startReplayPhase(TurnRecap turnData) {
		currentPhase = Phase.Replay;

		commands.clear();

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

		startTurn();

	}

	public void startCommandPhase() {
		currentPhase = Phase.Command;
		currentPlayer = localPlayer;
		startTurn();
		Gdx.app.log(TAG, "Player #" + localPlayer.id + " can now give his commands.");
	}

	public void startEnvironmentPhase() {
		currentPhase = Phase.Environment;
		currentPlayer = localPlayer;
	}

	// =================================================================================================================
	// Command registration and stuff
	// =================================================================================================================

	public boolean replayNextCommand() {
		if (commands.isEmpty()) {
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

	public void selectTargetTile(HexTile<Color> target) {
		selectedCommandBuilder.setTarget(target);
		runCommand(selectedCommandBuilder.build(), false);
		cancelCommand();
	}

	// =================================================================================================================
	// Getters
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

}
