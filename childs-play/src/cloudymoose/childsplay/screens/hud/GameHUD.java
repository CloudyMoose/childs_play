package cloudymoose.childsplay.screens.hud;

import cloudymoose.childsplay.ChildsPlayGame;
import cloudymoose.childsplay.screens.AbstractMenuStageManager;
import cloudymoose.childsplay.world.Constants;
import cloudymoose.childsplay.world.Player;
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
	Label labelInfoLog;
	HPBars hpBars;
	CommandMenu commandMenu;
	TextButton btnEnd;
	TileStatusPreview tsp;

	private int remainingInfoLogDisplayTime;
	/** In seconds */
	private static final int MSG_DISPLAY_TIME = 3;

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

		commandMenu = new CommandMenu(world.getLocalPlayer(), this.game.assetManager);
		stage.addActor(commandMenu);

		tsp = new TileStatusPreview(world.getLocalPlayer(), getSkin());
		stage.addActor(tsp);

		labelInfoLog = new Label("", getSkin());
		stage.addActor(labelInfoLog);

		hpBars = new HPBars(game.assetManager, world.getPlayers().get(1),
				world.getPlayers().size() == 2 ? Player.Gaia() : world.getPlayers().get(2));
		stage.addActor(hpBars);

		return null; /* We're adding the objects to the scene manually here */
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		labelUnitCount.setPosition(0, height - 20);
		labelTicketCount.setPosition(0, height - 40);

		labelInfoLog.setPosition(width / 2, height - 50);
		btnEnd.setPosition(200, 20);
		commandMenu.resize(width, height);
		Gdx.app.log(TAG, tsp.getWidth() + " " + tsp.getHeight());
		tsp.validate();
		Gdx.app.log(TAG, tsp.getWidth() + " " + tsp.getHeight());
		tsp.setPosition(100, 100);

		hpBars.setWidth(width * 0.75f);
		hpBars.setPosition(width / 2, height - 30);
	}

	private void updateUnitCount() {
		labelUnitCount.setText(String.format("%d units", world.getCurrentPlayer().units.size()));
	}

	private void updateTicketCount() {
		labelTicketCount.setText(String.format("%d/%d tickets", world.getCurrentPlayer().getRemainingTickets(),
				Constants.NB_TICKETS));
	}

	public void displayCommandMenu(int screenX, int screenY, HexTile<TileData> clickedTile) {
		Gdx.app.log(TAG, "displayCommandMenu");

		if (world.getPhase() != World.Phase.Command) {
			Gdx.app.error(TAG, "Can't display the command menu outside of the command phase");
			return;
		}

		if (clickedTile.value.getOccupant() == null || clickedTile.value.getOccupant().getSupportedCommands() == null
				|| clickedTile.value.getOccupant().getSupportedCommands().isEmpty()) {
			Gdx.app.error(TAG, "Can't display the command menu, there is no valid command for the unit "
					+ clickedTile.value.getOccupant());
			return;
		}

		commandMenu.setClickedTile(clickedTile);
		commandMenu.setVisible(true);
		commandMenu.setPosition(screenX, screenY);
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
		updateInfoLog();
		hpBars.updateHP();
		tsp.update();
	}

	public void setButtonsVisible(boolean visible) {
		btnEnd.setVisible(visible);
	}

	private void updateInfoLog() {
		if (remainingInfoLogDisplayTime > 0) {
			remainingInfoLogDisplayTime -= 1;
		} else {
			String txt = world.getInfoLogEntry();
			labelInfoLog.setText(txt);
			if (txt != null) {
				remainingInfoLogDisplayTime = ChildsPlayGame.FIXED_FPS * MSG_DISPLAY_TIME;
			}
		}
	}

	/** @return <code>true</code> if there is an animation or something running */
	@Override
	public boolean render(float dt) {
		super.render(dt);

		return remainingInfoLogDisplayTime > 0;
	}

}
