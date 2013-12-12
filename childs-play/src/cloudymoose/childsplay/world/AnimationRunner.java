package cloudymoose.childsplay.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cloudymoose.childsplay.world.units.Unit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class AnimationRunner {

	private final List<AnimationData> ongoingAnimations = new ArrayList<AnimationData>();
	private final Map<AnimationType, Animation[]> animations = new HashMap<AnimationType, Animation[]>();
	private final Map<AnimationType, Sound> sounds = new HashMap<AnimationType, Sound>();

	public AnimationRunner(TextureAtlas atlas, AssetManager am) {
		Array<AtlasRegion> catapultFire = atlas.findRegions("CatapultFire");
		Array<AtlasRegion> flippedCatapultFire = new Array<TextureAtlas.AtlasRegion>(true, catapultFire.size);
		for (AtlasRegion ar : catapultFire) {
			AtlasRegion far = new AtlasRegion(ar);
			far.flip(true, false);
			flippedCatapultFire.add(far);
		}

		animations.put(AnimationType.CatapultFire, new Animation[] { new Animation(0.3f, catapultFire),
				new Animation(0.3f, flippedCatapultFire) });
		animations.put(AnimationType.Melee, new Animation[] { new Animation(0.05f, atlas.findRegions("Cloud"),
				Animation.LOOP) });

		sounds.put(AnimationType.Melee, am.get("sounds/fight.mp3", Sound.class));
	}

	public boolean hasOngoingAnimation() {
		return !ongoingAnimations.isEmpty();
	}

	/**
	 * 
	 * @param dt
	 * @param sb
	 * @return <code>true</code> if the world update has to wait for an animation to end
	 */
	public boolean run(float dt, SpriteBatch sb) {
		List<AnimationData> finishedAnimations = new ArrayList<AnimationData>();
		boolean block = false;

		for (AnimationData data : ongoingAnimations) {
			for (Unit u : data.toHide) {
				u.setVisible(false);
			}

			data.stateTime += dt;

			Animation a;
			if (data.type == AnimationType.CatapultFire) {
				Gdx.app.log("AR", "Catapult firing for player "
						+ data.toHide[0].getOccupiedTile().value.getOccupant().id);

			}
			if (data.type == AnimationType.CatapultFire
					&& data.toHide[0].getOccupiedTile().value.getArea().getOwner().id == 2) {
				a = animations.get(data.type)[1];
			}
			else a = animations.get(data.type)[0];

			TextureRegion texture = a.getKeyFrame(data.stateTime);

			float xRatio = data.type.maxWidth / texture.getRegionWidth();
			float yRatio = data.type.maxHeight / texture.getRegionHeight();

			float drawWidth, drawHeight;
			if (xRatio < yRatio) {
				drawWidth = data.type.maxWidth;
				drawHeight = texture.getRegionHeight() * xRatio;
			} else if (yRatio != Float.POSITIVE_INFINITY) {
				drawWidth = texture.getRegionWidth() * yRatio;
				drawHeight = data.type.maxHeight;
			} else {
				drawWidth = texture.getRegionWidth();
				drawHeight = texture.getRegionHeight();
			}

			Gdx.app.log("AR", drawWidth + " " + drawHeight);

			sb.draw(texture, data.position.x - drawWidth / 2,
					data.position.y - drawHeight / 2, drawWidth, drawHeight);

			if (!data.loop) {
				if (a.isAnimationFinished(data.stateTime)) {
					finishedAnimations.add(data);
					for (Unit u : data.toHide) {
						u.setVisible(true);
					}
				} else {
					block = block || data.isBlocking;
				}
			}
		}

		ongoingAnimations.removeAll(finishedAnimations);

		return block;
	}

	/**
	 * @param data
	 *            <code>null</code> is safely ignored
	 */
	public void addAnimationData(AnimationData data) {
		if (data != null) {
			Sound sound = sounds.get(data.type);
			if (sound != null) sound.play();

			ongoingAnimations.add(data);
		}
	}

}
