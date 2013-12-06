package cloudymoose.childsplay.world;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cloudymoose.childsplay.world.hextiles.HexTile;
import cloudymoose.childsplay.world.units.EnvironmentUnit;

import com.badlogic.gdx.Gdx;

public class Area extends AbstractCollection<HexTile<TileData>> {

	private static final String TAG = "Area";

	private static int areaCount = 0;

	public final int id;
	private final HashSet<HexTile<TileData>> tiles;

	/** Tiles that have to be occupied to get the control of the area */
	private final Set<HexTile<TileData>> controlTiles;

	/** Benefits players get by controlling the area */
	private final Set<EnvironmentUnit> benefitProviders;

	private Player owner;
	private int controlPoints;

	public Area(Collection<HexTile<TileData>> tiles, List<HexTile<TileData>> controlTiles) {
		id = areaCount++;
		controlPoints = 0;
		this.tiles = new HashSet<HexTile<TileData>>(tiles);
		this.controlTiles = new HashSet<HexTile<TileData>>(controlTiles);
		this.benefitProviders = new HashSet<EnvironmentUnit>();

		for (HexTile<TileData> tile : tiles) {
			if (tile.value.isOccupied() && tile.value.getOccupant() instanceof EnvironmentUnit) {
				benefitProviders.add((EnvironmentUnit) tile.value.getOccupant());
			}
		}

		for (HexTile<TileData> tile : tiles) {
			tile.value.setArea(this);
		}

	}

	public Player getOwner() {
		return owner;
	}

	/** Set it to <code>null</code> to make it uncontrolled */
	public void setOwner(Player owner) {
		this.owner = owner;
	}

	public Set<HexTile<TileData>> getControlTiles() {
		return controlTiles;
	}

	@Override
	public boolean add(HexTile<TileData> tile) {
		tile.value.setArea(this);
		return super.add(tile);
	}

	@Override
	public Iterator<HexTile<TileData>> iterator() {
		return tiles.iterator();
	}

	@Override
	public int size() {
		return tiles.size();
	}

	@Override
	public String toString() {
		return "Area " + id;
	}

	/**
	 * Try to get control of the area. If the player already controls the area, it reinforces it, raising the control
	 * points toward the original value (number of controlTiles)
	 */
	public void doControlAttempt(Player player) {
		if (player != owner) {
			Gdx.app.log(TAG, "Control attempt on area " + id);
			if (controlPoints <= 1) {
				Gdx.app.log(TAG, "Area in control!");
				owner = player;
				controlPoints = 0;
			} else {
				controlPoints -= 1;
			}
		} else {
			if (controlPoints < controlTiles.size()) {
				Gdx.app.log(TAG, "Reinforcement on area " + id);
				controlPoints += 1;

				if (controlPoints == controlTiles.size()) {
					Gdx.app.log(TAG, "The area is now unconstested!");
				}
			}
		}
	}

	public void resetControlPoints() {
		controlPoints = controlTiles.size();
	}

	/** An area is considered contested when its control points are lower than the max. */
	public boolean isContested() {
		return controlPoints < controlTiles.size();
	}

	public String getControlPointStatus() {
		return "(" + controlPoints + "/" + controlTiles.size() + ")";
	}

	public String getStatusMessage(Player currentPlayer) {
		String areaStatus = "Area " + id + " " + getControlPointStatus();
		if (owner == null) {
			return areaStatus + " is neutral";
		} else if (controlPoints == controlTiles.size()) {
			return areaStatus + " is completely reinforced for " + owner;
		} else {
			if (owner == currentPlayer) {
				return areaStatus + " is being reinforced by " + owner;
			} else {
				return areaStatus + " is under attack";
			}

		}
	}

	public void getBenefits(World world) {
		for (EnvironmentUnit eu : benefitProviders) {
			eu.doEnvironmentalEffect(world);
		}
	}
}
