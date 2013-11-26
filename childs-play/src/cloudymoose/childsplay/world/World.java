package cloudymoose.childsplay.world;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import cloudymoose.childsplay.networking.Message.Init;
import cloudymoose.childsplay.world.commands.Command;
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

	private static final String TAG = "World";

	List<Player> players;
	WorldMap map;
	LocalPlayer localPlayer;

	// TODO: needed as attribute only for the reset() method. To be removed later.
	private Init initData;

	// Current turn info
	Player currentPlayer;
	private final Queue<Command> commands;
	private int remainingTickets;
	private CommandRunner ongoingCommand;

	public World(Init initData) {
		Gdx.app.log(TAG, "Init data: " + initData.toString());
		commands = new LinkedList<Command>();
		this.initData = initData;
		createDemoWorld();
	}

	/** TODO: will be replaced by a proper initialization from the map info */
	private void createDemoWorld() {
		map = createEmptyMap(20, 10);
		
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
			} else {
				int r = (i == 1) ? 1 : 19;
				player.addUnit(new Child(player, map.getTile(1, r).getPosition()));
				player.addUnit(new Child(player, map.getTile(7, r-3).getPosition()));
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
			if (y != height-1){
				Direction indentation = y%2 == 0 ? Direction.DownRight : Direction.DownLeft;
				columnHead = columnHead.setNeighbor(indentation, Color.GREEN);
			}
		}
		return newMap;
	}

	public LocalPlayer getLocalPlayer() {
		return localPlayer;
	}

	public void fixedUpdate(float dt) {
		if (ongoingCommand != null) {
			boolean running = ongoingCommand.run(dt);
			if (!running) {
				ongoingCommand = null;
			}
		}
	}

	public Unit hit(Vector3 worldCoordinates) {
		// TODO: change to a better way to look for the clicked unit (using map areas or something similar)
		for (Player p : players) {
			for (Unit u : p.units.values()) {
				if (u.isHit(worldCoordinates)) { return u; }
			}
		}
		return null;
	}

	/** Registers the commands and prepares the world to play them (reset tickets). */
	public void setReplayCommands(Command[] commands) {
		if (commands != null) {
			for (int i = 0; i < commands.length; i++) {
				this.commands.add(commands[i]);
			}
		}
		startTurn();
	}

	public boolean replayNextCommand() {
		if (commands.isEmpty()) {
			return false;
		} else {
			Command c = commands.remove();
			runCommand(c);
			Gdx.app.log(TAG, "Replaying command " + c);
			return true;
		}
	}

	/** Warning, also clears the command queue! */
	public Command[] exportCommands() {
		Command[] c = commands.toArray(new Command[commands.size()]);
		commands.clear();
		return c;
	}

	public void runCommand(Command command) {
		Gdx.app.log(TAG, "remainingTickets (before action): " + remainingTickets);
		// Check if the command is allowed
		if (remainingTickets <= 0) return;

		// Start it
		ongoingCommand = command.execute(this);
		ongoingCommand.start();

		// Register it
		commands.add(command);
		--remainingTickets;
	}

	public void reset() {
		createDemoWorld();
	}

	/** Currently only resets the tickets */
	public void startTurn() {
		Gdx.app.log(TAG, "startTurn");
		remainingTickets = Constants.NB_TICKETS;
	}

	public Unit getUnit(int unitId) {
		int playerId = unitId / Player.UNIT_ID_OFFSET;
		return players.get(playerId).units.get(unitId);
	}

	public boolean hasRunningCommand() {
		return ongoingCommand != null && ongoingCommand.isRunning();
	}

	public WorldMap getMap() {
		return map;
	}

}
