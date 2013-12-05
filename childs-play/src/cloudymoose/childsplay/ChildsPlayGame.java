package cloudymoose.childsplay;

import java.io.IOException;
import java.net.BindException;
import java.util.Arrays;
import java.util.Random;

import cloudymoose.childsplay.networking.GameClient;
import cloudymoose.childsplay.networking.GameServer;
import cloudymoose.childsplay.networking.Message.Init;
import cloudymoose.childsplay.networking.Message.TurnRecap;
import cloudymoose.childsplay.screens.GameScreen;
import cloudymoose.childsplay.screens.MainMenuScreen;
import cloudymoose.childsplay.screens.PostGameScreen;
import cloudymoose.childsplay.screens.PostGameScreen.EndReason;
import cloudymoose.childsplay.screens.WaitScreen;
import cloudymoose.childsplay.world.Constants;
import cloudymoose.childsplay.world.World;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
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
	private static Random rand;

	public InputMultiplexer multiplexer;
	public AssetManager assetManager;

	private MainMenuScreen mainMenuScreen;
	private WaitScreen waitScreen;
	private GameScreen gameScreen;
	private PostGameScreen postGameScreen;

	private boolean serverFound;
	private boolean hasFocus;
	private GameClient client;
	private GameServer server;

	private World world;

	private NotificationService notificationService;

	public ChildsPlayGame(NotificationService ns) {
		notificationService = ns;
	}

	@Override
	public void create() {
		Gdx.app.log(TAG, "Create");
		hasFocus = true;
		assetManager = initializeAssetManager();
		mainMenuScreen = new MainMenuScreen(this);
		waitScreen = new WaitScreen(this);
		gameScreen = new GameScreen(this);
		postGameScreen = new PostGameScreen(this);
		multiplexer = new InputMultiplexer();
		Gdx.input.setInputProcessor(multiplexer);
		setScreen(mainMenuScreen);
	}

	@Override
	public void dispose() {
		Gdx.app.log(TAG, "Dispose");
		super.dispose();
	}

	@Override
	public void pause() {
		Gdx.app.log(TAG, "Pause");
		hasFocus = false;
		super.pause();
	}

	@Override
	public void resume() {
		Gdx.app.log(TAG, "Resume");
		hasFocus = true;
		super.resume();
	}

	@Override
	public void resize(int width, int height) {
		Gdx.app.log(TAG, "Resize");
		super.resize(width, height);
	}

	/**
	 * Load all the textures for the game here. That way they can be properly cleared when the application is stopped
	 * (important on android)
	 */
	private static AssetManager initializeAssetManager() {
		AssetManager am = new AssetManager();
		am.load(Constants.SKIN_JSON_PATH, Skin.class);
		am.load("data/grass.png", Texture.class);
		am.load("data/sand.png", Texture.class);
		am.load("data/conceptKid.png", Texture.class);
		am.finishLoading();
		return am;
	}

	public void initWorld(Init initData) {
		rand = new Random(initData.randomSeed);
		world = new World(initData);
		gameScreen.init(world);
		setScreen(waitScreen);
		mainMenuScreen.dispose();
	}

	public void endTurn() {
		setScreen(waitScreen);
		client.send(world.exportCommands());
	}

	public void notifyTurnRecapReceived(TurnRecap turnData) {
		if (turnData.commands == null) {
			Gdx.app.log(TAG, "Initial turn 'go' received");
			world.startEnvironmentPhase();
		} else {
			Gdx.app.log(TAG, "Turn recap : " + Arrays.asList(turnData.commands));
			world.prepareReplays(turnData);
			world.startReplayEnvironmentPhase();
		}

		if (!hasFocus) notificationService.notifySomething();

		waitScreen.notifyTurnRecapReceived();
	}

	public void notifyEndGameReceived() {
		postGameScreen.setReason(EndReason.Forfeit);
		setScreen(postGameScreen);
	}

	public void startServer(int nbPlayers) {
		try {
			server = new GameServer(nbPlayers);
			server.start();
			serverFound = true;
			mainMenuScreen.notifyServerState(serverFound);
			mainMenuScreen.addNotification("Server started.");
		} catch (BindException e) {
			Gdx.app.log(TAG, "Address already in use. The server must be already started.");
			serverFound = true;
		} catch (IOException e) {
			Gdx.app.log(TAG, e.getMessage(), e);
			mainMenuScreen.addNotification(e.getMessage());
		} finally {
			if (!serverFound) server = null;
		}
	}

	public void connectToServer() {
		boolean success = false;
		try {
			client = new GameClient(this, notificationService);
			serverFound = client.connect();
			if (serverFound) {
				client.init();
				success = true;
				setScreen(waitScreen);
			} else {
				mainMenuScreen.addNotification("Server not found");
			}
		} catch (IOException e) {
			Gdx.app.error(TAG, e.getMessage(), e);
			mainMenuScreen.addNotification(e.getMessage());
		} finally {
			if (!success) client = null;
		}
	}

	public void startTurn() {
		setScreen(gameScreen);
	}

	public void endGame(boolean isWinner) {
		client.send(world.exportCommands(), true);
		postGameScreen.setReason(isWinner ? EndReason.Win : EndReason.Lose);
		setScreen(postGameScreen);
	}

	public void toMainMenu() {
		toMainMenu(null);
	}

	public void toMainMenu(String notification) {
		terminateGame();
		setScreen(mainMenuScreen);
		if (notification != null) mainMenuScreen.addNotification(notification);
	}

	private void terminateGame() {
		waitScreen.dispose();
		postGameScreen.dispose();
		gameScreen.dispose();
		world = null;
		if (client != null) {
			client.terminateConnection();
			client = null;
		}
		if (server != null) {
			server.terminateConnection();
			server = null;
		}
		rand = null;
	}

	public void onClientDisconnection() {
		if (getScreen() == waitScreen && waitScreen.receivedTurnRecap() || getScreen() == gameScreen) {
			Gdx.app.log(TAG, "Delaying disconnection");
			// Delay the disconnection in case the client is replaying the game end context.
			gameScreen.isDisconnected();
		} else {
			Gdx.app.log(TAG, "Disconnection!");
			toMainMenu("Disconnected");
		}
	}

	public boolean hasFocus() {
		return hasFocus;
	}

	public static Random getRandom() {
		if (rand == null) throw new NullPointerException("Rand not initialized");
		return rand;
	}
}
