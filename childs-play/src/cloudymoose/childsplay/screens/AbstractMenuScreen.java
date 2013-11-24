package cloudymoose.childsplay.screens;

import cloudymoose.childsplay.ChildsPlayGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.Stage;

public abstract class AbstractMenuScreen<T extends AbstractMenuStageManager> implements Screen {

	protected final ChildsPlayGame mGame;
	// protected final OrthographicCamera mCamera;
	protected Stage mStage;
	protected T mMenuStageManager;

	public final String TAG = "AbstractMenuScreen";

	/** Please call {@link #setMenuStageManager(AbstractMenuStageManager)} in the implemented constructor. */
	public AbstractMenuScreen(ChildsPlayGame game) {
		mGame = game;
		// mCamera = new OrthographicCamera();
		// mCamera.setToOrtho(false, ChildsPlayGame.VIEWPORT_WIDTH, ChildsPlayGame.VIEWPORT_HEIGHT);
	}

	protected void setMenuStageManager(T menuStageManager) {
		mMenuStageManager = menuStageManager;
		mStage = menuStageManager.getStage();
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		mMenuStageManager.render(delta);
	}

	@Override
	public void resize(int width, int height) {
		mMenuStageManager.resize(width, height);
	}

	@Override
	public void show() {
		mMenuStageManager.show();
	}

	@Override
	public void hide() {
		mMenuStageManager.hide();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		Gdx.app.log(TAG, "Dispose");
		mMenuStageManager.dispose();
	}

}