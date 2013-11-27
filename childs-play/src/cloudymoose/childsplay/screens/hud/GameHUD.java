package cloudymoose.childsplay.screens.hud;

import cloudymoose.childsplay.ChildsPlayGame;
import cloudymoose.childsplay.screens.AbstractMenuStageManager;
import cloudymoose.childsplay.world.LocalPlayer;
import cloudymoose.childsplay.world.World;
import cloudymoose.childsplay.world.hextiles.HexTile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class GameHUD extends AbstractMenuStageManager {

	World world;
	LocalPlayer player;

	Label labelUnitCount;
	CommandMenu commandMenu;
	TextButton btnEnd;

	public GameHUD(ChildsPlayGame game, Screen screen, World world) {
		super(game, screen);
		this.world = world;
		player = world.getLocalPlayer();
	}

	@Override
	protected Actor init() {

		labelUnitCount = new Label("0", getSkin());
		updateUnitCount();
		stage.addActor(labelUnitCount);

		btnEnd = new TextButton("End Turn", getSkin());
		btnEnd.setSize(200, 50);
		btnEnd.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.endTurn();
			}
		});
		stage.addActor(btnEnd);

		commandMenu = new CommandMenu(player, getSkin());
		stage.addActor(commandMenu);

		return null; /* We're adding the objects to the scene manually here */
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		labelUnitCount.setPosition(0, 20);
		btnEnd.setPosition(200, 20);
		commandMenu.resize(width, height);
	}

	public void updateUnitCount() {
		labelUnitCount.setText(String.format("%d units", player.units.size()));
	}

	public void displayCommandMenu(int screenX, int screenY, HexTile<?> clickedTile) {
		Gdx.app.log(TAG, "displayCommandMenu");
		commandMenu.setClickedTile(clickedTile);
		commandMenu.setPosition(screenX, screenY);
		commandMenu.setVisible(true);
	}

	public boolean isCommandMenuVisible() {
		return commandMenu.isVisible();
	}

	public void hideCommandMenu() {
		commandMenu.setVisible(false);
	}

	public void update() {
		updateUnitCount();
	}

}
