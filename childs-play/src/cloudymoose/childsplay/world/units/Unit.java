package cloudymoose.childsplay.world.units;

import java.util.List;

import cloudymoose.childsplay.world.Constants;
import cloudymoose.childsplay.world.Player;
import cloudymoose.childsplay.world.TileData;
import cloudymoose.childsplay.world.commands.Command;
import cloudymoose.childsplay.world.hextiles.HexTile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public abstract class Unit {

	/** center */
	public final Vector3 position;
	protected HexTile<TileData> occupiedTile;
	public final int id;
	public final int size;
	public final Rectangle hitbox;
	private boolean visible;

	public final int maxHealthPoints;
	private int currentHealthPoints;
	private final int attackRange;
	private final int attackDamage;
	private final int movementRange;

	private int usedTicketsCount;

	public Unit(int id, int size, int hp, int moveRange, int atkRange, int atkDamage) {
		position = new Vector3(0, 0, 0);
		occupiedTile = null;
		this.id = id;
		this.size = size;
		hitbox = new Rectangle(0, 0, size, size);
		hitbox.setCenter(position.x, position.y);
		visible = true;

		maxHealthPoints = hp;
		currentHealthPoints = hp;
		attackRange = atkRange;
		attackDamage = atkDamage;
		movementRange = moveRange;

		usedTicketsCount = 0;
	}

	public Unit(Player owner, int size, int hp, int moveRange, int atkRange, int atkDamage) {
		this(owner.generateUnitId(), size, hp, moveRange, atkRange, atkDamage);
	}

	/** Update the position of the center and the hitbox */
	public void setPosition(int x, int y) {
		position.set(x, y, 0);
		hitbox.setCenter(x, y);
	}

	/** Update the position of the center and the hitbox */
	public void setPosition(Vector3 newPosition) {
		position.set(newPosition.x, newPosition.y, 0);
		hitbox.setCenter(newPosition.x, newPosition.y);
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

	@Override
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
		target.takeDamage(getAttackDamage());
	}

	protected void takeDamage(int damage) {
		currentHealthPoints -= damage;
	}

	public boolean isDead() {
		return currentHealthPoints <= 0;
	}

	public HexTile<TileData> getOccupiedTile() {
		return occupiedTile;
	}

	public abstract List<Class<? extends Command>> getSupportedCommands();

	public double getUsedTicketsCount() {
		return usedTicketsCount;
	}

	/** Takes efficiency (relative to the number of tickets used by this unit) into account */
	public int getAttackRange() {
		return (int) Math.round(attackRange * efficiency());
	}

	/** Takes efficiency (relative to the number of tickets used by this unit) into account */
	public int getAttackDamage() {
		double rawAd = Math.round(attackDamage * efficiency());
		Gdx.app.log("AD Calculation", String.format("tickets: %d, efficiency: %f, rawAd: %f, rounded: %d",
				usedTicketsCount, efficiency(), rawAd, (int) rawAd));
		return (int) rawAd;
	}

	/** Takes efficiency (relative to the number of tickets used by this unit) into account */
	public int getMovementRange() {
		return (int) Math.round(movementRange * efficiency());
	}

	protected double efficiency() {
		return Math.pow(Constants.DIMINISHING_RETURN_BASE, usedTicketsCount);
	}

	public void useTicket() {
		usedTicketsCount++;
	}

	public void resetUsedTicketCount() {
		usedTicketsCount = 0;
	}

	/** Convenience method to set the tile and return the {@link Unit} instance */
	public Unit onTile(HexTile<TileData> tile) {
		if (occupiedTile != null) {
			occupiedTile.value.setOccupant(null);
		}

		occupiedTile = tile;
		tile.value.setOccupant(this);
		setPosition(tile.getPosition());

		return this;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isVisible() {
		return visible;
	}

}
