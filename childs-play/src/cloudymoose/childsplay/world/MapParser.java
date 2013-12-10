package cloudymoose.childsplay.world;

import java.util.ArrayList;
import java.util.List;

import cloudymoose.childsplay.world.hextiles.HexTile;
import cloudymoose.childsplay.world.units.AppleTree;
import cloudymoose.childsplay.world.units.Castle;
import cloudymoose.childsplay.world.units.Catapult;
import cloudymoose.childsplay.world.units.Child;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;

public class MapParser {

	private WorldMap map;
	private List<Player> players;
	
	private Player localPlayer;
	
	public void setLocalPlayer(Player localPlayer) {
		this.localPlayer = localPlayer;
	}
	
	
	public WorldMap getMap() {
		if (map == null) throw new NullPointerException();
		return map;
	}


	public List<Player> getPlayers() {
		if (players == null) throw new NullPointerException();
		return players;
	}



	public void parseJson(String mapName) {
		JsonMap jsonMap = new Json().fromJson(JsonMap.class, Gdx.files.internal("game/" + mapName + ".json"));
		
		map = new WorldMap();
		

		// Players
		players = new ArrayList<Player>(jsonMap.nb_players + 1);
		Player.setGaia(new Player(Player.GAIA_ID));
		players.add(Player.Gaia());

		for (int i = 1; i < jsonMap.nb_players + 1; i++) {
			Player player;
			if (localPlayer.id == i) {
				player = localPlayer;
			} else {
				player = new Player(i);
			}

			players.add(player);
		}
		
		
		List<List<HexTile<TileData>>> areaTiles = new ArrayList<List<HexTile<TileData>>>(jsonMap.nb_areas);
		for (int i = 0; i < jsonMap.nb_areas; i++) areaTiles.add(new ArrayList<HexTile<TileData>>());
		List<List<HexTile<TileData>>> controlPoints = new ArrayList<List<HexTile<TileData>>>(jsonMap.nb_areas);
		for (int i = 0; i < jsonMap.nb_areas; i++) controlPoints.add(new ArrayList<HexTile<TileData>>());

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
				HexTile<TileData> t = map.addValue(position.q, position.r, new TileData(tt));

				
				int areaid = jsonMap.areas[y][x][0] - 1;
				
				switch (jsonMap.objects[y][x][0]) {
				case 1:
					new Castle(players.get(jsonMap.objects[y][x][1])).onTile(t);
					break;
				case 2:
					new AppleTree().onTile(t);
					break;
				case 3:
					new Catapult().onTile(t);
					break;
				case 11:
					Player player = players.get(jsonMap.objects[y][x][1]);
					player.addUnit(new Child(player).onTile(t));
					break;
				}

				areaTiles.get(areaid).add(t);
				if(jsonMap.areas[y][x].length > 1) {
					controlPoints.get(areaid).add(t);
				}
			}

		}

		Area[] areas = new Area[jsonMap.nb_areas];
		for (int i = 0; i < jsonMap.nb_areas; ++i) {
			areas[i] = new Area(areaTiles.get(i), controlPoints.get(i));
		}
		
		map.setAreas(areas);
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
		public int nb_areas;
		public int nb_players;
		public int[][] tiles;
		public int[][][] areas;
		public int[][][] objects;
	}
}
