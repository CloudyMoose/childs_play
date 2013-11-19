package cm.childsplay.world;

import java.awt.Point;

public class Unit {

	private Point position;

	public Unit(int x, int y) {
		position = new Point(x, y);
	}

	public Unit() {
		this(0, 0);
	}

	public Point getPosition() {
		return position;
	}

}
