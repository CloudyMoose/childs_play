package cloudymoose.childsplay.screens;

import cloudymoose.childsplay.ChildsPlayGame;
import cloudymoose.childsplay.PlayerController;
import cloudymoose.childsplay.world.World;
import cloudymoose.childsplay.world.WorldRenderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;

public class GameScreen extends FixedTimestepScreen {

	private ChildsPlayGame game;
	private World world;
	private WorldRenderer renderer;
	private PlayerController playerController;
	public Mode mode = Mode.Default;

	public GameScreen(ChildsPlayGame game) {
		super(ChildsPlayGame.FIXED_FPS, ChildsPlayGame.MAX_UPDATES);
		this.game = game;
	}

	public void init(World world) {
		this.world = world;
		renderer = new WorldRenderer(world);
		playerController = new PlayerController(game, renderer);
	}

	@Override
	public void resize(int width, int height) {
		renderer.resize(width, height);
	}

	@Override
	public void show() {
		game.multiplexer.addProcessor(playerController);
	}

	@Override
	public void hide() {
		game.multiplexer.removeProcessor(playerController);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		// TODO important! will have to be implemented
	}

	@Override
	public void update(float dt) {
	}

	@Override
	public void fixedUpdate(float dt) {
		if (world.hasRunningCommand()) {
			playerController.enabled = false;
		} else {
			playerController.enabled = true;
		}
		world.fixedUpdate(dt);
	}

	@Override
	public void renderScreen(float dt) {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		renderer.render(dt);
		// playerController.pollInput(dt); // TODO Disabled camera scroll

	}

	public enum Mode {
		Default, Replay
	}
}
