package cloudymoose.childsplay;

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

	/** Registers itself as the game instance for static access. */
	public ChildsPlayGame() {
		super();
		if (instance != null) {
			// Should not happen, we create only one game
			throw new RuntimeException("Duplicate game instance creation");
		}
		instance = this;
	}

	@Override
	public void create() {
		multiplexer = new InputMultiplexer();
		Gdx.input.setInputProcessor(multiplexer);
		setScreen(new GameScreen());
	}

}
