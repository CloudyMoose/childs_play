package cloudymoose.childsplay.world;

import com.badlogic.gdx.math.Vector2;

public class Child extends Unit {

	public Child(Player owner, int x, int y) {
		super(owner, x, y, 20, 1, 0);
	}

	public Child(Player player, Vector2 position) {
		this(player, (int)position.x, (int)position.y);
	}

}
