package cloudymoose.childsplay.screens.hud;

import java.util.HashMap;
import java.util.Map;

import cloudymoose.childsplay.world.LocalPlayer;
import cloudymoose.childsplay.world.TileData;
import cloudymoose.childsplay.world.commands.AttackCommand;
import cloudymoose.childsplay.world.commands.CollectCommand;
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
		// table.debug();

		TextButton btnMove = new TextButton("Move", uiSkin);
		commandButtons.put(MoveCommand.class, btnMove);
		btnMove.addListener(new CommandListener(MoveCommand.class));

		TextButton btnAttack = new TextButton("Attack", uiSkin);
		commandButtons.put(AttackCommand.class, btnAttack);
		btnAttack.addListener(new CommandListener(AttackCommand.class));

		TextButton btnRecruit = new TextButton("Recruit", uiSkin);
		commandButtons.put(RecruitCommand.class, btnRecruit);
		btnRecruit.addListener(new CommandListener(RecruitCommand.class));

		TextButton btnCollect = new TextButton("Collect", uiSkin);
		commandButtons.put(CollectCommand.class, btnCollect);
		btnCollect.addListener(new CommandListener(CollectCommand.class));

		addActor(table);
		setVisible(false);
	}

	private void addCommandButtons() {
		table.clear();

		for (Class<? extends Command> clazz : clickedTile.value.getOccupant().getSupportedCommands()) {
			table.row();
			table.add(commandButtons.get(clazz)).size(80, 40).uniform().spaceBottom(10);
		}
	}

	public void setClickedTile(HexTile<TileData> selectedTile) {
		this.clickedTile = selectedTile;
	}

	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			addCommandButtons();
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
