package cloudymoose.childsplay.screens.hud;

import cloudymoose.childsplay.world.Constants;
import cloudymoose.childsplay.world.Player;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.esotericsoftware.tablelayout.Cell;

public class HPBars extends Table {

	private TextureRegion blueHpTexture, redHpTexture;
	private Player blue, red;

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
		blueHpTexture = atlas.findRegion("BlueHp");
		redHpTexture = atlas.findRegion("RedHp");

		blueHp = blue.getHp();
		redHp = red.getHp();
		for (int i = 0; i < nbCells; ++i) {
			if (i < Constants.PLAYER_HEALTH_POINTS) {
				cells.add(add(new Image(new TextureRegionDrawable(blueHpTexture), Scaling.fillX)));
			} else if (i == Constants.PLAYER_HEALTH_POINTS) {
				cells.add(add("Blue Red"));
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
		if (blueHp != blue.getHp()) {
			blueHp = blue.getHp();
			for (int i = Constants.PLAYER_HEALTH_POINTS - blueHp; i < Constants.PLAYER_HEALTH_POINTS; ++i) {
				((Image) cells.get(i).getWidget()).setVisible(false);
			}
		}
		if (redHp != red.getHp()) {
			redHp = red.getHp();
			for (int i = Constants.PLAYER_HEALTH_POINTS + 1 + redHp; i < cells.size; ++i) {
				((Image) cells.get(i).getWidget()).setVisible(false);
			}
		}

	}
}
