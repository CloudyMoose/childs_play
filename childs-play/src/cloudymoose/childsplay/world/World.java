package cloudymoose.childsplay.world;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector3;

public class World {

	List<Player> players;
	WorldMap map;

	private LocalPlayer localPlayer;

	public World(int localPlayerId) {
		// Local player id should be used to create the local player from the right
		// item in the list of players
		createDemoWorld();
	}

	private void createDemoWorld() {
		map = new WorldMap();

		localPlayer = new LocalPlayer();
		localPlayer.units.add(new Unit(10, 10));
		localPlayer.units.add(new Unit(-10, 10));
		localPlayer.units.add(new Unit(10, -10));
		localPlayer.units.add(new Unit(-10, -10));

		players = new ArrayList<Player>();
		players.add(localPlayer);
	}

	public LocalPlayer getLocalPlayer() {
		return localPlayer;
	}

	public void fixedUpdate(float dt) {
		// TODO Auto-generated method stub
	}

	public Unit hit(Vector3 worldCoordinates) {
		// TODO: change to a better way to look for the clicked unit (using map areas or something similar)
		for (Player p : players) {
			for (Unit u : p.units) {
				if (u.isHit(worldCoordinates)) {
					return u;
				}
			}
		}
		return null;
	}

}
