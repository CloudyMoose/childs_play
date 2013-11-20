package cloudymoose.childsplay.world;

import cloudymoose.childsplay.ChildsPlayGame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

/** Takes the current state of the world and renders it to the screen */
public class WorldRenderer {

	public final World world;
	public final OrthographicCamera cam;

	/** for debug rendering **/
	private ShapeRenderer debugRenderer = new ShapeRenderer();

	public WorldRenderer(World world) {
		this.world = world;
		cam = new OrthographicCamera();
		cam.setToOrtho(false, ChildsPlayGame.VIEWPORT_WIDTH / 2, ChildsPlayGame.VIEWPORT_HEIGHT / 2);
		cam.position.set(0, 0, 0);
		cam.update();
	}

	public void render(float dt) {
		debugRenderer.setProjectionMatrix(cam.combined);
		debugRenderer.begin(ShapeType.Line);

		LocalPlayer localPlayer = world.getLocalPlayer();

		for (Player player : world.players) {
			for (Unit unit : player.units) {
				final int unitSize = 10;
				final float x = unit.getPosition().x;
				final float y = unit.getPosition().y;

				if (localPlayer.isSelected(unit)) {
					debugRenderer.setColor(Color.YELLOW);
				} else {
					debugRenderer.setColor(Color.RED);
				}
				debugRenderer.rect(x, y, unitSize, unitSize);
			}
		}

		debugRenderer.end();

	}

	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	public void moveCamera(int camDx, int camDy) {
		cam.position.add(camDx, camDy, 0);
		cam.update();
	}

}
