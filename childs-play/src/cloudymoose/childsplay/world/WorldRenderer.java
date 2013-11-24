package cloudymoose.childsplay.world;

import cloudymoose.childsplay.world.hextiles.HexTile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

/** Takes the current state of the world and renders it to the screen */
public class WorldRenderer {

	public final World world;
	public final OrthographicCamera cam;

	private static final String TAG = "WorldRenderer";

	/** for debug rendering **/
	private ShapeRenderer debugRenderer = new ShapeRenderer();

	public WorldRenderer(World world) {
		this.world = world;
		cam = new OrthographicCamera();
	}

	public void render(float dt) {
		debugRenderer.setProjectionMatrix(cam.combined);

		// Render background hexagons
		debugRenderer.begin(ShapeType.Filled);
		for (HexTile<Color> tile : world.map) {
			float size = world.map.getTileSize();
			float height = 2 * size;
			float width = (float) (Math.sqrt(3) / 2f * height);
			Vector2 center = tile.getPosition();

			debugRenderer.setColor(tile.getValue());

			// Draw hexagon with one rectangle and two triangles for now
			debugRenderer.rect(
					center.x - width / 2, center.y - height / 4,
					width, height/2);

			debugRenderer.triangle(
					center.x - width / 2, center.y + height / 4,
					center.x,             center.y + height / 2,
					center.x + width / 2, center.y + height / 4);

			debugRenderer.triangle(
					center.x - width / 2, center.y - height / 4,
					center.x,             center.y - height / 2,
					center.x + width / 2, center.y - height / 4);
		}

		debugRenderer.end();

		debugRenderer.begin(ShapeType.Line);

		LocalPlayer localPlayer = world.getLocalPlayer();

		for (Player player : world.players) {
			for (Unit unit : player.units.values()) {
				if (localPlayer.selection == unit) {
					debugRenderer.setColor(Color.YELLOW);
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

	public void moveCamera(int camDx, int camDy) {
		cam.position.add(camDx, camDy, 0);
		cam.update();
	}

}
