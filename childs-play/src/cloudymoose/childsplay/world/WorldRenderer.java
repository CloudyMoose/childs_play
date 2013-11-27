package cloudymoose.childsplay.world;

import cloudymoose.childsplay.world.hextiles.HexTile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;

/** Takes the current state of the world and renders it to the screen */
public class WorldRenderer {

	public final World world;
	public final OrthographicCamera cam = new OrthographicCamera();
	private final ShapeRenderer debugRenderer = new ShapeRenderer();
	public static final AssetManager assetManager = new AssetManager();

	private static final String TAG = "WorldRenderer";

	public WorldRenderer(World world) {
		this.world = world;
	}

	public void render(float dt) {
		if (world.preferredCameraFocus != null) {
			// TODO: jumps at the beginning. It should be smoothed or something...
			setCameraPosition(world.preferredCameraFocus);
		}
		debugRenderer.setProjectionMatrix(cam.combined);

		// Render background hexagons
		HexTile<Color> selectedTile = world.getLocalPlayer().touchedTile;

		for (HexTile<Color> tile : world.map) {
			float size = world.map.getTileSize();
			float height = 2 * size;
			float width = (float) (Math.sqrt(3) / 2f * height);
			Vector3 center = tile.getPosition();

			debugRenderer.begin(ShapeType.Filled);
			debugRenderer.setColor(tile.getValue());

			if (selectedTile == tile) {
				debugRenderer.setColor(Color.WHITE);
			} else if (world.targetableTiles.contains(tile)) {
				debugRenderer.setColor(Color.ORANGE);
			}

			// Draw hexagon with one rectangle and two triangles for now
			debugRenderer.rect(center.x - width / 2, center.y - height / 4, width, height / 2);

			debugRenderer.triangle(center.x - width / 2, center.y + height / 4, center.x, center.y + height / 2,
					center.x + width / 2, center.y + height / 4);

			debugRenderer.triangle(center.x - width / 2, center.y - height / 4, center.x, center.y - height / 2,
					center.x + width / 2, center.y - height / 4);
			debugRenderer.end();

			debugRenderer.begin(ShapeType.Line);
			debugRenderer.setColor(Color.BLACK);
			debugRenderer
					.polygon(new float[] { center.x - width / 2, center.y + height / 4, center.x,
							center.y + height / 2, center.x + width / 2, center.y + height / 4, center.x + width / 2,
							center.y - height / 4, center.x, center.y - height / 2, center.x - width / 2,
							center.y - height / 4 });

			debugRenderer.end();

		}

		debugRenderer.begin(ShapeType.Filled);

		LocalPlayer localPlayer = world.getLocalPlayer();

		for (Player player : world.players) {
			for (Unit unit : player.units.values()) {
				if (localPlayer.selection == unit) {
					debugRenderer.setColor(Color.YELLOW);
				} else if (player == localPlayer){
					debugRenderer.setColor(Color.BLUE);
				} else if (player.id == Player.GAIA_ID){
					debugRenderer.setColor(Color.GRAY);					
				} else {
					debugRenderer.setColor(Color.RED);										
				}
				debugRenderer.rect(unit.hitbox.x, unit.hitbox.y, unit.hitbox.width, unit.hitbox.height);
			}
		}

		debugRenderer.end();

	}

	public void resize(int width, int height) {
		Gdx.app.log(TAG, "resize(" + width + ", " + height + ")");
		cam.setToOrtho(false, width / 2, height / 2);

		cam.position.set(0, 0, 0);

		cam.update();
	}

	/**
	 * @param moveVector
	 *            movement vector that will be used to move the current camera position
	 */
	public void moveCamera(Vector3 moveVector) {
		cam.position.add(moveVector);
		cam.update();
	}

	/**
	 * @param position
	 *            new camera position
	 */
	public void setCameraPosition(Vector3 position) {
		cam.position.set(position);
		cam.update();
	}

	public void dispose() {
		debugRenderer.dispose();
	}

}
