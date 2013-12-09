package cloudymoose.childsplay.world;

import java.util.ArrayList;
import java.util.List;

import cloudymoose.childsplay.world.hextiles.HexTile;
import cloudymoose.childsplay.world.units.Castle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;

public class MapParser {

	public static WorldMap fromJson() {
		JsonMap jsonMap = new Json().fromJson(JsonMap.class, Gdx.files.internal("game/map.json"));
		WorldMap newMap = new WorldMap();

		List<HexTile<TileData>> area = new ArrayList<HexTile<TileData>>();
		List<HexTile<TileData>> controlPoints = new ArrayList<HexTile<TileData>>();

		for (int y = 0; y < jsonMap.tiles.length; y++) {
			for (int x = 0; x < jsonMap.tiles[y].length; x++) {
				TileType tt;
				switch (jsonMap.tiles[y][x]) {
				case 1:
					tt = TileType.Sand;
					break;
				case 3:
					tt = TileType.Grass;
					break;
				default:
					continue;
				}
				AxialCoords position = convert(x, y);
				HexTile<TileData> t = newMap.addValue(position.q, position.r, new TileData(tt));

				switch (jsonMap.special[y][x]) {
				case 1:
					controlPoints.add(t);
					break;
				case 3:
					new Castle(Player.Gaia()).onTile(t);
					break;
				}

				area.add(t);
			}

		}

		newMap.setAreas(new Area(area, controlPoints));
		return newMap;
	}

	private static AxialCoords convert(int q, int r) {
		@SuppressWarnings("unused")
		int x, y, z;
		// convert even-r offset to cube
		// x = q - (r + (r & 1)) / 2;
		// z = r;
		// y = -x - z;

		// convert even-q offset to cube
		x = q;
		z = r - (q + (q & 1)) / 2;
		y = -x - z;
		return new AxialCoords(x, z);
	}

	private static class AxialCoords {
		int q, r;

		public AxialCoords(int q, int r) {
			this.q = q;
			this.r = r;
		}
	}

	private static class JsonMap {
		public int[][] tiles;
		public int[][] special;
	}
}
