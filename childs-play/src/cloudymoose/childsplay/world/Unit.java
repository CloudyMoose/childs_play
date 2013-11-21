package cloudymoose.childsplay.world;

import java.awt.Point;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public abstract class Unit {

	/** corner */
	protected Vector2 position;
	protected int size;
	protected Rectangle hitbox;
	protected int movementSpeed;
	protected int attackRange;
	protected Point destination;
	protected Vector2 currentMovement;
	public final int id;

	protected final float POSITION_EPSILON = 5f; // Position accuracy

	public Unit(int id, int x, int y, int size, int ms, int range) {
		position = new Vector2(x, y);
		this.size = size;
		hitbox = new Rectangle(x, y, size, size);
		movementSpeed = ms;
		attackRange = range;
		this.id = id;
	}

	public Unit(Player owner, int x, int y, int size, int ms, int range) {
		this(owner.generateUnitId(), x, y, size, ms, range);
	}

	public Vector2 getPosition() {
		return position;
	}

	public void setDestination(int x, int y) {
		destination = new Point(x, y);
		// TODO replace with pathfinding
		double angle = Math.atan2((y - position.y), x - position.x); // between the line made by the 2 points and the
																		// x-axis
		currentMovement = new Vector2((float) (movementSpeed * Math.cos(angle)),
				(float) (movementSpeed * Math.sin(angle)));
	}

	protected void moveTo(int x, int y) {
		position.x = x;
		position.y = y;
		hitbox.setPosition(x, y);
	}

	public void update(float dt) {
		updateMovement(dt);
	}

	protected void updateMovement(float dt) {
		if (destination != null) {
			position.add(currentMovement);
			hitbox.setPosition(position);

			if (Math.abs(position.x - destination.x) < POSITION_EPSILON
					&& Math.abs(position.y - destination.y) < POSITION_EPSILON) {
				// Trip over!
				destination = null;
				currentMovement = null;
			}
		}
	}

	/** @return true if coordinates is within the unit's hit box */
	public boolean isHit(Vector3 coordinates) {
		return hitbox.contains(coordinates.x, coordinates.y);
	}
}
