package cloudymoose.childsplay;

import cloudymoose.childsplay.world.Constants;
import cloudymoose.childsplay.world.LocalPlayer;
import cloudymoose.childsplay.world.Unit;
import cloudymoose.childsplay.world.World;
import cloudymoose.childsplay.world.WorldRenderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class PlayerController implements InputProcessor {
	private LocalPlayer player;
	/** Used to get the world coordinates out of the screen coordinates, control camera... */
	private WorldRenderer renderer;
	private World world;
	private ChildsPlayGame game;

	private static final String TAG = "PlayerController";

	/** set it to false to disable all input handling */
	public boolean enabled;

	public PlayerController(ChildsPlayGame game, WorldRenderer renderer) {
		this.game = game;
		this.renderer = renderer;
		this.world = renderer.world;
		this.player = world.getLocalPlayer();
		this.enabled = true;
	}

	public void pollInput(float dt) {
		if (!enabled) return;

		int camDx = 0, camDy = 0;

		int screenX = Gdx.input.getX();
		int screenY = Gdx.input.getY();

		if (screenX < ChildsPlayGame.VIEWPORT_WIDTH * 0.05) {
			camDx = -Constants.MAP_SCROLL_SPEED;
		} else if (screenX > ChildsPlayGame.VIEWPORT_WIDTH * 0.95) {
			camDx = Constants.MAP_SCROLL_SPEED;
		}

		if (screenY < ChildsPlayGame.VIEWPORT_HEIGHT * 0.05) {
			camDy = Constants.MAP_SCROLL_SPEED;
		} else if (screenY > ChildsPlayGame.VIEWPORT_HEIGHT * 0.95) {
			camDy = -Constants.MAP_SCROLL_SPEED;
		}

		renderer.moveCamera(camDx, camDy);
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (!enabled) return false;

		switch (keycode) {
		case Keys.ESCAPE:
			break;
		case Keys.BACKSPACE:
			world.reset();
			break;
		case Keys.ENTER:
			game.endTurn();
			break;
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (!enabled) return false;

		Vector3 worldCoordinates = new Vector3(screenX, screenY, 0);
		renderer.cam.unproject(worldCoordinates);

		Gdx.app.log(TAG, "Clicked: " + worldCoordinates.toString());

		Unit clicked = world.hit(worldCoordinates);
		if (clicked == null) {
			player.moveSelectionTo(worldCoordinates.x, worldCoordinates.y);
		} else if (player.owns(clicked)) {
			Gdx.app.log(TAG, "Toggling selection on: " + clicked.toString());
			player.select(clicked);
		} else {
			Gdx.app.log(TAG, "Can't select that unit (not owned)");

		}

		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		float x = screenX - ChildsPlayGame.VIEWPORT_WIDTH / 2;
		float y = -screenY + ChildsPlayGame.VIEWPORT_HEIGHT / 2;
		// Why do I have to divide by 2 to get this right? Seems weird...
		x /= 2;
		y /= 2;
		player.setCurrentPosition(new Vector2(x, y));
		return true;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

}
