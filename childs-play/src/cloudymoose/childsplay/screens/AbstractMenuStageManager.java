package cloudymoose.childsplay.screens;

import cloudymoose.childsplay.ChildsPlayGame;

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

public abstract class AbstractMenuStageManager {

	public final String TAG = getClass().getSimpleName();

	/**
	 * A SpriteBatch has to be provided to the stages. If it's not, when the stage is disposed, it disposes the batch
	 * and is unable to initialize it again afterwards, making the stage itself not reusable.
	 */
	public static final SpriteBatch SpriteBatch = new SpriteBatch();
	public static final Skin MENU_SKIN = new Skin(Gdx.files.internal("uiskin.json"));

	protected boolean mInitialized;
	protected final Stage mStage;
	protected final ChildsPlayGame mGame;
	protected final Screen mScreen;

	public AbstractMenuStageManager(ChildsPlayGame game, Screen screen) {
		mGame = game;
		mScreen = screen;
		mInitialized = false;
		mStage = new Stage(0, 0, true, SpriteBatch);
	}

	/**
	 * Called the first time {@link #show()} is called, to initialize the menu. This method is also called in
	 * {@link #show()} after a {@link #dispose()}.
	 * 
	 * @return the Actor to add to the stage
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
		return MENU_SKIN;
	}

	public Stage getStage() {
		return mStage;
	}

	public void render(float delta) {
		mStage.act(delta);
		mStage.draw();

		Table.drawDebug(mStage); // TODO: Remove to hide the all debug lines
	}

	public void resize(int width, int height) {
		mStage.setViewport(width, height, true);
	}

	/**
	 * Adds the actor returned by {@link #init()} to the stage and adds the stage as input processor.
	 */
	public void show() {
		if (!mInitialized) {
			Gdx.app.log(TAG, "Initializing");
			mStage.addActor(init());
			mInitialized = true;
		}
		mGame.multiplexer.addProcessor(mStage);
	}

	public void dispose() {
		mStage.dispose(); // removes the children of the stage. The menu is considered uninitialized then.
		mInitialized = false;
	}

	/** Removes the stage from the processors. */
	public void hide() {
		mGame.multiplexer.removeProcessor(mStage);
	}

	protected class ScreenSwitchListener extends ClickListener {

		private final boolean mDisposeScreen;
		private final Screen screen;

		public ScreenSwitchListener(boolean disposeScreen, Screen screen) {
			super();
			mDisposeScreen = disposeScreen;
			this.screen = screen;
		}

		@Override
		public void clicked(InputEvent event, float x, float y) {
			mGame.setScreen(screen);
			if (mDisposeScreen) {
				mScreen.dispose();
			}
		}
	}
}
