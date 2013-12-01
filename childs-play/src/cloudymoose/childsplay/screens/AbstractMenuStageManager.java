package cloudymoose.childsplay.screens;

import cloudymoose.childsplay.ChildsPlayGame;
import cloudymoose.childsplay.world.Constants;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/** Utility class allowing easier use of a {@link Stage} */
public abstract class AbstractMenuStageManager {

	public final String TAG = getClass().getSimpleName();

	/**
	 * A SpriteBatch has to be provided to the stages. If it's not, when the stage is disposed, it disposes the batch
	 * and is unable to initialize it again afterwards, making the stage itself not reusable.
	 */
	public static final SpriteBatch SpriteBatch = new SpriteBatch();
	private static Skin MENU_SKIN;

	protected boolean initialized;
	protected final Stage stage;
	protected final ChildsPlayGame game;
	protected final Screen screen;

	public AbstractMenuStageManager(ChildsPlayGame game, Screen screen) {
		this.game = game;
		this.screen = screen;
		initialized = false;
		stage = new Stage(0, 0, true, SpriteBatch);
	}

	/**
	 * Called the first time {@link #show()} is called, to initialize the menu. This method is also called in
	 * {@link #show()} after a {@link #dispose()}.
	 * 
	 * @return the Actor to add to the stage. Returning <code>null</code> is safe.
	 */
	protected abstract Actor init();

	/**
	 * Create a button with the provided text that commands the switch to the provided screen when clicked.
	 * 
	 * @param name
	 *            label of the button
	 * @param screenClass
	 *            class of the new screen to be called using {@link NyanicornGame#getScreen(Class)}
	 * @param disposeScreen
	 *            set to true to dispose the current screen after the switch.
	 */
	protected TextButton createScreenSwitchButton(String name, Screen screen, boolean disposeScreen) {
		TextButton button = new TextButton(name, getSkin());
		button.addListener(new ScreenSwitchListener(disposeScreen, screen));
		return button;
	}

	public Skin getSkin() {
		if (MENU_SKIN == null) {
			MENU_SKIN = game.assetManager.get(Constants.SKIN_JSON_PATH);
		}
		return MENU_SKIN;
	}

	public Stage getStage() {
		return stage;
	}

	/** @return <code>false</code> (in case extending classes want to return something) */
	public boolean render(float delta) {
		stage.act(delta);
		stage.draw();

		Table.drawDebug(stage); // TODO: Remove to hide the all debug lines
		return false;
	}

	public void resize(int width, int height) {
		stage.setViewport(width, height, true);
	}

	/**
	 * Adds the actor returned by {@link #init()} to the stage and adds the stage as input processor.
	 */
	public void show() {
		if (!initialized) {
			Gdx.app.log(TAG, "Initializing");
			Actor actor = init();
			if (actor != null) {
				stage.addActor(actor);
			}
			initialized = true;
		}
		game.multiplexer.addProcessor(stage);
	}

	public void dispose() {
		stage.dispose(); // removes the children of the stage. The menu is considered uninitialized then.
		initialized = false;
	}

	/** Removes the stage from the processors. */
	public void hide() {
		game.multiplexer.removeProcessor(stage);
	}

	protected class ScreenSwitchListener extends ClickListener {

		/** If true, the previous screen will be disposed. */
		private final boolean disposeScreen;
		/** screen we want to switch to */
		private final Screen destinationScreen;

		/**
		 * @param disposeScreen
		 *            if <code>true</code> the previous screen will be disposed after the switch
		 * @param screen
		 *            screen we want to switch to
		 */
		public ScreenSwitchListener(boolean disposeScreen, Screen screen) {
			super();
			this.disposeScreen = disposeScreen;
			this.destinationScreen = screen;
		}

		@Override
		public void clicked(InputEvent event, float x, float y) {
			game.setScreen(destinationScreen);
			if (disposeScreen) {
				screen.dispose();
			}
		}
	}
}
