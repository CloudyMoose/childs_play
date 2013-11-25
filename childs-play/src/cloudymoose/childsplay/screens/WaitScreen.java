package cloudymoose.childsplay.screens;

import cloudymoose.childsplay.ChildsPlayGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class WaitScreen extends AbstractMenuScreen<WaitScreen.StageManager> {

	public WaitScreen(ChildsPlayGame game) {
		super(game);
		setMenuStageManager(new StageManager(game, this));
	}

	public void notifyTurnRecapReceived() {
		mMenuStageManager.setTurnReadyState(true);
	}

	static class StageManager extends AbstractMenuStageManager {

		private static final String WAITING_LABEL = "Waiting";
		private static final String READY_LABEL = "Press start turn to play!";

		Label waitLabel;
		TextButton startTurnButton;

		public StageManager(ChildsPlayGame game, Screen screen) {
			super(game, screen);
			waitLabel = new Label(WAITING_LABEL, getSkin());
			startTurnButton = new TextButton("Start turn", getSkin());
			startTurnButton.setDisabled(true);
		}

		public void setTurnReadyState(boolean ready) {
			waitLabel.setText(ready ? READY_LABEL : WAITING_LABEL);
			startTurnButton.setDisabled(!ready);
		}

		@Override
		protected Actor init() {
			Table table = new Table(getSkin());
			// table.debug();
			table.setFillParent(true);

			table.add(waitLabel).spaceBottom(50);
			table.row();

			// register the button "Start turn". TODO: dispose current screen
			startTurnButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					if (!startTurnButton.isDisabled()) {
						setTurnReadyState(false);
						game.startTurn();
					}
				}
			});
			table.add(startTurnButton).size(300, 60).uniform().spaceBottom(10);
			table.row();

			// register the button "quit"
			TextButton quitButton = new TextButton("Quit Game", getSkin());
			quitButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					Gdx.app.exit();
				}
			});
			table.add(quitButton).uniform().fill();

			return table;
		}
	}

}
