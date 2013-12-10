package cloudymoose.childsplay.world;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import cloudymoose.childsplay.world.hextiles.Direction;
import cloudymoose.childsplay.world.hextiles.HexTile;
import cloudymoose.childsplay.world.units.AppleTree;
import cloudymoose.childsplay.world.units.Castle;
import cloudymoose.childsplay.world.units.Catapult;
import cloudymoose.childsplay.world.units.Child;
import cloudymoose.childsplay.world.units.Unit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
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
	private Map<Class<? extends Unit>, TextureRegion> unitTextures;

	private AnimationRunner animationRunner;

	public WorldRenderer(World world, AssetManager assetManager) {
		this.world = world;
		this.assetManager = assetManager;
		this.unitTextures = new HashMap<Class<? extends Unit>, TextureRegion>();
		this.tileTextures = new HashMap<TileType, TextureRegion>();
	}

	public void init() {
		TextureAtlas atlas = assetManager.get(Constants.GAME_ATLAS_PATH);
		animationRunner = new AnimationRunner(atlas);

		unitTextures.put(Catapult.class, new TextureRegion((Texture)assetManager.get("catapult.png")));
		unitTextures.put(Castle.class, atlas.findRegion("conceptKid"));
		unitTextures.put(Child.class, atlas.findRegion("conceptKid"));
		unitTextures.put(AppleTree.class, new TextureRegion((Texture)assetManager.get("apple_tree.png")));

		tileTextures.put(TileType.Grass, atlas.findRegion("grass"));
		tileTextures.put(TileType.Sand, atlas.findRegion("sand"));
	}

	public boolean render(float dt) {
		if (world.getPreferredCameraFocus() != null) {
			// TODO: jumps at the beginning. It should be smoothed or something...
			setCameraPosition(world.getPreferredCameraFocus());
		}

		// Render background hexagons

		sb.begin();
		sb.setProjectionMatrix(cam.combined);

		for (HexTile<TileData> tile : world.getMap()) {
			Color color = Color.WHITE;

			if (world.targetableTiles.contains(tile)) {
				color = Color.ORANGE;
			} else if (world.getMap().isControlPoint(tile)) {
				color = Color.MAGENTA;
			}

			this.renderTile(sb, tile, color);
		}

		sb.end();

		// Render overlays
		debugRenderer.setProjectionMatrix(cam.combined);

		for (HexTile<TileData> tile : world.getMap()) {
			Vector3 center = tile.getPosition();

			// Drawing area borders
			debugRenderer.begin(ShapeType.Line);

			// TODO: horrible but w/e
			if (!tile.value.borders.isEmpty()) {
				debugRenderer.setColor(Color.RED);
				for (float[] line : getEdges(center, tile.value.borders)) {
					debugRenderer.polyline(line);
				}
				Gdx.gl10.glLineWidth(1);
			}

			debugRenderer.end();

			Unit unit = tile.value.getOccupant();
			if (unit != null) {
				Color color = null;

				if (unit.getPlayerId() == world.getLocalPlayer().id) {
					color = Color.BLUE;
				} else if (unit.getPlayerId() == Player.GAIA_ID) {
					color = Color.GRAY;
				} else {
					color = Color.RED;
				}

				sb.begin();
				this.renderUnit(sb, unit, color);
				sb.end();

			}

		}

		if (animationRunner.hasOngoingAnimation()) {
			boolean block;
			sb.begin();
			block = animationRunner.run(dt, sb);
			sb.end();
			return block;
		} else {
			return false;
		}
	}

	public void addAnimationData(AnimationData data) {
		animationRunner.addAnimationData(data);
	}

	private void renderUnit(SpriteBatch sb, Unit unit, Color color) {
		TextureRegion textureRegion = unitTextures.get(unit.getClass());
		if (textureRegion == null) {
			throw new RuntimeException("Texture not found for " + unit.getClass().getSimpleName());
		}
		float aspectRatio = textureRegion.getRegionWidth() / (float) textureRegion.getRegionHeight();
		sb.setColor(color);
		sb.draw(textureRegion, unit.hitbox.x, unit.hitbox.y, aspectRatio * unit.hitbox.height, unit.hitbox.height);
		sb.setColor(Color.WHITE);
	}

	private void renderTile(SpriteBatch sb, HexTile<TileData> tile, Color color) {
		float size = Constants.TILE_SIZE;
		float height = 2 * size;
		float width = (float) (Math.sqrt(3) / 2f * height);

		Vector3 position = tile.getPosition();
		sb.setColor(color);
		sb.draw(tileTextures.get(tile.value.type), position.x - width / 2, position.y - height / 2, width, height);
		sb.setColor(Color.WHITE);
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

}
