package cloudymoose.childsplay;

import cloudymoose.childsplay.screens.hud.GameHUD;
import cloudymoose.childsplay.world.Constants;
import cloudymoose.childsplay.world.LocalPlayer;
import cloudymoose.childsplay.world.Unit;
import cloudymoose.childsplay.world.World;
import cloudymoose.childsplay.world.WorldRenderer;
import cloudymoose.childsplay.world.hextiles.HexTile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public class PlayerController implements InputProcessor {
	private LocalPlayer player;
	/** Used to get the world coordinates out of the screen coordinates, control camera... */
	private WorldRenderer renderer;
	private World world;
	private ChildsPlayGame game;
	private GameHUD hud;

	private HexTile<Color> touchedTile;
	private Vector3 touchedPosition;
	private boolean dragged;

	private static final String TAG = "PlayerController";

	/** set it to false to disable all input handling */
	public boolean enabled;

	public PlayerController(ChildsPlayGame game, WorldRenderer renderer, GameHUD hud) {
		this.game = game;
		this.hud = hud;
		this.renderer = renderer;
		this.world = renderer.world;
		this.player = world.getLocalPlayer();
		this.enabled = true;
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (!enabled) return false;

		switch (keycode) {
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
		touchedPosition = new Vector3(screenX, screenY, 0);
		renderer.cam.unproject(touchedPosition);
		touchedTile = world.getMap().getTileFromPosition(touchedPosition);

		if (hud.isCommandMenuVisible()) {
			hud.hideCommandMenu();
		}

		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (!enabled) return false;
		if (dragged) { // Do not consider this touchUp as a click, because the pointer was dragged before.
			dragged = false;
			return false;
		} else {
			dragged = false;
		}

		if (touchedTile == null) return false; // Clicked outside the map

		Gdx.app.log(TAG, "Touched tile: " + touchedTile.toString());

		if (!world.targetableTiles.isEmpty()) {
			handleTargetSelectTouch(screenX, screenY);
		} else {
			handleTileSelectTouch(screenX, screenY);
		}

		// finger released, there is no touched position anymore
		touchedTile = null;
		touchedPosition = null;
		return false;
	}

	private void handleTileSelectTouch(int screenX, int screenY) {
		Unit clicked = touchedTile.getOccupant();
		if (clicked != null && player.owns(clicked)) {
			player.selectTile(touchedTile);
			hud.displayCommandMenu(screenX, screenY, touchedTile);
		} else {
			hud.hideCommandMenu();
		}
	}

	private void handleTargetSelectTouch(int screenX, int screenY) {
		if (world.targetableTiles.contains(touchedTile)) {
			world.selectTargetTile(touchedTile);
		} else {
			world.cancelCommand();
		}
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (!enabled) return false;
		if (touchedTile == null) return false;

		Vector3 moveVector = new Vector3(screenX, screenY, 0);
		renderer.cam.unproject(moveVector);

		/* Check the dragging distance to avoid it being too sensitive */
		if (!dragged) { /* Once we start moving the camera, no need to check again */
			float dst = moveVector.dst2(touchedPosition);
			Gdx.app.log(TAG, "drag dist: " + dst);

			if (dst > 50 /* TODO Arbitrary value, to be tweaked */) {
				dragged = true;
			} else {
				return false;
			}
		}

		/* Compute camera movement */
		moveVector.sub(touchedPosition).scl(Constants.MAP_SCROLL_SPEED * -1 /* for reversed scroll direction */);
		Gdx.app.debug(TAG, "move vector: " + moveVector.toString());
		renderer.moveCamera(moveVector);

		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
