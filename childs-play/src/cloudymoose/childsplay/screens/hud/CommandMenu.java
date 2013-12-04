package cloudymoose.childsplay.screens.hud;

import java.util.HashMap;
import java.util.Map;

import cloudymoose.childsplay.world.LocalPlayer;
import cloudymoose.childsplay.world.TileData;
import cloudymoose.childsplay.world.commands.AttackCommand;
import cloudymoose.childsplay.world.commands.Command;
import cloudymoose.childsplay.world.commands.MoveCommand;
import cloudymoose.childsplay.world.commands.RecruitCommand;
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

	private HexTile<TileData> clickedTile;
	private LocalPlayer player;

	private Table table;

	Map<Class<? extends Command>, TextButton> commandButtons;

	public CommandMenu(LocalPlayer player, Skin uiSkin) {
		this.player = player;
		commandButtons = new HashMap<Class<? extends Command>, TextButton>();
		setTransform(false);
		build(uiSkin);
	}

	protected void build(Skin uiSkin) {
		table = new Table();
		table.debug();

		TextButton btnMove = new TextButton("Move", uiSkin);
		commandButtons.put(MoveCommand.class, btnMove);
		btnMove.addListener(new CommandListener(MoveCommand.class));
		table.add(btnMove).size(80, 40).uniform().spaceBottom(10);
		table.row();

		TextButton btnAttack = new TextButton("Attack", uiSkin);
		commandButtons.put(AttackCommand.class, btnAttack);
		btnAttack.addListener(new CommandListener(AttackCommand.class));
		table.add(btnAttack).size(80, 40).uniform().spaceBottom(10);
		table.row();

		TextButton btnRecruit = new TextButton("Recruit", uiSkin);
		commandButtons.put(RecruitCommand.class, btnRecruit);
		btnRecruit.addListener(new CommandListener(RecruitCommand.class));
		table.add(btnRecruit).size(80, 40).uniform().spaceBottom(10);
		table.row();

		addActor(table);
		setVisible(false);
	}

	public void setClickedTile(HexTile<TileData> selectedTile) {
		this.clickedTile = selectedTile;
	}

	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			for (Class<? extends Command> clazz : clickedTile.value.getOccupant().getSupportedCommands()) {
				commandButtons.get(clazz).setVisible(true);
			}
		} else {
			for (TextButton tb : commandButtons.values()) {
				tb.setVisible(false);
			}
		}
		super.setVisible(visible);
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
		Gdx.app.log(TAG, "resize");
	}

	protected class CommandListener extends ClickListener {
		private final Class<? extends Command> commandClass;

		public CommandListener(Class<? extends Command> commandClass) {
			this.commandClass = commandClass;
		}

		@Override
		public void clicked(InputEvent event, float x, float y) {
			setVisible(false);
			player.setSelectedCommand(Command.builder(commandClass).from(clickedTile));
		}
	}
}
