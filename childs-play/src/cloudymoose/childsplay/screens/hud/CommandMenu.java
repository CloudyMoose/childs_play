package cloudymoose.childsplay.screens.hud;

import cloudymoose.childsplay.screens.AbstractMenuStageManager;
import cloudymoose.childsplay.world.LocalPlayer;
import cloudymoose.childsplay.world.hextiles.HexTile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class CommandMenu extends Group {

	private static final String TAG = "CommandMenu";

	HexTile<?> clickedTile;
	TextButton btnMove;
	TextButton btnAttack;
	LocalPlayer player;

	Table table;

	public CommandMenu(LocalPlayer player) {
		this.player = player;
		setTransform(false);
		build();
	}

	protected void build() {
		table = new Table();
		table.debug();

		btnMove = new TextButton("Move", AbstractMenuStageManager.getSkin());
		btnMove.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				player.moveSelectionTo(clickedTile);
			}
		});
		table.add(btnMove).size(80, 40).uniform().spaceBottom(10);
		table.row();

		btnAttack = new TextButton("Attack", AbstractMenuStageManager.getSkin());
		btnAttack.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				// TODO
				Gdx.app.log(TAG, "TODO Attack");
			}
		});
		table.add(btnAttack).size(80, 40).uniform().spaceBottom(10);
		table.row();

		addActor(table);
		setVisible(false);
	}

	public void setClickedTile(HexTile<?> selectedTile) {
		this.clickedTile = selectedTile;
	}

	@Override
	public void setPosition(float x, float y) {

		Gdx.app.log(TAG, "New screen position: " + x + ", " + y);
		super.setPosition(x, Gdx.graphics.getHeight() - y); // Why isn't it the same origins as the screen coordinates?
		if (x < Gdx.graphics.getWidth() / 2) {
			table.setPosition(100, 0);
		} else {
			table.setPosition(-100, 0);
		}
	}

	public void resize(int width, int height) {
		Gdx.app.log(TAG, "resize command menu");
	}

}
