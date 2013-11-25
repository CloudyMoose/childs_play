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
	private GameHUD hud;

	public GameScreen(ChildsPlayGame game) {
		super(ChildsPlayGame.FIXED_FPS, ChildsPlayGame.MAX_UPDATES);
		this.game = game;
	}

	public void init(World world) {
		this.world = world;
		renderer = new WorldRenderer(world);
		hud = new GameHUD(game, this, world);
		playerController = new PlayerController(game, renderer, hud);
	}

	@Override
	public void resize(int width, int height) {
		renderer.resize(width, height);
		hud.resize(width, height);
	}

	@Override
	public void show() {
		game.multiplexer.addProcessor(playerController);
		hud.show();
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
		if (mode == Mode.Replay) {
			prepareReplayUpdate();
		} else {
			prepareDefaultUpdate();
		}

		world.fixedUpdate(dt);
	}

	/** Method called before {@link World#fixedUpdate(float)}, when in Default mode */
	private void prepareDefaultUpdate() {
		if (world.hasRunningCommand()) {
			playerController.enabled = false;
		} else {
			playerController.enabled = true;
		}
	}

	/** Method called before {@link World#fixedUpdate(float)}, when in Replay mode */
	private void prepareReplayUpdate() {
		playerController.enabled = false;
		if (!world.hasRunningCommand()) {
			boolean replayOver = !world.replayNextCommand();
			if (replayOver) {
				mode = Mode.Default;
				world.startTurn();
				Gdx.app.log(TAG, "Player #" + world.getLocalPlayer().id + " can now give his commands.");
			}
		}
	}

	@Override
	public void renderScreen(float dt) {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		renderer.render(dt);
		hud.render(dt);

		// playerController.pollInput(dt); // TODO Disabled camera scroll
	}

	public enum Mode {
		Default, Replay
	}
}
