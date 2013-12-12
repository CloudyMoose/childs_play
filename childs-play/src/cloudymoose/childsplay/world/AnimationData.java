package cloudymoose.childsplay.world;

import cloudymoose.childsplay.world.units.Unit;

import com.badlogic.gdx.math.Vector3;

public class AnimationData {

	public float stateTime;
	public AnimationType type;
	public SoundType sound;
	public Vector3 position;
	public boolean loop;
	public boolean isBlocking;
	public Unit[] toHide;

	public AnimationData(AnimationType type, Vector3 position, boolean blocking, boolean loop, Unit... toHide) {
		this.type = type;
		this.isBlocking = blocking;
		this.loop = loop;
		this.position = position;
		this.toHide = toHide;
	}
}
