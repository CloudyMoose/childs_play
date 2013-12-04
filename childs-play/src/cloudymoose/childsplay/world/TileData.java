package cloudymoose.childsplay.world;

import java.util.Arrays;
import java.util.EnumSet;

import cloudymoose.childsplay.world.hextiles.Direction;
import cloudymoose.childsplay.world.hextiles.HexTile;
import cloudymoose.childsplay.world.units.Unit;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;

public class TileData {
	private Texture texture;
	private Sprite sprite;
	protected Unit occupant;
	public final EnumSet<Direction> borders;
	protected Area area;

	public TileData(Texture texture) {
		this.texture = texture;
		borders = EnumSet.noneOf(Direction.class);
	}

	public Sprite getSprite(HexTile<TileData> tile) {
		// Only generate the sprite once
		if (sprite == null) {
			float size = Constants.TILE_SIZE;
			float height = 2 * size;
			float width = (float) (Math.sqrt(3) / 2f * height);
			sprite = new Sprite(texture);
			Vector3 position = tile.getPosition();
			sprite.setBounds(position.x - width / 2, position.y - height / 2, width, height);
			sprite.setSize(width, height);
		}

		return sprite;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
		this.sprite = null;
	}

	public Unit getOccupant() {
		return occupant;
	}

	/** Set the occupant of this tile. Set it to null to remove it. */
	public void setOccupant(Unit occupant) {
		this.occupant = occupant;
	}

	public boolean isOccupied() {
		return occupant != null;
	}

	public void addBorders(Direction... directions) {
		borders.addAll(Arrays.asList(directions));
	}

	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public String toString() {
		return String.format("{area: %s, occupant: %s}", area, occupant);
	}

	public boolean isControlTile() {
		// TODO Auto-generated method stub
		return false;
	}
}
