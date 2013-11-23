package cloudymoose.childsplay.world;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import cloudymoose.childsplay.networking.Message.Init;
import cloudymoose.childsplay.world.commands.Command;
import cloudymoose.childsplay.world.commands.CommandRunner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;

public class World {

	private static final String TAG = "World";

	List<Player> players;
	WorldMap map;
	LocalPlayer localPlayer;

	// TODO: needed as attribute only for the reset() method. To be removed later.
	private Init initData;

	// Current turn info
	private final Queue<Command> commands;
	private int remainingTickets;
	private CommandRunner ongoingCommand;

	public World(Init initData) {
		commands = new LinkedList<Command>();
		this.initData = initData;
		createDemoWorld();
	}

	/** TODO: will be replaced by a proper initialization from the map info */
	private void createDemoWorld() {
		map = new WorldMap();
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
				int x = (i == 1) ? -20 : 20;
				player.addUnit(new Child(player, x, -10));
				player.addUnit(new Child(player, x, 10));
			}

			players.add(player);
		}
	}

	public LocalPlayer getLocalPlayer() {
		return localPlayer;
	}

	public void fixedUpdate(float dt) {
		if (ongoingCommand != null) {
			boolean running = ongoingCommand.run(dt);
			Gdx.app.log(TAG, "A command is " + (running ? "running" : "stopping"));
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

	public void setReplayCommands(Command[] commands) {
		if (commands != null) {
			for (int i = 0; i < commands.length; i++) {
				this.commands.add(commands[i]);
			}
			// TODO run them
		}
		startTurn();
	}

	public Command[] getCommands() {
		return commands.toArray(new Command[commands.size()]);
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

}
