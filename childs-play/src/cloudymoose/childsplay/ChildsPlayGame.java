package cloudymoose.childsplay;

import java.io.IOException;

import cloudymoose.childsplay.networking.GameClient;
import cloudymoose.childsplay.networking.UpdateRequest.Init;
import cloudymoose.childsplay.networking.UpdateRequest.StartTurn;
import cloudymoose.childsplay.screens.GameScreen;
import cloudymoose.childsplay.screens.StartScreen;
import cloudymoose.childsplay.screens.WaitScreen;
import cloudymoose.childsplay.world.World;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;

public class ChildsPlayGame extends Game {
	/** the game uses fixed fps **/
	public static final int FIXED_FPS = 30;
	/** max fixed updates to prevent the game from hanging **/
	public static final int MAX_UPDATES = 20;

	/** defines the viewport width. also defines the window width on desktop **/
	public static final int VIEWPORT_WIDTH = 800;
	/** defines the viewport height. also defines the window height on desktop **/
	public static final int VIEWPORT_HEIGHT = 480;

	private static final String TAG = "ChildsPlayGame";

	public InputMultiplexer multiplexer;
	public final GameClient client;

	public final StartScreen startScreen;
	public final GameScreen gameScreen;
	public final WaitScreen waitScreen;

	private World world;

	public ChildsPlayGame() {
		client = new GameClient(this);
		try {
			client.connect();
		} catch (IOException e) {
			Gdx.app.error(TAG, "", e);
			Gdx.app.exit();
		}

		startScreen = new StartScreen(this);
		gameScreen = new GameScreen(this);
		waitScreen = new WaitScreen(this);
	}

	@Override
	public void create() {
		multiplexer = new InputMultiplexer();
		Gdx.input.setInputProcessor(multiplexer);
		setScreen(startScreen);
		client.init();
	}

	public void initWorld(Init initData) {
		world = new World(initData);
		gameScreen.init(world);
		setScreen(gameScreen);
	}

	public void endTurn() {
		setScreen(waitScreen);
		client.send(world.getCommands());
	}

	public void replay(StartTurn turnData) {
		Gdx.app.log(TAG, "Replay");
		gameScreen.mode = GameScreen.Mode.Replay;
		world.setReplayCommands(turnData.lastCommands);
		setScreen(gameScreen);

	}
}
