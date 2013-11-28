package cloudymoose.childsplay.screens;

import cloudymoose.childsplay.ChildsPlayGame;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class WaitScreen extends AbstractMenuScreen<WaitScreen.StageManager> {

	private boolean receivedTurnRecap = false;

	public WaitScreen(ChildsPlayGame game) {
		super(game);
		setMenuStageManager(new StageManager(game, this));
	}

	public void notifyTurnRecapReceived() {
		mMenuStageManager.setTurnReadyState(true);
	}

	class StageManager extends AbstractMenuStageManager {

		private static final String WAITING_LABEL = "Waiting";
		private static final String READY_LABEL = "Press start turn to play!";

		Label waitLabel;
		TextButton startTurnButton;

		public StageManager(ChildsPlayGame game, Screen screen) {
			super(game, screen);
		}

		public void setTurnReadyState(boolean ready) {
			waitLabel.setText(ready ? READY_LABEL : WAITING_LABEL);
			startTurnButton.setDisabled(!ready);
			receivedTurnRecap = ready;
		}

		@Override
		protected Actor init() {
			Table table = new Table(getSkin());
			// table.debug();
			table.setFillParent(true);

			waitLabel = new Label(WAITING_LABEL, getSkin());
			table.add(waitLabel).spaceBottom(50);
			table.row();

			// register the button "Start turn".
			startTurnButton = new TextButton("Start turn", getSkin());
			startTurnButton.setDisabled(true);
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
					game.toMainMenu();
				}
			});
			table.add(quitButton).uniform().fill();

			return table;
		}
	}

	public boolean receivedTurnRecap() {
		return receivedTurnRecap;
	}

}
