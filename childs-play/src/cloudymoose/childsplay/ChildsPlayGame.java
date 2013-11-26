package cloudymoose.childsplay;

import java.io.IOException;
import java.net.BindException;

import cloudymoose.childsplay.networking.GameClient;
import cloudymoose.childsplay.networking.GameServer;
import cloudymoose.childsplay.networking.Message.Init;
import cloudymoose.childsplay.networking.Message.TurnRecap;
import cloudymoose.childsplay.screens.GameScreen;
import cloudymoose.childsplay.screens.MainMenuScreen;
import cloudymoose.childsplay.screens.WaitScreen;
import cloudymoose.childsplay.world.Constants;
import cloudymoose.childsplay.world.World;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class ChildsPlayGame extends Game {
	/** the game uses fixed fps **/
	public static final int FIXED_FPS = 30;
	/** max fixed updates to prevent the game from hanging **/
	public static final int MAX_UPDATES = 20;

	// Nexus 4 native resolution
	/** defines the viewport width. also defines the window width on desktop **/
	public static final int VIEWPORT_WIDTH = 1280;
	/** defines the viewport height. also defines the window height on desktop **/
	public static final int VIEWPORT_HEIGHT = 768;

	private static final String TAG = "ChildsPlayGame";

	public InputMultiplexer multiplexer;

	public GameScreen gameScreen;
	public WaitScreen waitScreen;
	public MainMenuScreen mainMenuScreen;
	public AssetManager assetManager;

	private boolean serverFound;
	private GameClient client;

	private World world;

	@Override
	public void create() {
		assetManager = initializeAssetManager();
		gameScreen = new GameScreen(this);
		waitScreen = new WaitScreen(this);
		mainMenuScreen = new MainMenuScreen(this);
		multiplexer = new InputMultiplexer();
		Gdx.input.setInputProcessor(multiplexer);
		setScreen(mainMenuScreen);

	}

	/**
	 * Load all the textures for the game here. That way they can be properly cleared when the application is stopped
	 * (important on android)
	 */
	private static AssetManager initializeAssetManager() {
		AssetManager am = new AssetManager();
		am.load(Constants.SKIN_JSON_PATH, Skin.class);
		am.finishLoading();
		return am;
	}

	public void initWorld(Init initData) {
		world = new World(initData);
		gameScreen.init(world);
		setScreen(waitScreen);
	}

	public void endTurn() {
		setScreen(waitScreen);
		client.send(world.exportCommands());
	}

	public void notifyTurnRecapReceived(TurnRecap turnData) {
		Gdx.app.log(TAG, "Turn recap received");
		gameScreen.mode = GameScreen.Mode.Replay;
		world.setReplayCommands(turnData.commands);
		waitScreen.notifyTurnRecapReceived();
	}

	public void startServer(int nbPlayers) {
		GameServer server;
		try {
			server = new GameServer(nbPlayers);
			server.start();
			serverFound = true;
			mainMenuScreen.notifyServerState(serverFound);
		} catch (BindException e) {
			System.out.println("Address already in use. The server must be already started.");
		} catch (IOException e) {
			Gdx.app.log(TAG, e.getMessage(), e);
		}
	}

	public void connectToServer() {
		try {
			client = new GameClient(this);
			client.connect();
			client.init();
			setScreen(waitScreen);
		} catch (IOException e) {
			Gdx.app.error(TAG, e.getMessage(), e);
			Gdx.app.exit();
		}
	}

	public void startTurn() {
		setScreen(gameScreen);
	}
}
