package cloudymoose.childsplay.screens;

import cloudymoose.childsplay.ChildsPlayGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MainMenuScreen extends AbstractMenuScreen<MainMenuScreen.StageManager> {

	public MainMenuScreen(ChildsPlayGame game) {
		super(game);
		setMenuStageManager(new StageManager(game, this));
	}

	public void notifyServerState(boolean started) {
		mMenuStageManager.startServerButton.setDisabled(started);
	}

	static class StageManager extends AbstractMenuStageManager {
		public TextButton startServerButton;

		public StageManager(ChildsPlayGame game, Screen screen) {
			super(game, screen);
		}

		@Override
		protected Actor init() {
			Table table = new Table(getSkin());
			// table.debug();
			table.setFillParent(true);

			table.add("Child's Play").spaceBottom(50);
			table.row();

			// register the button "Play". TODO: dispose current screen
			TextButton playButton = new TextButton("Play", getSkin());
			playButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					mGame.connectToServer();
				}
			});
			table.add(playButton).size(300, 60).uniform().spaceBottom(10);
			table.row();

			// register the button "start server"
			startServerButton = new TextButton("Start Server", getSkin());
			startServerButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					mGame.startServer(2);
				}
			});
			table.add(startServerButton).uniform().fill().spaceBottom(10);
			table.row();

			// register the button "start server"
			TextButton tmpStartServButton = new TextButton("Start Server (debug: 1 player only)", getSkin());
			tmpStartServButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					mGame.startServer(1);
				}
			});
			table.add(tmpStartServButton).uniform().fill().spaceBottom(10);
			table.row();

			// register the button "quit"
			TextButton quitButton = new TextButton("Quit", getSkin());
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
