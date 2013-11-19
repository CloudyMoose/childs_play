package cloudymoose.childsplay.world;

import cloudymoose.childsplay.ChildsPlayGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

/** Takes the current state of the world and renders it to the screen */
public class WorldRenderer {

	private World world;
	private OrthographicCamera cam;

	/** for debug rendering **/
	private ShapeRenderer debugRenderer = new ShapeRenderer();

	public WorldRenderer(World world) {
		this.world = world;
		cam = new OrthographicCamera(10, 7);
		cam.setToOrtho(false, ChildsPlayGame.VIEWPORT_WIDTH, ChildsPlayGame.VIEWPORT_HEIGHT);
		cam.position.set(0, 0, 0);
		cam.update();
	}

	public void render(float dt) {
		debugRenderer.setProjectionMatrix(cam.combined);
		debugRenderer.begin(ShapeType.Line);

		for (Player player : world.players) {
			for (Unit unit : player.units) {
				Gdx.app.log("WR", "rendering unit: " + unit.toString());
				final int unitSize = 10;
				final int x = unit.getPosition().x;
				final int y = unit.getPosition().y;
				debugRenderer.setColor(Color.RED);
				debugRenderer.rect(x, y, unitSize, unitSize);
			}
		}

		debugRenderer.end();

	}

	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

}
