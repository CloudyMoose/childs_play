package cm.childsplay.world;

import java.util.ArrayList;
import java.util.List;

public class World {

	List<Player> players;
	WorldMap map;

	public World() {
		createDemoWorld();
	}

	private void createDemoWorld() {
		map = new WorldMap();

		Player p1 = new Player();
		p1.units.add(new Unit(10, 10));
		p1.units.add(new Unit(-10, 10));
		p1.units.add(new Unit(10, -10));
		p1.units.add(new Unit(-10, -10));

		players = new ArrayList<Player>();
		players.add(p1);
	}

	public void fixedUpdate(float dt) {
		// TODO Auto-generated method stub

	}

}
