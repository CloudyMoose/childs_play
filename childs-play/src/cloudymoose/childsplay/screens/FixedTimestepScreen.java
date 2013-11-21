package cloudymoose.childsplay.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;

public abstract class FixedTimestepScreen implements Screen {

	public static final String TAG = "FixedTimestepScreen";

	public float time;
	public float fixedFps;
	public float fixedDeltaTime;

	private float mCurrentTime;
	private float mMaxUpdates;

	public FixedTimestepScreen(float fps, int maxUpdates) {
		time = 0;
		fixedFps = fps;
		fixedDeltaTime = Math.max(1 / fps, 0.001f);
		mCurrentTime = 0;
		mMaxUpdates = maxUpdates;
	}

	@Override
	public void render(float dt) {
		int nbUpdates = 0;
		mCurrentTime += dt;

		update(dt);

		while (time < mCurrentTime) {
			nbUpdates++;
			time += fixedDeltaTime;
			fixedUpdate(fixedDeltaTime);
			if (nbUpdates >= mMaxUpdates) {
				Gdx.app.log(TAG, "update break at " + time);
				break;
			}
		}

		renderScreen(dt);
	}

	/** Called once per {@link #render(float)} call, at the beginning */
	public abstract void update(float dt);

	/**
	 * Can be called multiple times in {@link #render(float)}, between {@link #update(float)} and
	 * {@link #renderScreen(float)}
	 */
	public abstract void fixedUpdate(float dt);

	/** Called once per {@link #render(float)} call, at the end */
	public abstract void renderScreen(float dt);
}
