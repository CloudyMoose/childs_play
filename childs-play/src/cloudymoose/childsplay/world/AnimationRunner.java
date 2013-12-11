package cloudymoose.childsplay.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AnimationRunner {

	private final List<AnimationData> ongoingAnimations = new ArrayList<AnimationData>();
	private final Map<AnimationType, Animation> animations = new HashMap<AnimationType, Animation>();
	private final Map<AnimationType, Sound> sounds = new HashMap<AnimationType, Sound>();

	public AnimationRunner(TextureAtlas atlas, AssetManager am) {
		animations.put(AnimationType.Melee, new Animation(0.05f, atlas.findRegions("big_ball_of_violence")));
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
			data.stateTime += dt;
			Animation a = animations.get(data.type);
			TextureRegion texture = a.getKeyFrame(data.stateTime);
			sb.draw(texture, data.position.x - texture.getRegionWidth() / 2,
					data.position.y - texture.getRegionHeight() / 2);

			if (!data.loop) {
				if (a.isAnimationFinished(data.stateTime)) {
					finishedAnimations.add(data);
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
			if (sound != null)
				sound.play();

			ongoingAnimations.add(data);
		}
	}

}
