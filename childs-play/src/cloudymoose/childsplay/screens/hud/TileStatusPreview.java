package cloudymoose.childsplay.screens.hud;

import cloudymoose.childsplay.world.LocalPlayer;
import cloudymoose.childsplay.world.TileData;
import cloudymoose.childsplay.world.hextiles.HexTile;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

public class TileStatusPreview extends Table {

	private LocalPlayer player;
	
	private Label lblType;
	private Label lblCoords;
	private Label lblOccupant;
	private Label lblAreaName;
	private Label lblAreaOwner;
	private Label lblAreaStatus;
	
	public TileStatusPreview(LocalPlayer localPlayer, Skin skin) {
		super(skin);
		this.player = localPlayer;
		create(skin);
	}
	
	private void create(Skin skin) {
//		debug();
		lblType = new Label(null, skin);
		lblCoords = new Label(null, skin);
		lblOccupant = new Label(null, skin);
		lblAreaName = new Label(null, skin);
		lblAreaOwner = new Label(null, skin);
		lblAreaStatus = new Label(null, skin);

		add("Type: ").align(Align.left);
		add(lblType).align(Align.left);
		row();
		add("Position: ").align(Align.left);
		add(lblCoords).align(Align.left);
		row();
		add("Unit: ").align(Align.left);
		add(lblOccupant).align(Align.left);
		row();
		add("Area: ").align(Align.left);
		add(lblAreaName).align(Align.left);
		row();
		add("Area owner: ").align(Align.left);
		add(lblAreaOwner).align(Align.left);
		row();
		add("Area status: ").align(Align.left);
		add(lblAreaStatus).align(Align.left);
	}

	public void update() {
		HexTile<TileData> tile = player.getSelectedTile();
		if (tile == null) {
			setVisible(false);	
		} else {			
			lblType.setText(tile.value.color.toString());
			lblCoords.setText(tile.getQ() + " " + tile.getR());
			lblOccupant.setText(toStringOr(tile.value.getOccupant(), ""));
			lblAreaName.setText(tile.value.getArea().toString());
			lblAreaOwner.setText(toStringOr(tile.value.getArea().getOwner(), "Neutral"));
			lblAreaStatus.setText(tile.value.getArea().getControlPointStatus());
			setVisible(true);
		}
		
	}
	
	/** @return the alternate label if the object is null, its {@link Object#toString()} otherwise*/
	private String toStringOr(Object object, String altrernateLabel) {
		if (object == null) return altrernateLabel;
		else return object.toString();
	}

}
