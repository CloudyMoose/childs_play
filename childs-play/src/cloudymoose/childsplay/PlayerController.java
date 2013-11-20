package cloudymoose.childsplay;

import cloudymoose.childsplay.world.LocalPlayer;
import cloudymoose.childsplay.world.Unit;
import cloudymoose.childsplay.world.World;
import cloudymoose.childsplay.world.WorldRenderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;

public class PlayerController implements InputProcessor {
	public static final int MAP_SCROLL_SPEED = 2;

	private LocalPlayer player;
	/** Used to get the world coordinates out of the screen coordinates, control camera... */
	private WorldRenderer renderer;
	private World world;

	public PlayerController(LocalPlayer localPlayer, WorldRenderer renderer) {
		this.player = localPlayer;
		this.renderer = renderer;
		this.world = renderer.world;
	}

	public void pollInput(float dt) {
		int camDx = 0, camDy = 0;

		int screenX = Gdx.input.getX();
		int screenY = Gdx.input.getY();

		if (screenX < ChildsPlayGame.VIEWPORT_WIDTH * 0.05) {
			camDx = -MAP_SCROLL_SPEED;
		} else if (screenX > ChildsPlayGame.VIEWPORT_WIDTH * 0.95) {
			camDx = MAP_SCROLL_SPEED;
		}

		if (screenY < ChildsPlayGame.VIEWPORT_HEIGHT * 0.05) {
			camDy = MAP_SCROLL_SPEED;
		} else if (screenY > ChildsPlayGame.VIEWPORT_HEIGHT * 0.95) {
			camDy = -MAP_SCROLL_SPEED;
		}

		renderer.moveCamera(camDx, camDy);
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		Vector3 worldCoordinates = new Vector3(screenX, screenY, 0);
		renderer.cam.unproject(worldCoordinates);

		Gdx.app.log("PC", "Clicked: " + worldCoordinates.toString());

		Unit clicked = world.hit(worldCoordinates);
		if (clicked == null) {
			player.moveSelectionTo(worldCoordinates.x, worldCoordinates.y);
		} else {
			Gdx.app.log("PC", "Toggling selection on: " + clicked.toString());
			player.toggleSelect(clicked);
		}

		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}
