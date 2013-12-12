package cloudymoose.childsplay.world;

public enum AnimationType {
	Melee(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
	CatapultFire(Constants.ENVIRONMENT_UNIT_SIZE, Float.POSITIVE_INFINITY);

	public final float maxWidth;
	public final float maxHeight;

	private AnimationType(float maxWidth, float maxHeight) {
		this.maxWidth = maxWidth;
		this.maxHeight = maxHeight;
	}

}
