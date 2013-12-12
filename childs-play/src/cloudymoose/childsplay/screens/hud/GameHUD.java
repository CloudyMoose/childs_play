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
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class GameHUD extends AbstractMenuStageManager {
	World world;

	Sprite unitSpriteA, unitSpriteB;
	Sprite resourceSpriteA, resourceSpriteB;

	Label labelUnitCountA,labelUnitCountB;
	Label labelResourceCountA, labelResourceCountB;
	Label labelTicketCount;
	Label labelInfoLog;
	HPBars hpBars;
	CommandMenu commandMenu;
	Button btnEnd;
	TileStatusPreview tsp;

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
		TextureAtlas atlasUI = this.game.assetManager
				.get(Constants.UNITS_ICONS_ATLAS_PATH);

		AtlasRegion unitTextureA = atlasUI.findRegion("BlueTroops");
		AtlasRegion unitTextureB = atlasUI.findRegion("RedTroops");
		AtlasRegion resourceTexture = atlasUI.findRegion("Apple");

		unitSpriteA = new Sprite(unitTextureA);
		unitSpriteA.setSize(32, 32);
		unitSpriteB = new Sprite(unitTextureB);
		unitSpriteB.setSize(32, 32);

		resourceSpriteA = new Sprite(resourceTexture);
		resourceSpriteA.setSize(32, 32);
		resourceSpriteB = new Sprite(resourceTexture);
		resourceSpriteB.setSize(32, 32);

		labelUnitCountA = new Label("", getSkin());
		labelUnitCountB = new Label("", getSkin());
		stage.addActor(labelUnitCountA);
		stage.addActor(labelUnitCountB);
		updateUnitCount();

		labelResourceCountA = new Label("", getSkin());
		labelResourceCountB = new Label("", getSkin());
		stage.addActor(labelResourceCountA);
		stage.addActor(labelResourceCountB);
		updateResourceCount();

		labelTicketCount = new Label("", getSkin());
		updateTicketCount();
		stage.addActor(labelTicketCount);

		String name = "Hourglass" + (world.getLocalPlayer().id == 1 ? "Blue" : "Red");
		btnEnd = new Button(new TextureRegionDrawable(atlasUI.findRegion(name)));
		btnEnd.setSize(96, 96);
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

		hpBars = new HPBars(game.assetManager, blue, red);
		stage.addActor(hpBars);

		return null; /* We're adding the objects to the scene manually here */
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);

		TextBounds font = labelUnitCountA.getStyle().font.getBounds("1");

		// Place unit counter sprites
		float y = height - 5 - unitSpriteA.getHeight();
		unitSpriteA.setPosition(5, y);
		unitSpriteB.setPosition(width - 5 - unitSpriteB.getWidth(), y);
		y = height - unitSpriteA.getHeight() / 2 - font.height / 2;
		labelUnitCountA.setPosition(2 * 5 + unitSpriteA.getWidth(), y);
		font = labelUnitCountB.getStyle().font.getBounds(labelUnitCountB
				.getText());
		labelUnitCountB.setPosition(width - 2 * 5 - unitSpriteB.getWidth()
				- font.width, y);

		// Place unit counter labels
		y = height - 2 * 5 - resourceSpriteA.getHeight()
				- unitSpriteA.getHeight();
		resourceSpriteA.setPosition(5, y);
		resourceSpriteB.setPosition(width - 5 - unitSpriteB.getWidth(), y);
		y = height - 5 - resourceSpriteA.getHeight() / 2
				- unitSpriteA.getHeight() - font.height / 2;
		labelResourceCountA.setPosition(2 * 5 + unitSpriteA.getWidth(), y);
		font = labelResourceCountB.getStyle().font
				.getBounds(labelResourceCountB.getText());
		labelResourceCountB.setPosition(width - 2 * 5 - unitSpriteB.getWidth()
				- font.width, y);

		labelTicketCount.setPosition(0, height - 100);
		labelInfoLog.setPosition(width / 2, height - 110);
		btnEnd.setPosition(200, 20);
		commandMenu.resize(width, height);
		Gdx.app.log(TAG, tsp.getWidth() + " " + tsp.getHeight());
		tsp.validate();
		Gdx.app.log(TAG, tsp.getWidth() + " " + tsp.getHeight());
		tsp.setPosition(100, 100);

		hpBars.setWidth(width * 0.75f);
		hpBars.setPosition(width / 2, height - 30);
	}

	private static int units(Player p) {
		return  Math.max(0, p.units.size() - 1);
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

		SpriteBatch sb = new SpriteBatch();
		sb.begin();
		unitSpriteA.draw(sb);
		unitSpriteB.draw(sb);
		resourceSpriteA.draw(sb);
		resourceSpriteB.draw(sb);
		sb.end();

		return remainingInfoLogDisplayTime > 0;
	}

}
