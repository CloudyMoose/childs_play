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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class PlayerController implements InputProcessor {
	private LocalPlayer player;
	/** Used to get the world coordinates out of the screen coordinates, control camera... */
	private WorldRenderer renderer;
	private World world;
	private ChildsPlayGame game;
	private GameHUD hud;

	private Vector3 touchedWorldPosition;
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
		touchedWorldPosition = new Vector3(screenX, screenY, 0);
		renderer.cam.unproject(touchedWorldPosition);
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

		Gdx.app.log(TAG, "Clicked: " + touchedWorldPosition.toString());

		Unit clicked = world.hit(touchedWorldPosition);
		if (clicked == null) {
			if (hud.isCommandMenuVisible()) {
				hud.hideCommandMenu();
			} else {
				HexTile<?> clickedTile = world.getMap().getTileFromPosition(touchedWorldPosition);
				if (clickedTile == null) {
					Gdx.app.error(TAG, "No tile clicked! ");
				} else {
					if (player.hasSelectedUnit()) {
						hud.displayCommandMenu(screenX, screenY, clickedTile);
					}
				}

			}
			// player.moveSelectionTo(touchedWorldPosition.x, touchedWorldPosition.y);
		} else if (player.owns(clicked)) {
			Gdx.app.log(TAG, "Toggling selection on: " + clicked.toString());
			player.select(clicked);
		} else {
			Gdx.app.log(TAG, "Can't select that unit (not owned)");

		}

		// finger released, there is no touched position anymore
		touchedWorldPosition = null;
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (!enabled) return false;

		Vector3 moveVector = new Vector3(screenX, screenY, 0);
		renderer.cam.unproject(moveVector);

		/* Check the dragging distance to avoid it being too sensitive */
		if (!dragged) { /* Once we start moving the camera, no need to check again */
			float dst = moveVector.dst2(touchedWorldPosition);
			Gdx.app.log(TAG, "drag dist: " + dst);

			if (dst > 10 /* TODO Arbitrary value, to be tweaked */) {
				dragged = true;
			} else {
				return false;
			}
		}

		/* Compute camera movement */
		moveVector.sub(touchedWorldPosition).scl(Constants.MAP_SCROLL_SPEED * -1 /* for reversed scroll direction */);
		Gdx.app.debug(TAG, "move vector: " + moveVector.toString());
		renderer.moveCamera(moveVector);

		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		Vector3 v = new Vector3(screenX, screenY, 0);
		renderer.cam.unproject(v);
		player.setCurrentPosition(v);
		return true;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	/**
	 * Dummy class used for testing the hud without having to care about the exact math of the hex grid TODO: will have
	 * to be removed
	 */
	public static class DummyTile extends HexTile<Color> {
		public DummyTile(Vector3 position) {
			super((int) position.x, (int) position.y, Color.PINK, null);
		}

		public Vector2 getPosition() {
			return new Vector2((float) q, (float) r);
		}
	}

}
