package cloudymoose.childsplay.world;

import com.badlogic.gdx.graphics.Color;

public class Constants {

	public static final int NB_TICKETS = 5;
	public static final int STARTING_RESOURCE_POINTS = 0;
	public static final int CHILD_COST = 2;
	public static final Color[] PLAYER_COLORS = new Color[] { Color.WHITE, Color.BLUE, Color.RED };
	public static final int PLAYER_HEALTH_POINTS = 10;
	public static final int CHILD_MOVE_RANGE = 4;

	public static final int CHILD_SIZE = 90;
	public static final int ENVIRONMENT_UNIT_SIZE = 110;
	public static final int OBSTACLE_SIZE = 110;
	public static final int CASTLE_SIZE = 140;

	public static final float MAP_SCROLL_SPEED = 0.200f;
	public static final float TILE_SIZE = 64.0f;
	public static final double UNIT_MOVEMENT_SPEED = 15;
	public static final float DIMINISHING_RETURN_BASE = 0.66f;

	/** TODO will be generated or imported for the map file. */
	public static final int NB_MAP_AREAS = 3;

	// Texture paths
	public static final String MENU_SKIN_JSON_PATH = "menu_skin/uiskin.json";
	public static final String ANIMATIONS_ATLAS_PATH = "game/animations.txt";
	public static final String SPRITES_ATLAS_PATH = "game/sprites.txt";
}
