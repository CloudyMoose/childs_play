package cloudymoose.childsplay.screens.hud;

import cloudymoose.childsplay.ChildsPlayGame;
import cloudymoose.childsplay.screens.AbstractMenuStageManager;
import cloudymoose.childsplay.world.Constants;
import cloudymoose.childsplay.world.TileData;
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

	Label labelUnitCount;
	Label labelTicketCount;
	CommandMenu commandMenu;
	TextButton btnEnd;

	public GameHUD(ChildsPlayGame game, Screen screen, World world) {
		super(game, screen);
		this.world = world;
	}

	@Override
	protected Actor init() {

		labelUnitCount = new Label("", getSkin());
		updateUnitCount();
		stage.addActor(labelUnitCount);

		labelTicketCount = new Label("", getSkin());
		updateTicketCount();
		stage.addActor(labelTicketCount);

		btnEnd = new TextButton("End Turn", getSkin());
		btnEnd.setSize(200, 50);
		btnEnd.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.endTurn();
			}
		});
		stage.addActor(btnEnd);

		commandMenu = new CommandMenu(world.getLocalPlayer(), getSkin());
		stage.addActor(commandMenu);

		return null; /* We're adding the objects to the scene manually here */
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		labelUnitCount.setPosition(0, 20);
		labelTicketCount.setPosition(0, 40);
		btnEnd.setPosition(200, 20);
		commandMenu.resize(width, height);
	}

	public void updateUnitCount() {
		labelUnitCount.setText(String.format("%d units", world.getCurrentPlayer().units.size()));
	}

	private void updateTicketCount() {
		labelTicketCount.setText(String.format("%d/%d tickets", world.getCurrentPlayer().getRemainingTickets(),
				Constants.NB_TICKETS));
	}

	public void displayCommandMenu(int screenX, int screenY, HexTile<TileData> clickedTile) {
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
		updateTicketCount();
	}

}
