package cloudymoose.childsplay;

import cloudymoose.childsplay.networking.NetworkPeer;
import cloudymoose.childsplay.screens.GameScreen;

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

	public static ChildsPlayGame instance = null;

	public InputMultiplexer multiplexer;
	public final NetworkPeer networkPeer;

	private ChildsPlayGame(NetworkPeer networkPeer) {
		this.networkPeer = networkPeer;
	}

	public static ChildsPlayGame createInstance(NetworkPeer networkPeer) {
		instance = new ChildsPlayGame(networkPeer);
		return instance;
	}

	@Override
	public void create() {
		multiplexer = new InputMultiplexer();
		Gdx.input.setInputProcessor(multiplexer);
		setScreen(new GameScreen());
	}

}
