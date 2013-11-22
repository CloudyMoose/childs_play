package cloudymoose.childsplay.world;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import cloudymoose.childsplay.networking.UpdateRequest.Init;
import cloudymoose.childsplay.world.Command.Runner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;

public class World {

	List<Player> players;
	WorldMap map;

	private final Queue<Command> commands;

	private LocalPlayer localPlayer;
	private Init initData;

	private static final String TAG = "World";
	public static final int NB_TICKETS = 2;

	private int remainingTickets;

	private Runner ongoingCommand;

	public World(Init initData) {
		commands = new LinkedList<Command>();
		this.initData = initData;
		createDemoWorld();
	}

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
			Gdx.app.log(TAG, "updating running command");
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

	public void setReplayCommands(Command[] commands) {
		for (int i = 0; i < commands.length; i++) {
			this.commands.add(commands[i]);
		}
		// TODO run them
		startTurn();
	}

	public Command[] getCommands() {
		return commands.toArray(new Command[commands.size()]);
	}

	public void runCommand(Command command) {
		Gdx.app.log(TAG, "remainingTickets (before action): " + remainingTickets);
		if (remainingTickets <= 0) return;

		ongoingCommand = command.execute(this);
		ongoingCommand.start();
		commands.add(command);
		--remainingTickets;
	}

	public void reset() {
		createDemoWorld();
	}

	public void startTurn() {
		Gdx.app.log(TAG, "startTurn");
		remainingTickets = NB_TICKETS;
	}

	public Unit getUnit(int unitId) {
		int playerId = unitId / Player.UNIT_ID_OFFSET;
		return players.get(playerId).units.get(unitId);
	}

	public boolean hasRunningCommand() {
		return ongoingCommand != null && ongoingCommand.running;
	}

}
