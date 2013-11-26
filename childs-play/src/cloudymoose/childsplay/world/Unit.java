package cloudymoose.childsplay.world;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public abstract class Unit {

	/** center */
	public final Vector2 position;
	public final int size;
	public final Rectangle hitbox;
	public final int movementSpeed;
	public final int attackRange;
	public final int id;

	public Unit(int id, int x, int y, int size, int ms, int range) {
		position = new Vector2(x, y);
		this.size = size;
		hitbox = new Rectangle(0, 0, size, size);
		hitbox.setCenter(position);
		movementSpeed = ms;
		attackRange = range;
		this.id = id;
	}

	public Unit(Player owner, int x, int y, int size, int ms, int range) {
		this(owner.generateUnitId(), x, y, size, ms, range);
	}

	/** Update the position of the center and the hitbox */
	public void setPosition(int x, int y) {
		position.x = x;
		position.y = y;
		hitbox.setCenter(x, y);
	}

	/**  */
	public void move(Vector2 motion) {
		position.add(motion);
		hitbox.setCenter(position);
	}

	public Vector2 getPosition() {
		return position;
	}

	/** @return true if coordinates is within the unit's hit box */
	public boolean isHit(Vector3 coordinates) {
		return hitbox.contains(coordinates.x, coordinates.y);
	}

	public int getPlayerId() {
		return id / Player.UNIT_ID_OFFSET;
	}

	public String toString() {
		return "{id:" + id + " pos:(" + position.x + ", " + position.y + ")}";
	}
}
