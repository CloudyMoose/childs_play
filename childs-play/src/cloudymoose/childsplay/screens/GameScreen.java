package cloudymoose.childsplay.screens;

import cloudymoose.childsplay.ChildsPlayGame;
import cloudymoose.childsplay.PlayerController;
import cloudymoose.childsplay.world.World;
import cloudymoose.childsplay.world.WorldRenderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;

public class GameScreen extends FixedTimestepScreen {

	private World world;
	private WorldRenderer renderer;
	private PlayerController playerController;

	public GameScreen() {
		super(ChildsPlayGame.FIXED_FPS, ChildsPlayGame.MAX_UPDATES);
	}

	@Override
	public void resize(int width, int height) {
		renderer.resize(width, height);
	}

	@Override
	public void show() {
		world = new World(0);
		renderer = new WorldRenderer(world);

		playerController = new PlayerController(world.getLocalPlayer(), renderer);
		ChildsPlayGame.instance.multiplexer.addProcessor(playerController);
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(float dt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void fixedUpdate(float dt) {
		world.fixedUpdate(dt);
	}

	@Override
	public void renderScreen(float dt) {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		renderer.render(dt);
		playerController.pollInput(dt);

	}
}
