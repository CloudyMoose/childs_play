package cloudymoose.childsplay.screens;

import cloudymoose.childsplay.ChildsPlayGame;
import cloudymoose.childsplay.world.Constants;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.esotericsoftware.tablelayout.Cell;

public class PostGameScreen extends AbstractMenuScreen<PostGameScreen.StageManager> {

	public enum EndReason {
		Win("And the winner is ..."),
		Lose("And the winner is ..."),
		Forfeit("Ah! The cowards are retreating! The victory is ours!");

		public final String label;

		private EndReason(String label) {
			this.label = label;
		}

	}

	public PostGameScreen(ChildsPlayGame game) {
		super(game);
		setMenuStageManager(new StageManager(game, this));
	}

	public void setReason(EndReason reason, int playerId) {
		mMenuStageManager.postGameLabel.setText(reason.label);
		TextureAtlas atlas = mGame.assetManager.get(Constants.TRIM_ATLAS_PATH);
		if (reason == EndReason.Forfeit) {
			mMenuStageManager.im = new Image();
		} else {
			mMenuStageManager.im = new Image(atlas.findRegion(playerId == 1 ? "BlueTeam" : "RedTeam"));
		}
		// mMenuStageManager.winnerImageCell.setWidget(im);
	}

	class StageManager extends AbstractMenuStageManager {

		Label postGameLabel;
		Cell winnerImageCell;
		Image im;

		public StageManager(ChildsPlayGame game, Screen screen) {
			super(game, screen);
			postGameLabel = new Label("", getSkin());
			im = new Image();
		}

		@Override
		protected Actor init() {
			Table table = new Table(getSkin());
			// table.debug();
			table.setFillParent(true);

			table.add(postGameLabel).spaceBottom(50);
			table.row();
			winnerImageCell = table.add(im);
			winnerImageCell.spaceBottom(50);
			table.row();

			TextButton quitButton = new TextButton("Continue", getSkin());
			quitButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					game.toMainMenu();
				}
			});
			table.add(quitButton).size(300, 60).uniform().fill();

			return table;
		}
	}

}
