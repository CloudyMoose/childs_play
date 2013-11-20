package cloudymoose.childsplay.world;

import java.awt.Point;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class Unit {

	/** corner */
	private Point position;
	private int size;
	private Rectangle hitbox;

	public Unit(int x, int y) {
		position = new Point(x, y);
		size = 10;
		hitbox = new Rectangle(x, y, 10, 10);
	}

	public Unit() {
		this(0, 0);
	}

	public Point getPosition() {
		return position;
	}

	public void moveTo(float x, float y) {
		position.x = (int) x;
		position.y = (int) y;
		hitbox.setPosition(x, y);

	}

	/** @return true if coordinates is within the unit's hit box */
	public boolean isHit(Vector3 coordinates) {
		return hitbox.contains(coordinates.x, coordinates.y);
	}
}
