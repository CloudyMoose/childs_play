package cloudymoose.childsplay.world;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import cloudymoose.childsplay.world.commands.MoveCommand;
import cloudymoose.childsplay.world.hextiles.Direction;
import cloudymoose.childsplay.world.hextiles.HexTile;
import cloudymoose.childsplay.world.units.AppleTree;
import cloudymoose.childsplay.world.units.Castle;
import cloudymoose.childsplay.world.units.Catapult;
import cloudymoose.childsplay.world.units.Child;
import cloudymoose.childsplay.world.units.Unit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

/** Takes the current state of the world and renders it to the screen */
public class WorldRenderer {

	public final World world;
	public final OrthographicCamera cam = new OrthographicCamera();
	private final ShapeRenderer debugRenderer = new ShapeRenderer();
	private AssetManager assetManager;
	private final SpriteBatch sb = new SpriteBatch(); // Declared here so that it can be disposed.

	private static final String TAG = "WorldRenderer";

	// Materials
	private Map<TileType, TextureRegion> tileTextures;
	private Map<Class<? extends Unit>, TexturePair> unitTextures;

	private AnimationRunner animationRunner;

	public WorldRenderer(World world, AssetManager assetManager) {
		this.world = world;
		this.assetManager = assetManager;
		this.unitTextures = new HashMap<Class<? extends Unit>, TexturePair>();
		this.tileTextures = new HashMap<TileType, TextureRegion>();
	}

	public void init() {
		TextureAtlas atlasTF = assetManager.get(Constants.TILES_FLAGS_ATLAS_PATH);
		TextureAtlas atlasUI = assetManager.get(Constants.UNITS_ICONS_ATLAS_PATH);
		animationRunner = new AnimationRunner(atlasUI, assetManager);

		// Add move command sounds
		MoveCommand.Sounds.add(assetManager.get("sounds/ok.mp3", Sound.class));
		MoveCommand.Sounds.add(assetManager.get("sounds/hereWeGo.mp3", Sound.class));
		MoveCommand.Sounds.add(assetManager.get("sounds/yes.mp3", Sound.class));

		unitTextures.put(Catapult.class, new TexturePair(atlasUI.findRegion("catapult")));
		unitTextures.put(Castle.class, new TexturePair(atlasUI.findRegion("castle")));
		unitTextures.put(Child.class,
				new TexturePair(atlasUI.findRegion("BlueChild"), atlasUI.findRegion("RedChild")));
		unitTextures.put(AppleTree.class, new TexturePair(atlasUI.findRegion("apple_tree")));

		tileTextures.put(TileType.Grass, atlasTF.findRegion("Grass"));
		tileTextures.put(TileType.Sand, atlasTF.findRegion("Sand"));
	}

	public boolean render(float dt) {
		if (world.getPreferredCameraFocus() != null) {
			// TODO: jumps at the beginning. It should be smoothed or something...
			setCameraPosition(world.getPreferredCameraFocus());
		}

		sb.begin();
		sb.setProjectionMatrix(cam.combined);

		// Render background hexagons
		for (HexTile<TileData> tile : world.getMap()) {
			Color color = Color.WHITE;

			if (world.targetableTiles.contains(tile)) {
				color = Color.ORANGE;
			}

			this.renderTile(sb, tile, color);
		}

		for (HexTile<TileData> tile : world.getMap()) {
			if (world.getMap().isControlPoint(tile)) {
				renderFlag(sb, tile);
			}

			// Render units
			Unit unit = tile.value.getOccupant();
			if (unit != null) {
				this.renderUnit(sb, unit, Constants.PLAYER_COLORS[unit.getPlayerId()]);

			}
		}

		sb.end();

		if (animationRunner.hasOngoingAnimation()) {
			boolean block;
			sb.begin();
			sb.setColor(Color.WHITE);
			block = animationRunner.run(dt, sb);
			sb.end();
			return block;
		} else {
			return false;
		}
	}

	private void renderFlag(SpriteBatch batch, HexTile<TileData> tile) {
		TextureAtlas atlas0 = assetManager.get(Constants.TILES_FLAGS_ATLAS_PATH);
		TextureRegion flag = atlas0.findRegion("FlagRed");
		sb.setColor(Color.WHITE);
		Vector3 tilePosition = tile.getPosition();
		float aspectRatio = flag.getRegionWidth() / (float) flag.getRegionHeight();
		float drawHeight = Constants.TILE_SIZE * 2;
		float drawWidth = drawHeight * aspectRatio;
		sb.draw(flag, tilePosition.x - drawWidth / 2, tilePosition.y, drawWidth, drawHeight);
	}

	public void addAnimationData(AnimationData data) {
		animationRunner.addAnimationData(data);
	}

	private void renderUnit(SpriteBatch sb, Unit unit, Color color) {
		boolean flipTexture = false;

		if (unit.getPlayerId() == 2) flipTexture = true;
		else if (unit instanceof Catapult && unit.getOccupiedTile().value.getArea().isNeutral()
				&& world.getCurrentPlayer().id == 2) flipTexture = true;

		TextureRegion textureRegion = unitTextures.get(unit.getClass()).get(flipTexture);

		if (textureRegion == null) throw new RuntimeException("Texture not found for " + unit.getClass());

		float aspectRatio = textureRegion.getRegionWidth() / (float) textureRegion.getRegionHeight();

		// Center the sprite on the tile
		float drawWidth, drawHeight, drawX, drawY;
		if (aspectRatio > 0) {
			drawWidth = unit.hitbox.width;
			drawHeight = unit.hitbox.width / aspectRatio;
			drawX = unit.hitbox.x;
			drawY = unit.hitbox.y + unit.hitbox.height / 2 - drawHeight / 2;
		} else {
			drawWidth = unit.hitbox.height * aspectRatio;
			drawHeight = unit.hitbox.height;
			drawX = unit.hitbox.x + unit.hitbox.width / 2 - drawWidth / 2;
			drawY = unit.hitbox.y;
		}

		// sb.setColor(color);
		sb.draw(textureRegion, drawX, drawY, drawWidth, drawHeight);
	}

	private void renderTile(SpriteBatch sb, HexTile<TileData> tile, Color color) {
		float size = Constants.TILE_SIZE;
		float height = 2 * size;
		float width = (float) (Math.sqrt(3) / 2f * height);

		Vector3 position = tile.getPosition();
		sb.setColor(color);
		sb.draw(tileTextures.get(tile.value.type), position.x - width / 2, position.y - height / 2, width, height);
	}

	public void resize(int width, int height) {
		Gdx.app.log(TAG, "resize(" + width + ", " + height + ")");
		cam.setToOrtho(false, width / 2, height / 2);

		cam.position.set(0, 0, 0);

		cam.update();
	}

	/**
	 * @param moveVector
	 *            movement vector that will be used to move the current camera position
	 */
	public void moveCamera(Vector3 moveVector) {
		cam.position.add(moveVector);
		cam.update();
	}

	/**
	 * @param position
	 *            new camera position
	 */
	public void setCameraPosition(Vector3 position) {
		cam.position.set(position);
		cam.update();
	}

	public void dispose() {
		debugRenderer.dispose();
		sb.dispose();
	}

	private float[][] getEdges(Vector3 center, EnumSet<Direction> borders) {
		float height = 2 * world.getMap().getTileSize();
		float width = (float) (Math.sqrt(3) / 2f * height);
		// center.x - width / 2, center.y + height / 4
		// center.x, center.y + height / 2
		// center.x + width / 2, center.y + height / 4
		// center.x + width / 2, center.y - height / 4
		// center.x,center.y - height / 2
		// center.x - width / 2, center.y - height / 4

		float[][] edges = new float[borders.size()][];
		int i = 0;
		for (Direction d : borders) {
			switch (d) {
			case DownLeft:
				edges[i] = new float[] { center.x, center.y - height / 2, center.x - width / 2, center.y - height / 4 };
				break;
			case DownRight:
				edges[i] = new float[] { center.x + width / 2, center.y - height / 4, center.x, center.y - height / 2 };
				break;
			case Left:
				edges[i] = new float[] { center.x - width / 2, center.y - height / 4, center.x - width / 2,
						center.y + height / 4 };
				break;
			case Right:
				edges[i] = new float[] { center.x + width / 2, center.y + height / 4, center.x + width / 2,
						center.y - height / 4 };
				break;
			case UpLeft:
				edges[i] = new float[] { center.x - width / 2, center.y + height / 4, center.x, center.y + height / 2 };
				break;
			case UpRight:
				edges[i] = new float[] { center.x, center.y + height / 2, center.x + width / 2, center.y + height / 4 };
				break;
			}
			i++;
		}

		return edges;

	}

	private static class TexturePair {
		public final TextureRegion normal;
		public final TextureRegion flipped;

		public TexturePair(TextureRegion normal, TextureRegion flipped) {
			this.normal = normal;
			this.flipped = flipped;
		}

		public TexturePair(TextureRegion normal) {
			super();
			this.normal = normal;
			flipped = new TextureRegion(normal);
			flipped.flip(true, false);
		}

		public TextureRegion get(boolean flipped) {
			return flipped ? this.flipped : normal;
		}

	}

}
