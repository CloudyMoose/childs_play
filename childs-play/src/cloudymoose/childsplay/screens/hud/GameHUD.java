package cloudymoose.childsplay.screens.hud;

import cloudymoose.childsplay.ChildsPlayGame;
import cloudymoose.childsplay.screens.AbstractMenuStageManager;
import cloudymoose.childsplay.screens.GameScreen;
import cloudymoose.childsplay.world.Constants;
import cloudymoose.childsplay.world.Player;
import cloudymoose.childsplay.world.TileData;
import cloudymoose.childsplay.world.World;
import cloudymoose.childsplay.world.hextiles.HexTile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.esotericsoftware.tablelayout.Cell;

public class GameHUD extends AbstractMenuStageManager {
	World world;

	private Label labelUnitCountA, labelUnitCountB;
	private Label labelResourceCountA, labelResourceCountB;
	private Label labelTicketCount;
	private Label labelInfoLog;
	private HPBars hpBars;
	private CommandMenu commandMenu;
	private Button btnEnd;
	private TileStatusPreview tsp;
	private Table blueStatusRecap, redStatusRecap;

	private int remainingInfoLogDisplayTime;

	private Player blue, red;

	/** In seconds */
	private static final int MSG_DISPLAY_TIME = 3;

	public GameHUD(ChildsPlayGame game, Screen screen, World world) {
		super(game, screen);
		this.world = world;

		blue = world.getPlayers().get(1);
		red = world.getPlayers().size() == 2 ? Player.Gaia() : world
				.getPlayers().get(2);
	}

	@Override
	protected Actor init() {
		TextureAtlas atlas = this.game.assetManager.get(Constants.TRIM_ATLAS_PATH);

		AtlasRegion unitTextureA = atlas.findRegion("BlueTroops");
		AtlasRegion unitTextureB = atlas.findRegion("RedTroops");
		AtlasRegion resourceTexture = atlas.findRegion("Apple");

		labelUnitCountA = new Label("", getSkin());
		labelUnitCountB = new Label("", getSkin());

		labelResourceCountA = new Label("", getSkin());
		labelResourceCountB = new Label("", getSkin());

		labelTicketCount = new Label("", getSkin());

		float labelpadding = 20;
		blueStatusRecap = new Table(getSkin());
		// blueStatusRecap.debug();
		blueStatusRecap.pad(15);
		stage.addActor(blueStatusRecap);

		blueStatusRecap.add(new Image(unitTextureA)).expand().spaceRight(labelpadding);

		blueStatusRecap.add(labelUnitCountA);
		blueStatusRecap.row();
		blueStatusRecap.add(new Image(resourceTexture)).expand().spaceRight(labelpadding);
		blueStatusRecap.add(labelResourceCountA);
		blueStatusRecap.row();
		if (world.getCurrentPlayer().id == 1) {
			blueStatusRecap.add(labelTicketCount);
		}

		redStatusRecap = new Table(getSkin());
		redStatusRecap.pad(15);
		// redStatusRecap.debug();
		stage.addActor(redStatusRecap);
		redStatusRecap.add(labelUnitCountB).spaceRight(labelpadding);
		redStatusRecap.add(new Image(unitTextureB)).expand();
		redStatusRecap.row();
		redStatusRecap.add(labelResourceCountB).spaceRight(labelpadding);
		redStatusRecap.add(new Image(resourceTexture)).expand();
		redStatusRecap.row();
		if (world.getCurrentPlayer().id == 2) {
			redStatusRecap.add(labelTicketCount);
		}

		String name = "Hourglass" + (world.getLocalPlayer().id == 1 ? "Blue" : "Red");
		btnEnd = new Button(new TextureRegionDrawable(atlas.findRegion(name)));
		btnEnd.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				world.setPreferredCameraFocus(((GameScreen) screen).getWorldRenderer().cam.position);
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

		hpBars = new HPBars(game.assetManager, blue, red);
		stage.addActor(hpBars);

		update();
		return null; /* We're adding the objects to the scene manually here */
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);

		float bigIconSize = 128;
		resizeStatusRecap(width, height);

		btnEnd.setSize(bigIconSize, bigIconSize);
		btnEnd.setPosition(200, 20);

		labelInfoLog.setPosition(width / 2, height - 110);
		commandMenu.resize(width, height);
		Gdx.app.log(TAG, tsp.getWidth() + " " + tsp.getHeight());
		tsp.validate();
		Gdx.app.log(TAG, tsp.getWidth() + " " + tsp.getHeight());
		tsp.setPosition(100, 100);

		hpBars.setWidth(width * 0.75f);
		hpBars.setPosition(width / 2, height - 30);
	}

	@SuppressWarnings("rawtypes")
	private void resizeStatusRecap(int width, int height) {
		// TODO: separate that in another class?
		float smallIconSize = 64;

		float fontHeight = labelUnitCountA.getStyle().font.getBounds("1").height;

		for (Cell c : redStatusRecap.getCells()) {
			if (c.getWidget() instanceof Image) {
				c.size(smallIconSize);
			}
		}
		for (Cell c : blueStatusRecap.getCells()) {
			if (c.getWidget() instanceof Image) {
				c.size(smallIconSize);
			}
		}

		float tableHeight = smallIconSize * 2 + fontHeight + 40;
		float tableWidth = smallIconSize + 40;
		blueStatusRecap.setSize(tableWidth, tableHeight);
		redStatusRecap.setSize(tableWidth, tableHeight);
		blueStatusRecap.setPosition(0, height - tableHeight);
		redStatusRecap.setPosition(width - tableWidth, height - tableHeight);
	}

	private static int units(Player p) {
		return Math.max(0, p.units.size() - 1);
	}

	private void updateUnitCount() {
		labelUnitCountA.setText(String.format("%d", units(blue)));
		labelUnitCountB.setText(String.format("%d", units(red)));
	}

	private void updateResourceCount() {
		labelResourceCountA.setText(String.format("%d", blue.getResourcePoints()));
		labelResourceCountB.setText(String.format("%d", red.getResourcePoints()));
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
		updateResourceCount();
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

		// SpriteBatch sb = new SpriteBatch();
		// sb.begin();
		// unitSpriteA.draw(sb);
		// unitSpriteB.draw(sb);
		// resourceSpriteA.draw(sb);
		// resourceSpriteB.draw(sb);
		// sb.end();

		return remainingInfoLogDisplayTime > 0;
	}

}
