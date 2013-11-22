package cloudymoose.childsplay.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

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
