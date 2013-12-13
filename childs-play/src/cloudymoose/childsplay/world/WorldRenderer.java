package cloudymoose.childsplay.world;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import cloudymoose.childsplay.ChildsPlayGame;
import cloudymoose.childsplay.world.commands.MoveCommand;
import cloudymoose.childsplay.world.hextiles.HexTile;
import cloudymoose.childsplay.world.units.AppleTree;
import cloudymoose.childsplay.world.units.Bush;
import cloudymoose.childsplay.world.units.Castle;
import cloudymoose.childsplay.world.units.Catapult;
import cloudymoose.childsplay.world.units.Child;
import cloudymoose.childsplay.world.units.EnvironmentUnit;
import cloudymoose.childsplay.world.units.Tree;
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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.Array;

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
	private Array<TextureRegion> flagTextures;

	private AnimationRunner animationRunner;

	public WorldRenderer(World world, AssetManager assetManager) {
		this.world = world;
		this.assetManager = assetManager;
		this.unitTextures = new HashMap<Class<? extends Unit>, TexturePair>();
		this.tileTextures = new HashMap<TileType, TextureRegion>();
		this.flagTextures = new Array<TextureRegion>();
	}

	public void init() {
		TextureAtlas animationAtlas = assetManager.get(Constants.ANIMATIONS_ATLAS_PATH);
		TextureAtlas staticAtlas = assetManager.get(Constants.SPRITES_ATLAS_PATH);
		animationRunner = new AnimationRunner(animationAtlas, assetManager);

		// Add move command sounds
		MoveCommand.Sounds.add(assetManager.get("sounds/ok.mp3", Sound.class));
		MoveCommand.Sounds.add(assetManager.get("sounds/hereWeGo.mp3", Sound.class));
		MoveCommand.Sounds.add(assetManager.get("sounds/yes.mp3", Sound.class));

		unitTextures.put(Catapult.class, new TexturePair(animationAtlas.findRegion("CatapultFire")));
		unitTextures.put(Castle.class, new TexturePair(staticAtlas.findRegion("castle")));
		unitTextures.put(Child.class,
				new TexturePair(staticAtlas.findRegion("BlueChild"), staticAtlas.findRegion("RedChild")));
		unitTextures.put(AppleTree.class, new TexturePair(staticAtlas.findRegion("apple_tree")));
		unitTextures.put(Tree.class, new TexturePair(staticAtlas.findRegion("tree")));
		unitTextures.put(Bush.class, new TexturePair(staticAtlas.findRegion("bush")));

		tileTextures.put(TileType.Grass, staticAtlas.findRegion("grass"));
		tileTextures.put(TileType.Sand, staticAtlas.findRegion("sand"));

		flagTextures.addAll(animationAtlas.findRegions("FlagBlue"));
		flagTextures.add(animationAtlas.findRegion("FlagNeutral"));
		flagTextures.addAll(animationAtlas.findRegions("FlagRed"));
	}

	public boolean render(float dt) {
		if (world.getPreferredCameraFocus() != null) {
			// TODO: jumps at the beginning. It should be smoothed or something...
			setCameraPosition(world.getPreferredCameraFocus());
			world.setPreferredCameraFocus(null);
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
			if (unit != null && unit.isVisible()) {
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
		int nbFlags = flagTextures.size / 2;
		Area area = tile.value.getArea();

		int textureIndex;
		if (area.isNeutral()) textureIndex = nbFlags;
		else {
			if (area.getOwner().id == 1) textureIndex = 0;
			else textureIndex = nbFlags + 1;

			double rawProgress = area.getControlPoints() / (float) area.getControlTiles().size();
			int progress = (int) Math.ceil(rawProgress * (nbFlags - 1));
			textureIndex += progress;
			// Gdx.app.log(TAG, String.format("ti %d | progress %f-%d | status: %s ",
			// textureIndex, rawProgress, progress, area.getControlPointStatus()));
		}

		TextureRegion flag = flagTextures.get(textureIndex);
		sb.setColor(Color.WHITE);

		int alignX = Align.right;
		if (area.isNeutral()) {
			alignX = Align.center;
		} else if (area.getOwner().id == 1) {
			alignX = Align.left;
		}

		RenderingUtils.drawScaledTexture(batch, flag, Float.POSITIVE_INFINITY, Constants.TILE_SIZE * 2,
				tile.getPosition(), alignX, Align.bottom);
	}

	public void addAnimationData(Queue<AnimationData> data) {
		while (!data.isEmpty()) {
			animationRunner.addAnimationData(data.remove());
		}
	}

	private void renderUnit(SpriteBatch sb, Unit unit, Color color) {
		boolean flipTexture = false;

		if (unit.getPlayerId() == 2) flipTexture = true;
		else if (unit instanceof Catapult) {
			if (unit.getOccupiedTile().value.getArea().isNeutral() && world.getLocalPlayer().id == 2) flipTexture = true;
			else if (!unit.getOccupiedTile().value.getArea().isNeutral()
					&& unit.getOccupiedTile().value.getArea().getOwner().id == 2) flipTexture = true;
		}
		else if (unit.getPlayerId() == Player.GAIA_ID && !(unit instanceof EnvironmentUnit)) flipTexture = true;

		TextureRegion textureRegion = unitTextures.get(unit.getClass()).get(flipTexture);

		if (textureRegion == null) throw new RuntimeException("Texture not found for " + unit.getClass());

		RenderingUtils.drawScaledTexture(sb, textureRegion, unit.hitbox.width, unit.hitbox.height, new Vector3(
				unit.hitbox.getCenter(new Vector2(0, 0)), 0));

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
		cam.setToOrtho(false, width, height);

		cam.position.set(0, 0, 0);
		float Xratio = ChildsPlayGame.VIEWPORT_WIDTH / (float) width;
		float Yratio = ChildsPlayGame.VIEWPORT_HEIGHT / (float) height;
		Gdx.app.log(TAG, String.format("Resizing: %d %d, ratio: %f %f", width, height, Xratio, Yratio));
		cam.zoom = Math.min(Xratio, Yratio);
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

	class TexturePair {
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
