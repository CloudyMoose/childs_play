package cloudymoose.childsplay.world.units;

import cloudymoose.childsplay.world.Player;
import cloudymoose.childsplay.world.TileData;
import cloudymoose.childsplay.world.hextiles.HexTile;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public abstract class Unit {

	/** center */
	public final Vector3 position;
	protected HexTile<TileData> occupiedTile;
	public final int id;
	public final int size;
	public final Rectangle hitbox;

	public final int maxHealthPoints;
	private int currentHealthPoints;
	public final int attackRange;
	public final int attackDamage;
	public final int movementRange;

	public Unit(int id, HexTile<TileData> tile, int size, int hp, int moveRange, int atkRange, int atkDamage) {
		position = new Vector3(tile.getPosition());
		occupiedTile = tile;
		this.id = id;
		this.size = size;
		hitbox = new Rectangle(0, 0, size, size);
		hitbox.setCenter(position.x, position.y);

		maxHealthPoints = hp;
		currentHealthPoints = hp;
		attackRange = atkRange;
		attackDamage = atkDamage;
		movementRange = moveRange;

		tile.value.setOccupant(this);
	}

	public Unit(Player owner, HexTile<TileData> tile, int size, int hp, int moveRange, int atkRange, int atkDamage) {
		this(owner.generateUnitId(), tile, size, hp, moveRange, atkRange, atkDamage);
	}

	/** Update the position of the center and the hitbox */
	public void setPosition(int x, int y) {
		position.set(x, y, 0);
		hitbox.setCenter(x, y);
	}

	public void move(Vector3 motion) {
		position.add(motion);
		hitbox.setCenter(position.x, position.y);
	}

	public Vector3 getPosition() {
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

	/** Remember to call this at the end of any movement, to keep the world state coherent */
	public void updateOccupiedTile(HexTile<TileData> newOccupiedTile) {
		occupiedTile.value.setOccupant(null);
		occupiedTile = newOccupiedTile;
		if (newOccupiedTile != null) {
			occupiedTile.value.setOccupant(this);
		}
	}

	public void attack(Unit target) {
		target.currentHealthPoints -= attackDamage;
	}

	public boolean isDead() {
		return currentHealthPoints <= 0;
	}

	public HexTile<TileData> getOccupiedTile() {
		return occupiedTile;
	}

}
