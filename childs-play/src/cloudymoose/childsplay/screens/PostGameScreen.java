package cloudymoose.childsplay.screens;

import cloudymoose.childsplay.ChildsPlayGame;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class PostGameScreen extends AbstractMenuScreen<PostGameScreen.StageManager> {

	public enum EndReason {
		Win("Great, you won. Now what?"),
		Lose("Shame on you! They beat you and got your snack. Maybe Mom will give you another?"),
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

	public void setReason(EndReason reason) {
		mMenuStageManager.postGameLabel.setText(reason.label);
	}

	class StageManager extends AbstractMenuStageManager {

		Label postGameLabel;

		public StageManager(ChildsPlayGame game, Screen screen) {
			super(game, screen);
			postGameLabel = new Label("", getSkin());
		}

		@Override
		protected Actor init() {
			Table table = new Table(getSkin());
			// table.debug();
			table.setFillParent(true);

			table.add(postGameLabel).spaceBottom(50);
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
