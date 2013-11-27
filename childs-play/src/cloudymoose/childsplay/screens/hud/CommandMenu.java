package cloudymoose.childsplay.screens.hud;

import cloudymoose.childsplay.world.LocalPlayer;
import cloudymoose.childsplay.world.commands.Command;
import cloudymoose.childsplay.world.commands.MoveCommand;
import cloudymoose.childsplay.world.hextiles.HexTile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class CommandMenu extends Group {

	private static final String TAG = "CommandMenu";

	private HexTile<?> clickedTile;
	private LocalPlayer player;

	private TextButton btnMove;
	private TextButton btnAttack;
	private Table table;

	public CommandMenu(LocalPlayer player, Skin uiSkin) {
		this.player = player;
		setTransform(false);
		build(uiSkin);
	}

	protected void build(Skin uiSkin) {
		table = new Table();
		table.debug();

		btnMove = new TextButton("Move", uiSkin);
		btnMove.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				setVisible(false);
				player.setSelectedCommand(Command.builder(MoveCommand.class).from(clickedTile));
			}
		});
		table.add(btnMove).size(80, 40).uniform().spaceBottom(10);
		table.row();

		btnAttack = new TextButton("Attack", uiSkin);
		btnAttack.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				// TODO
				setVisible(false);
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
