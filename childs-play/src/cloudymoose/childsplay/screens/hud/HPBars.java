package cloudymoose.childsplay.screens.hud;

import cloudymoose.childsplay.world.Constants;
import cloudymoose.childsplay.world.Player;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.esotericsoftware.tablelayout.Cell;

public class HPBars extends Table {

	private TextureRegion blueHpTexture, redHpTexture;
	private Player blue, red;
	private Label lblHp;

	@SuppressWarnings("rawtypes")
	Array<Cell> cells;

	int blueHp, redHp;

	@SuppressWarnings("rawtypes")
	public HPBars(AssetManager assetManager, Player blue, Player red) {
		super((Skin) assetManager.get(Constants.MENU_SKIN_JSON_PATH));
		this.blue = blue;
		this.red = red;
		cells = new Array<Cell>(true, Constants.PLAYER_HEALTH_POINTS * 2 + 1, Cell.class);
		build(assetManager);
	}

	public void build(AssetManager assetManager) {
		// debug();
		int nbCells = (Constants.PLAYER_HEALTH_POINTS * 2) + 1; // 1 for each HP + 1 for the labels
		TextureAtlas atlas = assetManager.get(Constants.UNITS_ICONS_ATLAS_PATH);
		blueHpTexture = atlas.findRegion("BlueLife");
		redHpTexture = atlas.findRegion("RedLife");

		blueHp = blue.getHp();
		redHp = red.getHp();
		lblHp = new Label(String.format("%02d - %02d", blueHp, redHp),
				(Skin) assetManager.get(Constants.MENU_SKIN_JSON_PATH));
		for (int i = 0; i < nbCells; ++i) {
			if (i < Constants.PLAYER_HEALTH_POINTS) {
				cells.add(add(new Image(new TextureRegionDrawable(blueHpTexture), Scaling.fillX)));
			} else if (i == Constants.PLAYER_HEALTH_POINTS) {
				cells.add(add(lblHp));
			} else {
				cells.add(add(new Image(new TextureRegionDrawable(redHpTexture), Scaling.fillX)));
			}
		}
	}

	@Override
	public void setPosition(float x, float y) {
		super.setPosition(x - getWidth() / 2, y);
	}

	public void updateHP() {
		boolean hpChanged = false;
		if (blueHp != blue.getHp()) {
			hpChanged = true;
			blueHp = blue.getHp();
			for (int i = 0; i < Constants.PLAYER_HEALTH_POINTS - blueHp; ++i) {
				((Image) cells.get(i).getWidget()).setVisible(false);
			}
		}
		if (redHp != red.getHp()) {
			hpChanged = true;
			redHp = red.getHp();
			for (int i = Constants.PLAYER_HEALTH_POINTS + 1 + redHp; i < cells.size; ++i) {
				((Image) cells.get(i).getWidget()).setVisible(false);
			}
		}

		if (hpChanged) {
			lblHp.setText(String.format("%02d - %02d", blueHp, redHp));
		}

	}
}
